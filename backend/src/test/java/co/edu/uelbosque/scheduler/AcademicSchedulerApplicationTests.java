package co.edu.uelbosque.scheduler;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers(disabledWithoutDocker = true)
@SpringBootTest
@Import(TestcontainersConfiguration.class)
class AcademicSchedulerApplicationTests {

	@Test
	void contextLoads() {
	}

}
