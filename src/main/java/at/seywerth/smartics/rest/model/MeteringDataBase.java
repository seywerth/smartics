package at.seywerth.smartics.rest.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

/**
 * base entity for accessing metering data from the database.
 * - powerProduced in Wh
 * - powerConsumed in Wh
 * - powerFeedback in Wh
 * 
 * @author Raphael Seywerth
 *
 */
@MappedSuperclass
public abstract class MeteringDataBase implements MeteringData, Serializable {

	private static final long serialVersionUID = 1L;

	private Timestamp updateTime;

	private BigDecimal powerProduced;
	private BigDecimal powerConsumed;
	private BigDecimal powerFeedback;

	private Long statusCode;

	protected MeteringDataBase() {
	}

	public MeteringDataBase(final BigDecimal powerProduced,
						    final BigDecimal powerConsumed,
						    final BigDecimal powerFeedback,
						    final Long statusCode) {
		this.powerProduced = powerProduced;
		this.powerConsumed = powerConsumed;
		this.powerFeedback = powerFeedback;
		this.statusCode = statusCode;
	}

	@PrePersist
	@PreUpdate
	private void preUpdate() {
		this.updateTime = new Timestamp(System.currentTimeMillis());
	}

	public Timestamp getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Timestamp updateTime) {
		this.updateTime = updateTime;
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

	public Long getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(Long statusCode) {
		this.statusCode = statusCode;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((powerProduced == null) ? 0 : powerProduced.hashCode());
		result = prime * result + ((powerConsumed == null) ? 0 : powerConsumed.hashCode());
		result = prime * result + ((powerFeedback == null) ? 0 : powerFeedback.hashCode());
		result = prime * result + ((statusCode == null) ? 0 : statusCode.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null || getClass() != obj.getClass()) return false;
		MeteringDataBase other = (MeteringDataBase) obj;
		if (powerProduced == null) {
			if (other.powerProduced != null) return false;
		} else {
			if (!powerProduced.equals(other.powerProduced)) return false;
		}
		if (powerConsumed == null) {
			if (other.powerConsumed != null) return false;
		} else {
			if (!powerConsumed.equals(other.powerConsumed)) return false;
		}
		if (powerFeedback == null) {
			if (other.powerFeedback != null) return false;
		} else {
			if (!powerFeedback.equals(other.powerFeedback)) return false;
		}
		if (statusCode == null) {
			if (other.statusCode != null) return false;
		} else {
			if (!statusCode.equals(other.statusCode)) return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "MeteringData [powerProduced=" + powerProduced + ", powerConsumed="
				+ powerConsumed + ", powerFeedback=" + powerFeedback + ", statusCode=" + statusCode + "]";
	}

}