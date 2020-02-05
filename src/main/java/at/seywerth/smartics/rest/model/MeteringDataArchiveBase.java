package at.seywerth.smartics.rest.model;

import java.math.BigDecimal;

import javax.persistence.MappedSuperclass;

/**
 * base entity for accessing metering data from the database.
 * - archiveProduced in Wh
 * - archiveConsumed in Wh
 * - archiveFeedback in Wh
 * 
 * @author Raphael Seywerth
 *
 */
@MappedSuperclass
public abstract class MeteringDataArchiveBase extends MeteringDataBase {

	private static final long serialVersionUID = 1L;

	private BigDecimal archiveProduced;
	private BigDecimal archiveConsumed;
	private BigDecimal archiveFeedback;

	protected MeteringDataArchiveBase() {
	}

	public MeteringDataArchiveBase(final BigDecimal powerProduced,
								   final BigDecimal powerConsumed,
								   final BigDecimal powerFeedback,
								   final Long statusCode) {
		super(powerProduced, powerConsumed, powerFeedback, statusCode);
	}

	public MeteringDataArchiveBase(final BigDecimal archiveProduced,
			   					   final BigDecimal archiveConsumed,
			   					   final BigDecimal archiveFeedback) {
		this.archiveProduced = archiveProduced;
		this.archiveConsumed = archiveConsumed;
		this.archiveFeedback = archiveFeedback;
	}

	/**
	 * absolute produced energy.
	 * @return Wh
	 */
	public BigDecimal getArchiveProduced() {
		return archiveProduced;
	}

	public void setArchiveProduced(BigDecimal archiveProduced) {
		this.archiveProduced = archiveProduced;
	}

	/**
	 * absolute consumed energy from grid.
	 * @return Wh
	 */
	public BigDecimal getArchiveConsumed() {
		return archiveConsumed;
	}

	public void setArchiveConsumed(BigDecimal archiveConsumed) {
		this.archiveConsumed = archiveConsumed;
	}

	/**
	 * absolute fed back data including locally consumed energy.
	 * @return Wh
	 */
	public BigDecimal getArchiveFeedback() {
		return archiveFeedback;
	}

	public void setArchiveFeedback(BigDecimal archiveFeedback) {
		this.archiveFeedback = archiveFeedback;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((archiveProduced == null) ? 0 : archiveProduced.hashCode());
		result = prime * result + ((archiveConsumed == null) ? 0 : archiveConsumed.hashCode());
		result = prime * result + ((archiveFeedback == null) ? 0 : archiveFeedback.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null || getClass() != obj.getClass()) return false;
		MeteringDataArchiveBase other = (MeteringDataArchiveBase) obj;
		if (archiveProduced == null) {
			if (other.archiveProduced != null) return false;
		} else {
			if (!archiveProduced.equals(other.archiveProduced)) return false;
		}
		if (archiveConsumed == null) {
			if (other.archiveConsumed != null) return false;
		} else {
			if (!archiveConsumed.equals(other.archiveConsumed)) return false;
		}
		if (archiveFeedback == null) {
			if (other.archiveFeedback != null) return false;
		} else {
			if (!archiveFeedback.equals(other.archiveFeedback)) return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "MeteringData [archiveProduced=" + archiveProduced + ", archiveConsumed="
				+ archiveConsumed + ", archiveFeedback=" + archiveFeedback + ", " + super.toString() + "]";
	}

}