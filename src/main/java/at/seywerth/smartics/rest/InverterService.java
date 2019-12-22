package at.seywerth.smartics.rest;

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
import at.seywerth.smartics.rest.mapper.InverterArchiveMapper;
import at.seywerth.smartics.rest.model.InverterDto;
import at.seywerth.smartics.rest.model.InverterStatus;
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
			result.setStatus(InverterStatus.NOT_ENOUGH_DATA);
			return result;
		}

		LOG.trace("calculateSummary from {} until {} found: {} entries for calc..", startTime, untilTime, list.size());
		Instant startTimeData = untilTime;
		Instant untilTimeData = startTime;
		for (MeteringDataMin entry : list) {
			if (entry.getStartTime().toInstant().isBefore(startTimeData)) {
				startTimeData = entry.getStartTime().toInstant();
			}
			if (entry.getUntilTime().toInstant().isAfter(untilTimeData)) {
				untilTimeData = entry.getUntilTime().toInstant();
			}
			result.addMeteringDataMinDto(InverterArchiveMapper.convertToDto(entry));

			// TODO use data from archive if not realtime available
			result.setPowerProduced(result.getPowerProduced().add(entry.getPowerProduced()));
			result.setPowerConsumed(result.getPowerConsumed().add(entry.getPowerConsumed()));
			result.setPowerFeedback(result.getPowerFeedback().add(entry.getPowerFeedback()));
		}
		Double powerFromNetwork = InverterCalculatorUtil.calcPowerFromNetwork(result.getPowerConsumed(), result.getPowerProduced(), result.getPowerFeedback());
		result.setPowerFromNetwork(InverterCalculatorUtil.getBigDecimal(powerFromNetwork));
		result.setPowerFromProduction(InverterCalculatorUtil.calcPowerFromProduction(result.getPowerProduced(), result.getPowerFeedback()));
		// TODO get cost and income from settings
		Double costKwh = 0.09;
		Double incomeKwh = 0.07;
		result.setCost(InverterCalculatorUtil.calcCost(powerFromNetwork, costKwh));
		result.setIncome(InverterCalculatorUtil.calcIncome(result.getPowerFeedback(), incomeKwh));
		result.setAutonomy(InverterCalculatorUtil.calcAutonomy(result.getPowerFromProduction(), result.getPowerConsumed()));
		result.setFromTime(startTimeData);
		result.setUntilTime(untilTimeData);

		if (list.size() < 286) {
			result.setStatus(InverterStatus.OK_PARTIAL);
		} else {
			result.setStatus(InverterStatus.OK);
		}

		LOG.info("calculateSummary from {} until {} found: {} entries, consumed: {}, feedback: {}, produced: {}.",
				startTime, untilTime, list.size(), result.getPowerConsumed(), result.getPowerFeedback(), result.getPowerProduced());

		return result;
	}

}