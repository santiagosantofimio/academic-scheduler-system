package co.edu.uelbosque.scheduler.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import co.edu.uelbosque.scheduler.dto.*;
import co.edu.uelbosque.scheduler.service.AuditoriaService;
import co.edu.uelbosque.scheduler.service.VersionHorarioService;

@RestController
@RequestMapping("/api/versiones")
@Tag(name = "VersionHorario")
public class VersionHorarioController {
	private final VersionHorarioService versiones;
	private final AuditoriaService auditoria;

	public VersionHorarioController(VersionHorarioService versiones, AuditoriaService auditoria) {
		this.versiones = versiones;
		this.auditoria = auditoria;
	}

	@GetMapping
	@Operation(summary = "Listar versiones con sus indicadores para compararlas")
	public ResponseEntity<List<VersionResumenDTO>> listar() {
		return ResponseEntity.ok(versiones.listar());
	}

	@GetMapping("/{id}")
	@Operation(summary = "Consultar una version con sus sesiones")
	public ResponseEntity<VersionDetalleDTO> detalle(@PathVariable Long id) {
		return ResponseEntity.ok(versiones.detalle(id));
	}

	@GetMapping("/conflictos")
	@Operation(summary = "Conflictos del horario de trabajo con la regla incumplida")
	public ResponseEntity<List<ConflictoDTO>> conflictos() {
		return ResponseEntity.ok(versiones.conflictos());
	}

	@PostMapping("/borradores")
	@Operation(summary = "Guardar el horario de trabajo como borrador")
	public ResponseEntity<VersionResumenDTO> guardarBorrador(@RequestBody NombreVersionDTO data) {
		return ResponseEntity.status(201).body(versiones.guardarBorrador(data));
	}

	@PostMapping("/publicar")
	@Operation(summary = "Publicar el horario de trabajo si no tiene conflictos de reglas obligatorias")
	public ResponseEntity<VersionResumenDTO> publicar(@RequestBody NombreVersionDTO data) {
		return ResponseEntity.status(201).body(versiones.publicar(data));
	}

	@PostMapping("/{id}/cargar")
	@Operation(summary = "Tomar una version como horario de trabajo; si no es borrador, crea una version recuperada")
	public ResponseEntity<ResultadoCargaDTO> cargar(@PathVariable Long id) {
		return ResponseEntity.ok(versiones.cargar(id));
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "Eliminar un borrador")
	public ResponseEntity<Void> eliminar(@PathVariable Long id) {
		versiones.eliminarBorrador(id);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/auditoria")
	@Operation(summary = "Ultimos cambios del horario con usuario y fecha")
	public ResponseEntity<List<AuditoriaCambioDTO>> auditoria() {
		return ResponseEntity.ok(auditoria.recientes());
	}
}
