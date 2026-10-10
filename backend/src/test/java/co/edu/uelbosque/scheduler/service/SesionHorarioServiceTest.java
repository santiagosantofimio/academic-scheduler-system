package co.edu.uelbosque.scheduler.service;

import java.time.*;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import co.edu.uelbosque.scheduler.dto.SesionHorarioDTO;
import co.edu.uelbosque.scheduler.exception.*;
import co.edu.uelbosque.scheduler.entity.*;
import co.edu.uelbosque.scheduler.repository.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SesionHorarioServiceTest {
    private SesionHorarioRepository sesiones;
    private GrupoRepository grupos;
    private DocenteRepository docentes;
    private ConfiguracionHorariosRepository configuraciones;
    private RequisitoAsignaturaService requisitos;
    private SesionHorarioService servicio;
    private ConfiguracionHorarios config;
    private Grupo grupo;
    private Docente docente;
    private List<SesionHorario> existentes;
    private List<SesionHorario> agenda;

    @BeforeEach
    void preparar() {
        sesiones = mock(SesionHorarioRepository.class); grupos = mock(GrupoRepository.class);
        docentes = mock(DocenteRepository.class); configuraciones = mock(ConfiguracionHorariosRepository.class);
        requisitos = mock(RequisitoAsignaturaService.class);
        servicio = new SesionHorarioService(sesiones, grupos, docentes, configuraciones, requisitos);
        config = new ConfiguracionHorarios(LocalTime.of(8,0), LocalTime.of(12,0),
                LocalTime.of(8,0), LocalTime.of(12,0), null, null, 60, 6, false);
        config.setIdConfiguracion(1L);
        PlanEstudios plan = new PlanEstudios(); plan.setEstado(true);
        Asignatura asignatura = new Asignatura(); asignatura.setIdAsginatura(10L);
        asignatura.setEstado(true); asignatura.setSesionesSemanales(3); asignatura.setPlanEstudios(plan);
        docente = new Docente(); docente.setId(20L); docente.setEstado(true); docente.setHorasMaximasDocenciaSemanal(12);
        grupo = new Grupo(30L, "01", EstadoGrupo.ACTIVO); grupo.setAsignatura(asignatura); grupo.setDocente(docente);
        existentes = new ArrayList<>(); agenda = new ArrayList<>();
        when(configuraciones.bloquearConfiguraciones()).thenReturn(List.of(config));
        when(configuraciones.findAll()).thenReturn(List.of(config));
        when(grupos.findById(30L)).thenReturn(Optional.of(grupo));
        when(docentes.bloquearPorId(20L)).thenReturn(Optional.of(docente));
        when(docentes.findById(20L)).thenReturn(Optional.of(docente));
        when(docentes.existsById(20L)).thenReturn(true);
        when(sesiones.findByGrupoIdAndEstado(30L, EstadoSesion.PROGRAMADA)).thenReturn(Optional.of(existentes));
        when(sesiones.findByDocenteId(20L)).thenReturn(agenda);
        when(sesiones.saveAllAndFlush(anyList())).thenAnswer(invocacion -> {
            List<SesionHorario> nuevas = invocacion.getArgument(0);
            long id = 100;
            for (SesionHorario sesion : nuevas) sesion.setIdSesion(id++);
            existentes.addAll(nuevas); agenda.addAll(nuevas);
            return nuevas;
        });
    }
    private SesionHorario sesion(long id, DayOfWeek dia, int hora, int minuto, int duracion, Grupo destino) {
        LocalTime inicio = LocalTime.of(hora, minuto);
        SesionHorario s = new SesionHorario(dia, inicio, inicio.plusMinutes(duracion), EstadoSesion.PROGRAMADA);
        s.setIdSesion(id); s.setGrupo(destino); return s;
    }
    @Test void generaSolucionCompletaYRepetirEsIdempotente() {
        var primera = servicio.generarHorarioGrupo(30L);
        assertEquals(3, primera.size());
        assertEquals(LocalTime.of(8,0), primera.getFirst().getHoraInicio());
        assertEquals(primera, servicio.generarHorarioGrupo(30L));
        verify(sesiones, times(1)).saveAllAndFlush(anyList());
        var orden = inOrder(configuraciones, grupos, docentes, sesiones);
        orden.verify(configuraciones).bloquearConfiguraciones();
        orden.verify(grupos).findById(30L);
        orden.verify(docentes).bloquearPorId(20L);
        orden.verify(sesiones).saveAllAndFlush(anyList());
    }
    @Test void conservaSesionesExistentesYCompletaSoloFaltantes() {
        SesionHorario previa = sesion(70L, DayOfWeek.WEDNESDAY, 9, 0, 60, grupo);
        existentes.add(previa); agenda.add(previa);
        var resultado = servicio.generarHorarioGrupo(30L);
        assertEquals(3, resultado.size());
        assertTrue(resultado.stream().anyMatch(s -> s.getIdSesion().equals(70L)));
        assertEquals(3, existentes.size());
    }
    @Test void respetaDescansoYHorariosNoAlineadosDeOtroGrupo() {
        config.setHoraInicioDescanso(LocalTime.of(9,45)); config.setHoraFinDescanso(LocalTime.of(10,15));
        agenda.add(sesion(80L, DayOfWeek.MONDAY, 8, 0, 30, new Grupo(99L,"02",EstadoGrupo.ACTIVO)));
        var resultado = servicio.generarHorarioGrupo(30L);
        assertEquals(LocalTime.of(8,30), resultado.getFirst().getHoraInicio());
        assertEquals(LocalTime.of(10,15), resultado.get(1).getHoraInicio());
        assertTrue(resultado.stream().noneMatch(s -> s.getHoraInicio().isBefore(LocalTime.of(10,15))
                && s.getHoraFin().isAfter(LocalTime.of(9,45))));
    }
    @Test void retrocedeCuandoElegirLunesImpideCompletarSinDiasConsecutivos() {
        grupo.getAsignatura().setSesionesSemanales(4);
        config.setEvitarDiasConsecutivos(true);
        config.setHoraFinSemana(LocalTime.of(10,0)); config.setHoraFinSabado(LocalTime.of(10,0));
        // Capacidades L=1, M=2, X=0, J=2, V=0, S=0: elegir L primero no permite cuatro sesiones.
        agenda.add(sesion(81,DayOfWeek.MONDAY,9,0,60,null));
        agenda.add(sesion(82,DayOfWeek.WEDNESDAY,8,0,120,null));
        agenda.add(sesion(83,DayOfWeek.FRIDAY,8,0,120,null));
        agenda.add(sesion(84,DayOfWeek.SATURDAY,8,0,120,null));
        docente.setHorasMaximasDocenciaSemanal(20);
        var resultado = servicio.generarHorarioGrupo(30L);
        assertEquals(List.of(DayOfWeek.TUESDAY,DayOfWeek.TUESDAY,DayOfWeek.THURSDAY,DayOfWeek.THURSDAY),
                resultado.stream().map(SesionHorarioDTO::getDiaSemana).toList());
    }
    @Test void faltaDeEspacioNoGuardaUnaSolucionParcial() {
        for (int dia=1; dia<=6; dia++) agenda.add(sesion(80+dia,DayOfWeek.of(dia),8,0,240,null));
        docente.setHorasMaximasDocenciaSemanal(40);
        assertThrows(DisponibilidadInvalidaException.class, () -> servicio.generarHorarioGrupo(30L));
        verify(sesiones, never()).saveAllAndFlush(anyList());
    }
    @Test void cargaSemanalIncluyeTodosLosGruposDelDocente() {
        docente.setHorasMaximasDocenciaSemanal(3);
        agenda.add(sesion(80,DayOfWeek.FRIDAY,8,0,60,null));
        assertThrows(DisponibilidadInvalidaException.class, () -> servicio.generarHorarioGrupo(30L));
        verify(sesiones, never()).saveAllAndFlush(anyList());
    }
    @Test void canceladasNoOcupanEspacioNiConsumenCarga() {
        SesionHorario cancelada = sesion(80,DayOfWeek.MONDAY,8,0,240,null);
        cancelada.setEstado(EstadoSesion.CANCELADA); agenda.add(cancelada);
        docente.setHorasMaximasDocenciaSemanal(3);
        assertEquals(LocalTime.of(8,0), servicio.generarHorarioGrupo(30L).getFirst().getHoraInicio());
    }
    @Test void grupoSinDocenteNoPuedeGenerar() {
        grupo.setDocente(null);
        assertThrows(AsignacionAcademicaInvalidaException.class, () -> servicio.generarHorarioGrupo(30L));
        verify(sesiones, never()).saveAllAndFlush(anyList());
    }
    @Test void requisitoIncumplidoNoPersisteSesiones() {
        doThrow(new AsignacionAcademicaInvalidaException("No cumple"))
                .when(requisitos).validarDocente(10L,20L);
        assertThrows(AsignacionAcademicaInvalidaException.class, () -> servicio.generarHorarioGrupo(30L));
        verify(sesiones, never()).saveAllAndFlush(anyList());
    }
    @Test void actualizarNoConfundeLaPropiaSesionConUnCruce() {
        SesionHorario actual = sesion(70,DayOfWeek.MONDAY,8,0,60,grupo);
        existentes.add(actual); agenda.add(actual);
        when(sesiones.findById(70L)).thenReturn(Optional.of(actual));
        var dto = new SesionHorarioDTO(70L,DayOfWeek.MONDAY,LocalTime.of(8,0),LocalTime.of(9,0),EstadoSesion.PROGRAMADA);
        dto.setGrupoId(30L);
        assertEquals(1, servicio.updateById(70L,dto));
        verify(sesiones).saveAndFlush(actual);
    }
    @Test void altaManualRechazaCruceYDuracionIncorrecta() {
        agenda.add(sesion(80,DayOfWeek.MONDAY,8,0,60,null));
        var dto = new SesionHorarioDTO(DayOfWeek.MONDAY,LocalTime.of(8,0),LocalTime.of(9,0),EstadoSesion.PROGRAMADA);
        dto.setGrupoId(30L);
        assertThrows(CruceHorarioException.class, () -> servicio.create(dto));
        dto.setHoraFin(LocalTime.of(8,30));
        assertThrows(HorarioInvalidoException.class, () -> servicio.create(dto));
        verify(sesiones,never()).saveAndFlush(any());
    }
    @Test void limitesAdyacentesNoSonCruceYDomingoNoTieneFranja() {
        agenda.add(sesion(80,DayOfWeek.MONDAY,8,0,60,null));
        assertFalse(servicio.validarCruceDocente(20L,DayOfWeek.MONDAY,LocalTime.of(9,0),LocalTime.of(10,0)));
        assertFalse(servicio.validarFranjaInstitucional(DayOfWeek.SUNDAY,LocalTime.of(8,0),LocalTime.of(9,0)));
    }
}
