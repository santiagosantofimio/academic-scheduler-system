package co.edu.uelbosque.scheduler.controller;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import co.edu.uelbosque.scheduler.dto.HorarioDocenteDTO;
import co.edu.uelbosque.scheduler.service.HorarioDocenteService;

@RestController
@RequestMapping("/api/mi-horario")
@Tag(name = "Mi horario")
public class MiHorarioController {
	private final HorarioDocenteService horarioSer;

	public MiHorarioController(HorarioDocenteService horarioSer) {
		this.horarioSer = horarioSer;
	}

	@GetMapping
	@Operation(summary = "Consultar el horario del docente autenticado")
	public ResponseEntity<HorarioDocenteDTO> consultar(Authentication autenticacion) {
		return ResponseEntity.ok(horarioSer.consultar(autenticacion.getName()));
	}

	@GetMapping("/exportar/csv")
	@Operation(summary = "Exportar el horario del docente autenticado en CSV")
	public ResponseEntity<byte[]> csv(Authentication autenticacion) {
		return archivo(horarioSer.csv(autenticacion.getName()), MediaType.parseMediaType("text/csv;charset=UTF-8"),
				"mi-horario.csv");
	}

	@GetMapping("/exportar/pdf")
	@Operation(summary = "Exportar el horario del docente autenticado en PDF")
	public ResponseEntity<byte[]> pdf(Authentication autenticacion) {
		return archivo(horarioSer.pdf(autenticacion.getName()), MediaType.APPLICATION_PDF, "mi-horario.pdf");
	}

	private static ResponseEntity<byte[]> archivo(byte[] contenido, MediaType tipo, String nombre) {
		return ResponseEntity.ok().contentType(tipo)
				.header(HttpHeaders.CONTENT_DISPOSITION,
						ContentDisposition.attachment().filename(nombre).build().toString())
				.body(contenido);
	}
}
