package at.seywerth.smartics.rest;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

	private static final Logger LOG = LoggerFactory.getLogger(SettingService.class);

	@Autowired
	private SettingRepository settingsRepository;

	/**
	 * setup all settings after initialization
	 */
	@PostConstruct
	public void setup() {
		Setting settingVersion = null;
		int update = 0;
		// iterate over settings and check on missing and defaults
		for (SettingName setting : SettingName.values()) {
			Setting settingToCheck = settingsRepository.findByName(setting.name());
			if (SettingName.VERSION.name() == setting.name()) {
				settingVersion = settingToCheck;
			}
			if (settingToCheck == null || settingToCheck.getUpdateTime() == null) {
				LOG.info("setting setup: setting '{}' was not found, using default '{}' to save to db!",
						setting.name(), setting.getDefaultValue());
				Setting newSetting = new Setting(setting.name(), setting.getDefaultValue(), setting.getDescription());
				settingsRepository.save(newSetting);
				update += 1;
			} else if (SettingName.VERSION.name() == setting.name()
					&& !settingToCheck.getValue().equals(setting.getDefaultValue())) {
				LOG.info("setting setup: setting '{}' updated from '{}' to '{}'!",
						setting.name(), settingToCheck.getValue(), setting.getDefaultValue());
				settingToCheck.setValue(setting.getDefaultValue());
				settingsRepository.save(settingToCheck);
				update += 1;
			}
		}
		if (update == 0) {
			LOG.info("setting setup: everyting up to date!");
		} else {
			LOG.info("setting setup: {} setting(s) were updated!", update);
		}
		if (settingVersion != null) {
			LOG.info("setting setup: running app VERSION: {}", settingVersion.getValue());
		}
	}

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