package at.seywerth.smartics.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import at.seywerth.smartics.rest.model.InverterDto;
import at.seywerth.smartics.rest.model.MeteringDataMin;
import at.seywerth.smartics.rest.model.MeteringDataSec;
import at.seywerth.smartics.rest.model.MeteringDataSummaryDto;

/**
 * rest controller for inverter actions.
 * 
 * @author Raphael Seywerth
 *
 */
@RestController
public class MeteringDataController {

	@Autowired
	private InverterService inverterService;
	@Autowired
	private MeteringDataSecService meteringDataService;

	@GetMapping("/api/inverters")
	public List<MeteringDataSec> getAllInverters() {
		return meteringDataService.findAll();
	}
	
	@PutMapping("/api/inverter")
	public MeteringDataSec saveInverter(MeteringDataSec meteringDataSec) {
		return meteringDataService.save(meteringDataSec);
	}

	@GetMapping("api/inverterrealtime")
	public InverterDto getRealtimeData() {
		// read json and write to db
		return inverterService.getRealtimeData();
	}

	@GetMapping("api/inverterarchive/{day}")
	public List<MeteringDataMin> getArchiveData(@PathVariable String day) {
		return inverterService.getArchiveData(day);
	}

	@GetMapping("api/inverterarchiverecalc/{day}")
	public List<MeteringDataMin> recalculateArchiveData(@PathVariable String day) {
		return inverterService.recalculateArchiveData(day);
	}

	/**
	 * get summary for current or a specific day
	 * @param day
	 * @return
	 */
	@GetMapping("api/meterdatasummary/{day}")
	public MeteringDataSummaryDto calculateSummary(@PathVariable String day) {
		return inverterService.calculateSummary(day);
	}

}
