package at.seywerth.smartics;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * backend part
 * 
 * @author Raphael Seywerth
 *
 */
@SpringBootApplication
@EnableScheduling
public class SmarticsApplication {

	public static void main(String[] args) {
		SpringApplication.run(SmarticsApplication.class, args);
	}

}
