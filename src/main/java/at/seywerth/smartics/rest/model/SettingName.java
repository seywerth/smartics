package at.seywerth.smartics.rest.model;

/**
 * enum for settings
 * 
 * @author Raphael Seywerth
 *
 */
public enum SettingName {

	/**
	 * version of app
	 */
	VERSION("0.1.3", "current version"),
	/**
	 * enable automatic daily logger
	 */
	SCHEDULE_DAILY("false", "automatic daily metering data logger"),
	/**
	 * enable automatic logger each 5 seconds
	 */
	SCHEDULE_DETAIL("false", "automatic detail (every other second) metering data logger"),
	/**
	 * enable automatic logger each 5 minutes
	 */
	SCHEDULE_ROUGH("false", "automatic (every other minute) metering data logger"),
	/**
	 * inverter api url
	 */
	INVERTER_URL("http://192.168.1.200", "url of inverter to query for metering data"),
	/**
	 * inverter time difference in seconds
	 * - null being not initialized
	 */
	INVERTER_TIME_DIFFERENCE(null, "time difference of inverter to app"),
	/**
	 * charger api url
	 */
	CHARGER_URL("http://192.168.1.151", "url of charger to query and set data to"),
	/**
	 * charging mode
	 * - null, UNAVAILABLE charger not available
	 * - DEACTIVATED, charging disabled
	 * - FIXED with fixed ampere
	 * - SMART ampere automatically chosen
	 */
	CHARGER_MODE(null, "charger mode: unavailable, deactivated (if charging disabled), fixed or smart"),
	/**
	 * voltage used by charger
	 */
	CHARGER_VOLTAGE("230", "voltage for the charger, is needed for the calculation of ampere"),
	/**
	 * current ampere specified at charger
	 */
	CHARGER_AMPERE_CURRENT(null, "current ampere set as charger output"),
	/**
	 * status checks (setup settings with default values if missing)
	 * - true: do with daily scheduler
	 * - skip
	 */
	STATUS_CHECK("true", "enables a status check to f.e. set time differences");


	private final String defaultValue;
	private final String description;

	SettingName(final String defaultValue, final String description) {
		this.defaultValue = defaultValue;
		this.description = description;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public String getDescription() {
		return description;
	}
}