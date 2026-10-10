package co.edu.uelbosque.scheduler.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class OpenApiConfig {

	@Bean
	public OpenAPI academicSchedulerApi() {
		return new OpenAPI().info(new Info().title("Programacion de horarios academicos").version("1.0")
				.description("Gestion academica y generacion semanal de horarios. HTTP Basic con correo institucional; "
						+ "las escrituras requieren cookie de sesion y token obtenido en GET /api/csrf. "
						+ "Los endpoints de gestion requieren ADMINISTRADOR."))
				.components(new Components().addSecuritySchemes("basicAuth",
						new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("basic")))
				.addSecurityItem(new SecurityRequirement().addList("basicAuth"));
	}
}
