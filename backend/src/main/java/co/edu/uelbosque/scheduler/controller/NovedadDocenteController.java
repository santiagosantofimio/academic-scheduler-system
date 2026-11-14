package co.edu.uelbosque.scheduler.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import co.edu.uelbosque.scheduler.dto.ClaseAfectadaDTO;
import co.edu.uelbosque.scheduler.dto.NovedadDocenteDTO;
import co.edu.uelbosque.scheduler.service.NovedadDocenteService;

@RestController
@RequestMapping("/api/novedades")
@Tag(name = "NovedadDocente")
public class NovedadDocenteController {
	private final NovedadDocenteService novedades;

	public NovedadDocenteController(NovedadDocenteService novedades) {
		this.novedades = novedades;
	}

	@GetMapping
	@Operation(summary = "Listar novedades de docentes")
	public ResponseEntity<List<NovedadDocenteDTO>> listar() {
		return ResponseEntity.ok(novedades.listar());
	}

	@PostMapping
	@Operation(summary = "Registrar una novedad; mientras este abierta, el docente no se programa en esa franja")
	public ResponseEntity<NovedadDocenteDTO> registrar(@RequestBody NovedadDocenteDTO data) {
		return ResponseEntity.status(201).body(novedades.registrar(data));
	}

	@GetMapping("/{id}/afectadas")
	@Operation(summary = "Clases afectadas por la novedad con franjas validas para reprogramarlas")
	public ResponseEntity<List<ClaseAfectadaDTO>> afectadas(@PathVariable Long id) {
		return ResponseEntity.ok(novedades.afectadas(id));
	}

	@PostMapping("/{id}/cerrar")
	@Operation(summary = "Cerrar una novedad")
	public ResponseEntity<NovedadDocenteDTO> cerrar(@PathVariable Long id) {
		return ResponseEntity.ok(novedades.cerrar(id));
	}
}
