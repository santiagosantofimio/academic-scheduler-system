package co.edu.uelbosque.scheduler.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import co.edu.uelbosque.scheduler.dto.UsuarioActualDTO;
import co.edu.uelbosque.scheduler.service.UsuarioActualService;

@RestController
@Tag(name = "Seguridad")
public class UsuarioActualController {
	private final UsuarioActualService usuarioActualSer;

	public UsuarioActualController(UsuarioActualService usuarioActualSer) {
		this.usuarioActualSer = usuarioActualSer;
	}

	@GetMapping("/api/usuarios/actual")
	@Operation(summary = "Consultar el usuario autenticado y su rol")
	public ResponseEntity<UsuarioActualDTO> actual(Authentication autenticacion) {
		return ResponseEntity.ok(usuarioActualSer.consultar(autenticacion.getName()));
	}
}
