package at.seywerth.smartics.rest;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import at.seywerth.smartics.rest.api.SettingRepository;
import at.seywerth.smartics.rest.model.Setting;
import at.seywerth.smartics.rest.model.SettingName;

/**
 * service to access setting data by rest.
 * 
 * @author Raphael Seywerth
 *
 */
@Service
public class SettingService {

	@Autowired
	private SettingRepository settingsRepository;
	
	public Setting save(Setting setting) {
		return settingsRepository.save(setting);
	}

	public List<Setting> findAll() {
		List<Setting> list = new ArrayList<>();
		settingsRepository.findAll().forEach(list::add);

		return list;
	}

	public Setting findByName(SettingName name) {
		return settingsRepository.findByName(name.name());
	}
}