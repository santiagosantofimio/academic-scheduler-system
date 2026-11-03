package co.edu.uelbosque.scheduler.service;

import java.time.*;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import co.edu.uelbosque.scheduler.entity.*;
import co.edu.uelbosque.scheduler.exception.RecursoNoEncontradoException;
import co.edu.uelbosque.scheduler.repository.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class HorarioDocenteServiceTest {
    private UsuarioRepository usuarios;
    private ExportacionHorarioService exportacion;
    private HorarioDocenteService servicio;

    @BeforeEach
    void preparar() {
        usuarios = mock(UsuarioRepository.class);
        var grupos = mock(GrupoRepository.class);
        var sesiones = mock(SesionHorarioRepository.class);
        var configuraciones = mock(ConfiguracionHorariosRepository.class);
        exportacion = mock(ExportacionHorarioService.class);
        Docente docente = new Docente(); docente.setId(20L);
        Administrador admin = new Administrador(); admin.setId(1L);
        when(usuarios.findByCorreoInstitucional("docente@example.test")).thenReturn(Optional.of(docente));
        when(usuarios.findByCorreoInstitucional("admin@example.test")).thenReturn(Optional.of(admin));
        Asignatura asignatura = new Asignatura(); asignatura.setIdAsginatura(10L);
        Grupo grupo = new Grupo(30L, "01", EstadoGrupo.ACTIVO); grupo.setAsignatura(asignatura); grupo.setDocente(docente);
        Grupo otro = new Grupo(31L, "02", EstadoGrupo.ACTIVO); otro.setAsignatura(asignatura); otro.setDocente(docente);
        when(grupos.buscarPorDocente(20L)).thenReturn(List.of(grupo, otro));
        SesionHorario viernes = new SesionHorario(DayOfWeek.FRIDAY, LocalTime.of(8,0), LocalTime.of(10,0), EstadoSesion.PROGRAMADA);
        SesionHorario lunes = new SesionHorario(DayOfWeek.MONDAY, LocalTime.of(8,0), LocalTime.of(10,0), EstadoSesion.PROGRAMADA);
        SesionHorario cancelada = new SesionHorario(DayOfWeek.TUESDAY, LocalTime.of(8,0), LocalTime.of(10,0), EstadoSesion.CANCELADA);
        for (var s : List.of(viernes, lunes, cancelada)) s.setGrupo(grupo);
        when(sesiones.findByDocenteId(20L)).thenReturn(List.of(viernes, cancelada, lunes));
        when(configuraciones.findAll()).thenReturn(List.of());
        servicio = new HorarioDocenteService(usuarios, grupos, sesiones, configuraciones, exportacion);
    }
    @Test void devuelveSoloSesionesProgramadasOrdenadasYAsignaturasSinRepetir() {
        var horario = servicio.consultar("docente@example.test");
        assertEquals(List.of(DayOfWeek.MONDAY, DayOfWeek.FRIDAY), horario.sesiones().stream().map(s -> s.getDiaSemana()).toList());
        assertEquals(2, horario.grupos().size());
        assertEquals(1, horario.asignaturas().size());
        assertNull(horario.configuracion());
    }
    @Test void exportaSoloConElIdDelDocenteAutenticado() {
        servicio.pdf("docente@example.test");
        verify(exportacion).pdf(null, 20L);
    }
    @Test void unaCuentaQueNoEsDocenteNoTieneHorario() {
        assertThrows(RecursoNoEncontradoException.class, () -> servicio.consultar("admin@example.test"));
        assertThrows(RecursoNoEncontradoException.class, () -> servicio.csv("nadie@example.test"));
    }
}
