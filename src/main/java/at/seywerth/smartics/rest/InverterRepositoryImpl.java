package at.seywerth.smartics.rest;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import at.seywerth.smartics.rest.api.InverterRepository;
import at.seywerth.smartics.rest.mapper.InverterArchiveMapper;
import at.seywerth.smartics.rest.mapper.InverterRealtimeMapper;
import at.seywerth.smartics.rest.model.InverterDto;
import at.seywerth.smartics.rest.model.MeteringDataMin;
import at.seywerth.smartics.rest.model.PowerProducedDto;

/**
 * repository interface for inverter data.
 * 
 * @author Raphael Seywerth
 *
 */
@Repository
public class InverterRepositoryImpl implements InverterRepository {

	private static final Logger LOG = LoggerFactory.getLogger(InverterRepositoryImpl.class);

	private static final String INVERTER_IP = "http://192.168.1.200";
	private static final String INVERTER_CHANNEL = "&Channel=";

	@Autowired
	private ObjectMapper mapper;


	@Override
	public InverterDto getRealtimeData() {
    	//read JSON like DOM Parser
    	JsonNode rootNode;
		try {
			rootNode = mapper.readTree(new URL(INVERTER_IP + "/solar_api/v1/GetPowerFlowRealtimeData.fcgi"));
			// map json response
	    	return InverterRealtimeMapper.convertToDto(rootNode);
		} catch (IOException e) {
			// TODO handle connection timeout
			LOG.error("getRealtimeData Exception on parsing json data {}", e.getMessage());
		}

		return new InverterDto(); 
	}

	@Override
	public List<MeteringDataMin> getArchiveDataPVForDay(String day) {
    	JsonNode rootNode;
		try {
			// date is needed as dd.mm.yyyy, ex: 30.11.2019
			rootNode = mapper.readTree(new URL(INVERTER_IP + "/solar_api/v1/GetArchiveData.cgi?Scope=System&StartDate="
					+ day + "&EndDate=" + day
					+ INVERTER_CHANNEL + InverterArchiveMapper.INVERTER_CHANNEL_PROD
					+ INVERTER_CHANNEL + InverterArchiveMapper.INVERTER_CHANNEL_CONS
					+ INVERTER_CHANNEL + InverterArchiveMapper.INVERTER_CHANNEL_FEED));
			// map json response
	    	List<PowerProducedDto> prodList = InverterArchiveMapper.convertToDto(rootNode, day, InverterArchiveMapper.INVERTER_CHANNEL_PROD);
	    	List<PowerProducedDto> consList = InverterArchiveMapper.convertToDto(rootNode, day, InverterArchiveMapper.INVERTER_CHANNEL_CONS);
	    	List<PowerProducedDto> feedList = InverterArchiveMapper.convertToDto(rootNode, day, InverterArchiveMapper.INVERTER_CHANNEL_FEED);

			List<MeteringDataMin> result = new ArrayList<>();
			for (int index = 0; index < prodList.size(); index++) {
				if (index >= feedList.size() || index >= consList.size()) {
					LOG.error("getArchiveData: could not sum/convert for time: {}", prodList.get(index).getCurrentTime());
					continue;
				}
				MeteringDataMin entry = new MeteringDataMin(Timestamp.from(prodList.get(index).getCurrentTime()), null,
						BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, 0L);
				entry.setArchiveProduced(prodList.get(index).getPvCurrent());
				entry.setArchiveConsumed(consList.get(index).getPvCurrent());
				entry.setArchiveFeedback(feedList.get(index).getPvCurrent());
				result.add(entry);
			}
			return result;
		} catch (IOException e) {
			// exception with response
			LOG.error("getArchiveDataPVForDay Exception on parsing json data {}", e.getMessage());
		}

		return new ArrayList<>();
	}
}