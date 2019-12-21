package at.seywerth.smartics.rest.model;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * DTO for inverter realtime data by rest.
 * 
 * @author Raphael Seywerth
 *
 */
public class InverterDto {

	private BigDecimal pvDay;

	private BigDecimal pvCurrent;
	private BigDecimal loadCurrent;
	private BigDecimal loadAkku;
	private BigDecimal loadGrid;

	private Instant currentTime;
	private InverterStatus statusCode;

	public InverterDto() {
	}

	public InverterDto(BigDecimal pvDay, BigDecimal pvCurrent, BigDecimal loadCurrent, BigDecimal loadAkku,
			BigDecimal loadGrid, Instant currentTime, InverterStatus statusCode) {
		this.pvDay = pvDay;
		
		this.pvCurrent = pvCurrent;
		this.loadCurrent = loadCurrent;
		this.loadAkku = loadAkku;
		this.loadGrid = loadGrid;

		this.currentTime = currentTime;
		this.statusCode = statusCode;
	}

	public BigDecimal getPvDay() {
		return pvDay;
	}

	public void setPvDay(BigDecimal pvDay) {
		this.pvDay = pvDay;
	}

	public BigDecimal getPvCurrent() {
		return pvCurrent;
	}

	public void setPvCurrent(BigDecimal pvCurrent) {
		this.pvCurrent = pvCurrent;
	}

	public BigDecimal getLoadCurrent() {
		return loadCurrent;
	}

	public void setLoadCurrent(BigDecimal loadCurrent) {
		this.loadCurrent = loadCurrent;
	}

	public BigDecimal getLoadAkku() {
		return loadAkku;
	}

	public void setLoadAkku(BigDecimal loadAkku) {
		this.loadAkku = loadAkku;
	}

	public BigDecimal getLoadGrid() {
		return loadGrid;
	}

	public void setLoadGrid(BigDecimal loadGrid) {
		this.loadGrid = loadGrid;
	}

	public Instant getCurrentTime() {
		return currentTime;
	}

	public void setCurrentTime(Instant currentTime) {
		this.currentTime = currentTime;
	}

	public InverterStatus getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(InverterStatus statusCode) {
		this.statusCode = statusCode;
	}

}