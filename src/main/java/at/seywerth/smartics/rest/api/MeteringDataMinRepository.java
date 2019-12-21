package at.seywerth.smartics.rest.api;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import at.seywerth.smartics.rest.model.MeteringDataMin;

/**
 * repository interface for metering data rough/minute wise.
 * 
 * @author Raphael Seywerth
 *
 */
public interface MeteringDataMinRepository extends PagingAndSortingRepository<MeteringDataMin, Long> {

	MeteringDataMin findByStartTime(Timestamp startTime);

	@Query("select md from MeteringDataMin md where md.startTime >= :startTime and md.startTime <= :untilTime order by md.startTime")
	List<MeteringDataMin> findWithinTimeRange(Timestamp startTime, Timestamp untilTime);

	@Query("select md from MeteringDataMin md where md.startTime >= :startTime order by md.startTime")
	List<MeteringDataMin> findSinceStartTime(@Param("startTime") Timestamp startTime);

	MeteringDataMin findTopByOrderByUntilTimeDesc();

}