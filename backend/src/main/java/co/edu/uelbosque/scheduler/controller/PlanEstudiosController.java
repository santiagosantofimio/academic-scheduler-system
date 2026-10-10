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
@RequestMapping("/api/planes-estudios")
@Tag(name = "PlanEstudios")
@ApiResponses({ @ApiResponse(responseCode = "400", description = "Datos invalidos"),
		@ApiResponse(responseCode = "401", description = "Autenticacion requerida"),
		@ApiResponse(responseCode = "403", description = "Sin permiso o token CSRF"),
		@ApiResponse(responseCode = "404", description = "Recurso no encontrado"),
		@ApiResponse(responseCode = "409", description = "Conflicto de integridad o programacion") })
public class PlanEstudiosController {
	private final PlanEstudiosService planEstudiosSer;

	public PlanEstudiosController(PlanEstudiosService planEstudiosSer) {
		this.planEstudiosSer = planEstudiosSer;
	}

	@PostMapping
	@Operation(summary = "Crear PlanEstudios")
	@ApiResponse(responseCode = "201", description = "Recurso creado")
	public ResponseEntity<String> crearPlan(@RequestBody PlanEstudiosDTO data) {
		planEstudiosSer.create(data);
		return ResponseEntity.status(201).body("PlanEstudios creado");
	}

	@GetMapping
	@Operation(summary = "Listar PlanEstudios")
	public ResponseEntity<List<PlanEstudiosDTO>> mostrarTodo() {
		return ResponseEntity.ok(planEstudiosSer.getAll());
	}

	@GetMapping("/{id}")
	@Operation(summary = "Obtener PlanEstudios por ID")
	public ResponseEntity<PlanEstudiosDTO> buscarPorId(@PathVariable Long id) {
		return ResponseEntity.ok(planEstudiosSer.getById(id));
	}

	@PutMapping("/{id}")
	@Operation(summary = "Actualizar PlanEstudios")
	public ResponseEntity<String> actualizar(@PathVariable Long id, @RequestBody PlanEstudiosDTO data) {
		planEstudiosSer.updateById(id, data);
		return ResponseEntity.ok("PlanEstudios actualizado");
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "Eliminar PlanEstudios; no elimina recursos relacionados en cascada")
	@ApiResponse(responseCode = "204", description = "Recurso eliminado")
	public ResponseEntity<String> delete(@PathVariable Long id) {
		planEstudiosSer.deleteById(id);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/cantidad")
	public ResponseEntity<Long> count() {
		return ResponseEntity.ok(planEstudiosSer.count());
	}

	@GetMapping("/{id}/existe")
	public ResponseEntity<Boolean> exist(@PathVariable Long id) {
		return ResponseEntity.ok(planEstudiosSer.exist(id));
	}

	@GetMapping("/por-nombre-programa")
	@Operation(summary = "Buscar PlanEstudios por NombrePrograma")
	public ResponseEntity<List<PlanEstudiosDTO>> buscarPorNombrePrograma(@RequestParam("valor") String valor) {
		return ResponseEntity.ok(planEstudiosSer.findByNombrePrograma(valor));
	}

	@GetMapping("/por-codigo-programa")
	@Operation(summary = "Buscar PlanEstudios por CodigoPrograma")
	public ResponseEntity<List<PlanEstudiosDTO>> buscarPorCodigoPrograma(@RequestParam("valor") String valor) {
		return ResponseEntity.ok(planEstudiosSer.findByCodigoPrograma(valor));
	}

	@GetMapping("/por-version-plan")
	@Operation(summary = "Buscar PlanEstudios por VersionPlan")
	public ResponseEntity<List<PlanEstudiosDTO>> buscarPorVersionPlan(@RequestParam("valor") String valor) {
		return ResponseEntity.ok(planEstudiosSer.findByVersionPlan(valor));
	}

	@GetMapping("/por-estado")
	@Operation(summary = "Buscar PlanEstudios por Estado")
	public ResponseEntity<List<PlanEstudiosDTO>> buscarPorEstado(@RequestParam("valor") boolean valor) {
		return ResponseEntity.ok(planEstudiosSer.findByEstado(valor));
	}

	/** Sobrecarga escalar del UML; la API HTTP utiliza el DTO documentado. */
	public ResponseEntity<String> crearPlan(String nombrePrograma, String codigoPrograma, String versionPlan,
			int numerosSemestres, boolean estado) {
		PlanEstudiosDTO data = new PlanEstudiosDTO(nombrePrograma, codigoPrograma, versionPlan, numerosSemestres,
				estado);
		return crearPlan(data);
	}

	public ResponseEntity<String> actualizar(Long id, String nombrePrograma, String codigoPrograma, String versionPlan,
			int numerosSemestres, boolean estado) {
		return actualizar(id,
				new PlanEstudiosDTO(nombrePrograma, codigoPrograma, versionPlan, numerosSemestres, estado));
	}
}
