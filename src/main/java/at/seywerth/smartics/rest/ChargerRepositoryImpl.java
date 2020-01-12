package at.seywerth.smartics.rest;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import at.seywerth.smartics.rest.api.ChargerRepository;
import at.seywerth.smartics.rest.mapper.ChargerStatusMapper;
import at.seywerth.smartics.rest.model.ChargerStatusDto;

/**
 * repository interface for charger data.
 * 
 * @author Raphael Seywerth
 *
 */
@Repository
public class ChargerRepositoryImpl implements ChargerRepository {

	private static final Logger LOG = LoggerFactory.getLogger(ChargerRepositoryImpl.class);

	private static final String CHARGER_IP = "http://192.168.1.151";
	private static final String CHARGER_STATUS = "/status";
	private static final String CHARGER_SET = "/mqtt?";
	private static final String CHARGER_QUERY = "payload=";

	@Autowired
	private ObjectMapper mapper;


	@Override
	public ChargerStatusDto getStatusData() {
    	//read JSON like DOM Parser
    	JsonNode rootNode;
		try {
			rootNode = mapper.readTree(new URL(CHARGER_IP + CHARGER_STATUS));
			// map json response
			return ChargerStatusMapper.convertToDto(rootNode);
		} catch (IOException e) {
			// TODO handle connection timeout
			LOG.error("getStatusData Exception on parsing json data {}", e.getMessage());
		}

		return new ChargerStatusDto(); 
	}

	@Override
	public ChargerStatusDto setChargerData(final String ampere, final String color, final Boolean allowCharging) {
    	JsonNode rootNode;
    	try {
    		String params = concatParams(ampere, color, allowCharging);
    		if (params.isEmpty()) {
    			LOG.warn("setChargerData with empty params: {}, returning status!", params);
    			rootNode = mapper.readTree(new URL(CHARGER_IP + CHARGER_STATUS));
    		} else {
    			final String paramsEnc = URLEncoder.encode(params, StandardCharsets.UTF_8.name());
    			LOG.info("setChargerData called with params: {}, encoded: {}", params, paramsEnc);
    			String query = CHARGER_IP + CHARGER_SET + CHARGER_QUERY + paramsEnc;
    			LOG.info("GET: {}", query);
    			final URLConnection connection = new URL(query).openConnection();
    			connection.setRequestProperty("Accept-Charset", StandardCharsets.UTF_8.name());
    			final InputStream response = connection.getInputStream();
    			rootNode = mapper.readTree(response);
    		}
    		// map json response
    		return ChargerStatusMapper.convertToDto(rootNode);
    	} catch (IOException e) {
    		// TODO handle connection timeout
    		LOG.error("setStatusData Exception on parsing json data {}", e.getMessage());
    	}

		return new ChargerStatusDto();
	}

	@Override
	public boolean setAmpere(final String ampere) {
		JsonNode rootNode;
    	try {
    		final String query = CHARGER_IP + CHARGER_SET + CHARGER_QUERY + "amp=" + ampere;
    		LOG.info("GET: {}", query);
    		final InputStream response = new URL(query).openStream();
    		rootNode = mapper.readTree(response);

    		// map json response
    		ChargerStatusDto result = ChargerStatusMapper.convertToDto(rootNode);
    		if (result.getAmpere().compareTo(Integer.valueOf(ampere)) == 0) {
    			return true;
    		}
    	} catch (IOException e) {
    		// TODO handle connection timeout
    		LOG.error("setStatusData Exception on parsing json data {}", e.getMessage());
    	}
		return false;
	}

	@Override
	public boolean setColorCharging(final String color) {
		JsonNode rootNode;
    	try {
    		final String query = CHARGER_IP + CHARGER_SET + CHARGER_QUERY + "cch=" + color;
    		LOG.info("GET: {}", query);
    		final InputStream response = new URL(query).openStream();
    		rootNode = mapper.readTree(response);

    		// map json response
    		ChargerStatusDto result = ChargerStatusMapper.convertToDto(rootNode);
    		if (result.getColorCharging().compareTo(Integer.valueOf(color)) == 0) {
    			return true;
    		}
    	} catch (IOException e) {
    		// TODO handle connection timeout
    		LOG.error("setStatusData Exception on parsing json data {}", e.getMessage());
    	}
		return false;
	}

	@Override
	public boolean setAllowCharging(Boolean allowCharging) {
		JsonNode rootNode;
    	try {
    		final String query = CHARGER_IP + CHARGER_SET + CHARGER_QUERY + "alw=" + (allowCharging.booleanValue() ? "1" : "0");
    		LOG.info("GET: {}", query);
    		final InputStream response = new URL(query).openStream();
    		rootNode = mapper.readTree(response);

    		// map json response
    		ChargerStatusDto result = ChargerStatusMapper.convertToDto(rootNode);
    		if (result.getAllowCharging().compareTo(allowCharging) == 0) {
    			return true;
    		}
    	} catch (IOException e) {
    		// TODO handle connection timeout
    		LOG.error("setStatusData Exception on parsing json data {}", e.getMessage());
    	}
		return false;
	}

	private String concatParams(final String ampere, final String colorCharging, final Boolean allowCharging) {
		String payload = "";
		if (ampere != null && !ampere.isEmpty()) {
			payload = concatPayload(payload, "amp=" + ampere);
		}
		if (colorCharging != null && !colorCharging.isEmpty()) {
			payload = concatPayload(payload, "cch=" + colorCharging);
		}
		if (allowCharging != null) {
			payload = concatPayload(payload, "alw=" + (allowCharging.booleanValue() ? "1" : "0"));
		}
		return payload;
	}

	private String concatPayload(String payload, String param) {
		String result = "";
		if (!payload.isEmpty()) {
			result = payload.concat(",");
		}
		return result.concat(param);
	}

}