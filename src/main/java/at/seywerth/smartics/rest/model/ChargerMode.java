package at.seywerth.smartics.rest.model;

/**
 * enum for charging mode of charger
 * - UNAVAILABLE or null if no state was queried
 * - DEACTIVATED, charging disabled
 * - FIXED with fixed ampere
 * - SMART ampere automatically chosen
 * 
 * @author Raphael Seywerth
 *
 */
public enum ChargerMode {

	/**
	 * charger not available
	 */
	UNAVAILABLE,
	/**
	 * no charging
	 */
	DEACTIVATED,
	/**
	 * charger set to fixed ampere
	 */
	FIXED,
	/**
	 * charger automatically retrieves ampere
	 */
	SMART;

	public static ChargerMode getByCode(String code) {
		if (code == null) {
			return ChargerMode.UNAVAILABLE;
		}
	    for (ChargerMode state : values()) {
	        if (state.name().equals(code)) {
	            return state;
	        }
	    }
	    return null;
	}
}