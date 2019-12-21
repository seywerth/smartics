package at.seywerth.smartics.rest.api;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import at.seywerth.smartics.rest.model.MeteringDataDay;

/**
 * repository interface for daily metering data.
 * 
 * @author Raphael Seywerth
 *
 */
public interface MeteringDataDayRepository extends CrudRepository<MeteringDataDay, Long> {

	MeteringDataDay findByStartTime(Timestamp startTime);

	MeteringDataDay findTopByOrderByUntilTimeDesc();

	@Query("select md from MeteringDataDay md where md.startTime >= :startTime and md.startTime <= :untilTime order by md.startTime")
	List<MeteringDataDay> findWithinTimeRange(Timestamp startTime, Timestamp untilTime);

}