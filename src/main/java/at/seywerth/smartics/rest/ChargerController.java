package at.seywerth.smartics.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import at.seywerth.smartics.rest.model.ChargerStatusDto;

/**
 * rest controller for charger actions.
 * 
 * @author Raphael Seywerth
 *
 */
@RestController
public class ChargerController {

	@Autowired
	private ChargerService chargerService;

	/**
	 * read current status from charger.
	 * @return {@link ChargerStatusDto}
	 */
	@GetMapping("/api/chargerstatus")
	public ChargerStatusDto getChargerStatus() {
		return chargerService.getStatusData();
	}

	/**
	 * set charger data.
	 * curl -X PUT http://127.0.0.1:8080/api/chargerstatus/ -H 'cache-control: no-cache' -H 'content-type: application/json' -d '{"ampere": 8}'
	 * @param chargerStatus
	 * @return {@link ChargerStatusDto}
	 */
	@PutMapping("/api/chargerstatus")
	public ChargerStatusDto setChargerStatus(@RequestBody ChargerStatusDto chargerStatus) {
		return chargerService.setStatusData(chargerStatus);
	}

	/**
	 * set parameter of charger with specified name and value in body.
	 * curl -X PUT http://127.0.0.1:8080/api/charger/ampere -H 'cache-control: no-cache' -H 'content-type: application/json' -d '10'
	 * curl -X PUT http://127.0.0.1:8080/api/charger/allowCharging -H 'cache-control: no-cache' -H 'content-type: application/json' -d 'true'
	 *
	 * @param name
	 * @param value
	 * @return true if it was set correctly, false otherwise
	 */
	@PutMapping("api/charger/{name}")
	public boolean setParameter(@PathVariable String name,
								@RequestBody String value) {
		if (name.equals("ampere")) {
			return chargerService.setAmpere(value);
		} else if (name.equals("colorCharging")) {
			return chargerService.setColorCharging(value);
		} else if (name.equals("allowCharging")) {
			return chargerService.setAllowCharging(value);
		} else if (name.equals("mode")) {
			return chargerService.setMode(value);
		}
		return false;
	}
}