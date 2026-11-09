package co.edu.uelbosque.scheduler.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import co.edu.uelbosque.scheduler.dto.DisponibilidadDocenteDTO;
import co.edu.uelbosque.scheduler.service.DisponibilidadDocenteService;

@RestController
@RequestMapping("/api/disponibilidades")
@Tag(name = "DisponibilidadDocente")
@ApiResponses({ @ApiResponse(responseCode = "400", description = "Datos invalidos"),
		@ApiResponse(responseCode = "401", description = "Autenticacion requerida"),
		@ApiResponse(responseCode = "403", description = "Sin permiso o token CSRF"),
		@ApiResponse(responseCode = "404", description = "Recurso no encontrado"),
		@ApiResponse(responseCode = "409", description = "Conflicto de integridad o programacion") })
public class DisponibilidadDocenteController {
	private final DisponibilidadDocenteService disponibilidadSer;

	public DisponibilidadDocenteController(DisponibilidadDocenteService disponibilidadSer) {
		this.disponibilidadSer = disponibilidadSer;
	}

	@PostMapping
	@Operation(summary = "Crear DisponibilidadDocente")
	@ApiResponse(responseCode = "201", description = "Recurso creado")
	public ResponseEntity<String> crearDisponibilidad(@RequestBody DisponibilidadDocenteDTO data) {
		disponibilidadSer.create(data);
		return ResponseEntity.status(201).body("Disponibilidad creada");
	}

	@GetMapping
	@Operation(summary = "Listar DisponibilidadDocente")
	public ResponseEntity<List<DisponibilidadDocenteDTO>> mostrarTodo() {
		return ResponseEntity.ok(disponibilidadSer.getAll());
	}

	@GetMapping("/{id}")
	@Operation(summary = "Obtener DisponibilidadDocente por ID")
	public ResponseEntity<DisponibilidadDocenteDTO> buscarPorId(@PathVariable Long id) {
		return ResponseEntity.ok(disponibilidadSer.getById(id));
	}

	@PutMapping("/{id}")
	@Operation(summary = "Actualizar DisponibilidadDocente")
	public ResponseEntity<String> actualizar(@PathVariable Long id, @RequestBody DisponibilidadDocenteDTO data) {
		disponibilidadSer.updateById(id, data);
		return ResponseEntity.ok("Disponibilidad actualizada");
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "Eliminar DisponibilidadDocente")
	@ApiResponse(responseCode = "204", description = "Recurso eliminado")
	public ResponseEntity<String> delete(@PathVariable Long id) {
		disponibilidadSer.deleteById(id);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/por-docente")
	@Operation(summary = "Buscar DisponibilidadDocente por Docente")
	public ResponseEntity<List<DisponibilidadDocenteDTO>> buscarPorDocente(@RequestParam("valor") Long valor) {
		return ResponseEntity.ok(disponibilidadSer.findByDocenteId(valor));
	}
}
