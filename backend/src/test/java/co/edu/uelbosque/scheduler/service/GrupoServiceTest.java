package co.edu.uelbosque.scheduler.service;

import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import co.edu.uelbosque.scheduler.dto.GrupoDTO;
import co.edu.uelbosque.scheduler.entity.*;
import co.edu.uelbosque.scheduler.exception.*;
import co.edu.uelbosque.scheduler.repository.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GrupoServiceTest {
    private GrupoRepository grupos;
    private SesionHorarioRepository sesiones;
    private GrupoService servicio;
    private Grupo grupo;

    @BeforeEach
    void preparar() {
        grupos = mock(GrupoRepository.class);
        sesiones = mock(SesionHorarioRepository.class);
        var asignaturas = mock(AsignaturaServiceRepository.class);
        var configuraciones = mock(ConfiguracionHorariosRepository.class);
        PlanEstudios plan = new PlanEstudios(); plan.setEstado(true);
        Asignatura asignatura = new Asignatura(); asignatura.setIdAsginatura(10L); asignatura.setEstado(true); asignatura.setPlanEstudios(plan);
        Docente docente = new Docente(); docente.setId(20L);
        grupo = new Grupo(30L, "01", EstadoGrupo.ACTIVO); grupo.setAsignatura(asignatura); grupo.setDocente(docente); grupo.setCupoEsperado(30);
        when(grupos.findById(30L)).thenReturn(Optional.of(grupo));
        when(grupos.findByAsignaturaIdAndNumeroGrupo(10L, "01")).thenReturn(Optional.of(List.of(grupo)));
        when(asignaturas.findById(10L)).thenReturn(Optional.of(asignatura));
        when(configuraciones.findAll()).thenReturn(List.of());
        servicio = new GrupoService(grupos, asignaturas, mock(DocenteRepository.class), sesiones,
                mock(RequisitoAsignaturaService.class), configuraciones);
    }
    private GrupoDTO sinDocente() {
        var dto = new GrupoDTO(30L, "01", EstadoGrupo.ACTIVO);
        dto.setAsignaturaId(10L); dto.setCupoEsperado(30); dto.setDocenteId(null);
        return dto;
    }
    @Test void dejarElGrupoSinDocenteQuitaLaAsignacion() {
        assertEquals(1, servicio.updateById(30L, sinDocente()));
        assertNull(grupo.getDocente());
    }
    @Test void noSeQuitaElDocenteSiElGrupoTieneSesionesProgramadas() {
        when(sesiones.existenSesionesDeGrupo(30L, EstadoSesion.PROGRAMADA)).thenReturn(true);
        assertThrows(AsignacionAcademicaInvalidaException.class, () -> servicio.updateById(30L, sinDocente()));
        assertNotNull(grupo.getDocente());
    }
}
