package at.seywerth.smartics.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.seywerth.smartics.rest.model.MeteringDataDay;
import at.seywerth.smartics.rest.model.MeteringDataMin;
import at.seywerth.smartics.rest.model.MeteringDataSec;

/**
 * tests for inverter calculator util.
 * 
 * @author Raphael Seywerth
 *
 */
public class InverterCalculatorUtilTest {

	private static final Logger LOG = LoggerFactory.getLogger(InverterCalculatorUtilTest.class);

    @Test
    public void testCalculateSumForMinEntries() {
    	ArrayList<MeteringDataMin> list = createTestListMin();

    	MeteringDataDay result = InverterCalculatorUtil.calculateSumForMinEntries(list);

    	assertNotNull(result);
    	LOG.info("Day summary from {} to {} produced: {}", result.getStartTime(), result.getUntilTime(), result.getPowerProduced());
    	assertThat(result.getPowerProduced()).isEqualTo(new BigDecimal("157.91"));
    	assertThat(result.getUntilTime()).isEqualTo(list.get(list.size()-1).getUntilTime());
    }

    private ArrayList<MeteringDataMin> createTestListMin() {
    	ArrayList<MeteringDataMin> list = new ArrayList<>();
    	list.add(createEntryMin(Timestamp.valueOf("2019-12-15 18:00:00.000456"), Timestamp.valueOf("2019-12-15 18:05:05.000547"), new BigDecimal("27.49")));
    	list.add(createEntryMin(Timestamp.valueOf("2019-12-15 18:05:05.000547"), Timestamp.valueOf("2019-12-15 18:10:10.001254"), new BigDecimal("20.03")));
    	list.add(createEntryMin(Timestamp.valueOf("2019-12-15 18:10:10.001254"), Timestamp.valueOf("2019-12-15 18:15:15.000903"), new BigDecimal("26.01")));
    	list.add(createEntryMin(Timestamp.valueOf("2019-12-15 18:15:15.000903"), Timestamp.valueOf("2019-12-15 18:20:20.000752"), new BigDecimal("28.38")));
    	list.add(createEntryMin(Timestamp.valueOf("2019-12-15 18:20:20.000752"), Timestamp.valueOf("2019-12-15 18:25:25.000379"), new BigDecimal("29.92")));
    	list.add(createEntryMin(Timestamp.valueOf("2019-12-15 18:25:25.000379"), Timestamp.valueOf("2019-12-15 18:30:30.001194"), new BigDecimal("26.22")));
    	list.add(createEntryMin(Timestamp.valueOf("2019-12-15 18:30:30.001194"), Timestamp.valueOf("2019-12-15 18:35:30.001194"), new BigDecimal("27.35")));

    	return list;
    }

    private MeteringDataMin createEntryMin(Timestamp startTime, Timestamp untilTime, BigDecimal prod) {
    	MeteringDataMin meteringData = new MeteringDataMin(startTime, untilTime, prod, BigDecimal.ZERO, BigDecimal.ZERO, 0L);

        return meteringData;
    }

    @Test
    public void testCalculateSumForSecEntries() {
    	ArrayList<MeteringDataSec> list = createTestListSec();

    	MeteringDataMin result = InverterCalculatorUtil.calculateSumForSecEntries(list);

    	assertNotNull(result);
    	LOG.info("Min summary from {} to {} produced: {}", result.getStartTime(), result.getUntilTime(), result.getPowerProduced());
    	assertThat(result.getPowerProduced()).isEqualTo(new BigDecimal("22.13"));
    	assertThat(result.getUntilTime()).isEqualTo(list.get(list.size()-1).getCreationTime());
    }

    private ArrayList<MeteringDataSec> createTestListSec() {
    	ArrayList<MeteringDataSec> list = new ArrayList<>();
    	list.add(createEntrySec(Timestamp.valueOf("2019-12-15 18:00:00.000456"), new BigDecimal("271.49")));
    	list.add(createEntrySec(Timestamp.valueOf("2019-12-15 18:00:05.000547"), new BigDecimal("270.03")));
    	list.add(createEntrySec(Timestamp.valueOf("2019-12-15 18:00:10.001254"), new BigDecimal("269.01")));
    	list.add(createEntrySec(Timestamp.valueOf("2019-12-15 18:00:15.000903"), new BigDecimal("268.38")));
    	list.add(createEntrySec(Timestamp.valueOf("2019-12-15 18:00:20.000752"), new BigDecimal("269.92")));
    	list.add(createEntrySec(Timestamp.valueOf("2019-12-15 18:00:25.000379"), new BigDecimal("268.22")));
    	list.add(createEntrySec(Timestamp.valueOf("2019-12-15 18:00:30.001194"), new BigDecimal("267.35")));
    	list.add(createEntrySec(Timestamp.valueOf("2019-12-15 18:00:35.000725"), new BigDecimal("268.28")));
    	list.add(createEntrySec(Timestamp.valueOf("2019-12-15 18:00:40.000985"), new BigDecimal("267.46")));
    	list.add(createEntrySec(Timestamp.valueOf("2019-12-15 18:00:45.000833"), new BigDecimal("267.73")));
    	list.add(createEntrySec(Timestamp.valueOf("2019-12-15 18:00:50.001046"), new BigDecimal("268.47")));
    	list.add(createEntrySec(Timestamp.valueOf("2019-12-15 18:00:55.000443"), new BigDecimal("267.46")));
    	list.add(createEntrySec(Timestamp.valueOf("2019-12-15 18:01:00.001107"), new BigDecimal("268.29")));
    	list.add(createEntrySec(Timestamp.valueOf("2019-12-15 18:01:05.000725"), new BigDecimal("268.05")));
    	list.add(createEntrySec(Timestamp.valueOf("2019-12-15 18:01:10.001161"), new BigDecimal("268.12")));
    	list.add(createEntrySec(Timestamp.valueOf("2019-12-15 18:01:15.001235"), new BigDecimal("284.66")));
    	list.add(createEntrySec(Timestamp.valueOf("2019-12-15 18:01:20.000646"), new BigDecimal("273.00")));
    	list.add(createEntrySec(Timestamp.valueOf("2019-12-15 18:01:25.000369"), new BigDecimal("289.58")));
    	list.add(createEntrySec(Timestamp.valueOf("2019-12-15 18:01:30.000485"), new BigDecimal("285.95")));
    	list.add(createEntrySec(Timestamp.valueOf("2019-12-15 18:01:35.001016"), new BigDecimal("276.23")));
    	list.add(createEntrySec(Timestamp.valueOf("2019-12-15 18:01:40.001076"), new BigDecimal("292.41")));
    	list.add(createEntrySec(Timestamp.valueOf("2019-12-15 18:01:45.001181"), new BigDecimal("268.63")));
    	list.add(createEntrySec(Timestamp.valueOf("2019-12-15 18:01:50.000359"), new BigDecimal("268.30")));
    	list.add(createEntrySec(Timestamp.valueOf("2019-12-15 18:01:55.000783"), new BigDecimal("271.71")));
    	list.add(createEntrySec(Timestamp.valueOf("2019-12-15 18:02:00.001131"), new BigDecimal("268.73")));
    	list.add(createEntrySec(Timestamp.valueOf("2019-12-15 18:02:05.000661"), new BigDecimal("267.33")));
    	list.add(createEntrySec(Timestamp.valueOf("2019-12-15 18:02:10.000820"), new BigDecimal("268.67")));
    	list.add(createEntrySec(Timestamp.valueOf("2019-12-15 18:02:15.000881"), new BigDecimal("269.25")));
    	list.add(createEntrySec(Timestamp.valueOf("2019-12-15 18:02:20.001244"), new BigDecimal("267.35")));
    	list.add(createEntrySec(Timestamp.valueOf("2019-12-15 18:02:25.000515"), new BigDecimal("269.25")));
    	list.add(createEntrySec(Timestamp.valueOf("2019-12-15 18:02:30.000680"), new BigDecimal("267.12")));
    	list.add(createEntrySec(Timestamp.valueOf("2019-12-15 18:02:35.000942"), new BigDecimal("268.44")));
    	list.add(createEntrySec(Timestamp.valueOf("2019-12-15 18:02:40.000791"), new BigDecimal("267.70")));
    	list.add(createEntrySec(Timestamp.valueOf("2019-12-15 18:02:45.000426"), new BigDecimal("268.65")));
    	list.add(createEntrySec(Timestamp.valueOf("2019-12-15 18:02:50.000745"), new BigDecimal("266.83")));
    	list.add(createEntrySec(Timestamp.valueOf("2019-12-15 18:02:55.001232"), new BigDecimal("267.44")));
    	list.add(createEntrySec(Timestamp.valueOf("2019-12-15 18:03:00.001167"), new BigDecimal("267.20")));
    	list.add(createEntrySec(Timestamp.valueOf("2019-12-15 18:03:05.000528"), new BigDecimal("267.46")));
    	list.add(createEntrySec(Timestamp.valueOf("2019-12-15 18:03:10.001117"), new BigDecimal("267.55")));
    	list.add(createEntrySec(Timestamp.valueOf("2019-12-15 18:03:15.000748"), new BigDecimal("269.11")));
    	list.add(createEntrySec(Timestamp.valueOf("2019-12-15 18:03:20.001097"), new BigDecimal("266.65")));
    	list.add(createEntrySec(Timestamp.valueOf("2019-12-15 18:03:25.000415"), new BigDecimal("267.22")));
    	list.add(createEntrySec(Timestamp.valueOf("2019-12-15 18:03:30.001028"), new BigDecimal("265.88")));
    	list.add(createEntrySec(Timestamp.valueOf("2019-12-15 18:03:35.000885"), new BigDecimal("265.34")));
    	list.add(createEntrySec(Timestamp.valueOf("2019-12-15 18:03:40.000338"), new BigDecimal("272.95")));
    	list.add(createEntrySec(Timestamp.valueOf("2019-12-15 18:03:45.000974"), new BigDecimal("273.24")));
    	list.add(createEntrySec(Timestamp.valueOf("2019-12-15 18:03:50.000942"), new BigDecimal("265.88")));
    	list.add(createEntrySec(Timestamp.valueOf("2019-12-15 18:03:55.000646"), new BigDecimal("268.16")));
    	list.add(createEntrySec(Timestamp.valueOf("2019-12-15 18:04:00.001171"), new BigDecimal("266.25")));
    	list.add(createEntrySec(Timestamp.valueOf("2019-12-15 18:04:05.000501"), new BigDecimal("263.48")));
    	list.add(createEntrySec(Timestamp.valueOf("2019-12-15 18:04:10.000604"), new BigDecimal("266.06")));
    	list.add(createEntrySec(Timestamp.valueOf("2019-12-15 18:04:15.000786"), new BigDecimal("264.46")));
    	list.add(createEntrySec(Timestamp.valueOf("2019-12-15 18:04:20.000659"), new BigDecimal("264.07")));
    	list.add(createEntrySec(Timestamp.valueOf("2019-12-15 18:04:25.001017"), new BigDecimal("264.42")));
    	list.add(createEntrySec(Timestamp.valueOf("2019-12-15 18:04:30.000700"), new BigDecimal("266.92")));
    	list.add(createEntrySec(Timestamp.valueOf("2019-12-15 18:04:35.001090"), new BigDecimal("267.05")));
    	list.add(createEntrySec(Timestamp.valueOf("2019-12-15 18:04:40.000614"), new BigDecimal("265.58")));
    	list.add(createEntrySec(Timestamp.valueOf("2019-12-15 18:04:45.000591"), new BigDecimal("266.14")));
    	list.add(createEntrySec(Timestamp.valueOf("2019-12-15 18:04:50.001095"), new BigDecimal("217.70")));
    	list.add(createEntrySec(Timestamp.valueOf("2019-12-15 18:04:55.000378"), new BigDecimal("179.38")));
    	list.add(createEntrySec(Timestamp.valueOf("2019-12-15 18:05:00.000579"), new BigDecimal("179.23")));

    	return list;
    }

    private MeteringDataSec createEntrySec(Timestamp creationTime, BigDecimal prod) {
        MeteringDataSec meteringData = new MeteringDataSec(creationTime, prod, null, null, 0L);

        return meteringData;
    }

}