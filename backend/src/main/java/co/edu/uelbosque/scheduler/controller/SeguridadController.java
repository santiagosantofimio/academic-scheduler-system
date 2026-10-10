package co.edu.uelbosque.scheduler.controller;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Seguridad")
public class SeguridadController {
	@GetMapping("/api/csrf")
	@Operation(summary = "Obtener token CSRF; conservar la cookie de sesion y enviar el token en headerName al escribir")
	public TokenCsrf csrf(CsrfToken token) {
		return new TokenCsrf(token.getHeaderName(), token.getParameterName(), token.getToken());
	}

	public record TokenCsrf(String headerName, String parameterName, String token) {
	}
}
