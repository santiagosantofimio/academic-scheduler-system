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
@RequestMapping("/api/asignaturas")
@Tag(name = "Asignatura")
@ApiResponses({ @ApiResponse(responseCode = "400", description = "Datos invalidos"),
		@ApiResponse(responseCode = "401", description = "Autenticacion requerida"),
		@ApiResponse(responseCode = "403", description = "Sin permiso o token CSRF"),
		@ApiResponse(responseCode = "404", description = "Recurso no encontrado"),
		@ApiResponse(responseCode = "409", description = "Conflicto de integridad o programacion") })
public class AsignaturaController {
	private final AsignaturaService asignaturaSer;

	public AsignaturaController(AsignaturaService asignaturaSer) {
		this.asignaturaSer = asignaturaSer;
	}

	@PostMapping
	@Operation(summary = "Crear Asignatura")
	@ApiResponse(responseCode = "201", description = "Recurso creado")
	public ResponseEntity<String> crearAsignatura(@RequestBody AsignaturaDTO data) {
		asignaturaSer.create(data);
		return ResponseEntity.status(201).body("Asignatura creado");
	}

	@GetMapping
	@Operation(summary = "Listar Asignatura")
	public ResponseEntity<List<AsignaturaDTO>> mostrarTodo() {
		return ResponseEntity.ok(asignaturaSer.getAll());
	}

	@GetMapping("/{id}")
	@Operation(summary = "Obtener Asignatura por ID")
	public ResponseEntity<AsignaturaDTO> buscarPorId(@PathVariable Long id) {
		return ResponseEntity.ok(asignaturaSer.getById(id));
	}

	@PutMapping("/{id}")
	@Operation(summary = "Actualizar Asignatura")
	public ResponseEntity<String> actualizar(@PathVariable Long id, @RequestBody AsignaturaDTO data) {
		asignaturaSer.updateById(id, data);
		return ResponseEntity.ok("Asignatura actualizado");
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "Eliminar Asignatura; no elimina recursos relacionados en cascada")
	@ApiResponse(responseCode = "204", description = "Recurso eliminado")
	public ResponseEntity<String> delete(@PathVariable Long id) {
		asignaturaSer.deleteById(id);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/cantidad")
	public ResponseEntity<Long> count() {
		return ResponseEntity.ok(asignaturaSer.count());
	}

	@GetMapping("/{id}/existe")
	public ResponseEntity<Boolean> exist(@PathVariable Long id) {
		return ResponseEntity.ok(asignaturaSer.exist(id));
	}

	@GetMapping("/por-codigo")
	@Operation(summary = "Buscar Asignatura por Codigo")
	public ResponseEntity<List<AsignaturaDTO>> buscarPorCodigo(@RequestParam("valor") String valor) {
		return ResponseEntity.ok(asignaturaSer.findByCodigo(valor));
	}

	@GetMapping("/por-nombre")
	@Operation(summary = "Buscar Asignatura por Nombre")
	public ResponseEntity<List<AsignaturaDTO>> buscarPorNombre(@RequestParam("valor") String valor) {
		return ResponseEntity.ok(asignaturaSer.findByNombre(valor));
	}

	@GetMapping("/por-semestre")
	@Operation(summary = "Buscar Asignatura por Semestre")
	public ResponseEntity<List<AsignaturaDTO>> buscarPorSemestre(@RequestParam("valor") int valor) {
		return ResponseEntity.ok(asignaturaSer.findBySemestre(valor));
	}

	@GetMapping("/por-area-conocimiento")
	@Operation(summary = "Buscar Asignatura por AreaConocimiento")
	public ResponseEntity<List<AsignaturaDTO>> buscarPorAreaConocimiento(@RequestParam("valor") String valor) {
		return ResponseEntity.ok(asignaturaSer.findByAreaConocimiento(valor));
	}

	@GetMapping("/por-estado")
	@Operation(summary = "Buscar Asignatura por Estado")
	public ResponseEntity<List<AsignaturaDTO>> buscarPorEstado(@RequestParam("valor") boolean valor) {
		return ResponseEntity.ok(asignaturaSer.findByEstado(valor));
	}

	@GetMapping("/por-plan-estudios")
	@Operation(summary = "Buscar Asignatura por PlanEstudios")
	public ResponseEntity<List<AsignaturaDTO>> buscarPorPlanEstudios(@RequestParam("valor") Long valor) {
		return ResponseEntity.ok(asignaturaSer.findByPlanEstudiosId(valor));
	}

	@PostMapping("/planEstudios/{id}")
	@Operation(summary = "Registrar Asignatura en PlanEstudios")
	@ApiResponse(responseCode = "201", description = "Recurso asociado y creado")
	public ResponseEntity<String> registrar(@PathVariable Long id, @RequestBody AsignaturaDTO data) {
		asignaturaSer.registrarAsignaturaEnPlan(id, data);
		return ResponseEntity.status(201).body("Asignatura registrado");
	}

	/** Sobrecarga escalar del UML; la API HTTP utiliza el DTO documentado. */
	public ResponseEntity<String> crearAsignatura(Long planEstudiosId, String codigo, String nombre, int semestre,
			int creditos, int sesionesSemanales, String areaConocimiento, boolean estado) {
		AsignaturaDTO data = new AsignaturaDTO(codigo, nombre, semestre, creditos, sesionesSemanales, areaConocimiento,
				estado);
		data.setPlanEstudiosId(planEstudiosId);
		return crearAsignatura(data);
	}

	public ResponseEntity<String> actualizar(Long id, String codigo, String nombre, int semestre, int creditos,
			int sesionesSemanales, String areaConocimiento, boolean estado) {
		return actualizar(id,
				new AsignaturaDTO(codigo, nombre, semestre, creditos, sesionesSemanales, areaConocimiento, estado));
	}
}
