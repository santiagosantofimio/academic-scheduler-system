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
@RequestMapping("/api/formaciones")
@Tag(name = "FormacionAcademica")
@ApiResponses({ @ApiResponse(responseCode = "400", description = "Datos invalidos"),
		@ApiResponse(responseCode = "401", description = "Autenticacion requerida"),
		@ApiResponse(responseCode = "403", description = "Sin permiso o token CSRF"),
		@ApiResponse(responseCode = "404", description = "Recurso no encontrado"),
		@ApiResponse(responseCode = "409", description = "Conflicto de integridad o programacion") })
public class FormacionAcademicaController {
	private final FormacionAcademicaService formacionAcademicaSer;

	public FormacionAcademicaController(FormacionAcademicaService formacionAcademicaSer) {
		this.formacionAcademicaSer = formacionAcademicaSer;
	}

	@PostMapping
	@Operation(summary = "Crear FormacionAcademica")
	@ApiResponse(responseCode = "201", description = "Recurso creado")
	public ResponseEntity<String> crearFormacion(@RequestBody FormacionAcademicaDTO data) {
		formacionAcademicaSer.create(data);
		return ResponseEntity.status(201).body("FormacionAcademica creado");
	}

	@GetMapping
	@Operation(summary = "Listar FormacionAcademica")
	public ResponseEntity<List<FormacionAcademicaDTO>> mostrarTodo() {
		return ResponseEntity.ok(formacionAcademicaSer.getAll());
	}

	@GetMapping("/{id}")
	@Operation(summary = "Obtener FormacionAcademica por ID")
	public ResponseEntity<FormacionAcademicaDTO> buscarPorId(@PathVariable Long id) {
		return ResponseEntity.ok(formacionAcademicaSer.getById(id));
	}

	@PutMapping("/{id}")
	@Operation(summary = "Actualizar FormacionAcademica")
	public ResponseEntity<String> actualizar(@PathVariable Long id, @RequestBody FormacionAcademicaDTO data) {
		formacionAcademicaSer.updateById(id, data);
		return ResponseEntity.ok("FormacionAcademica actualizado");
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "Eliminar FormacionAcademica; no elimina recursos relacionados en cascada")
	@ApiResponse(responseCode = "204", description = "Recurso eliminado")
	public ResponseEntity<String> delete(@PathVariable Long id) {
		formacionAcademicaSer.deleteById(id);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/cantidad")
	public ResponseEntity<Long> count() {
		return ResponseEntity.ok(formacionAcademicaSer.count());
	}

	@GetMapping("/{id}/existe")
	public ResponseEntity<Boolean> exist(@PathVariable Long id) {
		return ResponseEntity.ok(formacionAcademicaSer.exist(id));
	}

	@GetMapping("/por-docente")
	@Operation(summary = "Buscar FormacionAcademica por Docente")
	public ResponseEntity<List<FormacionAcademicaDTO>> buscarPorDocente(@RequestParam("valor") Long valor) {
		return ResponseEntity.ok(formacionAcademicaSer.findByDocenteId(valor));
	}

	@GetMapping("/por-nivel-formacion")
	@Operation(summary = "Buscar FormacionAcademica por NivelFormacion")
	public ResponseEntity<List<FormacionAcademicaDTO>> buscarPorNivelFormacion(
			@RequestParam("valor") NivelFormacion valor) {
		return ResponseEntity.ok(formacionAcademicaSer.findByNivelFormacion(valor));
	}

	@GetMapping("/por-institucion")
	@Operation(summary = "Buscar FormacionAcademica por Institucion")
	public ResponseEntity<List<FormacionAcademicaDTO>> buscarPorInstitucion(@RequestParam("valor") String valor) {
		return ResponseEntity.ok(formacionAcademicaSer.findByInstitucion(valor));
	}

	@GetMapping("/por-area-conocimiento")
	@Operation(summary = "Buscar FormacionAcademica por AreaConocimiento")
	public ResponseEntity<List<FormacionAcademicaDTO>> buscarPorAreaConocimiento(@RequestParam("valor") String valor) {
		return ResponseEntity.ok(formacionAcademicaSer.findByAreaConocimiento(valor));
	}

	@PostMapping("/docente/{id}")
	@Operation(summary = "Registrar FormacionAcademica en Docente")
	@ApiResponse(responseCode = "201", description = "Recurso asociado y creado")
	public ResponseEntity<String> registrar(@PathVariable Long id, @RequestBody FormacionAcademicaDTO data) {
		formacionAcademicaSer.registrarFormacionDocente(id, data);
		return ResponseEntity.status(201).body("FormacionAcademica registrado");
	}

	/** Sobrecarga escalar del UML; la API HTTP utiliza el DTO documentado. */
	public ResponseEntity<String> crearFormacion(Long docenteId, String tituloObtenido, NivelFormacion nivelFormacion,
			String institucion, String areaConocimiento, int anioGraduacion) {
		FormacionAcademicaDTO data = new FormacionAcademicaDTO(tituloObtenido, nivelFormacion, institucion,
				areaConocimiento, anioGraduacion);
		data.setDocenteId(docenteId);
		return crearFormacion(data);
	}

	public ResponseEntity<String> actualizar(Long id, String tituloObtenido, NivelFormacion nivelFormacion,
			String institucion, String areaConocimiento, int anioGraduacion) {
		return actualizar(id, new FormacionAcademicaDTO(tituloObtenido, nivelFormacion, institucion, areaConocimiento,
				anioGraduacion));
	}
}
