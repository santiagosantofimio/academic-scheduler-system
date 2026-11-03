package co.edu.uelbosque.scheduler.service;

import java.nio.charset.StandardCharsets;
import java.time.*;
import java.util.*;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.parser.PdfTextExtractor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import co.edu.uelbosque.scheduler.entity.*;
import co.edu.uelbosque.scheduler.repository.SesionHorarioRepository;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ExportacionHorarioServiceTest {
    private ExportacionHorarioService servicio;
    private Docente ana;

    @BeforeEach
    void preparar() {
        ana = new Docente(); ana.setId(20L); ana.setNombre("Ana"); ana.setApellido("Rios");
        Docente luis = new Docente(); luis.setId(21L); luis.setNombre("Luis"); luis.setApellido("Paz");
        var sesiones = mock(SesionHorarioRepository.class);
        when(sesiones.findByEstado(EstadoSesion.PROGRAMADA)).thenReturn(Optional.of(List.of(
                sesion(31L, "=SUMA(1;2)", luis, DayOfWeek.WEDNESDAY, 9),
                sesion(30L, "Calculo; \"avanzado\"", ana, DayOfWeek.WEDNESDAY, 7),
                sesion(30L, "Calculo; \"avanzado\"", ana, DayOfWeek.MONDAY, 9))));
        servicio = new ExportacionHorarioService(sesiones);
    }
    private SesionHorario sesion(long grupoId, String nombre, Docente docente, DayOfWeek dia, int hora) {
        Asignatura asignatura = new Asignatura(); asignatura.setCodigo("IS" + grupoId); asignatura.setNombre(nombre);
        Grupo grupo = new Grupo(grupoId, "01", EstadoGrupo.ACTIVO); grupo.setAsignatura(asignatura); grupo.setDocente(docente);
        SesionHorario s = new SesionHorario(dia, LocalTime.of(hora,0), LocalTime.of(hora+2,0), EstadoSesion.PROGRAMADA);
        s.setGrupo(grupo); return s;
    }
    private List<String> lineas(byte[] csv) {
        return new String(csv, StandardCharsets.UTF_8).lines().toList();
    }
    @Test void elCsvIncluyeMarcaUtf8EncabezadoYOrdenCronologico() {
        var filas = lineas(servicio.csv(null, null));
        assertEquals("\uFEFFDia;Inicio;Fin;Codigo;Asignatura;Grupo;Docente", filas.getFirst());
        assertEquals(4, filas.size());
        assertTrue(filas.get(1).startsWith("Lunes;09:00;11:00;IS30;"));
        assertTrue(filas.get(2).startsWith("Miércoles;07:00;09:00;"));
        assertTrue(filas.get(3).startsWith("Miércoles;09:00;11:00;"));
    }
    @Test void elCsvEscapaSeparadoresComillasYFormulas() {
        var filas = lineas(servicio.csv(null, null));
        assertTrue(filas.get(1).contains("\"Calculo; \"\"avanzado\"\"\""));
        assertTrue(filas.get(3).contains("\"'=SUMA(1;2)\""));
    }
    @Test void losFiltrosSeAplicanPorGrupoYDocente() {
        assertEquals(3, lineas(servicio.csv(30L, null)).size());
        assertEquals(2, lineas(servicio.csv(null, 21L)).size());
        assertEquals(1, lineas(servicio.csv(31L, 20L)).size());
    }
    @Test void elPdfEsValidoYContieneLasSesiones() throws Exception {
        byte[] pdf = servicio.pdf(30L, null);
        assertEquals("%PDF", new String(pdf, 0, 4, StandardCharsets.US_ASCII));
        try (var lector = new PdfReader(pdf)) {
            String texto = new PdfTextExtractor(lector).getTextFromPage(1);
            assertTrue(texto.contains("Horario academico"));
            assertTrue(texto.contains("Ana Rios"));
            assertFalse(texto.contains("Luis Paz"));
        }
    }
    @Test void sinSesionesElPdfSigueSiendoValido() throws Exception {
        try (var lector = new PdfReader(servicio.pdf(99L, null))) {
            assertEquals(1, lector.getNumberOfPages());
        }
    }
}
