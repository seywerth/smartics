package at.seywerth.smartics.rest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.ObjectMapper;

import at.seywerth.smartics.BaseIntegrationTest;
import at.seywerth.smartics.rest.mapper.InverterRealtimeMapper;
import at.seywerth.smartics.rest.model.InverterDto;

/**
 * Tests for controller methods.
 * 
 * @author Raphael Seywerth
 *
 */
public class MeteringDataControllerTest extends BaseIntegrationTest {

	@Autowired
    private MeteringDataController controller;
	@Mock
	private ObjectMapper objectMapper;
	@Mock
	private InverterRealtimeMapper inverterMapper;

	@Test
	public void testGetInverterData() throws MalformedURLException, IOException, JSONException {
    	//when(objectMapper.readTree(any(URL.class))).thenReturn(null);
		
		InverterDto dto = controller.getRealtimeData();
   
		assertNotNull(dto);
    }

	private static String readAll(Reader rd) throws IOException {
		StringBuilder sb = new StringBuilder();
		int cp;
		while ((cp = rd.read()) != -1) {
			sb.append((char) cp);
		}
		return sb.toString();
	}

	public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
		InputStream is = new URL(url).openStream();
		try {
			BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
			String jsonText = readAll(rd);
			JSONObject json = new JSONObject(jsonText);
			return json;
		} finally {
			is.close();
		}
	}
}