package at.seywerth.smartics.rest.api;

import org.springframework.stereotype.Repository;

import at.seywerth.smartics.rest.model.ChargerStatusDto;
import at.seywerth.smartics.rest.model.MeteringDataMin;
import at.seywerth.smartics.rest.model.Setting;

/**
 * repository interface for charger data.
 * 
 * @author Raphael Seywerth
 *
 */
@Repository
public interface ChargerRepository {

	/**
	 * get current status data (ampere, mode, time) from charger.
	 * 
	 * @return {@link ChargerStatusDto}
	 */
	public ChargerStatusDto getStatusData();

	/**
	 * set data (ampere, color) on charger.
	 * 
	 * @param String ampere 6-32/maxAmpere
	 * @param String color charging
	 * @param Boolean allow charging
	 * @return {@link ChargerStatusDto}
	 */
	public ChargerStatusDto setChargerData(final String ampere, final String color, final Boolean allowCharging);

	/**
	 * set current ampere at charger.
	 * 
	 * @param ampere
	 * @return true if successful, false otherwise
	 */
	public boolean setAmpere(final String ampere);

	/**
	 * set color for charging state at charger.
	 * 
	 * @param color as int value
	 * @return true if successful, false otherwise
	 */
	public boolean setColorCharging(final String color);

	/**
	 * set if charging is allowed at charger currently.
	 * 
	 * @param allowCharging true if allowed
	 * @return true if successful, false otherwise
	 */
	public boolean setAllowCharging(final Boolean allowCharging);

	/**
	 * analyzes and sets charger status data according to settings.
	 * 
	 * @param chargerMode mode to use: smart, fixed, deactivated
	 * @param meteringData summary data of last mins
	 */
	public void analyzeChargerStatus(Setting chargerMode, MeteringDataMin meteringData);

}