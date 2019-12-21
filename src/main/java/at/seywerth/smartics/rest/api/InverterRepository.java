package at.seywerth.smartics.rest.api;

import java.util.List;

import org.springframework.stereotype.Repository;

import at.seywerth.smartics.rest.model.InverterDto;
import at.seywerth.smartics.rest.model.MeteringDataMin;

/**
 * repository interface for inverter data.
 * 
 * @author Raphael Seywerth
 *
 */
@Repository
public interface InverterRepository {

	/**
	 * get current power and consumption data from inverter.
	 * 
	 * @return {@link InverterDto}
	 */
	public InverterDto getRealtimeData();

	/**
	 * supply date for which archive data should be queried.
	 * 
	 * @param string day
	 * @return list of values and timestamp for day
	 */
	public List<MeteringDataMin> getArchiveDataPVForDay(final String day);

}