package co.edu.uelbosque.scheduler.service;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import co.edu.uelbosque.scheduler.entity.*;
import co.edu.uelbosque.scheduler.repository.SesionHorarioRepository;

@Service
@Transactional(readOnly = true)
public class ExportacionHorarioService {
	private static final List<String> COLUMNAS = List.of("Dia", "Inicio", "Fin", "Codigo", "Asignatura", "Grupo",
			"Docente");
	private static final String SEPARADOR = ";";
	private static final String MARCA_UTF8 = "\uFEFF";
	private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm");
	private static final Locale ESPANOL = Locale.of("es");

	private final SesionHorarioRepository sesiones;

	public ExportacionHorarioService(SesionHorarioRepository sesiones) {
		this.sesiones = sesiones;
	}

	public byte[] csv(Long grupo, Long docente) {
		StringBuilder contenido = new StringBuilder(MARCA_UTF8);
		contenido.append(String.join(SEPARADOR, COLUMNAS)).append("\r\n");
		for (List<String> fila : filas(grupo, docente))
			contenido.append(fila.stream().map(ExportacionHorarioService::celdaCsv)
					.collect(Collectors.joining(SEPARADOR))).append("\r\n");
		return contenido.toString().getBytes(StandardCharsets.UTF_8);
	}

	public byte[] pdf(Long grupo, Long docente) {
		ByteArrayOutputStream salida = new ByteArrayOutputStream();
		Document documento = new Document(PageSize.A4.rotate(), 36, 36, 36, 36);
		PdfWriter.getInstance(documento, salida);
		documento.open();
		Paragraph titulo = new Paragraph("Horario academico", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16));
		titulo.setSpacingAfter(12);
		documento.add(titulo);
		PdfPTable tabla = new PdfPTable(COLUMNAS.size());
		tabla.setWidthPercentage(100);
		tabla.setHeaderRows(1);
		Font encabezado = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9);
		Font cuerpo = FontFactory.getFont(FontFactory.HELVETICA, 9);
		COLUMNAS.forEach(columna -> tabla.addCell(celdaPdf(columna, encabezado)));
		filas(grupo, docente).forEach(fila -> fila.forEach(valor -> tabla.addCell(celdaPdf(valor, cuerpo))));
		documento.add(tabla);
		documento.close();
		return salida.toByteArray();
	}

	private List<List<String>> filas(Long grupo, Long docente) {
		return sesiones.findByEstado(EstadoSesion.PROGRAMADA).orElseGet(List::of).stream()
				.filter(s -> grupo == null || grupo.equals(s.getGrupo().getIdGrupo()))
				.filter(s -> docente == null || (s.getGrupo().getDocente() != null
						&& docente.equals(s.getGrupo().getDocente().getId())))
				.sorted(Comparator.comparing(SesionHorario::getDiaSemana).thenComparing(SesionHorario::getHoraInicio))
				.map(ExportacionHorarioService::fila).toList();
	}

	private static List<String> fila(SesionHorario sesion) {
		Grupo grupo = sesion.getGrupo();
		Docente docente = grupo.getDocente();
		String dia = sesion.getDiaSemana().getDisplayName(TextStyle.FULL, ESPANOL);
		return List.of(dia.substring(0, 1).toUpperCase(ESPANOL) + dia.substring(1),
				sesion.getHoraInicio().format(HORA), sesion.getHoraFin().format(HORA),
				grupo.getAsignatura().getCodigo(), grupo.getAsignatura().getNombre(), grupo.getNumeroGrupo(),
				docente == null ? "" : docente.getNombre() + " " + docente.getApellido());
	}

	private static String celdaCsv(String valor) {
		String seguro = !valor.isEmpty() && "=+-@\t\r".indexOf(valor.charAt(0)) >= 0 ? "'" + valor : valor;
		boolean entrecomillar = seguro.contains(SEPARADOR) || seguro.contains("\"") || seguro.contains("\n")
				|| seguro.contains("\r");
		return entrecomillar ? "\"" + seguro.replace("\"", "\"\"") + "\"" : seguro;
	}

	private static PdfPCell celdaPdf(String valor, Font fuente) {
		PdfPCell celda = new PdfPCell(new Phrase(valor, fuente));
		celda.setPadding(4);
		celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
		return celda;
	}
}
