package at.seywerth.smartics.rest.mapper;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;

import at.seywerth.smartics.rest.model.InverterDto;
import at.seywerth.smartics.rest.model.InverterStatus;
import at.seywerth.smartics.rest.model.MeteringDataSec;
import at.seywerth.smartics.util.InverterDateTimeFormater;

/**
 * Mapper for realtime Inverter data.
 * 
 * @author Raphael Seywerth
 *
 */
@Component
public class InverterRealtimeMapper {

	public static MeteringDataSec convertToEntity(InverterDto dto) {
		return new MeteringDataSec(Timestamp.from(dto.getCurrentTime()),
								   dto.getPvCurrent(),
								   calcConsumptionValue(dto),
								   calcFeedbackValue(dto),
								   dto.getStatusCode().getCode());
	}

	private static BigDecimal calcConsumptionValue(InverterDto dto) {
		// calculate how much energy is consumed in total
		BigDecimal pv = dto.getPvCurrent() != null ? dto.getPvCurrent() : new BigDecimal("0");
		BigDecimal grid = dto.getLoadGrid();

		return pv.add(grid);
	}

	private static BigDecimal calcFeedbackValue(InverterDto dto) {
		// calculate how much energy is fed back into the grid
		if (dto.getLoadGrid() != null && dto.getLoadGrid().signum() < 0) {
			return dto.getLoadGrid().abs();
		}
		return new BigDecimal("0");
	}

	/**
	 * converts json to inverter DTO data
	 * 
	 * @param rootNode
	 * @return {@link InverterDto}
	 */
	public static InverterDto convertToDto(JsonNode rootNode) {
		// map json
    	JsonNode site = rootNode.findPath("Site");
    	// implemented since Fronius Non Hybrid version 3.4.1-7
    	// Energy [Wh] this day, null if no inverter is connected
    	BigDecimal pvDay = getBigDecimal(site.get("E_Day").asText());

    	// this value is null if inverter is not running ( + production ( default ) )
    	BigDecimal pvCurrent = getBigDecimal(site.get("P_PV").asText());
    	// this value is null if no meter is enabled ( + generator , - consumer )
    	BigDecimal loadCurrent = getBigDecimal(site.get("P_Load").asText());
    	// this value is null if no battery is active ( + charge , - discharge )
    	BigDecimal loadAkku= getBigDecimal(site.get("P_Akku").asText());
    	// this value is null if no meter is enabled ( + from grid, - to grid )
    	BigDecimal loadGrid = getBigDecimal(site.get("P_Grid").asText());

    	// RFC3339 format with time zone offset "2017-02-14T07:08:45+01:00"
    	Instant currentTime = getInstant(rootNode.findPath("Timestamp").asText());
    	// Status code reflecting the operational state of the inverter
    	// 0 - 6 Startup, 7 Running, 8 Standby,	9 Bootloading, 10 Error
    	Long statusCode = rootNode.findPath("Status").get("Code").asLong();

    	return new InverterDto(pvDay, pvCurrent, loadCurrent, loadAkku, loadGrid, currentTime, InverterStatus.getByCode(statusCode));
	}

	private static Instant getInstant(String text) {
		if (text.equals("null")) {
			return null;
		}
		// parse iso time "yyyy-mm-ddTHH:mm:ss"
		return InverterDateTimeFormater.getInstantForISOOffsetDateTime(text);
	}

	private static BigDecimal getBigDecimal(String text) {
		if (text.equals("null")) {
			return null;
		}
		return new BigDecimal(text);
	}

}