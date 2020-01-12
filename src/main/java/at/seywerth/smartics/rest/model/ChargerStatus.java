package at.seywerth.smartics.rest.model;

/**
 * enum for connection status of charger
 * - 1: station free, nothing connected
 * - 2: loading
 * - 3: waiting for device
 * - 4: loading ended, device connected
 * 
 * @author Raphael Seywerth
 *
 */
public enum ChargerStatus {

	/**
	 * undefined
	 */
	UNDEFINED(0),
	/**
	 * station free, nothing connected
	 */
	READY(1),
	/**
	 * loading
	 */
	LOADING(2),
	/**
	 * waiting for device
	 */
	WAITING_FOR_DEVICE(3),
	/**
	 * loading ended, device connected
	 */
	LOADING_FINISHED(4);

	private final int code;

	ChargerStatus(int code) {
		this.code = code;
	}

	public int getCode() {
		return code;
	}

	public static ChargerStatus getByCode(Integer code){
		if (code == null || code == 0) {
			return ChargerStatus.UNDEFINED;
		}
	    for(ChargerStatus state : values()){
	        if(state.getCode() == code){
	            return state;
	        }
	    }
	    return null;
	}
}