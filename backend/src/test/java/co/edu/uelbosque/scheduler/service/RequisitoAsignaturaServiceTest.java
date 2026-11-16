package co.edu.uelbosque.scheduler.service;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import co.edu.uelbosque.scheduler.dto.RequisitoAsignaturaDTO;
import co.edu.uelbosque.scheduler.exception.AsignacionAcademicaInvalidaException;
import co.edu.uelbosque.scheduler.entity.*;
import co.edu.uelbosque.scheduler.repository.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RequisitoAsignaturaServiceTest {
    private RequisitoAsignaturaRepository requisitos;
    private AsignaturaServiceRepository asignaturas;
    private GrupoRepository grupos;
    private FormacionAcademicaRepository formaciones;
    private ExperienciaDocenteRepository experiencias;
    private RequisitoAsignaturaService servicio;
    private RequisitoAsignatura requisito;
    private Docente docente;
    @BeforeEach void preparar() {
        requisitos=mock(RequisitoAsignaturaRepository.class); formaciones=mock(FormacionAcademicaRepository.class);
        experiencias=mock(ExperienciaDocenteRepository.class);
        asignaturas = mock(AsignaturaServiceRepository.class);
        grupos = mock(GrupoRepository.class);
        DocenteRepository docentes=mock(DocenteRepository.class);
        docente = new Docente();
        docente.setId(2L);
        docente.setNombre("Camila");
        docente.setApellido("Torres");
        docente.setEstado(true);
        when(docentes.findById(2L)).thenReturn(Optional.of(docente));
        servicio=new RequisitoAsignaturaService(requisitos,asignaturas,docentes,formaciones,
                experiencias,grupos,Clock.fixed(Instant.parse("2026-01-01T12:00:00Z"),ZoneOffset.UTC),
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
    @Test void alGuardarRequisitoIndicaQueFormacionFaltaEnLaFichaDocente() {
        Asignatura asignatura = new Asignatura();
        asignatura.setIdAsginatura(1L);
        Grupo grupo = new Grupo("01", EstadoGrupo.ACTIVO);
        grupo.setDocente(docente);
        when(asignaturas.findById(1L)).thenReturn(Optional.of(asignatura));
        when(grupos.findByAsignaturaId(1L)).thenReturn(Optional.of(List.of(grupo)));
        RequisitoAsignaturaDTO nuevoRequisito = new RequisitoAsignaturaDTO(
                TipoRequisito.FORMACION_ACADEMICA, "Perfil para Infraestructura TI 1", "Redes e infraestructura",
                NivelFormacion.PREGRADO, 0);

        var error = assertThrows(AsignacionAcademicaInvalidaException.class,
                () -> servicio.registrarRequisitoAsignatura(1L, nuevoRequisito));
        assertTrue(error.getMessage().contains("Camila Torres"));
        assertTrue(error.getMessage().contains("Perfil para Infraestructura TI 1"));
        assertTrue(error.getMessage().contains("En su ficha debe constar formación académica en Redes e infraestructura "
                + "con nivel mínimo PREGRADO"));
        verify(requisitos, never()).saveAndFlush(any());
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
        var error = assertThrows(AsignacionAcademicaInvalidaException.class, () -> servicio.validarDocente(1L,2L));
        assertTrue(error.getMessage().contains("al menos un registro de experiencia docente en Sistemas"));
        when(experiencias.findByDocenteIdAndTipoExperiencia(2L,TipoExperiencia.DOCENTE))
                .thenReturn(Optional.of(List.of(experiencia("2025-01-01",null))));
        assertDoesNotThrow(() -> servicio.validarDocente(1L,2L));
    }
}
