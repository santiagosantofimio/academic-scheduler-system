package co.edu.uelbosque.scheduler.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	@Bean
	public PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests(auth -> auth
				.requestMatchers("/actuator/health", "/actuator/info", "/v3/api-docs/**", "/swagger-ui/**",
						"/swagger-ui.html")
				.permitAll().requestMatchers("/api/csrf", "/api/usuarios/actual").authenticated()
				.requestMatchers(HttpMethod.GET, "/api/mi-horario", "/api/mi-horario/**").hasRole("DOCENTE")
				.requestMatchers("/api/**").hasRole("ADMINISTRADOR")
				.anyRequest().denyAll())
				.httpBasic(basic -> basic.authenticationEntryPoint((request, response, exception) -> {
					response.setStatus(401);
					if (!"XMLHttpRequest".equals(request.getHeader("X-Requested-With")))
						response.setHeader("WWW-Authenticate", "Basic realm=\"academic-scheduler\"");
					response.setContentType("application/problem+json");
					response.getWriter().write(
							"{\"title\":\"Autenticacion requerida\",\"status\":401,\"detail\":\"Credenciales ausentes o invalidas\"}");
				})).exceptionHandling(errors -> errors.accessDeniedHandler((request, response, exception) -> {
					response.setStatus(403);
					response.setContentType("application/problem+json");
					response.getWriter().write(
							"{\"title\":\"Acceso denegado\",\"status\":403,\"detail\":\"Permiso insuficiente o token CSRF ausente o invalido\"}");
				}));
		return http.build();
	}
}
