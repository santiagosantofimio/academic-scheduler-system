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
@RequestMapping("/api/configuracion-horarios")
@Tag(name = "ConfiguracionHorarios")
@ApiResponses({ @ApiResponse(responseCode = "400", description = "Datos invalidos"),
		@ApiResponse(responseCode = "401", description = "Autenticacion requerida"),
		@ApiResponse(responseCode = "403", description = "Sin permiso o token CSRF"),
		@ApiResponse(responseCode = "404", description = "Recurso no encontrado"),
		@ApiResponse(responseCode = "409", description = "Conflicto de integridad o programacion") })
public class ConfiguracionHorariosController {
	private final ConfiguracionHorariosService configuracionHorariosSer;

	public ConfiguracionHorariosController(ConfiguracionHorariosService configuracionHorariosSer) {
		this.configuracionHorariosSer = configuracionHorariosSer;
	}

	@PostMapping
	@Operation(summary = "Crear ConfiguracionHorarios")
	@ApiResponse(responseCode = "201", description = "Recurso creado")
	public ResponseEntity<String> crearConfiguracion(@RequestBody ConfiguracionHorariosDTO data) {
		configuracionHorariosSer.create(data);
		return ResponseEntity.status(201).body("ConfiguracionHorarios creado");
	}

	@GetMapping
	@Operation(summary = "Listar ConfiguracionHorarios")
	public ResponseEntity<List<ConfiguracionHorariosDTO>> mostrarTodo() {
		return ResponseEntity.ok(configuracionHorariosSer.getAll());
	}

	@GetMapping("/{id}")
	@Operation(summary = "Obtener ConfiguracionHorarios por ID")
	public ResponseEntity<ConfiguracionHorariosDTO> buscarPorId(@PathVariable Long id) {
		return ResponseEntity.ok(configuracionHorariosSer.getById(id));
	}

	@PutMapping("/{id}")
	@Operation(summary = "Actualizar ConfiguracionHorarios")
	public ResponseEntity<String> actualizar(@PathVariable Long id, @RequestBody ConfiguracionHorariosDTO data) {
		configuracionHorariosSer.updateById(id, data);
		return ResponseEntity.ok("ConfiguracionHorarios actualizado");
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "Eliminar ConfiguracionHorarios; no elimina recursos relacionados en cascada")
	@ApiResponse(responseCode = "204", description = "Recurso eliminado")
	public ResponseEntity<String> delete(@PathVariable Long id) {
		configuracionHorariosSer.deleteById(id);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/cantidad")
	public ResponseEntity<Long> count() {
		return ResponseEntity.ok(configuracionHorariosSer.count());
	}

	@GetMapping("/{id}/existe")
	public ResponseEntity<Boolean> exist(@PathVariable Long id) {
		return ResponseEntity.ok(configuracionHorariosSer.exist(id));
	}

	/** Sobrecarga escalar del UML; la API HTTP utiliza el DTO documentado. */
	public ResponseEntity<String> crearConfiguracion(LocalTime horaInicioSemana, LocalTime horaFinSemana,
			LocalTime horaInicioSabado, LocalTime horaFinSabado, LocalTime horaInicioDescanso,
			LocalTime horaFinDescanso, int duracionSesionMinutos, int maxSesionesSemanales,
			boolean evitarDiasConsecutivos) {
		ConfiguracionHorariosDTO data = new ConfiguracionHorariosDTO(horaInicioSemana, horaFinSemana, horaInicioSabado,
				horaFinSabado, horaInicioDescanso, horaFinDescanso, duracionSesionMinutos, maxSesionesSemanales,
				evitarDiasConsecutivos);
		return crearConfiguracion(data);
	}

	public ResponseEntity<String> actualizar(Long id, LocalTime horaInicioSemana, LocalTime horaFinSemana,
			LocalTime horaInicioSabado, LocalTime horaFinSabado, LocalTime horaInicioDescanso,
			LocalTime horaFinDescanso, int duracionSesionMinutos, int maxSesionesSemanales,
			boolean evitarDiasConsecutivos) {
		return actualizar(id,
				new ConfiguracionHorariosDTO(horaInicioSemana, horaFinSemana, horaInicioSabado, horaFinSabado,
						horaInicioDescanso, horaFinDescanso, duracionSesionMinutos, maxSesionesSemanales,
						evitarDiasConsecutivos));
	}

	@GetMapping("/actual")
	public ResponseEntity<ConfiguracionHorariosDTO> getConfiguracion() {
		return ResponseEntity.ok(configuracionHorariosSer.getConfiguracion());
	}
}
