package at.seywerth.smartics.rest;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import at.seywerth.smartics.rest.api.InverterRepository;
import at.seywerth.smartics.rest.model.InverterDto;
import at.seywerth.smartics.rest.model.MeteringDataDay;
import at.seywerth.smartics.rest.model.MeteringDataMin;
import at.seywerth.smartics.rest.model.MeteringDataSec;
import at.seywerth.smartics.rest.model.MeteringDataSummaryDto;
import at.seywerth.smartics.util.InverterCalculatorUtil;
import at.seywerth.smartics.util.InverterDateTimeFormater;

/**
 * service to access inverter data by rest.
 * 
 * @author Raphael Seywerth
 *
 */
@Service
public class InverterService {

	private static final Logger LOG = LoggerFactory.getLogger(InverterService.class);

	@Autowired
	private InverterRepository inverterRepository;

	@Autowired
	private MeteringDataDayService meteringDataDayService;
	@Autowired
	private MeteringDataMinService meteringDataMinService;
	@Autowired
	private MeteringDataSecService meteringDataSecService;


	public InverterDto getRealtimeData() {
		return inverterRepository.getRealtimeData();
	}

	public List<MeteringDataMin> getArchiveData(String day) {
		return inverterRepository.getArchiveDataPVForDay(day);
	}

	/**
	 * recalculates the archive data for the specified day.
	 * uses inverter data, as well as db data if existing.
	 * @param day
	 * @return
	 */
	public List<MeteringDataMin> recalculateArchiveData(String day) {
		List<MeteringDataMin> archiveData = inverterRepository.getArchiveDataPVForDay(day);

		List<MeteringDataMin> resultList = new ArrayList<>();
		for (MeteringDataMin data : archiveData) {
			// find archived data if already exists
			MeteringDataMin savedData = meteringDataMinService.findArchiveDataEntry(data.getStartTime().toInstant());

			Instant archiveUntilTime = data.getUntilTime() != null ? data.getUntilTime().toInstant() : null;
			Instant startTime = savedData != null ? savedData.getStartTime().toInstant() : data.getStartTime().toInstant();
			Instant untilTime = savedData != null ? savedData.getUntilTime().toInstant() : archiveUntilTime;
			// find written realtime data to check, if it exists
			List<MeteringDataSec> list = meteringDataSecService.findWithinTimeRange(startTime, untilTime);
			MeteringDataMin calculatedData = InverterCalculatorUtil.calculateSumForSecEntries(list);

			MeteringDataMin checkedData = meteringDataMinService.recheck(savedData, calculatedData, data);
			if (calculatedData != null) {
				checkedData.setStatusCode(MeteringDataMinService.getStatusForMeteringData(calculatedData, list.size()).getCode());
			}

			if (checkedData != null) {
				// save update
				resultList.add(meteringDataMinService.save(checkedData));
				LOG.info("rough from {} saved: {} produced, {} archive produced, {} consumed, {} archive consumed!",
						checkedData.getStartTime(),	checkedData.getPowerProduced(), checkedData.getArchiveProduced(),
						checkedData.getPowerConsumed(), checkedData.getArchiveConsumed());
			}
		}
		// if result is given for the whole day, write daily summary
		MeteringDataDay meteringDataDay = InverterCalculatorUtil.calculateSumForMinEntries(resultList);
		if (meteringDataDay != null) {
			// get saved entry to update if exists
			MeteringDataDay savedDataDay = meteringDataDayService.findArchiveDataEntry(meteringDataDay.getStartTime().toInstant());
			// verify/override savedDay
			MeteringDataDay checkedDataDay = meteringDataDayService.recheck(savedDataDay, meteringDataDay);

			checkedDataDay.setStatusCode(MeteringDataDayService.getStatusForMeteringData(checkedDataDay, resultList.size()).getCode());
			meteringDataDayService.save(checkedDataDay);
			LOG.info("daily from {} saved: {} produced, {} archive produced, {} consumed, {} archive consumed!",
					checkedDataDay.getStartTime(), checkedDataDay.getPowerProduced(), checkedDataDay.getArchiveProduced(),
					checkedDataDay.getPowerConsumed(), checkedDataDay.getArchiveConsumed());
		}

		return resultList;
	}

	public MeteringDataSummaryDto calculateSummary(String day) {
		MeteringDataSummaryDto result = new MeteringDataSummaryDto();
    	Instant startTime;
		try {
			startTime = InverterDateTimeFormater.getInstantForSDF(day);
		} catch (ParseException e) {
			LOG.error("calculateSummary parse day format exception {}", e.getMessage());
			return result;
		}

		// find archived data if already exists
		Instant untilTime = startTime.plus(1, ChronoUnit.DAYS);
		result.setFromTime(startTime);
		result.setUntilTime(untilTime);
		List<MeteringDataMin> list = meteringDataMinService.findWithinRange(startTime, untilTime);
		if (list.isEmpty()) {
			LOG.warn("calculateSummary no entries were found between: {} and {}!", startTime, untilTime);
			return result;
		}

		LOG.info("calculateSummary from {} until {} found: {} entries for calc..", startTime, untilTime, list.size());
		result.setMeteringDataMins(list);
		for (MeteringDataMin entry : list) {
			// TODO use data from archive if not realtime available
			result.setPowerProduced(result.getPowerProduced().add(entry.getPowerProduced()));
			result.setPowerConsumed(result.getPowerConsumed().add(entry.getPowerConsumed()));
			result.setPowerFeedback(result.getPowerFeedback().add(entry.getPowerFeedback()));
		}
		Double powerFromNetwork = calcPowerFromNetwork(result.getPowerConsumed(), result.getPowerProduced(), result.getPowerFeedback());
		result.setPowerFromNetwork(getBigDecimal(powerFromNetwork));
		result.setPowerFromProduction(result.getPowerProduced().subtract(result.getPowerFeedback()));
		// TODO get cost and income from settings
		Double costKwh = 0.09;
		Double incomeKwh = 0.07;
		result.setCost(calcCost(powerFromNetwork, costKwh));
		result.setIncome(calcIncome(result.getPowerFeedback(), incomeKwh));
		result.setAutonomy(calcAutonomy(result.getPowerFromProduction(), result.getPowerConsumed()));

		LOG.info("calculateSummary from {} until {} found: {} entries, consumed: {}, feedback: {}, produced: {}.",
				startTime, untilTime, list.size(), result.getPowerConsumed(), result.getPowerFeedback(), result.getPowerFeedback());

		return result;
	}

	private static BigDecimal calcAutonomy(BigDecimal powerFromProduction, BigDecimal powerConsumed) {
		return BigDecimal.valueOf(powerFromProduction.doubleValue() / powerConsumed.doubleValue())
				.setScale(2, RoundingMode.HALF_UP);
	}
	private static BigDecimal getBigDecimal(Double value) {
		return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP);
	}

	private static BigDecimal calcCost(Double powerFromNetwork, Double costKwh) {
		return getBigDecimal(powerFromNetwork / 1000 * costKwh).setScale(2, RoundingMode.HALF_UP);
	}

	private static BigDecimal calcIncome(BigDecimal feedback, Double incomeKwh) {
		return getBigDecimal(feedback.doubleValue() / 1000 * incomeKwh).setScale(2, RoundingMode.HALF_UP);
	}

	private static Double calcPowerFromNetwork(BigDecimal consumed, BigDecimal produced, BigDecimal feedback) {
		return consumed.subtract(produced.subtract(feedback)).doubleValue();
	}
}