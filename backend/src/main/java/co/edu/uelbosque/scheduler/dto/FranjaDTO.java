package co.edu.uelbosque.scheduler.dto;

import java.time.DayOfWeek;
import java.time.LocalTime;

public record FranjaDTO(DayOfWeek diaSemana, LocalTime horaInicio, LocalTime horaFin) {
}
