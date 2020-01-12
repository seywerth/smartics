package at.seywerth.smartics.rest.model;

/**
 * enum for status of inverter
 * 
 * @author Raphael Seywerth
 *
 */
public enum InverterStatus {

	/**
	 * undefined
	 */
	UNDEFINED(0),
	/**
	 * sucessful
	 */
	OK(200),
	/**
	 * 206 Partial Content
	 */
	OK_PARTIAL(206),
	/**
	 * 416 Range Not Satisfiable
	 */
	NOT_ENOUGH_DATA(416),
	/**
	 * 502 Bad Gateway
	 */
	NOT_RESPONDING(504);

	private final long code;

	InverterStatus(long code) {
		this.code = code;
	}

	public long getCode() {
		return code;
	}

	public static InverterStatus getByCode(Long code){
		if (code == null || code == 0) {
			return InverterStatus.OK;
		}
	    for(InverterStatus state : values()){
	        if(state.getCode() == code){
	            return state;
	        }
	    }
	    return null;
	}
}