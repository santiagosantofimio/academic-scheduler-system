package co.edu.uelbosque.scheduler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.boot.test.context.SpringBootTest;

// Prueba de integracion original: requiere configurar expresamente una base de datos.
@EnabledIfSystemProperty(named = "scheduler.integration", matches = "true")
@SpringBootTest
class AcademicSchedulerApplicationTests {

	@Test
	void contextLoads() {
	}

}
