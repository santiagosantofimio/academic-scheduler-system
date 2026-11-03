package co.edu.uelbosque.scheduler.controller;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import co.edu.uelbosque.scheduler.service.ExportacionHorarioService;

@RestController
@RequestMapping("/api/sesiones/exportar")
@Tag(name = "SesionHorario")
public class ExportacionHorarioController {
	private static final MediaType TEXTO_CSV = MediaType.parseMediaType("text/csv;charset=UTF-8");

	private final ExportacionHorarioService exportacionSer;

	public ExportacionHorarioController(ExportacionHorarioService exportacionSer) {
		this.exportacionSer = exportacionSer;
	}

	@GetMapping("/csv")
	@Operation(summary = "Exportar sesiones programadas en CSV separado por punto y coma")
	public ResponseEntity<byte[]> csv(@RequestParam(required = false) Long grupo,
			@RequestParam(required = false) Long docente) {
		return archivo(exportacionSer.csv(grupo, docente), TEXTO_CSV, "horario.csv");
	}

	@GetMapping("/pdf")
	@Operation(summary = "Exportar sesiones programadas en PDF")
	public ResponseEntity<byte[]> pdf(@RequestParam(required = false) Long grupo,
			@RequestParam(required = false) Long docente) {
		return archivo(exportacionSer.pdf(grupo, docente), MediaType.APPLICATION_PDF, "horario.pdf");
	}

	private static ResponseEntity<byte[]> archivo(byte[] contenido, MediaType tipo, String nombre) {
		return ResponseEntity.ok().contentType(tipo)
				.header(HttpHeaders.CONTENT_DISPOSITION,
						ContentDisposition.attachment().filename(nombre).build().toString())
				.body(contenido);
	}
}
