package at.seywerth.smartics.rest.api;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import at.seywerth.smartics.rest.model.MeteringDataSec;

/**
 * repository interface for detailed metering data (seconds).
 * 
 * @author Raphael Seywerth
 *
 */
public interface MeteringDataSecRepository extends CrudRepository<MeteringDataSec, Long> {

	MeteringDataSec findByCreationTime(Timestamp creationTime);

	@Query("select md from MeteringDataSec md where md.creationTime >= :startTime and md.creationTime <= :untilTime order by md.creationTime")
	List<MeteringDataSec> findWithinTimeRange(Timestamp startTime, Timestamp untilTime);

	@Query("select md from MeteringDataSec md where md.creationTime >= :creationTime order by md.creationTime")
	List<MeteringDataSec> findSinceCreationTime(@Param("creationTime") Timestamp creationTime);

	MeteringDataSec findTopByOrderByCreationTimeDesc();

}