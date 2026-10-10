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
@RequestMapping("/api/sesiones")
@Tag(name = "SesionHorario")
@ApiResponses({ @ApiResponse(responseCode = "400", description = "Datos invalidos"),
		@ApiResponse(responseCode = "401", description = "Autenticacion requerida"),
		@ApiResponse(responseCode = "403", description = "Sin permiso o token CSRF"),
		@ApiResponse(responseCode = "404", description = "Recurso no encontrado"),
		@ApiResponse(responseCode = "409", description = "Conflicto de integridad o programacion") })
public class SesionHorarioController {
	private final SesionHorarioService sesionHorarioSer;

	public SesionHorarioController(SesionHorarioService sesionHorarioSer) {
		this.sesionHorarioSer = sesionHorarioSer;
	}

	@PostMapping
	@Operation(summary = "Crear SesionHorario")
	@ApiResponse(responseCode = "201", description = "Recurso creado")
	public ResponseEntity<String> crearSesion(@RequestBody SesionHorarioDTO data) {
		sesionHorarioSer.create(data);
		return ResponseEntity.status(201).body("SesionHorario creado");
	}

	@GetMapping
	@Operation(summary = "Listar SesionHorario")
	public ResponseEntity<List<SesionHorarioDTO>> mostrarTodo() {
		return ResponseEntity.ok(sesionHorarioSer.getAll());
	}

	@GetMapping("/{id}")
	@Operation(summary = "Obtener SesionHorario por ID")
	public ResponseEntity<SesionHorarioDTO> buscarPorId(@PathVariable Long id) {
		return ResponseEntity.ok(sesionHorarioSer.getById(id));
	}

	@PutMapping("/{id}")
	@Operation(summary = "Actualizar SesionHorario")
	public ResponseEntity<String> actualizar(@PathVariable Long id, @RequestBody SesionHorarioDTO data) {
		sesionHorarioSer.updateById(id, data);
		return ResponseEntity.ok("SesionHorario actualizado");
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "Eliminar SesionHorario; no elimina recursos relacionados en cascada")
	@ApiResponse(responseCode = "204", description = "Recurso eliminado")
	public ResponseEntity<String> delete(@PathVariable Long id) {
		sesionHorarioSer.deleteById(id);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/cantidad")
	public ResponseEntity<Long> count() {
		return ResponseEntity.ok(sesionHorarioSer.count());
	}

	@GetMapping("/{id}/existe")
	public ResponseEntity<Boolean> exist(@PathVariable Long id) {
		return ResponseEntity.ok(sesionHorarioSer.exist(id));
	}

	@GetMapping("/por-grupo")
	@Operation(summary = "Buscar SesionHorario por Grupo")
	public ResponseEntity<List<SesionHorarioDTO>> buscarPorGrupo(@RequestParam("valor") Long valor) {
		return ResponseEntity.ok(sesionHorarioSer.findByGrupoId(valor));
	}

	@GetMapping("/por-dia-semana")
	@Operation(summary = "Buscar SesionHorario por DiaSemana")
	public ResponseEntity<List<SesionHorarioDTO>> buscarPorDiaSemana(@RequestParam("valor") DayOfWeek valor) {
		return ResponseEntity.ok(sesionHorarioSer.findByDiaSemana(valor));
	}

	@GetMapping("/por-docente")
	@Operation(summary = "Buscar SesionHorario por Docente")
	public ResponseEntity<List<SesionHorarioDTO>> buscarPorDocente(@RequestParam("valor") Long valor) {
		return ResponseEntity.ok(sesionHorarioSer.findByDocenteId(valor));
	}

	@PostMapping("/grupo/{id}")
	@Operation(summary = "Registrar SesionHorario en Grupo")
	@ApiResponse(responseCode = "201", description = "Recurso asociado y creado")
	public ResponseEntity<String> registrar(@PathVariable Long id, @RequestBody SesionHorarioDTO data) {
		sesionHorarioSer.registrarSesion(id, data);
		return ResponseEntity.status(201).body("SesionHorario registrado");
	}

	/** Sobrecarga escalar del UML; la API HTTP utiliza el DTO documentado. */
	public ResponseEntity<String> crearSesion(Long grupoId, DayOfWeek diaSemana, LocalTime horaInicio,
			LocalTime horaFin, EstadoSesion estado) {
		SesionHorarioDTO data = new SesionHorarioDTO(diaSemana, horaInicio, horaFin, estado);
		data.setGrupoId(grupoId);
		return crearSesion(data);
	}

	public ResponseEntity<String> actualizar(Long id, DayOfWeek diaSemana, LocalTime horaInicio, LocalTime horaFin,
			EstadoSesion estado) {
		return actualizar(id, new SesionHorarioDTO(diaSemana, horaInicio, horaFin, estado));
	}

	@PostMapping("/grupo/{id}/generar")
	@Operation(summary = "Completar el horario del grupo", description = "Conserva sesiones programadas y agrega solo las faltantes. Si no existe solucion completa responde 409 sin guardar resultados parciales.")
	public ResponseEntity<List<SesionHorarioDTO>> generarHorarioGrupo(@PathVariable Long id) {
		return ResponseEntity.ok(sesionHorarioSer.generarHorarioGrupo(id));
	}
}
