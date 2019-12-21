package at.seywerth.smartics.rest.api;

import org.springframework.data.repository.CrudRepository;

import at.seywerth.smartics.rest.model.Setting;

/**
 * repository interface for settings data.
 * 
 * @author Raphael Seywerth
 *
 */
public interface SettingRepository extends CrudRepository<Setting, String> {

	Setting findByName(String name);

}