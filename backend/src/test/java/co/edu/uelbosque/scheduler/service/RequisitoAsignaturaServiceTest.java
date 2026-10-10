package co.edu.uelbosque.scheduler.service;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import co.edu.uelbosque.scheduler.exception.AsignacionAcademicaInvalidaException;
import co.edu.uelbosque.scheduler.entity.*;
import co.edu.uelbosque.scheduler.repository.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RequisitoAsignaturaServiceTest {
    private RequisitoAsignaturaRepository requisitos;
    private FormacionAcademicaRepository formaciones;
    private ExperienciaDocenteRepository experiencias;
    private RequisitoAsignaturaService servicio;
    private RequisitoAsignatura requisito;
    @BeforeEach void preparar() {
        requisitos=mock(RequisitoAsignaturaRepository.class); formaciones=mock(FormacionAcademicaRepository.class);
        experiencias=mock(ExperienciaDocenteRepository.class);
        DocenteRepository docentes=mock(DocenteRepository.class);
        Docente docente=new Docente(); docente.setId(2L); docente.setEstado(true);
        when(docentes.findById(2L)).thenReturn(Optional.of(docente));
        servicio=new RequisitoAsignaturaService(requisitos,mock(AsignaturaServiceRepository.class),docentes,formaciones,
                experiencias,mock(GrupoRepository.class),Clock.fixed(Instant.parse("2026-01-01T12:00:00Z"),ZoneOffset.UTC),
                mock(ConfiguracionHorariosRepository.class));
        requisito=new RequisitoAsignatura(); requisito.setDescripcion("Formacion en sistemas");
        requisito.setAreaConocimiento("Sistemas"); requisito.setTipoRequisito(TipoRequisito.FORMACION_ACADEMICA);
        requisito.setNivelFormacionMinimo(NivelFormacion.MAESTRIA);
        when(requisitos.findByAsignaturaId(1L)).thenReturn(Optional.of(List.of(requisito)));
    }
    @Test void formacionDebeCumplirNivelYAreaEnElMismoRegistro() {
        FormacionAcademica formacion=new FormacionAcademica(); formacion.setAreaConocimiento(" sistemas ");
        formacion.setNivelFormacion(NivelFormacion.DOCTORADO);
        when(formaciones.findByDocenteId(2L)).thenReturn(Optional.of(List.of(formacion)));
        assertDoesNotThrow(() -> servicio.validarDocente(1L,2L));
        formacion.setAreaConocimiento("Otra area");
        assertThrows(AsignacionAcademicaInvalidaException.class, () -> servicio.validarDocente(1L,2L));
        formacion.setAreaConocimiento("Sistemas"); formacion.setNivelFormacion(NivelFormacion.PREGRADO);
        assertThrows(AsignacionAcademicaInvalidaException.class, () -> servicio.validarDocente(1L,2L));
    }
    private ExperienciaDocente experiencia(String desde,String hasta) {
        ExperienciaDocente e=new ExperienciaDocente(); e.setFechaInicio(LocalDate.parse(desde));
        e.setFechaFin(hasta==null?null:LocalDate.parse(hasta)); e.setAreaConocimiento("Sistemas"); return e;
    }
    @Test void experienciaConcurrenteNoSeCuentaDosVecesYLaActualTerminaHoy() {
        List<ExperienciaDocente> registros=List.of(experiencia("2024-01-01","2024-12-31"),
                experiencia("2024-06-01","2025-04-01"),experiencia("2025-01-01",null));
        assertEquals(ChronoUnit.DAYS.between(LocalDate.of(2024,1,1),LocalDate.of(2026,1,1)),
                RequisitoAsignaturaService.diasExperiencia(registros,LocalDate.of(2026,1,1)));
    }
    @Test void experienciaCeroAniosAunRequiereUnRegistroDelTipoYArea() {
        requisito.setTipoRequisito(TipoRequisito.EXPERIENCIA_DOCENTE); requisito.setAniosExperienciaMinima(0);
        when(experiencias.findByDocenteIdAndTipoExperiencia(2L,TipoExperiencia.DOCENTE)).thenReturn(Optional.empty());
        assertThrows(AsignacionAcademicaInvalidaException.class, () -> servicio.validarDocente(1L,2L));
        when(experiencias.findByDocenteIdAndTipoExperiencia(2L,TipoExperiencia.DOCENTE))
                .thenReturn(Optional.of(List.of(experiencia("2025-01-01",null))));
        assertDoesNotThrow(() -> servicio.validarDocente(1L,2L));
    }
}
