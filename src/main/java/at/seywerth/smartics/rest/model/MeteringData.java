package at.seywerth.smartics.rest.model;

import java.math.BigDecimal;

/**
 * interface for base entity for accessing metering data.
 * - powerProduced in Wh
 * - powerConsumed in Wh
 * - powerFeedback in Wh
 * 
 * @author Raphael Seywerth
 *
 */
public interface MeteringData {

	public BigDecimal getPowerProduced();

	public void setPowerProduced(BigDecimal powerProduced);

	public BigDecimal getPowerConsumed();

	public void setPowerConsumed(BigDecimal powerConsumed);

	public BigDecimal getPowerFeedback();

	public void setPowerFeedback(BigDecimal powerFeedback);

	public Long getStatusCode();

	public void setStatusCode(Long statusCode);

}