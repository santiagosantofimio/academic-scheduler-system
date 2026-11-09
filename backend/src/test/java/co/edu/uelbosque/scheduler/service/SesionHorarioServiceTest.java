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
    private List<DisponibilidadDocente> ventanas;

    @BeforeEach
    void preparar() {
        sesiones = mock(SesionHorarioRepository.class); grupos = mock(GrupoRepository.class);
        docentes = mock(DocenteRepository.class); configuraciones = mock(ConfiguracionHorariosRepository.class);
        requisitos = mock(RequisitoAsignaturaService.class);
        var disponibilidades = mock(DisponibilidadDocenteRepository.class);
        ventanas = new ArrayList<>();
        when(disponibilidades.findByDocenteId(20L)).thenReturn(ventanas);
        servicio = new SesionHorarioService(sesiones, grupos, docentes, configuraciones, requisitos, disponibilidades);
        config = new ConfiguracionHorarios(LocalTime.of(8,0), LocalTime.of(12,0),
                LocalTime.of(8,0), LocalTime.of(12,0), null, null, 60, 6, false);
        config.setIdConfiguracion(1L);
        PlanEstudios plan = new PlanEstudios(); plan.setEstado(true);
        Asignatura asignatura = new Asignatura(); asignatura.setIdAsginatura(10L);
        asignatura.setEstado(true); asignatura.setSesionesSemanales(3); asignatura.setPlanEstudios(plan);
        docente = new Docente(); docente.setId(20L); docente.setEstado(true); docente.setHorasMaximasDocenciaSemanal(12);
        grupo = new Grupo(30L, "01", EstadoGrupo.ACTIVO); grupo.setAsignatura(asignatura); grupo.setDocente(docente);
        grupo.setCupoEsperado(30);
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
        grupo.getAsignatura().setSesionesSemanales(2);
        docente.setHorasMaximasDocenciaSemanal(40);
        agenda.add(sesion(80L, DayOfWeek.MONDAY, 8, 0, 30, new Grupo(99L,"02",EstadoGrupo.ACTIVO)));
        for (int dia=2; dia<=6; dia++) agenda.add(sesion(90+dia,DayOfWeek.of(dia),8,0,240,null));
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
        agenda.add(sesion(81,DayOfWeek.MONDAY,9,0,60,null));
        agenda.add(sesion(82,DayOfWeek.WEDNESDAY,8,0,120,null));
        agenda.add(sesion(83,DayOfWeek.FRIDAY,8,0,120,null));
        agenda.add(sesion(84,DayOfWeek.SATURDAY,8,0,120,null));
        docente.setHorasMaximasDocenciaSemanal(20);
        var resultado = servicio.generarHorarioGrupo(30L);
        assertEquals(List.of(DayOfWeek.TUESDAY,DayOfWeek.TUESDAY,DayOfWeek.THURSDAY,DayOfWeek.THURSDAY),
                resultado.stream().map(SesionHorarioDTO::getDiaSemana).toList());
    }
    @Test void reparteLasSesionesEnDiasSeparadosAunqueQuepanEnUnoSolo() {
        var dias = servicio.generarHorarioGrupo(30L).stream().map(SesionHorarioDTO::getDiaSemana).toList();
        assertEquals(List.of(DayOfWeek.MONDAY,DayOfWeek.WEDNESDAY,DayOfWeek.FRIDAY), dias);
    }
    @Test void usaDiasContiguosAntesDeRepetirDiaCuandoLaConfiguracionLoPermite() {
        grupo.getAsignatura().setSesionesSemanales(4);
        var dias = servicio.generarHorarioGrupo(30L).stream().map(SesionHorarioDTO::getDiaSemana).toList();
        assertEquals(4, new HashSet<>(dias).size());
    }
    @Test void cuentaLasSesionesExistentesAlRepartirPorDia() {
        SesionHorario previa = sesion(70L, DayOfWeek.MONDAY, 8, 0, 60, grupo);
        existentes.add(previa); agenda.add(previa);
        var dias = servicio.generarHorarioGrupo(30L).stream().map(SesionHorarioDTO::getDiaSemana).toList();
        assertEquals(3, new HashSet<>(dias).size());
    }
    @Test void soloRepiteDiaCuandoNoHayOtraOpcion() {
        grupo.getAsignatura().setSesionesSemanales(2);
        docente.setHorasMaximasDocenciaSemanal(40);
        for (int dia=2; dia<=6; dia++) agenda.add(sesion(90+dia,DayOfWeek.of(dia),8,0,240,null));
        var dias = servicio.generarHorarioGrupo(30L).stream().map(SesionHorarioDTO::getDiaSemana).toList();
        assertEquals(List.of(DayOfWeek.MONDAY,DayOfWeek.MONDAY), dias);
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
    private SesionHorarioDTO nuevaSesion(DayOfWeek dia, int hora) {
        var dto = new SesionHorarioDTO(dia,LocalTime.of(hora,0),LocalTime.of(hora+1,0),EstadoSesion.PROGRAMADA);
        dto.setGrupoId(30L); return dto;
    }
    @Test void elCupoRespetaElMaximoConTolerancia() {
        grupo.setCupoEsperado(44);
        assertEquals(1, servicio.create(nuevaSesion(DayOfWeek.MONDAY,8)));
        grupo.setCupoEsperado(45);
        assertThrows(DatosInvalidosException.class, () -> servicio.create(nuevaSesion(DayOfWeek.TUESDAY,8)));
        grupo.setCupoEsperado(9);
        assertThrows(DatosInvalidosException.class, () -> servicio.generarHorarioGrupo(30L));
    }
    @Test void unaSesionCanceladaNoSeValidaContraLaAgenda() {
        agenda.add(sesion(80,DayOfWeek.MONDAY,8,0,60,null));
        var dto = nuevaSesion(DayOfWeek.MONDAY,8);
        dto.setEstado(EstadoSesion.CANCELADA);
        assertEquals(1, servicio.create(dto));
    }
    private void disponible(DayOfWeek dia, int desde, int hasta) {
        ventanas.add(new DisponibilidadDocente(ventanas.size()+1L,dia,LocalTime.of(desde,0),LocalTime.of(hasta,0)));
    }
    @Test void sinDisponibilidadRegistradaElDocenteNoTieneRestricciones() {
        assertEquals(3, servicio.generarHorarioGrupo(30L).size());
    }
    @Test void laGeneracionSoloUsaLasFranjasDisponiblesDelDocente() {
        disponible(DayOfWeek.TUESDAY,9,12); disponible(DayOfWeek.THURSDAY,8,10); disponible(DayOfWeek.SATURDAY,10,11);
        var resultado = servicio.generarHorarioGrupo(30L);
        assertEquals(List.of(DayOfWeek.TUESDAY,DayOfWeek.THURSDAY,DayOfWeek.SATURDAY),
                resultado.stream().map(SesionHorarioDTO::getDiaSemana).toList());
        assertEquals(LocalTime.of(9,0), resultado.getFirst().getHoraInicio());
        assertEquals(LocalTime.of(10,0), resultado.getLast().getHoraInicio());
    }
    @Test void laGeneracionFallaSiLaDisponibilidadNoAlcanza() {
        disponible(DayOfWeek.MONDAY,8,10);
        assertThrows(DisponibilidadInvalidaException.class, () -> servicio.generarHorarioGrupo(30L));
        verify(sesiones, never()).saveAllAndFlush(anyList());
    }
    @Test void altaManualRechazaFranjaFueraDeDisponibilidadYAceptaVentanasContiguas() {
        disponible(DayOfWeek.MONDAY,8,9); disponible(DayOfWeek.MONDAY,9,10);
        var dto = new SesionHorarioDTO(DayOfWeek.MONDAY,LocalTime.of(8,30),LocalTime.of(9,30),EstadoSesion.PROGRAMADA);
        dto.setGrupoId(30L);
        assertEquals(1, servicio.create(dto));
        assertThrows(DisponibilidadInvalidaException.class, () -> servicio.create(nuevaSesion(DayOfWeek.MONDAY,10)));
        assertThrows(DisponibilidadInvalidaException.class, () -> servicio.create(nuevaSesion(DayOfWeek.TUESDAY,8)));
    }
    @Test void limitesAdyacentesNoSonCruceYDomingoNoTieneFranja() {
        agenda.add(sesion(80,DayOfWeek.MONDAY,8,0,60,null));
        assertFalse(servicio.validarCruceDocente(20L,DayOfWeek.MONDAY,LocalTime.of(9,0),LocalTime.of(10,0)));
        assertFalse(servicio.validarFranjaInstitucional(DayOfWeek.SUNDAY,LocalTime.of(8,0),LocalTime.of(9,0)));
    }
}
