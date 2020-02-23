package at.seywerth.smartics.rest.mapper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.ParseException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;

import at.seywerth.smartics.rest.model.InverterDto;
import at.seywerth.smartics.rest.model.InverterStatus;
import at.seywerth.smartics.rest.model.MeteringDataDay;
import at.seywerth.smartics.rest.model.MeteringDataMin;
import at.seywerth.smartics.rest.model.MeteringDataMinDto;
import at.seywerth.smartics.rest.model.PowerProducedDto;
import at.seywerth.smartics.util.InverterCalculatorUtil;
import at.seywerth.smartics.util.InverterDateTimeFormater;

/**
 * Mapper for archive Inverter data.
 * the following values can be gotten:
 *  - EnergyReal_WAC_Sum_Produced   Wh produced by PV
 *  - EnergyReal_WAC_Minus_Absolute Wh that got into the grid
 *  - EnergyReal_WAC_Plus_Absolute  Wh that were consumed from the grid
 * 
 * @author Raphael Seywerth
 *
 */
@Component
public class InverterArchiveMapper {

   private static final Logger LOG = LoggerFactory.getLogger(InverterArchiveMapper.class);

   public static final String INVERTER_CHANNEL_PROD = "EnergyReal_WAC_Sum_Produced";
   public static final String INVERTER_CHANNEL_CONS = "EnergyReal_WAC_Plus_Absolute";
   public static final String INVERTER_CHANNEL_FEED = "EnergyReal_WAC_Minus_Absolute";

   public static MeteringDataMin convertToEntity(InverterDto dto) {
      MeteringDataMin entity =
            new MeteringDataMin(Timestamp.from(dto.getCurrentTime()), Timestamp.from(dto.getCurrentTime()),
                  dto.getPvCurrent(), dto.getLoadCurrent(), dto.getLoadGrid(), dto.getStatusCode().getCode());
      return entity;
   }

   /**
    * converts a {@link MeteringDataMin} into {@link MeteringDataMinDto}. uses
    * alternative archive data if values are null.
    */
   public static MeteringDataMinDto convertToDto(MeteringDataMin entity) {
      BigDecimal powerConsumed = entity.getPowerConsumed();
      BigDecimal powerProduced = entity.getPowerProduced();
      BigDecimal powerFeedback = entity.getPowerFeedback();

      // override data with archive or zero to prevent calc error
      // TODO: also check for entity.getStatusCode()?
      // combine with other convertToDto-method
      if (powerProduced == null || powerProduced.compareTo(BigDecimal.ZERO) == 0) {
         powerProduced = entity.getArchiveProduced() != null ? entity.getArchiveProduced() : BigDecimal.ZERO;
      }
      if (powerFeedback == null || powerFeedback.compareTo(BigDecimal.ZERO) == 0) {
         powerFeedback = entity.getArchiveFeedback() != null ? entity.getArchiveFeedback() : BigDecimal.ZERO;
      }
      if (powerConsumed == null || powerConsumed.compareTo(BigDecimal.ZERO) == 0) {
         if (entity.getArchiveConsumed() != null) {
            // value differs for absolute archive data: consumption = consumed + produced - feedback
            powerConsumed = entity.getArchiveConsumed().add(powerProduced).subtract(powerFeedback);
         } else {
            powerConsumed = BigDecimal.ZERO;
         }
      }

      MeteringDataMinDto dto = new MeteringDataMinDto(
            entity.getStartTime().toInstant(),
            entity.getUntilTime().toInstant(),
            powerProduced,
            powerConsumed,
            powerFeedback,
            InverterStatus.getByCode(entity.getStatusCode()));
      // additional calculations
      dto.setAutonomy(InverterCalculatorUtil.calcAutonomy(
            InverterCalculatorUtil.calcPowerFromProduction(powerProduced, powerFeedback), powerConsumed));

      return dto;
   }

   /**
    * converts a {@link MeteringDataDay} into {@link MeteringDataMinDto}. uses
    * alternative archive data if values are null.
    */
   public static MeteringDataMinDto convertToDto(MeteringDataDay entity) {
      BigDecimal powerConsumed = entity.getPowerConsumed();
      BigDecimal powerProduced = entity.getPowerProduced();
      BigDecimal powerFeedback = entity.getPowerFeedback();
      InverterStatus statusCode = InverterStatus.getByCode(entity.getStatusCode());

      // override data with archive or zero to prevent calc error // also check for entity.getStatusCode() ?
      if (powerProduced == null || powerProduced.compareTo(BigDecimal.ZERO) == 0) {
         powerProduced = entity.getArchiveProduced() != null ? entity.getArchiveProduced() : BigDecimal.ZERO;
      }
      if (powerFeedback == null || powerFeedback.compareTo(BigDecimal.ZERO) == 0) {
         powerFeedback = entity.getArchiveFeedback() != null ? entity.getArchiveFeedback() : BigDecimal.ZERO;
      }
      if (powerConsumed == null || powerConsumed.compareTo(BigDecimal.ZERO) == 0
            || InverterStatus.NOT_ENOUGH_DATA == statusCode) {
         if (entity.getArchiveConsumed() != null) {
            // value differs for absolute archive data: consumption = consumed + produced - feedback
            powerConsumed = entity.getArchiveConsumed().add(powerProduced).subtract(powerFeedback);
         } else {
            powerConsumed = BigDecimal.ZERO;
         }
      }

      MeteringDataMinDto dto = new MeteringDataMinDto(
            entity.getStartTime().toInstant(),
            entity.getUntilTime().toInstant(),
            powerProduced,
            powerConsumed,
            powerFeedback,
            InverterStatus.getByCode(entity.getStatusCode()));
      // additional calculations
      dto.setAutonomy(InverterCalculatorUtil.calcAutonomy(
            InverterCalculatorUtil.calcPowerFromProduction(powerProduced, powerFeedback), powerConsumed));

      return dto;
   }

   public static List<PowerProducedDto> convertToDto(JsonNode rootNode, String day, String channel) {
      ArrayList<PowerProducedDto> list = new ArrayList<>();
      // map json
      JsonNode data = rootNode.findPath(channel);

      Instant curtime;
      try {
         curtime = InverterDateTimeFormater.getInstantForSDF(day);
      } catch (ParseException e) {
         LOG.error("convertToDto parse day format exception {}", e.getMessage());
         return list;
      }
      // loop through data
      data.get("Values").fields().forEachRemaining(element -> {
         Instant test = curtime.plusSeconds(getLong(element.getKey()));
         list.add(new PowerProducedDto(getBigDecimal(element.getValue().asText()), test));
      });
      // order by time
      List<PowerProducedDto> sortedList = list.stream().sorted(Comparator.comparing(PowerProducedDto::getCurrentTime))
            .collect(Collectors.toList());
      // if data is absolute, postprocess
      if (INVERTER_CHANNEL_CONS.equals(channel) || INVERTER_CHANNEL_FEED.equals(channel)) {
         List<PowerProducedDto> resultList = new ArrayList<>();
         // for the first one data would be missing otherwise
         resultList.add(new PowerProducedDto(BigDecimal.ZERO, sortedList.get(0).getCurrentTime()));

         for (int index = 1; index < sortedList.size(); index++) {
            // calculate from sorted list as result list is changing
            BigDecimal power = getDifference(sortedList.get(index).getPvCurrent(),
                  sortedList.get(index - 1).getPvCurrent());
            resultList.add(new PowerProducedDto(power, sortedList.get(index).getCurrentTime()));
         }
         return resultList;
      }

      return sortedList;
   }

   private static Long getLong(String text) {
      if (text.equals("null")) {
         return null;
      }
      return Long.valueOf(text);
   }

   private static BigDecimal getBigDecimal(String text) {
      if (text.equals("null")) {
         return null;
      }
      // produced data would have lots of decimal places
      return new BigDecimal(text).setScale(2, RoundingMode.HALF_UP);
   }

   private static BigDecimal getDifference(BigDecimal powerCurrent, BigDecimal powerBefore) {
      BigDecimal before = (powerBefore == null) ? BigDecimal.ZERO : powerBefore;
      BigDecimal current = (powerCurrent == null) ? BigDecimal.ZERO : powerCurrent;
      if (before.compareTo(BigDecimal.ZERO) == 0 && current.compareTo(BigDecimal.ZERO) == 0) {
         return BigDecimal.ZERO;
      }
      if (current.compareTo(before) < 0) {
         LOG.error("InverterArchiveMapper current power {} is less than value before that {}!", current, before);
      }
      return current.subtract(before);
   }
}