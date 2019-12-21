package at.seywerth.smartics.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import at.seywerth.smartics.rest.model.Setting;
import at.seywerth.smartics.rest.model.SettingName;

/**
 * rest controller for settings.
 * 
 * @author Raphael Seywerth
 *
 */
@RestController
public class SettingController {

	@Autowired
	private SettingService service;

	@GetMapping("/api/settings")
	public List<Setting> getAllSettings() {
		return service.findAll();
	}

	// curl -X PUT http://127.0.0.1:8080/api/setting/SCHEDULE_DETAIL -H 'cache-control: no-cache' -H 'content-type: application/json' -d 'false'
	@PutMapping("api/setting/{name}")
	public Setting setSetting(@PathVariable String name,
							  @RequestBody String value) throws Exception {
		// handle: java.lang.IllegalArgumentException: No enum constant
		Setting setting = service.findByName(SettingName.valueOf(name));
		if (setting == null) {
			throw new Exception("Setting with name " + name + " not found!");
		}
		setting.setValue(value);

		return service.save(setting);
	}

}
