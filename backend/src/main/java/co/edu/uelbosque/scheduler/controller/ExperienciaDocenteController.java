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
@RequestMapping("/api/experiencias")
@Tag(name = "ExperienciaDocente")
@ApiResponses({ @ApiResponse(responseCode = "400", description = "Datos invalidos"),
		@ApiResponse(responseCode = "401", description = "Autenticacion requerida"),
		@ApiResponse(responseCode = "403", description = "Sin permiso o token CSRF"),
		@ApiResponse(responseCode = "404", description = "Recurso no encontrado"),
		@ApiResponse(responseCode = "409", description = "Conflicto de integridad o programacion") })
public class ExperienciaDocenteController {
	private final ExperienciaDocenteService experienciaDocenteSer;

	public ExperienciaDocenteController(ExperienciaDocenteService experienciaDocenteSer) {
		this.experienciaDocenteSer = experienciaDocenteSer;
	}

	@PostMapping
	@Operation(summary = "Crear ExperienciaDocente")
	@ApiResponse(responseCode = "201", description = "Recurso creado")
	public ResponseEntity<String> crearExperiencia(@RequestBody ExperienciaDocenteDTO data) {
		experienciaDocenteSer.create(data);
		return ResponseEntity.status(201).body("ExperienciaDocente creado");
	}

	@GetMapping
	@Operation(summary = "Listar ExperienciaDocente")
	public ResponseEntity<List<ExperienciaDocenteDTO>> mostrarTodo() {
		return ResponseEntity.ok(experienciaDocenteSer.getAll());
	}

	@GetMapping("/{id}")
	@Operation(summary = "Obtener ExperienciaDocente por ID")
	public ResponseEntity<ExperienciaDocenteDTO> buscarPorId(@PathVariable Long id) {
		return ResponseEntity.ok(experienciaDocenteSer.getById(id));
	}

	@PutMapping("/{id}")
	@Operation(summary = "Actualizar ExperienciaDocente")
	public ResponseEntity<String> actualizar(@PathVariable Long id, @RequestBody ExperienciaDocenteDTO data) {
		experienciaDocenteSer.updateById(id, data);
		return ResponseEntity.ok("ExperienciaDocente actualizado");
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "Eliminar ExperienciaDocente; no elimina recursos relacionados en cascada")
	@ApiResponse(responseCode = "204", description = "Recurso eliminado")
	public ResponseEntity<String> delete(@PathVariable Long id) {
		experienciaDocenteSer.deleteById(id);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/cantidad")
	public ResponseEntity<Long> count() {
		return ResponseEntity.ok(experienciaDocenteSer.count());
	}

	@GetMapping("/{id}/existe")
	public ResponseEntity<Boolean> exist(@PathVariable Long id) {
		return ResponseEntity.ok(experienciaDocenteSer.exist(id));
	}

	@GetMapping("/por-docente")
	@Operation(summary = "Buscar ExperienciaDocente por Docente")
	public ResponseEntity<List<ExperienciaDocenteDTO>> buscarPorDocente(@RequestParam("valor") Long valor) {
		return ResponseEntity.ok(experienciaDocenteSer.findByDocenteId(valor));
	}

	@GetMapping("/por-tipo-experiencia")
	@Operation(summary = "Buscar ExperienciaDocente por TipoExperiencia")
	public ResponseEntity<List<ExperienciaDocenteDTO>> buscarPorTipoExperiencia(
			@RequestParam("valor") TipoExperiencia valor) {
		return ResponseEntity.ok(experienciaDocenteSer.findByTipoExperiencia(valor));
	}

	@GetMapping("/por-institucion")
	@Operation(summary = "Buscar ExperienciaDocente por Institucion")
	public ResponseEntity<List<ExperienciaDocenteDTO>> buscarPorInstitucion(@RequestParam("valor") String valor) {
		return ResponseEntity.ok(experienciaDocenteSer.findByInstitucion(valor));
	}

	@GetMapping("/por-area-conocimiento")
	@Operation(summary = "Buscar ExperienciaDocente por AreaConocimiento")
	public ResponseEntity<List<ExperienciaDocenteDTO>> buscarPorAreaConocimiento(@RequestParam("valor") String valor) {
		return ResponseEntity.ok(experienciaDocenteSer.findByAreaConocimiento(valor));
	}

	@PostMapping("/docente/{id}")
	@Operation(summary = "Registrar ExperienciaDocente en Docente")
	@ApiResponse(responseCode = "201", description = "Recurso asociado y creado")
	public ResponseEntity<String> registrar(@PathVariable Long id, @RequestBody ExperienciaDocenteDTO data) {
		experienciaDocenteSer.registrarExperienciaDocente(id, data);
		return ResponseEntity.status(201).body("ExperienciaDocente registrado");
	}

	/** Sobrecarga escalar del UML; la API HTTP utiliza el DTO documentado. */
	public ResponseEntity<String> crearExperiencia(Long docenteId, TipoExperiencia tipoExperiencia, String institucion,
			String cargo, String areaConocimiento, String descripcion, LocalDate fechaInicio, LocalDate fechaFin) {
		ExperienciaDocenteDTO data = new ExperienciaDocenteDTO(tipoExperiencia, institucion, cargo, areaConocimiento,
				descripcion, fechaInicio, fechaFin);
		data.setDocenteId(docenteId);
		return crearExperiencia(data);
	}

	public ResponseEntity<String> actualizar(Long id, TipoExperiencia tipoExperiencia, String institucion, String cargo,
			String areaConocimiento, String descripcion, LocalDate fechaInicio, LocalDate fechaFin) {
		return actualizar(id, new ExperienciaDocenteDTO(tipoExperiencia, institucion, cargo, areaConocimiento,
				descripcion, fechaInicio, fechaFin));
	}
}
