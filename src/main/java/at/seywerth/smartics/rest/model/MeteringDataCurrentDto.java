package at.seywerth.smartics.rest.model;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * DTO for inverter current data by rest.
 * 
 * @author Raphael Seywerth
 *
 */
public class MeteringDataCurrentDto {

   private Instant creationTime;
   private InverterStatus status;

   private BigDecimal powerProduced;
   private BigDecimal powerConsumed;
   private BigDecimal powerFeedback;

   private BigDecimal powerFromNetwork;
   private BigDecimal powerFromProduction;

   public MeteringDataCurrentDto() {
      this.powerProduced = BigDecimal.ZERO;
      this.powerConsumed = BigDecimal.ZERO;
      this.powerFeedback = BigDecimal.ZERO;
   }

   public MeteringDataCurrentDto(Instant creationTime,
                                 BigDecimal powerProduced,
                                 BigDecimal powerConsumed,
                                 BigDecimal powerFeedback,
                                 InverterStatus status) {
      this.creationTime = creationTime;
      this.status = status;
      this.powerProduced = powerProduced;
      this.powerConsumed = powerConsumed;
      this.powerFeedback = powerFeedback;
   }

   public Instant getCreationTime() {
      return creationTime;
   }

   public void setCreationTime(Instant creationTime) {
      this.creationTime = creationTime;
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

}