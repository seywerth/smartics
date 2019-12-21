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

import at.seywerth.smartics.rest.api.MeteringDataSecRepository;
import at.seywerth.smartics.rest.model.MeteringDataSec;

/**
 * service to access metering data by rest.
 * 
 * @author Raphael Seywerth
 *
 */
@Service
public class MeteringDataSecService {

	private static final Logger LOG = LoggerFactory.getLogger(MeteringDataSecService.class);

	@Autowired
	private MeteringDataSecRepository meteringDataRepository;

	public MeteringDataSec save(final MeteringDataSec meteringData) {
		return meteringDataRepository.save(meteringData);
	}

	public List<MeteringDataSec> findForLastMinutes(final Instant currentTime,
													final long numberOfMin) {
		Timestamp creation = Timestamp.from(currentTime.minusSeconds(numberOfMin * 60));
		return meteringDataRepository.findSinceCreationTime(creation);
	}

	public MeteringDataSec getLatest() {
		return meteringDataRepository.findTopByOrderByCreationTimeDesc();
	}

	/**
	 * remove since a lot of entries would be found.
	 * @return
	 */
	@Deprecated
	public List<MeteringDataSec> findAll() {
		List<MeteringDataSec> list = new ArrayList<>();
		meteringDataRepository.findAll().forEach(list::add);

		return list;
	}

	/**
	 * find all entries since the specified time.
	 * @param sinceTime Instant
	 * @return
	 */
	public List<MeteringDataSec> findAllSince(final Instant sinceTime) {
		return meteringDataRepository.findSinceCreationTime(Timestamp.from(sinceTime));
	}

	/**
	 * find all entries between the specified times
	 * @param startTime
	 * @param untilTime
	 * @return list of entries
	 */
	public List<MeteringDataSec> findWithinTimeRange(final Instant startTime, final Instant untilTime) {
		Instant until = untilTime == null ? startTime.plus(5, ChronoUnit.MINUTES) : untilTime;

		List<MeteringDataSec> matches = meteringDataRepository.findWithinTimeRange(Timestamp.from(startTime), Timestamp.from(until));
		if (!matches.isEmpty()) {
			LOG.info("findWithinTimeRange found {} entries starting with: {}", matches.size(), matches.get(0).getCreationTime());
		}

		return matches;
	}

}