package at.seywerth.smartics.rest.mapper;

import java.io.IOException;
import java.text.ParseException;
import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;

import at.seywerth.smartics.rest.model.ChargerStatus;
import at.seywerth.smartics.rest.model.ChargerStatusDto;
import at.seywerth.smartics.util.InverterDateTimeFormater;

/**
 * Mapper for charger status data.
 * 
 * @author Raphael Seywerth
 *
 */
@Component
public class ChargerStatusMapper {
	private static final Logger LOG = LoggerFactory.getLogger(ChargerStatusMapper.class);

	/**
	 * converts json to charger DTO data
	 * 
	 * @param rootNode
	 * @return {@link ChargerStatusDto}
	 * @throws IOException 
	 */
	public static ChargerStatusDto convertToDto(JsonNode rootNode) throws IOException {
	   if (rootNode == null) {
	      // TODO use custom exception ChargerReadError?
	      throw new IOException("ChargerStatusMapper: rootNode was null!");
	   }
		// map json
		Instant currentTime = getInstant(rootNode.get("tme").asText());
		Integer connectionStatus = getInteger(rootNode.get("car").asText());
		Integer temperature = getInteger(rootNode.get("tmp").asText());
		Integer maxAmpere = getInteger(rootNode.get("ama").asText());
		Integer loadedMkWhTotal = getInteger(rootNode.get("eto").asText());
		ChargerStatusDto status = new ChargerStatusDto(currentTime, ChargerStatus.getByCode(connectionStatus),
				temperature, maxAmpere, loadedMkWhTotal);

		status.setAllowCharging(getBoolean(rootNode.get("alw").asText()));
		status.setAmpere(getInteger(rootNode.get("amp").asText()));
		status.setColorCharging(getInteger(rootNode.get("cch").asText()));
		status.setColorIdle(getInteger(rootNode.get("cfi").asText()));

		status.setAutoStop(getBoolean(rootNode.get("stp").asText()));
		status.setAutoStopMkWh(getInteger(rootNode.get("dwo").asText()));
		status.setLoadedDWh(getInteger(rootNode.get("dws").asText()));

    	return status;
	}

	private static Instant getInstant(String text) {
		if (text.equals("")) {
			return null;
		}
		try {
			// parse time "ddmmyyhhmm"
			return InverterDateTimeFormater.getInstantForDMYHMDateTime(text);
		} catch (ParseException e) {
			LOG.error("ChargerStatusMapper dateTime could not be parsed: {}", text);
			return null;
		}
	}

	private static Integer getInteger(String text) {
		if (text.equals("")) {
			return null;
		}
		return new Integer(text);
	}

	private static Boolean getBoolean(String text) {
		if (text.equals("")) {
			// since null for Boolean is kind of a code smell
			return false;
		}
		// 0 = false, everything else = true
		return !text.contentEquals("0");
	}
}