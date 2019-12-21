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
	VERSION("0.1.1"),
	/**
	 * enable automatic daily logger
	 */
	SCHEDULE_DAILY("false"),
	/**
	 * enable automatic logger each 5 seconds
	 */
	SCHEDULE_DETAIL("false"),
	/**
	 * enable automatic logger each 5 minutes
	 */
	SCHEDULE_ROUGH("false"),
	/**
	 * inverter api url
	 */
	INVERTER_URL("http://192.168.1.200"),
	/**
	 * inverter time difference in seconds
	 * - null being not initialized
	 */
	INVERTER_TIME_DIFFERENCE(null);

	private final String defaultValue;

	SettingName(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	String getDefaultValue() {
		return defaultValue;
	}

}