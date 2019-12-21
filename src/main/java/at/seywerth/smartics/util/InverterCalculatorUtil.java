package at.seywerth.smartics.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.seywerth.smartics.rest.model.MeteringDataDay;
import at.seywerth.smartics.rest.model.MeteringDataMin;
import at.seywerth.smartics.rest.model.MeteringDataSec;

/**
 * util to calculate values from inverter.
 * 
 * @author Raphael Seywerth
 *
 */
public class InverterCalculatorUtil {

	private static final Logger LOG = LoggerFactory.getLogger(InverterCalculatorUtil.class);

    /**
     * Hour contains 60*60 seconds.
     */
	private static final int HOURSECONDS = 3600;
    

    /**
     * calculate sum for entries by seconds
     * @param list
     * @return
     */
	public static MeteringDataMin calculateSumForSecEntries(List<MeteringDataSec> list) {
		if (list == null || list.isEmpty()) {
			return null;
		}
		// first value only used for from time
		Instant fromTime = list.get(0).getCreationTime().toInstant();
		double powerProduced = 0;
		double powerConsumed = 0;
		double powerFeedback = 0;

		for (int i=1; i < list.size(); i++) {
			MeteringDataSec data = list.get(i);
			// calculate millis for being able to round (otherwise there might be 11% difference through cutting off)
			long durMs = fromTime.until(data.getCreationTime().toInstant(), ChronoUnit.MILLIS);
			long durSecs = Math.round((double) durMs / 1000);
			LOG.trace("#{} duration of {} ms calculated: {} s", i, durMs, durSecs);

			powerProduced = powerProduced + calculatePowerByHour(data.getPowerProduced(), durSecs);
			powerConsumed = powerConsumed + calculatePowerByHour(data.getPowerConsumed(), durSecs);
			powerFeedback = powerFeedback + calculatePowerByHour(data.getPowerFeedback(), durSecs);

			// use as next from time
			fromTime = data.getCreationTime().toInstant();
		}

		MeteringDataMin meteringData = new MeteringDataMin(list.get(0).getCreationTime(), list.get(0).getCreationTime(),
				BigDecimal.valueOf(powerProduced).setScale(2, RoundingMode.HALF_UP),
				BigDecimal.valueOf(powerConsumed).setScale(2, RoundingMode.HALF_UP),
				BigDecimal.valueOf(powerFeedback).setScale(2, RoundingMode.HALF_UP), null);
		// add endtime of last entry
		if (list.size() > 1) {
			meteringData.setUntilTime(list.get(list.size()-1).getCreationTime());
		}
		LOG.trace("from {} until {} calculated produced: {} Wh", meteringData.getStartTime(), meteringData.getUntilTime(), meteringData.getPowerProduced());

		return meteringData;
	}

	private static double calculatePowerByHour(BigDecimal power, long durationSeconds) {
		if (power == null || power.compareTo(BigDecimal.ZERO) <= 0) {
			return 0;
		}
		return power.doubleValue() / HOURSECONDS * durationSeconds;
	}

    /**
     * calculate sum for entries by seconds
     * @param list
     * @return
     */
	public static MeteringDataDay calculateSumForMinEntries(List<MeteringDataMin> list) {
		if (list == null || list.isEmpty()) {
			return null;
		}
		MeteringDataDay meteringData = new MeteringDataDay(list.get(0).getStartTime(), list.get(0).getUntilTime(),
				BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, null);
		meteringData.setArchiveProduced(BigDecimal.ZERO);
		meteringData.setArchiveConsumed(BigDecimal.ZERO);
		meteringData.setArchiveFeedback(BigDecimal.ZERO);

		for (int i=1; i < list.size(); i++) {
			MeteringDataMin data = list.get(i);

			meteringData.setPowerProduced(meteringData.getPowerProduced().add(getValue(data.getPowerProduced())));
			meteringData.setPowerConsumed(meteringData.getPowerConsumed().add(getValue(data.getPowerConsumed())));
			meteringData.setPowerFeedback(meteringData.getPowerFeedback().add(getValue(data.getPowerFeedback())));

			meteringData.setArchiveProduced(meteringData.getArchiveProduced().add(getValue(data.getArchiveProduced())));
			meteringData.setArchiveConsumed(meteringData.getArchiveConsumed().add(getValue(data.getArchiveConsumed())));
			meteringData.setArchiveFeedback(meteringData.getArchiveFeedback().add(getValue(data.getArchiveFeedback())));
		}
		// add endtime of last entry
		if (list.size() > 1) {
			meteringData.setUntilTime(list.get(list.size()-1).getUntilTime());
		}

		return meteringData;
	}

	/**
	 * get null safe BigDecimal value.
	 * @param value
	 * @return
	 */
	public static BigDecimal getValue(final BigDecimal value) {
		if (value == null) {
			return BigDecimal.ZERO;
		}
		return value;
	}

	/**
	 * compare BigDecimal values
	 * @param oldValue
	 * @param newValue
	 * @return true when the same
	 */
	public static boolean compareBigDecimal(BigDecimal oldValue, BigDecimal newValue) {
		if (oldValue == null && newValue == null) {
			return true;
		}
		if (oldValue == null || newValue == null) {
			return false;
		}
		return oldValue.compareTo(newValue) == 0;
	}

}