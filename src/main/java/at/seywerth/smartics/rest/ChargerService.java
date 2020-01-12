package at.seywerth.smartics.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import at.seywerth.smartics.rest.api.ChargerRepository;
import at.seywerth.smartics.rest.model.ChargerStatusDto;


/**
 * service to access charger data by rest.
 * 
 * @author Raphael Seywerth
 *
 */
@Service
public class ChargerService {

	private static final Logger LOG = LoggerFactory.getLogger(ChargerService.class);

	@Autowired
	private ChargerRepository chargerRepository;

	public ChargerStatusDto getStatusData() {
		ChargerStatusDto status = chargerRepository.getStatusData();
		LOG.debug("charger queried, returns time: {}, ampere {}, connectionStatus {}",
				status.getCurrentTime(), status.getAmpere(), status.getConnectionStatus());
		return status;
	}

	public ChargerStatusDto setStatusData(final ChargerStatusDto chargerStatus) {
		// add checks
		String ampere = chargerStatus.getAmpere() != null ? chargerStatus.getAmpere().toString() : null;
		String colorCharging = chargerStatus.getColorCharging() != null ? chargerStatus.getColorCharging().toString() : null;

		return chargerRepository.setChargerData(ampere, colorCharging, chargerStatus.getAllowCharging());
	}

	public boolean setAmpere(final String ampere) {
		if (ampere == null || ampere.isEmpty()) {
			return false;
		}
		return chargerRepository.setAmpere(ampere);
	}

	public boolean setColorCharging(final String color) {
		if (color == null || color.isEmpty()) {
			return false;
		}
		return chargerRepository.setColorCharging(color);
	}

	public boolean setAllowCharging(final String allowCharging) {
		if (allowCharging == null || allowCharging.isEmpty()) {
			return false;
		}
		return chargerRepository.setAllowCharging(Boolean.valueOf(allowCharging));
	}
}