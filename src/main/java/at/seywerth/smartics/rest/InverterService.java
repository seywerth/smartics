package at.seywerth.smartics.rest;

import java.math.BigDecimal;
import java.text.ParseException;
import java.time.Instant;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import at.seywerth.smartics.rest.api.InverterRepository;
import at.seywerth.smartics.rest.mapper.InverterArchiveMapper;
import at.seywerth.smartics.rest.model.InverterDto;
import at.seywerth.smartics.rest.model.InverterStatus;
import at.seywerth.smartics.rest.model.MeteringDataCurrentDto;
import at.seywerth.smartics.rest.model.MeteringDataDay;
import at.seywerth.smartics.rest.model.MeteringDataMin;
import at.seywerth.smartics.rest.model.MeteringDataMinDto;
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

	public MeteringDataCurrentDto currentSummary() {
	   MeteringDataCurrentDto dto = new MeteringDataCurrentDto();
	   MeteringDataSec data = meteringDataSecService.getLatest();
	   if (data != null) {
	      dto.setCreationTime(data.getCreationTime().toInstant());
	      dto.setPowerConsumed(data.getPowerConsumed());
	      dto.setPowerProduced(data.getPowerProduced() != null ? data.getPowerProduced() : BigDecimal.ZERO);
	      dto.setPowerFeedback(data.getPowerFeedback() != null ? data.getPowerFeedback() : BigDecimal.ZERO);
	      dto.setStatus(InverterStatus.getByCode(data.getStatusCode()));
	      dto.setPowerFromNetwork(InverterCalculatorUtil.getBigDecimal(
	            InverterCalculatorUtil.calcPowerFromNetwork(dto.getPowerConsumed(), dto.getPowerProduced(), dto.getPowerFeedback())));
	      dto.setPowerFromProduction(InverterCalculatorUtil.calcPowerFromProduction(dto.getPowerProduced(), dto.getPowerFeedback()));
	   }
	   return dto;
	}

	public MeteringDataSummaryDto calculateSummary(String date) {
	   // get type, start and end date
	   final String[] dateSplit = date.split("\\.");
	   ChronoUnit unit = ChronoUnit.YEARS;
      Calendar cal = Calendar.getInstance();
      Instant startTime = null;
      Instant untilTime = null;
      try {
   	   if (dateSplit.length > 3) {
   	      LOG.error("calculateSummary parse date format exception for date: {}", date);
            return new MeteringDataSummaryDto();
   	   } else if (dateSplit.length == 3) {
   	      unit = ChronoUnit.DAYS;
   	      startTime = InverterDateTimeFormater.getInstantForSDF(date);
   	      untilTime = startTime.plus(1, unit);
   	   } else if (dateSplit.length == 2) {
   	      unit = ChronoUnit.MONTHS;
   	      Integer year = Integer.parseInt(dateSplit[1]);
   	      Integer month = Integer.parseInt(dateSplit[0]) -1;
   	      Integer day = 1;
   	      cal.set(year, month, day, 0, 0, 0);
   	      cal.set(Calendar.MILLISECOND, 0);
   	      // substract 30 mins to take inaccuracy of min-entry creation time into account
   	      startTime = cal.toInstant().minus(30, ChronoUnit.MINUTES);
   	      cal.set(year, month, cal.getActualMaximum(Calendar.DAY_OF_MONTH), 23, 59, 59);
            // substract 30 mins to take inaccuracy of min-entry creation time into account
   	      untilTime = cal.toInstant().minus(30, ChronoUnit.MINUTES);
   	   } else {
            Integer year = Integer.parseInt(dateSplit[0]);
            Integer month = 0;
            Integer day = 1;
            cal.set(year, month, day, 0, 0, 0);
            cal.set(Calendar.MILLISECOND, 0);
            startTime = cal.toInstant();
            cal.set(year, 11, cal.getActualMaximum(Calendar.DAY_OF_YEAR), 23, 59, 59);
            untilTime = cal.toInstant();
   	   }
      } catch (ParseException e) {
         LOG.error("calculateSummary parse day format exception {}", e.getMessage());
         return new MeteringDataSummaryDto();
      }

      LOG.info("calculateSummary input: {}, unit: {}, start: {} until: {}", date, unit, startTime, untilTime);

      if (ChronoUnit.DAYS == unit) {
         return calculateSummaryMin(startTime, untilTime);
      }
      
      return calculateSummaryDay(startTime, untilTime);
	}

	private MeteringDataSummaryDto calculateSummaryMin(final Instant startTime, final Instant untilTime) {
	   MeteringDataSummaryDto result = new MeteringDataSummaryDto();
		// find archived data if already exists
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
			// uses data from archive if not realtime available
			MeteringDataMinDto convertedData = InverterArchiveMapper.convertToDto(entry);
			result.addMeteringDataMinDto(convertedData);

			result.setPowerProduced(result.getPowerProduced().add(convertedData.getPowerProduced()));
			result.setPowerConsumed(result.getPowerConsumed().add(convertedData.getPowerConsumed()));
			result.setPowerFeedback(result.getPowerFeedback().add(convertedData.getPowerFeedback()));
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

  private MeteringDataSummaryDto calculateSummaryDay(final Instant startTime, final Instant untilTime) {
      MeteringDataSummaryDto result = new MeteringDataSummaryDto();
      // find archived data if already exists
      result.setFromTime(startTime);
      result.setUntilTime(untilTime);
      List<MeteringDataDay> list = meteringDataDayService.findWithinTimeRange(startTime, untilTime);
      if (list.isEmpty()) {
         LOG.warn("calculateSummary no entries were found between: {} and {}!", startTime, untilTime);
         result.setStatus(InverterStatus.NOT_ENOUGH_DATA);
         return result;
      }

      LOG.trace("calculateSummary from {} until {} found: {} entries for calc..", startTime, untilTime, list.size());
      Instant startTimeData = untilTime;
      Instant untilTimeData = startTime;
      for (MeteringDataDay entry : list) {
         if (entry.getStartTime().toInstant().isBefore(startTimeData)) {
            startTimeData = entry.getStartTime().toInstant();
         }
         if (entry.getUntilTime().toInstant().isAfter(untilTimeData)) {
            untilTimeData = entry.getUntilTime().toInstant();
         }
         // uses data from archive if not realtime available
         MeteringDataMinDto convertedData = InverterArchiveMapper.convertToDto(entry);
         result.addMeteringDataMinDto(convertedData);

         result.setPowerProduced(result.getPowerProduced().add(convertedData.getPowerProduced()));
         result.setPowerConsumed(result.getPowerConsumed().add(convertedData.getPowerConsumed()));
         result.setPowerFeedback(result.getPowerFeedback().add(convertedData.getPowerFeedback()));
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

      final int dayCount = YearMonth.from(startTime.plus(1, ChronoUnit.HOURS).atZone(ZoneId.of("Europe/Vienna"))).lengthOfMonth();
      LOG.trace("month has {} days", dayCount);
      if (list.size() < dayCount) {
         result.setStatus(InverterStatus.OK_PARTIAL);
      } else {
         result.setStatus(InverterStatus.OK);
      }

      LOG.info("calculateSummary from {} until {} found: {} entries, consumed: {}, feedback: {}, produced: {}.",
            startTime, untilTime, list.size(), result.getPowerConsumed(), result.getPowerFeedback(), result.getPowerProduced());

      return result;
   }
}