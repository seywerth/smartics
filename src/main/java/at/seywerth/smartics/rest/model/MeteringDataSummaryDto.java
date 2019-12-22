package at.seywerth.smartics.rest.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO for inverter summary data by rest.
 * 
 * @author Raphael Seywerth
 *
 */
public class MeteringDataSummaryDto {

	private Instant fromTime;
	private Instant untilTime;
	private InverterStatus status;

	private BigDecimal powerProduced;
	private BigDecimal powerConsumed;
	private BigDecimal powerFeedback;

	private BigDecimal powerFromNetwork;
	private BigDecimal powerFromProduction;

	private BigDecimal autonomy;
	private BigDecimal cost;
	private BigDecimal income;

	private List<MeteringDataMinDto> meteringDataMinDtos;

	public MeteringDataSummaryDto() {
		this.powerProduced = BigDecimal.ZERO;
		this.powerConsumed = BigDecimal.ZERO;
		this.powerFeedback = BigDecimal.ZERO;
	}

	public MeteringDataSummaryDto(Instant fromTime,
								  Instant untilTime,
								  BigDecimal powerProduced,
								  BigDecimal powerConsumed,
								  BigDecimal powerFeedback,
								  InverterStatus status) {
		this.fromTime = fromTime;
		this.untilTime = untilTime;
		this.status = status;
		this.powerProduced = powerProduced;
		this.powerConsumed = powerConsumed;
		this.powerFeedback = powerFeedback;
	}

	public Instant getFromTime() {
		return fromTime;
	}

	public void setFromTime(Instant fromTime) {
		this.fromTime = fromTime;
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

	public BigDecimal getPowerFromNetwork() {
		return powerFromNetwork;
	}

	public void setPowerFromNetwork(BigDecimal powerFromNetwork) {
		this.powerFromNetwork = powerFromNetwork;
	}

	public BigDecimal getPowerFromProduction() {
		return powerFromProduction;
	}

	public void setPowerFromProduction(BigDecimal powerFromProduction) {
		this.powerFromProduction = powerFromProduction;
	}

	public BigDecimal getAutonomy() {
		return autonomy;
	}

	public void setAutonomy(BigDecimal autonomy) {
		this.autonomy = autonomy;
	}

	public BigDecimal getCost() {
		return cost;
	}

	public void setCost(BigDecimal cost) {
		this.cost = cost;
	}

	public BigDecimal getIncome() {
		return income;
	}

	public void setIncome(BigDecimal income) {
		this.income = income;
	}

	public List<MeteringDataMinDto> getMeteringDataMinDtos() {
		return meteringDataMinDtos;
	}

	public void addMeteringDataMinDto(MeteringDataMinDto meteringDataMinDto) {
		if (this.meteringDataMinDtos == null) {
			this.meteringDataMinDtos = new ArrayList<>();
		}
		this.meteringDataMinDtos.add(meteringDataMinDto);
	}

	public void setMeteringDataMinDtos(List<MeteringDataMinDto> meteringDataMinDtos) {
		this.meteringDataMinDtos = meteringDataMinDtos;
	}

}