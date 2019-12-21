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

import at.seywerth.smartics.rest.api.MeteringDataMinRepository;
import at.seywerth.smartics.rest.model.InverterStatus;
import at.seywerth.smartics.rest.model.MeteringDataMin;
import at.seywerth.smartics.util.InverterCalculatorUtil;

/**
 * service to access metering data by rest.
 * 
 * @author Raphael Seywerth
 *
 */
@Service
public class MeteringDataMinService {

	private static final Logger LOG = LoggerFactory.getLogger(MeteringDataMinService.class);

	@Autowired
	private MeteringDataMinRepository meteringDataRepository;

	public MeteringDataMin save(final MeteringDataMin meteringData) {
		return meteringDataRepository.save(meteringData);
	}

	public List<MeteringDataMin> findForLastHours(final Instant currentTime,
												  final long numberOfHrs) {
		Timestamp creation = Timestamp.from(currentTime.minusSeconds(numberOfHrs * 60 * 60));
		return meteringDataRepository.findSinceStartTime(creation);
	}

	public MeteringDataMin getLatest() {
		return meteringDataRepository.findTopByOrderByUntilTimeDesc();
	}

	public List<MeteringDataMin> findWithinRange(final Instant startTime, final Instant untilTime) {
		return meteringDataRepository.findWithinTimeRange(Timestamp.from(startTime), Timestamp.from(untilTime));
	}

	/**
	 * remove since a lot of entries would be found.
	 * @return
	 */
	@Deprecated
	public List<MeteringDataMin> findAll() {
		List<MeteringDataMin> list = new ArrayList<>();
		meteringDataRepository.findAll().forEach(list::add);

		return list;
	}

	/**
	 * find all entries since the specified time.
	 * @param sinceTime Instant
	 * @return
	 */
	public List<MeteringDataMin> findAllSince(final Instant sinceTime) {
		return meteringDataRepository.findSinceStartTime(Timestamp.from(sinceTime));
	}

	/**
	 * find archive entry roughly for the specified time.
	 * @param creationTime
	 * @return null if none found or first entry found
	 */
	public MeteringDataMin findArchiveDataEntry(Instant creationTime) {
		// try to find already in db
		Instant startTime = creationTime.minus(150, ChronoUnit.SECONDS);
		Instant untilTime = creationTime.plus(150, ChronoUnit.SECONDS);
		List<MeteringDataMin> matches = meteringDataRepository.findWithinTimeRange(Timestamp.from(startTime), Timestamp.from(untilTime));

		if (matches == null || matches.isEmpty()) {
			LOG.warn("recalculate: no matching entry between {} and {} was found!", startTime, untilTime);
			return null;
		}
		if (matches.size() > 1) {
			LOG.info("recalculate: multiple matching entries, selecting startTime: {}", matches.get(0).getStartTime());
		}

		return matches.get(0);
	}

	/** recheck values of archive, calculated realtime and inverter archive data
	 * @param archiveData
	 * @return
	 */
	public MeteringDataMin recheck(MeteringDataMin savedData, MeteringDataMin calculatedData, MeteringDataMin inverterData) {
		if (savedData == null && calculatedData == null && inverterData == null) {
			LOG.error("recheck: FATAL neither data was saved nor calculated or gotten by inverter!");
			return null;
		}
		MeteringDataMin result = null;
		boolean checkSavedData = savedData != null;
		boolean checkCalculation = calculatedData != null;
		boolean checkArchive = inverterData != null;

		if (checkSavedData) {
			result = savedData;
			// check saved..
		}
		if (result == null && calculatedData != null) {
			LOG.warn("recheck: no data was saved yet, using calculated data!");
			result = calculatedData;
		}
		if (result == null) {
			LOG.warn("recheck: no data was saved and no realtime data available, using rough inverter data!");
			result = inverterData;
			if (result.getUntilTime() == null) {
				Instant until = result.getStartTime().toInstant().plus(5, ChronoUnit.MINUTES);
				LOG.trace("recalculate: setting untilTime to {}!", until);
				result.setUntilTime(Timestamp.from(until));
			}
			result.setStatusCode(InverterStatus.OK_PARTIAL.getCode());
		}

		if (checkCalculation) {
			result = recheckCalculation(result, calculatedData);
		}
		if (checkArchive) {
			result = recheckArchive(result, inverterData);
		}

		return result;
	}

	private MeteringDataMin recheckCalculation(MeteringDataMin result, MeteringDataMin calculated) {
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

		return result;
	}

	public static InverterStatus getStatusForMeteringData(final MeteringDataMin meteringData, final long size) {
		// warning on non 5-min spans
		long diffSecs = meteringData.getStartTime().toInstant().until(meteringData.getUntilTime().toInstant(), ChronoUnit.SECONDS);
		if (diffSecs > 330 || diffSecs < 230) {
			LOG.warn("rough: run combined with {} secs not really 5 minutes, data might not be valid!", diffSecs);
			return InverterStatus.NOT_ENOUGH_DATA;
		} else if (size < 60) {
			LOG.warn("rough: run combined {} entries - less than 12*5mins (60), data might be really rough!", size);
			return InverterStatus.OK_PARTIAL;
		}
		return InverterStatus.OK;
	}

	private MeteringDataMin recheckArchive(MeteringDataMin result, MeteringDataMin inverterData) {
		if (!InverterCalculatorUtil.compareBigDecimal(result.getArchiveProduced(), inverterData.getArchiveProduced())) {
			LOG.info("recheckArchive: produced values differ, old {} and new {}, setting new!", result.getArchiveProduced(), inverterData.getArchiveProduced());
			result.setArchiveProduced(inverterData.getArchiveProduced());
		}
		if (!InverterCalculatorUtil.compareBigDecimal(result.getArchiveConsumed(), inverterData.getArchiveConsumed())) {
			LOG.info("recheckArchive: consumed values differ, old {} and new {}, setting new!", result.getArchiveConsumed(), inverterData.getArchiveConsumed());
			result.setArchiveConsumed(inverterData.getArchiveConsumed());
		}
		if (!InverterCalculatorUtil.compareBigDecimal(result.getArchiveFeedback(), inverterData.getArchiveFeedback())) {
			LOG.info("recheckArchive: feedback values differ, old {} and new {}, setting new!", result.getArchiveFeedback(), inverterData.getArchiveFeedback());
			result.setArchiveFeedback(inverterData.getArchiveFeedback());
		}
		
		return result;
	}

}