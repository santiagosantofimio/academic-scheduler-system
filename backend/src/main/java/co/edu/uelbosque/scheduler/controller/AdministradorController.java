package co.edu.uelbosque.scheduler.controller;


import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import co.edu.uelbosque.scheduler.dto.*;
import co.edu.uelbosque.scheduler.service.*;

@RestController
@RequestMapping("/api/administradores")
@Tag(name = "Administrador")
@ApiResponses({ @ApiResponse(responseCode = "400", description = "Datos invalidos"),
		@ApiResponse(responseCode = "401", description = "Autenticacion requerida"),
		@ApiResponse(responseCode = "403", description = "Sin permiso o token CSRF"),
		@ApiResponse(responseCode = "404", description = "Recurso no encontrado"),
		@ApiResponse(responseCode = "409", description = "Conflicto de integridad o programacion") })

public class AdministradorController {
	private final AdministradorService administradorSer;

	public AdministradorController(AdministradorService administradorSer) {
		this.administradorSer = administradorSer;
	}

	@PostMapping
	@Operation(summary = "Crear Administrador")
	@ApiResponse(responseCode = "201", description = "Recurso creado")
	public ResponseEntity<String> crearAdministrador(@RequestBody AdministradorDTO data) {
		administradorSer.create(data);
		return ResponseEntity.status(201).body("Administrador creado");
	}

	@GetMapping
	@Operation(summary = "Listar Administrador")
	public ResponseEntity<List<AdministradorDTO>> mostrarTodo() {
		return ResponseEntity.ok(administradorSer.getAll());
	}

	@GetMapping("/{id}")
	@Operation(summary = "Obtener Administrador por ID")
	public ResponseEntity<AdministradorDTO> buscarPorId(@PathVariable Long id) {
		return ResponseEntity.ok(administradorSer.getById(id));
	}

	@PutMapping("/{id}")
	@Operation(summary = "Actualizar Administrador")
	public ResponseEntity<String> actualizar(@PathVariable Long id, @RequestBody AdministradorDTO data) {
		administradorSer.updateById(id, data);
		return ResponseEntity.ok("Administrador actualizado");
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "Eliminar Administrador; no elimina recursos relacionados en cascada")
	@ApiResponse(responseCode = "204", description = "Recurso eliminado")
	public ResponseEntity<String> delete(@PathVariable Long id) {
		administradorSer.deleteById(id);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/cantidad")
	public ResponseEntity<Long> count() {
		return ResponseEntity.ok(administradorSer.count());
	}

	@GetMapping("/{id}/existe")
	public ResponseEntity<Boolean> exist(@PathVariable Long id) {
		return ResponseEntity.ok(administradorSer.exist(id));
	}

	@GetMapping("/por-nombre")
	@Operation(summary = "Buscar Administrador por Nombre")
	public ResponseEntity<List<AdministradorDTO>> buscarPorNombre(@RequestParam("valor") String valor) {
		return ResponseEntity.ok(administradorSer.findByNombre(valor));
	}

	@GetMapping("/por-numero-documento")
	@Operation(summary = "Buscar Administrador por NumeroDocumento")
	public ResponseEntity<List<AdministradorDTO>> buscarPorNumeroDocumento(@RequestParam("valor") String valor) {
		return ResponseEntity.ok(administradorSer.findByNumeroDocumento(valor));
	}

	@GetMapping("/por-correo-institucional")
	@Operation(summary = "Buscar Administrador por CorreoInstitucional")
	public ResponseEntity<List<AdministradorDTO>> buscarPorCorreoInstitucional(@RequestParam("valor") String valor) {
		return ResponseEntity.ok(administradorSer.findByCorreoInstitucional(valor));
	}

	@GetMapping("/por-cargo")
	@Operation(summary = "Buscar Administrador por Cargo")
	public ResponseEntity<List<AdministradorDTO>> buscarPorCargo(@RequestParam("valor") String valor) {
		return ResponseEntity.ok(administradorSer.findByCargo(valor));
	}

	/** Sobrecarga escalar del UML; la API HTTP utiliza el DTO documentado. */
	public ResponseEntity<String> crearAdministrador(String nombre, String apellido, String numeroDocumento,
			String correoInstitucional, String contrasenia, boolean estado, String cargo) {
		AdministradorDTO data = new AdministradorDTO(nombre, apellido, numeroDocumento, correoInstitucional,
				contrasenia, estado, cargo);
		return crearAdministrador(data);
	}

	public ResponseEntity<String> actualizar(Long id, String nombre, String apellido, String numeroDocumento,
			String correoInstitucional, String contrasenia, boolean estado, String cargo) {
		return actualizar(id, new AdministradorDTO(nombre, apellido, numeroDocumento, correoInstitucional, contrasenia,
				estado, cargo));
	}
}
