package co.edu.uelbosque.scheduler.controller;

import java.time.*;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import co.edu.uelbosque.scheduler.dto.*;
import co.edu.uelbosque.scheduler.service.*;
import co.edu.uelbosque.scheduler.entity.*;

@RestController
@RequestMapping("/api/requisitos-asignatura")
@Tag(name = "RequisitoAsignatura")
@ApiResponses({ @ApiResponse(responseCode = "400", description = "Datos invalidos"),
		@ApiResponse(responseCode = "401", description = "Autenticacion requerida"),
		@ApiResponse(responseCode = "403", description = "Sin permiso o token CSRF"),
		@ApiResponse(responseCode = "404", description = "Recurso no encontrado"),
		@ApiResponse(responseCode = "409", description = "Conflicto de integridad o programacion") })
public class RequisitoAsignaturaController {
	private final RequisitoAsignaturaService requisitoAsignaturaSer;

	public RequisitoAsignaturaController(RequisitoAsignaturaService requisitoAsignaturaSer) {
		this.requisitoAsignaturaSer = requisitoAsignaturaSer;
	}

	@PostMapping
	@Operation(summary = "Crear RequisitoAsignatura")
	@ApiResponse(responseCode = "201", description = "Recurso creado")
	public ResponseEntity<String> crearRequisito(@RequestBody RequisitoAsignaturaDTO data) {
		requisitoAsignaturaSer.create(data);
		return ResponseEntity.status(201).body("RequisitoAsignatura creado");
	}

	@GetMapping
	@Operation(summary = "Listar RequisitoAsignatura")
	public ResponseEntity<List<RequisitoAsignaturaDTO>> mostrarTodo() {
		return ResponseEntity.ok(requisitoAsignaturaSer.getAll());
	}

	@GetMapping("/{id}")
	@Operation(summary = "Obtener RequisitoAsignatura por ID")
	public ResponseEntity<RequisitoAsignaturaDTO> buscarPorId(@PathVariable Long id) {
		return ResponseEntity.ok(requisitoAsignaturaSer.getById(id));
	}

	@PutMapping("/{id}")
	@Operation(summary = "Actualizar RequisitoAsignatura")
	public ResponseEntity<String> actualizar(@PathVariable Long id, @RequestBody RequisitoAsignaturaDTO data) {
		requisitoAsignaturaSer.updateById(id, data);
		return ResponseEntity.ok("RequisitoAsignatura actualizado");
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "Eliminar RequisitoAsignatura; no elimina recursos relacionados en cascada")
	@ApiResponse(responseCode = "204", description = "Recurso eliminado")
	public ResponseEntity<String> delete(@PathVariable Long id) {
		requisitoAsignaturaSer.deleteById(id);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/cantidad")
	public ResponseEntity<Long> count() {
		return ResponseEntity.ok(requisitoAsignaturaSer.count());
	}

	@GetMapping("/{id}/existe")
	public ResponseEntity<Boolean> exist(@PathVariable Long id) {
		return ResponseEntity.ok(requisitoAsignaturaSer.exist(id));
	}

	@GetMapping("/por-asignatura")
	@Operation(summary = "Buscar RequisitoAsignatura por Asignatura")
	public ResponseEntity<List<RequisitoAsignaturaDTO>> buscarPorAsignatura(@RequestParam("valor") Long valor) {
		return ResponseEntity.ok(requisitoAsignaturaSer.findByAsignaturaId(valor));
	}

	@GetMapping("/por-tipo-requisito")
	@Operation(summary = "Buscar RequisitoAsignatura por TipoRequisito")
	public ResponseEntity<List<RequisitoAsignaturaDTO>> buscarPorTipoRequisito(
			@RequestParam("valor") TipoRequisito valor) {
		return ResponseEntity.ok(requisitoAsignaturaSer.findByTipoRequisito(valor));
	}

	@GetMapping("/por-area-conocimiento")
	@Operation(summary = "Buscar RequisitoAsignatura por AreaConocimiento")
	public ResponseEntity<List<RequisitoAsignaturaDTO>> buscarPorAreaConocimiento(@RequestParam("valor") String valor) {
		return ResponseEntity.ok(requisitoAsignaturaSer.findByAreaConocimiento(valor));
	}

	@PostMapping("/asignatura/{id}")
	@Operation(summary = "Registrar RequisitoAsignatura en Asignatura")
	@ApiResponse(responseCode = "201", description = "Recurso asociado y creado")
	public ResponseEntity<String> registrar(@PathVariable Long id, @RequestBody RequisitoAsignaturaDTO data) {
		requisitoAsignaturaSer.registrarRequisitoAsignatura(id, data);
		return ResponseEntity.status(201).body("RequisitoAsignatura registrado");
	}

	/** Sobrecarga escalar del UML; la API HTTP utiliza el DTO documentado. */
	public ResponseEntity<String> crearRequisito(Long asignaturaId, TipoRequisito tipoRequisito, String descripcion,
			String areaConocimiento, NivelFormacion nivelFormacionMinimo, int aniosExperienciaMinima) {
		RequisitoAsignaturaDTO data = new RequisitoAsignaturaDTO(tipoRequisito, descripcion, areaConocimiento,
				nivelFormacionMinimo, aniosExperienciaMinima);
		data.setAsignaturaId(asignaturaId);
		return crearRequisito(data);
	}

	public ResponseEntity<String> actualizar(Long id, TipoRequisito tipoRequisito, String descripcion,
			String areaConocimiento, NivelFormacion nivelFormacionMinimo, int aniosExperienciaMinima) {
		return actualizar(id, new RequisitoAsignaturaDTO(tipoRequisito, descripcion, areaConocimiento,
				nivelFormacionMinimo, aniosExperienciaMinima));
	}
}
