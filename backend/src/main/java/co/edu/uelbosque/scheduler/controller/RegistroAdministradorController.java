package co.edu.uelbosque.scheduler.controller;

import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import co.edu.uelbosque.scheduler.dto.RegistroAdministradorDTO;
import co.edu.uelbosque.scheduler.service.RegistroAdministradorService;

@RestController
@RequestMapping("/api/registro/administrador")
@Tag(name = "Seguridad")
public class RegistroAdministradorController {
	private final RegistroAdministradorService registro;

	public RegistroAdministradorController(RegistroAdministradorService registro) {
		this.registro = registro;
	}

	@GetMapping
	@Operation(summary = "Indicar si el registro de administradores pide codigo")
	public Map<String, Boolean> requisitos() {
		return Map.of("pideCodigo", registro.pideCodigo());
	}

	@PostMapping
	@Operation(summary = "Crear una cuenta de administrador con correo institucional")
	public ResponseEntity<Map<String, String>> registrar(@RequestBody RegistroAdministradorDTO data) {
		registro.registrar(data);
		return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("mensaje", "Cuenta de administrador creada"));
	}
}
