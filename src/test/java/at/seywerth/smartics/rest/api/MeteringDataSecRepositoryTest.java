package at.seywerth.smartics.rest.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.stream.StreamSupport;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import at.seywerth.smartics.BaseIntegrationTest;
import at.seywerth.smartics.rest.api.MeteringDataSecRepository;
import at.seywerth.smartics.rest.model.InverterStatus;
import at.seywerth.smartics.rest.model.MeteringDataSec;

/**
 * tests for inverter repository.
 * 
 * @author Raphael Seywerth
 *
 */
@Transactional
public class MeteringDataSecRepositoryTest extends BaseIntegrationTest {

    @Autowired
    private MeteringDataSecRepository meteringDataRepository;

    @BeforeEach
    public void setUp() throws Exception {
        MeteringDataSec inverter = new MeteringDataSec(new Timestamp(1), new BigDecimal("1"), new BigDecimal("2"),
        		new BigDecimal("3"), InverterStatus.OK.getCode());
        assertNull(inverter.getUpdateTime());
        
        // save, verify updatetime after save
        MeteringDataSec result = meteringDataRepository.save(inverter);
        assertNotNull(result.getUpdateTime());
    }

    @Test
    public void testFindSinceCreationTime() {
    	// create 2 additional entities
        MeteringDataSec meteringData2 = new MeteringDataSec(new Timestamp(2), new BigDecimal("1"), null, null, 0L);
        meteringDataRepository.save(meteringData2);
        MeteringDataSec meteringData3 = new MeteringDataSec(new Timestamp(3), new BigDecimal("2"), null, null, 0L);
        meteringDataRepository.save(meteringData3);

    	List<MeteringDataSec> list = meteringDataRepository.findSinceCreationTime(new Timestamp(2));
    	assertNotNull(list);
    	assertThat(list).hasSize(2);
    }

    @Test
    public void testFindAll() {
    	MeteringDataSec inverter = meteringDataRepository.findByCreationTime(new Timestamp(1));
        assertNotNull(inverter);
        assertEquals(new BigDecimal("1"), inverter.getPowerProduced());

        Iterable<MeteringDataSec> inverters = meteringDataRepository.findAll();
        assertEquals(1, StreamSupport.stream(inverters.spliterator(), false).count());
    }

    @Test
    public void testSave() {
    	MeteringDataSec inverter = new MeteringDataSec(new Timestamp(2), new BigDecimal("1"), new BigDecimal("0"),
    			new BigDecimal("2"), 1L);

    	MeteringDataSec result = meteringDataRepository.save(inverter);
    	assertNotNull(result.getUpdateTime());
        Iterable<MeteringDataSec> inverters = meteringDataRepository.findAll();
    	assertEquals(2, StreamSupport.stream(inverters.spliterator(), false).count());
    }

}