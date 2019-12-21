package at.seywerth.smartics.rest;

import static org.mockito.Mockito.verify;

import java.sql.Timestamp;
import java.time.Instant;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import at.seywerth.smartics.rest.api.MeteringDataSecRepository;

/**
 * Tests for service methods.
 * 
 * @author Raphael Seywerth
 *
 */
@ExtendWith(MockitoExtension.class)
public class MeteringDataSecServiceTest {

	@Mock
	private MeteringDataSecRepository repository;

	@InjectMocks
    private MeteringDataSecService service;


	@Test
	public void testFindForLastMinutes() {

		service.findForLastMinutes(Instant.now(), 2);
   
		verify(repository).findSinceCreationTime(Mockito.any(Timestamp.class));
    }

}