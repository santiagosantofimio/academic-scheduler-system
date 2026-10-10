package co.edu.uelbosque.scheduler.configuration;

import java.time.Clock;
import java.time.ZoneId;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {

	@Bean
	public Clock clock() {
		return Clock.system(ZoneId.of("America/Bogota"));
	}

}
