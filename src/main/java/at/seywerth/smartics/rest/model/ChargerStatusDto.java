package at.seywerth.smartics.rest.model;

import java.time.Instant;

/**
 * DTO for inverter realtime data by rest.
 * - all kWh are actually 0.1 kw here named mkWhs
 * 
 * @author Raphael Seywerth
 *
 */
public class ChargerStatusDto {

	private Instant currentTime;
	private ChargerStatus connectionStatus;
	private Integer temperature;
	private Integer maxAmpere;
	private Integer loadedMkWhTotal;

	private Boolean allowCharging;
	private Integer ampere;
	private Integer colorCharging;
	private Integer colorIdle;

	private Boolean autoStop;
	private Integer autoStopMkWh;
	private Integer loadedDWh;


	public ChargerStatusDto() {
	}

	public ChargerStatusDto(Instant currentTime,
							ChargerStatus connectionStatus,
							Integer temperature,
							Integer maxAmpere,
							Integer loadedMkWhTotal) {
		this.currentTime = currentTime;
		this.connectionStatus = connectionStatus;
		this.temperature = temperature;
		this.maxAmpere = maxAmpere;
		this.loadedMkWhTotal = loadedMkWhTotal;
	}

	public Instant getCurrentTime() {
		return currentTime;
	}

	public void setCurrentTime(Instant currentTime) {
		this.currentTime = currentTime;
	}

	public ChargerStatus getConnectionStatus() {
		return connectionStatus;
	}

	public void setConnectionStatus(ChargerStatus connectionStatus) {
		this.connectionStatus = connectionStatus;
	}

	public Integer getTemperature() {
		return temperature;
	}

	public void setTemperature(Integer temperature) {
		this.temperature = temperature;
	}

	public Integer getMaxAmpere() {
		return maxAmpere;
	}

	public void setMaxAmpere(Integer maxAmpere) {
		this.maxAmpere = maxAmpere;
	}

	public Integer getLoadedMkWhTotal() {
		return loadedMkWhTotal;
	}

	public void setLoadedMkWhTotal(Integer loadedMkWhTotal) {
		this.loadedMkWhTotal = loadedMkWhTotal;
	}

	public Boolean getAllowCharging() {
		return allowCharging;
	}

	public void setAllowCharging(Boolean allowCharging) {
		this.allowCharging = allowCharging;
	}

	public Integer getAmpere() {
		return ampere;
	}

	public void setAmpere(Integer ampere) {
		this.ampere = ampere;
	}

	public Integer getColorCharging() {
		return colorCharging;
	}

	public void setColorCharging(Integer colorCharging) {
		this.colorCharging = colorCharging;
	}

	public Integer getColorIdle() {
		return colorIdle;
	}

	public void setColorIdle(Integer colorIdle) {
		this.colorIdle = colorIdle;
	}

	public Boolean getAutoStop() {
		return autoStop;
	}

	public void setAutoStop(Boolean autoStop) {
		this.autoStop = autoStop;
	}

	public Integer getAutoStopMkWh() {
		return autoStopMkWh;
	}

	public void setAutoStopMkWh(Integer autoStopMkWh) {
		this.autoStopMkWh = autoStopMkWh;
	}

	public Integer getLoadedDWh() {
		return loadedDWh;
	}

	public void setLoadedDWh(Integer loadedDWh) {
		this.loadedDWh = loadedDWh;
	}
}