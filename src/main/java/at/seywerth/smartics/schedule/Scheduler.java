package at.seywerth.smartics.schedule;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import at.seywerth.smartics.rest.MeteringDataDayService;
import at.seywerth.smartics.rest.MeteringDataMinService;
import at.seywerth.smartics.rest.MeteringDataSecService;
import at.seywerth.smartics.rest.SettingService;
import at.seywerth.smartics.rest.api.ChargerRepository;
import at.seywerth.smartics.rest.api.InverterRepository;
import at.seywerth.smartics.rest.mapper.InverterRealtimeMapper;
import at.seywerth.smartics.rest.model.InverterDto;
import at.seywerth.smartics.rest.model.InverterStatus;
import at.seywerth.smartics.rest.model.MeteringDataDay;
import at.seywerth.smartics.rest.model.MeteringDataMin;
import at.seywerth.smartics.rest.model.MeteringDataSec;
import at.seywerth.smartics.rest.model.Setting;
import at.seywerth.smartics.rest.model.SettingName;
import at.seywerth.smartics.util.InverterCalculatorUtil;
import at.seywerth.smartics.util.InverterDateTimeFormater;

/**
 * scheduler to query inverter data. will provide following options
 *  - detail: every 5 seconds
 *  - rough: every 5 minutes (inverter default for historic data)
 *  - daily: every day at 22:00
 * 
 * @author Raphael Seywerth
 *
 */
@Component
public class Scheduler {

	private static final Logger LOG = LoggerFactory.getLogger(Scheduler.class);

	@Autowired
	private SettingService settingService;
	@Autowired
	private InverterRepository inverterRepository;
	@Autowired
	private MeteringDataSecService meteringDataSecService;
	@Autowired
	private MeteringDataMinService meteringDataMinService;
	@Autowired
	private MeteringDataDayService meteringDataDayService;
	@Autowired
	private ChargerRepository chargerRepository;


	/**
	 * detailed scheduler every 5 seconds.
	 * - fixedRate = 5000
	 */
	@Scheduled(cron = "*/5 * * * * *")
	public void detailScheduler() {
		Instant currentTime = Instant.now();
		Setting detail = settingService.findByName(SettingName.SCHEDULE_DETAIL);

		if (detail != null && detail.getValue() != null && detail.getValue().equals("true")) {
			LOG.debug("DETAIL scheduler run at {}", InverterDateTimeFormater.getTimeReadableFormatted(currentTime));

			// PROBLEM: creationTime of inverter is later - check which time to use!
			// check if data was not already written (has to be older than 4.5 secs)
			MeteringDataSec latestData = meteringDataSecService.getLatest();
			if (latestData != null && latestData.getCreationTime().toInstant().isAfter(currentTime.minus(4500, ChronoUnit.MILLIS))) {
				LOG.info("detail: ran already, skip summarizing!");
				return;
			}

			InverterDto inverterDto = inverterRepository.getRealtimeData();
			// TODO handle timeouts
			MeteringDataSec meteringData = InverterRealtimeMapper.convertToEntity(inverterDto);
			// override with local time, server time might be a little different!
			meteringData.setCreationTime(Timestamp.from(currentTime));
			meteringDataSecService.save(meteringData);
			LOG.debug("detail saved: {} produced, {} consumed, {} feedback!", meteringData.getPowerProduced(),
					meteringData.getPowerConsumed(), meteringData.getPowerFeedback());
		}
	}

	/**
	 * rough scheduler every 5 minutes.
	 * - fixedRate = 300000
	 */
	@Scheduled(cron = "0 */5 * * * *")
	public void roughScheduler() {
		final Instant currentTime = Instant.now();
		final Setting rough = settingService.findByName(SettingName.SCHEDULE_ROUGH);
		MeteringDataMin meteringData = null;

		// write metering data if available
		if (rough != null && rough.getValue() != null && rough.getValue().equals("true")) {
			LOG.info("ROUGH scheduler run at {}", InverterDateTimeFormater.getTimeReadableFormatted(currentTime));

			// check if data was not already written (has to be older than 4.5 mins)
			MeteringDataMin latestData = meteringDataMinService.getLatest();
			if (latestData != null && latestData.getUntilTime().toInstant().isAfter(currentTime.minus(230, ChronoUnit.SECONDS))) {
				LOG.info("rough: ran already, skip summarizing!");
				return;
			}

			// option: calculate during archived detail data
			List<MeteringDataSec> list;
			long diffLatestSecs = latestData != null
					? latestData.getUntilTime().toInstant().until(currentTime, ChronoUnit.SECONDS)
					: 0;
			// use latest entry if roughly 5 mins ago
			if (diffLatestSecs < 330 && diffLatestSecs > 230) {
				list = meteringDataSecService.findAllSince(latestData.getUntilTime().toInstant());
			} else {
				LOG.warn("rough: no latest entry roughly 5 mins ago found using all within last 5 mins!");
				list = meteringDataSecService.findForLastMinutes(currentTime, 5);
			}

			// option: no detail data available - get from realtime data
			if (list.isEmpty()) {
				LOG.info("rough: no detail data found, getting realtime data..");
				InverterDto inverterDto = inverterRepository.getRealtimeData();
				// minus 5 mins starttime if only one realtime entry exists, not using inverter time for following runs compatibility
				Instant correctStart = currentTime.minus(5, ChronoUnit.MINUTES);
				MeteringDataSec startMeteringData = new MeteringDataSec(Timestamp.from(correctStart), BigDecimal.ZERO,
						BigDecimal.ZERO, BigDecimal.ZERO, InverterStatus.OK.getCode());
				list.add(startMeteringData);
				MeteringDataSec meteringDataSec = InverterRealtimeMapper.convertToEntity(inverterDto);
				// use app time for consistency
				// TODO: should take difference to inverter into account in future
				meteringDataSec.setCreationTime(Timestamp.from(currentTime));
				list.add(meteringDataSec);
			}
			meteringData = InverterCalculatorUtil.calculateSumForSecEntries(list);

			// warning on non 5-min spans
			meteringData.setStatusCode(MeteringDataMinService.getStatusForMeteringData(meteringData, list.size()).getCode());

			meteringDataMinService.save(meteringData);
			LOG.info("rough saved: {} produced, {} consumed, {} feedback!", meteringData.getPowerProduced(),
					meteringData.getPowerConsumed(), meteringData.getPowerFeedback());
		}

		// do smart charging if enabled
		Setting chargerMode = settingService.findByName(SettingName.CHARGER_MODE);
		if (chargerMode != null && meteringData != null) {
			chargerRepository.analyzeChargerStatus(chargerMode, meteringData);
		}
	}

	/**
	 * daily scheduler every day at 12 AM (midnight).
	 */
	@Scheduled(cron = "0 0 0 * * *")
	public void dailyScheduler() {
		Instant currentTime = Instant.now();
		Setting daily = settingService.findByName(SettingName.SCHEDULE_DAILY);

		if (daily != null && daily.getValue() != null && daily.getValue().equals("true")) {
			LOG.info("daily scheduler run at {}", InverterDateTimeFormater.getTimeReadableFormatted(currentTime));
			
		// check if data was not already written (has to be older than 23 hrs)
		MeteringDataDay latestData = meteringDataDayService.getLatest();
		if (latestData != null && latestData.getUntilTime().toInstant().isAfter(currentTime.minus(23, ChronoUnit.HOURS))) {
			LOG.info("daily: ran already, skip summarizing!");
			return;
		}

		// option: calculate during archived detail data
		List<MeteringDataMin> list;
		long diffLatestSecs = latestData != null
				? latestData.getUntilTime().toInstant().until(currentTime, ChronoUnit.MINUTES)
				: 0;
		// use latest entry if roughly 24 hrs ago
		if (diffLatestSecs < 1470 && diffLatestSecs > 1410) {
			list = meteringDataMinService.findAllSince(latestData.getUntilTime().toInstant());
		} else {
			LOG.warn("daily: no latest entry roughly 24 hrs ago found using all within last 24 hrs!");
			list = meteringDataMinService.findForLastHours(currentTime, 24);
		}

		// option: no current data available, get archive data
		if (list.isEmpty()) {
			LOG.info("daily: no detail data found, getting archive data.. -TODO");
		}
		MeteringDataDay meteringData = InverterCalculatorUtil.calculateSumForMinEntries(list);

		// warning on non 24-hr spans
		meteringData.setStatusCode(MeteringDataDayService.getStatusForMeteringData(meteringData, list.size()).getCode());


		meteringDataDayService.save(meteringData);
		LOG.info("daily (used {} min entries) saved: {} produced, {} consumed, {} feedback!", list.size(),
				meteringData.getPowerProduced(), meteringData.getPowerConsumed(), meteringData.getPowerFeedback());
		}

	}

}