package co.edu.uelbosque.scheduler.service;

import java.time.*;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import co.edu.uelbosque.scheduler.dto.DisponibilidadDocenteDTO;
import co.edu.uelbosque.scheduler.exception.*;
import co.edu.uelbosque.scheduler.entity.*;
import co.edu.uelbosque.scheduler.repository.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DisponibilidadDocenteServiceTest {
    private DisponibilidadDocenteRepository rep;
    private SesionHorarioRepository sesiones;
    private DisponibilidadDocenteService servicio;
    private Docente docente;
    private List<DisponibilidadDocente> ventanas;

    @BeforeEach
    void preparar() {
        rep = mock(DisponibilidadDocenteRepository.class); sesiones = mock(SesionHorarioRepository.class);
        var docentes = mock(DocenteRepository.class);
        docente = new Docente(); docente.setId(20L);
        ventanas = new ArrayList<>();
        when(docentes.findById(20L)).thenReturn(Optional.of(docente));
        when(rep.findByDocenteId(20L)).thenReturn(ventanas);
        when(sesiones.findByDocenteId(20L)).thenReturn(List.of());
        servicio = new DisponibilidadDocenteService(rep, docentes, sesiones, mock(ConfiguracionHorariosRepository.class));
    }
    private DisponibilidadDocenteDTO dto(DayOfWeek dia, int desde, int hasta) {
        return new DisponibilidadDocenteDTO(dia, LocalTime.of(desde,0), LocalTime.of(hasta,0), 20L);
    }
    private DisponibilidadDocente ventana(long id, DayOfWeek dia, int desde, int hasta) {
        var v = new DisponibilidadDocente(id, dia, LocalTime.of(desde,0), LocalTime.of(hasta,0));
        v.setDocente(docente); ventanas.add(v);
        when(rep.findById(id)).thenReturn(Optional.of(v));
        return v;
    }
    private void sesionProgramada(DayOfWeek dia, int desde, int hasta) {
        when(sesiones.findByDocenteId(20L)).thenReturn(List.of(new SesionHorario(dia, LocalTime.of(desde,0), LocalTime.of(hasta,0), EstadoSesion.PROGRAMADA)));
    }
    @Test void registraUnaFranjaValida() {
        assertEquals(1, servicio.create(dto(DayOfWeek.MONDAY,8,12)));
        verify(rep).saveAndFlush(argThat(d -> d.getDocente() == docente));
    }
    @Test void rechazaDomingoHorasInvertidasYDocenteAusente() {
        assertThrows(HorarioInvalidoException.class, () -> servicio.create(dto(DayOfWeek.SUNDAY,8,12)));
        assertThrows(HorarioInvalidoException.class, () -> servicio.create(dto(DayOfWeek.MONDAY,12,8)));
        var sinDocente = dto(DayOfWeek.MONDAY,8,12); sinDocente.setDocenteId(null);
        assertThrows(IdInvalidoException.class, () -> servicio.create(sinDocente));
    }
    @Test void rechazaFranjasSuperpuestasYAceptaContiguas() {
        ventana(1L, DayOfWeek.MONDAY, 8, 10);
        assertThrows(DisponibilidadInvalidaException.class, () -> servicio.create(dto(DayOfWeek.MONDAY,9,11)));
        assertEquals(1, servicio.create(dto(DayOfWeek.MONDAY,10,12)));
    }
    @Test void noPermiteDejarSesionesProgramadasFueraDeLaDisponibilidad() {
        sesionProgramada(DayOfWeek.TUESDAY, 8, 10);
        assertThrows(DisponibilidadInvalidaException.class, () -> servicio.create(dto(DayOfWeek.MONDAY,8,12)));
        assertEquals(1, servicio.create(dto(DayOfWeek.TUESDAY,8,12)));
    }
    @Test void eliminarLaUnicaFranjaLiberaAlDocenteYEliminarUnaUsadaSeRechaza() {
        ventana(1L, DayOfWeek.TUESDAY, 8, 12);
        ventana(2L, DayOfWeek.MONDAY, 8, 12);
        sesionProgramada(DayOfWeek.TUESDAY, 8, 10);
        assertThrows(DisponibilidadInvalidaException.class, () -> servicio.deleteById(1L));
        assertEquals(1, servicio.deleteById(2L));
    }
}
