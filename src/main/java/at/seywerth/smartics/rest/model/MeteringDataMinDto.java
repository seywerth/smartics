package at.seywerth.smartics.rest.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

/**
 * DTO for inverter summary data for 5 mins by rest.
 * 
 * @author Raphael Seywerth
 *
 */
public class MeteringDataMinDto implements Serializable {

	private static final long serialVersionUID = 1L;

	private Instant startTime;
	private Instant untilTime;
	private InverterStatus status;

	private BigDecimal powerProduced;
	private BigDecimal powerConsumed;
	private BigDecimal powerFeedback;

	private BigDecimal autonomy;

	public MeteringDataMinDto() {
	}

	public MeteringDataMinDto(final Instant startTime,
							  final Instant untilTime,
							  final BigDecimal powerProduced,
							  final BigDecimal powerConsumed,
							  final BigDecimal powerFeedback,
							  final InverterStatus status) {
		this.startTime = startTime;
		this.untilTime = untilTime;
		this.status = status;
		this.powerProduced = powerProduced;
		this.powerConsumed = powerConsumed;
		this.powerFeedback = powerFeedback;
	}

	public Instant getStartTime() {
		return startTime;
	}

	public void setStartTime(Instant startTime) {
		this.startTime = startTime;
	}

	public Instant getUntilTime() {
		return untilTime;
	}

	public void setUntilTime(Instant untilTime) {
		this.untilTime = untilTime;
	}

	public InverterStatus getStatus() {
		return status;
	}

	public void setStatus(InverterStatus status) {
		this.status = status;
	}

	public BigDecimal getPowerProduced() {
		return powerProduced;
	}

	public void setPowerProduced(BigDecimal powerProduced) {
		this.powerProduced = powerProduced;
	}

	public BigDecimal getPowerConsumed() {
		return powerConsumed;
	}

	public void setPowerConsumed(BigDecimal powerConsumed) {
		this.powerConsumed = powerConsumed;
	}

	public BigDecimal getPowerFeedback() {
		return powerFeedback;
	}

	public void setPowerFeedback(BigDecimal powerFeedback) {
		this.powerFeedback = powerFeedback;
	}

	public BigDecimal getAutonomy() {
		return autonomy;
	}

	public void setAutonomy(BigDecimal autonomy) {
		this.autonomy = autonomy;
	}

}