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
@RequestMapping("/api/grupos")
@Tag(name = "Grupo")
@ApiResponses({ @ApiResponse(responseCode = "400", description = "Datos invalidos"),
		@ApiResponse(responseCode = "401", description = "Autenticacion requerida"),
		@ApiResponse(responseCode = "403", description = "Sin permiso o token CSRF"),
		@ApiResponse(responseCode = "404", description = "Recurso no encontrado"),
		@ApiResponse(responseCode = "409", description = "Conflicto de integridad o programacion") })
public class GrupoController {
	private final GrupoService grupoSer;

	public GrupoController(GrupoService grupoSer) {
		this.grupoSer = grupoSer;
	}

	@PostMapping
	@Operation(summary = "Crear Grupo")
	@ApiResponse(responseCode = "201", description = "Recurso creado")
	public ResponseEntity<String> crearGrupo(@RequestBody GrupoDTO data) {
		grupoSer.create(data);
		return ResponseEntity.status(201).body("Grupo creado");
	}

	@GetMapping
	@Operation(summary = "Listar Grupo")
	public ResponseEntity<List<GrupoDTO>> mostrarTodo() {
		return ResponseEntity.ok(grupoSer.getAll());
	}

	@GetMapping("/{id}")
	@Operation(summary = "Obtener Grupo por ID")
	public ResponseEntity<GrupoDTO> buscarPorId(@PathVariable Long id) {
		return ResponseEntity.ok(grupoSer.getById(id));
	}

	@PutMapping("/{id}")
	@Operation(summary = "Actualizar Grupo")
	public ResponseEntity<String> actualizar(@PathVariable Long id, @RequestBody GrupoDTO data) {
		grupoSer.updateById(id, data);
		return ResponseEntity.ok("Grupo actualizado");
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "Eliminar Grupo; no elimina recursos relacionados en cascada")
	@ApiResponse(responseCode = "204", description = "Recurso eliminado")
	public ResponseEntity<String> delete(@PathVariable Long id) {
		grupoSer.deleteById(id);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/cantidad")
	public ResponseEntity<Long> count() {
		return ResponseEntity.ok(grupoSer.count());
	}

	@GetMapping("/{id}/existe")
	public ResponseEntity<Boolean> exist(@PathVariable Long id) {
		return ResponseEntity.ok(grupoSer.exist(id));
	}

	@GetMapping("/por-numero-grupo")
	@Operation(summary = "Buscar Grupo por NumeroGrupo")
	public ResponseEntity<List<GrupoDTO>> buscarPorNumeroGrupo(@RequestParam("valor") String valor) {
		return ResponseEntity.ok(grupoSer.findByNumeroGrupo(valor));
	}

	@GetMapping("/por-estado")
	@Operation(summary = "Buscar Grupo por Estado")
	public ResponseEntity<List<GrupoDTO>> buscarPorEstado(@RequestParam("valor") EstadoGrupo valor) {
		return ResponseEntity.ok(grupoSer.findByEstado(valor));
	}

	@GetMapping("/por-asignatura")
	@Operation(summary = "Buscar Grupo por Asignatura")
	public ResponseEntity<List<GrupoDTO>> buscarPorAsignatura(@RequestParam("valor") Long valor) {
		return ResponseEntity.ok(grupoSer.findByAsignaturaId(valor));
	}

	@PostMapping("/asignatura/{id}")
	@Operation(summary = "Registrar Grupo en Asignatura")
	@ApiResponse(responseCode = "201", description = "Recurso asociado y creado")
	public ResponseEntity<String> registrar(@PathVariable Long id, @RequestBody GrupoDTO data) {
		grupoSer.registrarGrupoAsignatura(id, data);
		return ResponseEntity.status(201).body("Grupo registrado");
	}

	/** Sobrecarga escalar del UML; la API HTTP utiliza el DTO documentado. */
	public ResponseEntity<String> crearGrupo(Long asignaturaId, String numeroGrupo, EstadoGrupo estado) {
		GrupoDTO data = new GrupoDTO(numeroGrupo, estado);
		data.setAsignaturaId(asignaturaId);
		return crearGrupo(data);
	}

	public ResponseEntity<String> actualizar(Long id, String numeroGrupo, EstadoGrupo estado) {
		return actualizar(id, new GrupoDTO(numeroGrupo, estado));
	}
}
