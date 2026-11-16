package co.edu.uelbosque.scheduler.security;

import java.nio.charset.StandardCharsets;
import jakarta.servlet.DispatcherType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CsrfException;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.AndRequestMatcher;
import org.springframework.security.web.util.matcher.DispatcherTypeRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	@Bean
	public PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		var despachoDeError = new AndRequestMatcher(new DispatcherTypeRequestMatcher(DispatcherType.ERROR),
				PathPatternRequestMatcher.withDefaults().matcher("/error"));
		http.csrf(csrf -> csrf.ignoringRequestMatchers("/api/registro/administrador")).authorizeHttpRequests(auth -> auth
				.requestMatchers(despachoDeError).permitAll()
				.requestMatchers("/actuator/health", "/actuator/info", "/v3/api-docs/**", "/swagger-ui/**",
						"/swagger-ui.html")
				.permitAll().requestMatchers("/api/registro/administrador").permitAll()
				.requestMatchers("/api/csrf", "/api/usuarios/actual").authenticated()
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
					boolean csrfInvalido = exception instanceof CsrfException;
					String codigo = csrfInvalido ? "CSRF_INVALIDO" : "PERMISO_INSUFICIENTE";
					String detalle = csrfInvalido
							? "No se pudo validar el token de seguridad. Vuelve a iniciar sesión e inténtalo de nuevo."
							: "Tu cuenta no tiene permiso para esta operación.";
					response.setStatus(403);
					response.setCharacterEncoding(StandardCharsets.UTF_8.name());
					response.setContentType("application/problem+json");
					response.getWriter().write("""
							{"title":"Acceso denegado","status":403,"code":"%s","detail":"%s"}
							""".formatted(codigo, detalle));
				}));
		return http.build();
	}
}
