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
@RequestMapping("/api/docentes")
@Tag(name = "Docente")
@ApiResponses({ @ApiResponse(responseCode = "400", description = "Datos invalidos"),
		@ApiResponse(responseCode = "401", description = "Autenticacion requerida"),
		@ApiResponse(responseCode = "403", description = "Sin permiso o token CSRF"),
		@ApiResponse(responseCode = "404", description = "Recurso no encontrado"),
		@ApiResponse(responseCode = "409", description = "Conflicto de integridad o programacion") })
public class DocenteController {
	private final DocenteService docenteSer;

	public DocenteController(DocenteService docenteSer) {
		this.docenteSer = docenteSer;
	}

	@PostMapping
	@Operation(summary = "Crear Docente")
	@ApiResponse(responseCode = "201", description = "Recurso creado")
	public ResponseEntity<String> crearDocente(@RequestBody DocenteDTO data) {
		docenteSer.create(data);
		return ResponseEntity.status(201).body("Docente creado");
	}

	@GetMapping
	@Operation(summary = "Listar Docente")
	public ResponseEntity<List<DocenteDTO>> mostrarTodo() {
		return ResponseEntity.ok(docenteSer.getAll());
	}

	@GetMapping("/{id}")
	@Operation(summary = "Obtener Docente por ID")
	public ResponseEntity<DocenteDTO> buscarPorId(@PathVariable Long id) {
		return ResponseEntity.ok(docenteSer.getById(id));
	}

	@PutMapping("/{id}")
	@Operation(summary = "Actualizar Docente")
	public ResponseEntity<String> actualizar(@PathVariable Long id, @RequestBody DocenteDTO data) {
		docenteSer.updateById(id, data);
		return ResponseEntity.ok("Docente actualizado");
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "Eliminar Docente; no elimina recursos relacionados en cascada")
	@ApiResponse(responseCode = "204", description = "Recurso eliminado")
	public ResponseEntity<String> delete(@PathVariable Long id) {
		docenteSer.deleteById(id);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/cantidad")
	public ResponseEntity<Long> count() {
		return ResponseEntity.ok(docenteSer.count());
	}

	@GetMapping("/{id}/existe")
	public ResponseEntity<Boolean> exist(@PathVariable Long id) {
		return ResponseEntity.ok(docenteSer.exist(id));
	}

	@GetMapping("/por-nombre")
	@Operation(summary = "Buscar Docente por Nombre")
	public ResponseEntity<List<DocenteDTO>> buscarPorNombre(@RequestParam("valor") String valor) {
		return ResponseEntity.ok(docenteSer.findByNombre(valor));
	}

	@GetMapping("/por-numero-documento")
	@Operation(summary = "Buscar Docente por NumeroDocumento")
	public ResponseEntity<List<DocenteDTO>> buscarPorNumeroDocumento(@RequestParam("valor") String valor) {
		return ResponseEntity.ok(docenteSer.findByNumeroDocumento(valor));
	}

	@GetMapping("/por-correo-institucional")
	@Operation(summary = "Buscar Docente por CorreoInstitucional")
	public ResponseEntity<List<DocenteDTO>> buscarPorCorreoInstitucional(@RequestParam("valor") String valor) {
		return ResponseEntity.ok(docenteSer.findByCorreoInstitucional(valor));
	}

	@GetMapping("/por-tipo-vinculacion")
	@Operation(summary = "Buscar Docente por TipoVinculacion")
	public ResponseEntity<List<DocenteDTO>> buscarPorTipoVinculacion(@RequestParam("valor") TipoVinculacion valor) {
		return ResponseEntity.ok(docenteSer.findByTipoVinculacion(valor));
	}

	@GetMapping("/por-especialidad")
	@Operation(summary = "Buscar Docente por Especialidad")
	public ResponseEntity<List<DocenteDTO>> buscarPorEspecialidad(@RequestParam("valor") String valor) {
		return ResponseEntity.ok(docenteSer.findByEspecialidad(valor));
	}

	@GetMapping("/por-escalafon")
	@Operation(summary = "Buscar Docente por Escalafon")
	public ResponseEntity<List<DocenteDTO>> buscarPorEscalafon(@RequestParam("valor") String valor) {
		return ResponseEntity.ok(docenteSer.findByEscalafon(valor));
	}

	/** Sobrecarga escalar del UML; la API HTTP utiliza el DTO documentado. */
	public ResponseEntity<String> crearDocente(String nombre, String apellido, String numeroDocumento,
			String correoInstitucional, String contrasenia, boolean estado, TipoVinculacion tipoVinculacion,
			String escalafon, String especialidad, int horasMaximasDocenciaSemanal) {
		DocenteDTO data = new DocenteDTO(nombre, apellido, numeroDocumento, correoInstitucional, contrasenia, estado,
				tipoVinculacion, escalafon, especialidad, horasMaximasDocenciaSemanal);
		return crearDocente(data);
	}

	public ResponseEntity<String> actualizar(Long id, String nombre, String apellido, String numeroDocumento,
			String correoInstitucional, String contrasenia, boolean estado, TipoVinculacion tipoVinculacion,
			String escalafon, String especialidad, int horasMaximasDocenciaSemanal) {
		return actualizar(id, new DocenteDTO(nombre, apellido, numeroDocumento, correoInstitucional, contrasenia,
				estado, tipoVinculacion, escalafon, especialidad, horasMaximasDocenciaSemanal));
	}
}
