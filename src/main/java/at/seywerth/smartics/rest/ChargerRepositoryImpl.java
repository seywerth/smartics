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
import at.seywerth.smartics.rest.model.ChargerMode;
import at.seywerth.smartics.rest.model.ChargerStatus;
import at.seywerth.smartics.rest.model.ChargerStatusDto;
import at.seywerth.smartics.rest.model.MeteringDataMin;
import at.seywerth.smartics.rest.model.Setting;
import at.seywerth.smartics.rest.model.SettingName;
import at.seywerth.smartics.util.InverterCalculatorUtil;

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

	public static final int AMPERE_MIN = 6;
	public static final int COLOR_YELLOW = 16776960;
	public static final int COLOR_MAGENTA = 16711935;
	public static final int COLOR_BLUE = 65535;

	@Autowired
	private ObjectMapper mapper;
	@Autowired
	private SettingService settingService;


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
			LOG.error("setAmpere change to {} A on charger failed!", ampere);
    	} catch (IOException e) {
    		// TODO handle connection timeout
    		LOG.error("setAmpere Exception on parsing json data {}", e.getMessage());
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
    		LOG.error("setColorCharging change to color {} on charger failed!", color);
    	} catch (IOException e) {
    		// TODO handle connection timeout
    		LOG.error("setColorCharging Exception on parsing json data {}", e.getMessage());
    	}
		return false;
	}

	@Override
	public boolean setAllowCharging(final Boolean allowCharging) {
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
    		LOG.error("setAllowCharging setting activation on charger to {} failed!", allowCharging);
    	} catch (IOException e) {
    		// TODO handle connection timeout
    		LOG.error("setAllowCharging Exception on parsing json data {}", e.getMessage());
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

	@Override
	public void analyzeChargerStatus(Setting settingChargerMode, final MeteringDataMin meteringData) {
		ChargerMode chargerMode = ChargerMode.getByCode(settingChargerMode.getValue());
		// check availability
		final ChargerStatusDto chargerStatus = getStatusData();
		if (ChargerMode.UNAVAILABLE == chargerMode) {
			LOG.info("analyzeChargerStatus: last charger state was not available, rechecking..");
			if (chargerStatus == null) {
				LOG.info("analyzeChargerStatus: charger still unavailable!");
				return;
			} else {
				chargerMode = ChargerMode.DEACTIVATED;
			}
		}
		Setting settingChargerAmp = settingService.findByName(SettingName.CHARGER_AMPERE_CURRENT);

		// read on true..
		if (ChargerMode.SMART == chargerMode) {
			// calculate ampere and color for charging
			final Integer currentAmpere = calculateSmartCharging(chargerStatus, meteringData);
			if (currentAmpere != null) {
				settingChargerAmp.setValue(String.valueOf(currentAmpere));
				settingService.save(settingChargerAmp);
			}
		} else {
			// update charger state from charger
			if (chargerStatus == null) {
				settingChargerMode.setValue(ChargerMode.UNAVAILABLE.name());
			} else if (!chargerStatus.getAllowCharging().booleanValue()) {
				settingChargerMode.setValue(ChargerMode.DEACTIVATED.name());
			} else if (chargerStatus.getAllowCharging().booleanValue()) {
				settingChargerMode.setValue(ChargerMode.FIXED.name());
				settingChargerAmp.setValue(chargerStatus.getAmpere().toString());
				settingService.save(settingChargerAmp);
			}
			LOG.info("analyzeChargerStatus: no smart charging, charger status is {}", settingChargerMode.getValue());
			settingService.save(settingChargerMode);
		}
	}

	/**
	 * calculate and set smart charging data.
	 * 
	 * @param chargerStatus
	 * @param meteringData
	 * @return current ampere number
	 */
	protected Integer calculateSmartCharging(final ChargerStatusDto chargerStatus, final MeteringDataMin meteringData) {
		double excessEnergyWh = meteringData.getPowerFeedback().doubleValue() * 12;
		double extendEnergyWh = InverterCalculatorUtil.calcPowerFromNetwork(meteringData.getPowerConsumed(),
				meteringData.getPowerProduced(), meteringData.getPowerFeedback()) * 12;
		if (excessEnergyWh <= 0 && ChargerStatus.LOADING != chargerStatus.getConnectionStatus()) {
			LOG.info("analyzeChargerStatus: no excess energy available for smart charging!");
			return null;
		}
		// devide by Volt
		Setting settingChargerVolt = settingService.findByName(SettingName.CHARGER_VOLTAGE);
		if (settingChargerVolt == null || settingChargerVolt.getValue().isEmpty()) {
			LOG.error("analyzeChargerStatus: voltage has not been specified for charger, no smart charging!");
			return null;
		}
		double excessAmpere = excessEnergyWh / Integer.parseInt(settingChargerVolt.getValue());
		double extendAmpere = extendEnergyWh / Integer.parseInt(settingChargerVolt.getValue());
		LOG.info("analyzeChargerStatus: smart excess energy {} Wh, {} A", excessEnergyWh, excessAmpere);
		LOG.info("analyzeChargerStatus: smart extend energy {} Wh, {} A", extendEnergyWh, extendAmpere);
		// set color, activation.. if at least 2A excess
		if (excessAmpere > 2) {
			// todo decide on rounding..
			int ampereToSet = (int) excessAmpere;
			if (chargerStatus.getMaxAmpere() <= excessAmpere) {
				ampereToSet = chargerStatus.getMaxAmpere();
			}
			if (ChargerRepositoryImpl.AMPERE_MIN > ampereToSet) {
				ampereToSet = ChargerRepositoryImpl.AMPERE_MIN;
			}
			LOG.info("analyzeChargerStatus: smart usage of {} A", ampereToSet);
			// set color
			int colorCharging = ampereToSet <= excessAmpere ? ChargerRepositoryImpl.COLOR_YELLOW : ChargerRepositoryImpl.COLOR_MAGENTA;
			setAmpere(String.valueOf(ampereToSet));
			setColorCharging(String.valueOf(colorCharging));
			setAllowCharging(true);
			return ampereToSet;
		} else if (extendAmpere > 0) {
			// stop charging if less available
			// reduce charging
			if (chargerStatus.getAmpere() > 0 && ChargerStatus.LOADING == chargerStatus.getConnectionStatus()) {
				int ampereToSet = chargerStatus.getAmpere();
				if (extendAmpere >= ampereToSet || ampereToSet - extendAmpere <= ChargerRepositoryImpl.AMPERE_MIN) {
					// deactivate
					LOG.info("analyzeChargerStatus: smart usage deactivate");
					ampereToSet = 0;
					setAllowCharging(false);
				} else {
					ampereToSet = (int) (ampereToSet - extendAmpere);
					LOG.info("analyzeChargerStatus: smart usage reduced to {} A", ampereToSet);
					setAmpere(String.valueOf(ampereToSet));
				}
				return ampereToSet;
			}
		}
		return null;
	}
}