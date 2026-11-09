package co.edu.uelbosque.scheduler.service;

import java.time.*;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import co.edu.uelbosque.scheduler.entity.*;
import co.edu.uelbosque.scheduler.exception.*;
import co.edu.uelbosque.scheduler.repository.*;
import co.edu.uelbosque.scheduler.util.DatosUsuario;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DocenteServiceTest {
    private DocenteRepository docentes;
    private GrupoRepository grupos;
    private FormacionAcademicaRepository formaciones;
    private ExperienciaDocenteRepository experiencias;
    private DisponibilidadDocenteRepository disponibilidades;
    private DocenteService servicio;
    private Docente docente;

    @BeforeEach
    void preparar() {
        docentes = mock(DocenteRepository.class); grupos = mock(GrupoRepository.class);
        formaciones = mock(FormacionAcademicaRepository.class); experiencias = mock(ExperienciaDocenteRepository.class);
        disponibilidades = mock(DisponibilidadDocenteRepository.class);
        docente = new Docente(); docente.setId(20L);
        when(docentes.findById(20L)).thenReturn(Optional.of(docente));
        when(formaciones.findByDocenteId(20L)).thenReturn(Optional.of(List.of(new FormacionAcademica())));
        when(experiencias.findByDocenteId(20L)).thenReturn(Optional.of(List.of(new ExperienciaDocente())));
        when(disponibilidades.findByDocenteId(20L)).thenReturn(List.of(new DisponibilidadDocente(DayOfWeek.MONDAY, LocalTime.of(7,0), LocalTime.of(9,0))));
        servicio = new DocenteService(docentes, mock(DatosUsuario.class), mock(SesionHorarioRepository.class), grupos,
                formaciones, experiencias, disponibilidades, mock(ConfiguracionHorariosRepository.class));
    }
    @Test void conGruposAsignadosExplicaPorQueNoSePuedeEliminar() {
        Asignatura asignatura = new Asignatura(); asignatura.setCodigo("IS302");
        Grupo grupo = new Grupo(30L, "01", EstadoGrupo.ACTIVO); grupo.setAsignatura(asignatura);
        when(grupos.buscarPorDocente(20L)).thenReturn(List.of(grupo));
        var error = assertThrows(AsignacionAcademicaInvalidaException.class, () -> servicio.deleteById(20L));
        assertTrue(error.getMessage().contains("IS302 grupo 01"));
        verify(docentes, never()).delete(any());
        verify(disponibilidades, never()).deleteAll(any());
    }
    @Test void sinGruposEliminaAlDocenteConSuFormacionExperienciaYDisponibilidad() {
        when(grupos.buscarPorDocente(20L)).thenReturn(List.of());
        assertEquals(1, servicio.deleteById(20L));
        verify(formaciones).deleteAll(anyList());
        verify(experiencias).deleteAll(anyList());
        verify(disponibilidades).deleteAll(anyList());
        verify(docentes).delete(docente);
    }
}
