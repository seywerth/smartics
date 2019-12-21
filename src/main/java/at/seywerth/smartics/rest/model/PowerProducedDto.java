package at.seywerth.smartics.rest.model;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * DTO for inverter archive data by rest.
 * 
 * @author Raphael Seywerth
 *
 */
public class PowerProducedDto {

	private BigDecimal pvCurrent;
	private Instant currentTime;

	public PowerProducedDto() {
	}

	public PowerProducedDto(BigDecimal pvCurrent, Instant currentTime) {
		this.pvCurrent = pvCurrent;
		this.currentTime = currentTime;
	}

	public BigDecimal getPvCurrent() {
		return pvCurrent;
	}

	public void setPvCurrent(BigDecimal pvCurrent) {
		this.pvCurrent = pvCurrent;
	}

	public Instant getCurrentTime() {
		return currentTime;
	}

	public void setCurrentTime(Instant currentTime) {
		this.currentTime = currentTime;
	}

}