package at.seywerth.smartics.rest.api;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import at.seywerth.smartics.rest.model.ChargingData;

/**
 * repository interface for charging data rough/minute wise.
 * 
 * @author Raphael Seywerth
 *
 */
public interface ChargingDataRepository extends PagingAndSortingRepository<ChargingData, Long> {

   ChargingData findByStartTime(Timestamp startTime);

   @Query("select md from ChargingData md where md.startTime >= :startTime and md.startTime <= :untilTime order by md.startTime")
   List<ChargingData> findWithinTimeRange(Timestamp startTime, Timestamp untilTime);

   @Query("select md from ChargingData md where md.startTime >= :startTime order by md.startTime")
   List<ChargingData> findSinceStartTime(@Param("startTime") Timestamp startTime);

   ChargingData findTopByOrderByUntilTimeDesc();

}