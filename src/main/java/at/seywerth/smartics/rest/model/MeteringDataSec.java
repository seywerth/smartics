package at.seywerth.smartics.rest.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * entity for accessing most exact metering data (lots of values, by sec) from the database.
 * - powerProduced in Wh
 * - powerConsumed in Wh
 * - powerFeedback in Wh
 * 
 * @author Raphael Seywerth
 *
 */
@Entity
@Table(name = "meteringDataSec")
public class MeteringDataSec extends MeteringDataBase {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(nullable = false, unique = true)
	private Timestamp creationTime;

	@SuppressWarnings("unused")
	private MeteringDataSec() {
		super();
	}

	public MeteringDataSec(final Timestamp creationTime,
						   final BigDecimal powerProduced,
						   final BigDecimal powerConsumed,
						   final BigDecimal powerFeedback,
						   final Long statusCode) {
		super(powerProduced, powerConsumed, powerFeedback, statusCode);
		this.creationTime = creationTime;
	}

	public Timestamp getCreationTime() {
		return creationTime;
	}

	public void setCreationTime(Timestamp creationTime) {
		this.creationTime = creationTime;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((creationTime == null) ? 0 : creationTime.hashCode());
		result = prime * result + super.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null || getClass() != obj.getClass()) return false;
		MeteringDataSec other = (MeteringDataSec) obj;
		if (creationTime == null) {
			if (other.creationTime != null)	return false;
		} else {
			if (!creationTime.equals(other.creationTime)) return false;
		}
		return super.equals(obj);
	}

	@Override
	public String toString() {
		return "MeteringDataSec [creationTime=" + creationTime + ", " + super.toString() + "]";
	}

}