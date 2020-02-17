package at.seywerth.smartics.rest.model;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * entity for accessing charging data from the database. - has startTime and
 * untilTime - ampere, volt and additional charger data
 * 
 * @author Raphael Seywerth
 *
 */
@Entity
@Table(name = "chargingData")
public class ChargingData implements Serializable {

   private static final long serialVersionUID = 1L;

   @Id
   @Column(nullable = false, unique = true)
   private Timestamp startTime;
   private Timestamp untilTime;

   private Integer ampere;
   private Integer volt;

   private String connectionStatus;
   private String chargerMode;

   private Integer temperature;
   private Integer totalMkwh;

   @SuppressWarnings("unused")
   private ChargingData() {
      super();
   }

   public ChargingData(final Timestamp startTime,
                       final Timestamp untilTime,
                       final Integer ampere,
                       final Integer volt,
                       final String connectionStatus,
                       final String chargerMode,
                       final Integer temperature,
                       final Integer totalMkwh) {
      this.startTime = startTime;
      this.untilTime = untilTime;
      this.ampere = ampere;
      this.volt = volt;
      this.connectionStatus = connectionStatus;
      this.chargerMode = chargerMode;
      this.temperature = temperature;
      this.totalMkwh = totalMkwh;
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

   public Integer getAmpere() {
      return ampere;
   }

   public void setAmpere(Integer ampere) {
      this.ampere = ampere;
   }

   public Integer getVolt() {
      return volt;
   }

   public void setVolt(Integer volt) {
      this.volt = volt;
   }

   public String getConnectionStatus() {
      return connectionStatus;
   }

   public void setConnectionStatus(String connectionStatus) {
      this.connectionStatus = connectionStatus;
   }

   public String getChargerMode() {
      return chargerMode;
   }

   public void setChargerMode(String chargerMode) {
      this.chargerMode = chargerMode;
   }

   public Integer getTemperature() {
      return temperature;
   }

   public void setTemperature(Integer temperature) {
      this.temperature = temperature;
   }

   public Integer getTotalMkwh() {
      return totalMkwh;
   }

   public void setTotalMkwh(Integer totalMkwh) {
      this.totalMkwh = totalMkwh;
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
      if (this == obj)
         return true;
      if (obj == null || getClass() != obj.getClass())
         return false;
      ChargingData other = (ChargingData) obj;
      if (startTime == null) {
         if (other.startTime != null)
            return false;
      } else {
         if (!startTime.equals(other.startTime))
            return false;
      }
      if (untilTime == null) {
         if (other.untilTime != null)
            return false;
      } else {
         if (!untilTime.equals(other.untilTime))
            return false;
      }
      return super.equals(obj);
   }

   @Override
   public String toString() {
      return "Charger [startTime=" + startTime + ", untilTime=" + untilTime + ", " + super.toString() + "]";
   }

}