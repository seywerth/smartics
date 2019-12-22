package at.seywerth.smartics.rest;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import at.seywerth.smartics.rest.api.MeteringDataDayRepository;
import at.seywerth.smartics.rest.model.InverterStatus;
import at.seywerth.smartics.rest.model.MeteringDataDay;
import at.seywerth.smartics.util.InverterCalculatorUtil;

/**
 * service to access metering data by rest.
 * 
 * @author Raphael Seywerth
 *
 */
@Service
public class MeteringDataDayService {

	private static final Logger LOG = LoggerFactory.getLogger(MeteringDataDayService.class);

	@Autowired
	private MeteringDataDayRepository meteringDataRepository;

	public MeteringDataDay save(final MeteringDataDay meteringData) {
		return meteringDataRepository.save(meteringData);
	}

	/**
	 * find all entries between the specified times
	 * @param startTime
	 * @param untilTime
	 * @return list of entries
	 */
	public List<MeteringDataDay> findWithinTimeRange(final Instant startTime, final Instant untilTime) {
		Instant until = untilTime == null ? startTime.plus(24, ChronoUnit.HOURS) : untilTime;

		List<MeteringDataDay> matches = meteringDataRepository.findWithinTimeRange(Timestamp.from(startTime), Timestamp.from(until));
		if (!matches.isEmpty()) {
			LOG.info("findWithinTimeRange found {} entries starting with: {}", matches.size(), matches.get(0).getStartTime());
		}

		return matches;
	}

	public MeteringDataDay getLatest() {
		return meteringDataRepository.findTopByOrderByUntilTimeDesc();
	}

	/**
	 * finds all daily entries.
	 * @return list of {@link MeteringDataDay}
	 */
	public List<MeteringDataDay> findAll() {
		List<MeteringDataDay> list = new ArrayList<>();
		meteringDataRepository.findAll().forEach(list::add);

		return list;
	}

	public static InverterStatus getStatusForMeteringData(final MeteringDataDay meteringData, final long size) {
		// warning on non 24-hr spans
		long diffSecs = meteringData.getStartTime().toInstant().until(meteringData.getUntilTime().toInstant(), ChronoUnit.MINUTES);
		if (diffSecs > 1470 || diffSecs < 1410) {
			LOG.warn("daily: run combined with {} mins not really 24 hours, data might not be valid!", diffSecs);
			return InverterStatus.NOT_ENOUGH_DATA;
		} else if (size < 286) {
			LOG.warn("daily: run combined {} entries - less than 12*24hrs (288-2/variance), data might be incomplete!", size);
			return InverterStatus.OK_PARTIAL;
		}
		return InverterStatus.OK;
	}

	/**
	 * find archive entry roughly for the specified time.
	 * @param creationTime
	 * @return null if none found or first entry found
	 */
	public MeteringDataDay findArchiveDataEntry(Instant creationTime) {
		// try to find already in db
		Instant startTime = creationTime.minus(2, ChronoUnit.HOURS);
		Instant untilTime = creationTime.plus(2, ChronoUnit.HOURS);
		List<MeteringDataDay> matches = meteringDataRepository.findWithinTimeRange(Timestamp.from(startTime), Timestamp.from(untilTime));

		if (matches == null || matches.isEmpty()) {
			LOG.warn("findArchiveDataEntry: no matching entry between {} and {} was found!", startTime, untilTime);
			return null;
		}
		if (matches.size() > 1) {
			LOG.info("findArchiveDataEntry: multiple matching entries, selecting startTime: {}", matches.get(0).getStartTime());
		}

		return matches.get(0);
	}

	/** recheck values of archive, calculated realtime data
	 * @param archiveData
	 * @param calculatedData
	 * @return
	 */
	public MeteringDataDay recheck(MeteringDataDay savedData, MeteringDataDay calculatedData) {
		if (savedData == null && calculatedData == null) {
			LOG.error("recheck: FATAL neither data was saved nor calculated!");
			return null;
		}
		MeteringDataDay result = null;
		boolean checkSavedData = savedData != null;
		boolean checkCalculation = calculatedData != null;

		if (checkSavedData) {
			result = savedData;
			// check saved..
		}
		if (result == null) {
			LOG.warn("recheck: no data was saved yet, using calculated data!");
			result = calculatedData;
		}

		if (checkCalculation) {
			recheckCalculation(result, calculatedData);
		}

		return result;
	}

	private MeteringDataDay recheckCalculation(MeteringDataDay result, MeteringDataDay calculated) {
		if (!InverterCalculatorUtil.compareBigDecimal(result.getPowerProduced(), calculated.getPowerProduced())) {
			LOG.warn("recheckCalculation: produced values differ, old {} and new {}, setting new!", result.getPowerProduced(), calculated.getPowerProduced());
			result.setPowerProduced(calculated.getPowerProduced());
		}
		if (!InverterCalculatorUtil.compareBigDecimal(result.getPowerConsumed(), calculated.getPowerConsumed())) {
			LOG.warn("recheckCalculation: consumed values differ, old {} and new {}, setting new!", result.getPowerConsumed(), calculated.getPowerConsumed());
			result.setPowerConsumed(calculated.getPowerConsumed());
		}
		if (!InverterCalculatorUtil.compareBigDecimal(result.getPowerFeedback(), calculated.getPowerFeedback())) {
			LOG.warn("recheckCalculation: feedback values differ, old {} and new {}, setting new!", result.getPowerFeedback(), calculated.getPowerFeedback());
			result.setPowerFeedback(calculated.getPowerFeedback());
		}
		if (!InverterCalculatorUtil.compareBigDecimal(result.getArchiveProduced(), calculated.getArchiveProduced())) {
			LOG.info("recheckArchive: produced values differ, old {} and new {}, setting new!", result.getArchiveProduced(), calculated.getArchiveProduced());
			result.setArchiveProduced(calculated.getArchiveProduced());
		}
		if (!InverterCalculatorUtil.compareBigDecimal(result.getArchiveConsumed(), calculated.getArchiveConsumed())) {
			LOG.info("recheckArchive: consumed values differ, old {} and new {}, setting new!", result.getArchiveConsumed(), calculated.getArchiveConsumed());
			result.setArchiveConsumed(calculated.getArchiveConsumed());
		}
		if (!InverterCalculatorUtil.compareBigDecimal(result.getArchiveFeedback(), calculated.getArchiveFeedback())) {
			LOG.info("recheckArchive: feedback values differ, old {} and new {}, setting new!", result.getArchiveFeedback(), calculated.getArchiveFeedback());
			result.setArchiveFeedback(calculated.getArchiveFeedback());
		}

		return result;
	}

}