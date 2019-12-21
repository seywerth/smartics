package at.seywerth.smartics.rest.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * entity for accessing metering data (daily, a value each day) from the database.
 * 
 * @author Raphael Seywerth
 *
 */
@Entity
@Table(name = "meteringDataDay")
public class MeteringDataDay extends MeteringDataArchiveBase {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(nullable = false, unique = true)
	private Timestamp startTime;
	private Timestamp untilTime;

	@SuppressWarnings("unused")
	private MeteringDataDay() {
		super();
	}

	public MeteringDataDay(final Timestamp startTime,
			   			   final Timestamp untilTime,
			   			   final BigDecimal powerProduced,
			   			   final BigDecimal powerConsumed,
			   			   final BigDecimal powerFeedback,
			   			   final Long statusCode) {
		super(powerProduced, powerConsumed, powerFeedback, statusCode);
		this.startTime = startTime;
		this.untilTime = untilTime;
	}

	public Timestamp getStartTime() {
		return startTime;
	}

	public void setStartTime(Timestamp startTime) {
		this.startTime = startTime;
	}

	public Timestamp getUntilTime() {
		return untilTime;
	}

	public void setUntilTime(Timestamp untilTime) {
		this.untilTime = untilTime;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((startTime == null) ? 0 : startTime.hashCode());
		result = prime * result + ((untilTime == null) ? 0 : untilTime.hashCode());
		result = prime * result + super.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null || getClass() != obj.getClass()) return false;
		MeteringDataDay other = (MeteringDataDay) obj;
		if (startTime == null) {
			if (other.startTime != null)	return false;
		} else {
			if (!startTime.equals(other.startTime)) return false;
		}
		if (untilTime == null) {
			if (other.untilTime != null)	return false;
		} else {
			if (!untilTime.equals(other.untilTime)) return false;
		}
		return super.equals(obj);
	}

	@Override
	public String toString() {
		return "Inverter [startTime=" + startTime + ", untilTime + " + untilTime + ", " + super.toString() + "]";
	}

}