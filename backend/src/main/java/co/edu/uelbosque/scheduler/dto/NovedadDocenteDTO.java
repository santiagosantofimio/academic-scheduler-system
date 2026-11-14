package co.edu.uelbosque.scheduler.dto;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import co.edu.uelbosque.scheduler.entity.EstadoNovedad;
import co.edu.uelbosque.scheduler.entity.TipoNovedad;

public record NovedadDocenteDTO(Long idNovedad, Long docenteId, String docente, TipoNovedad tipo, DayOfWeek diaSemana,
		LocalTime horaInicio, LocalTime horaFin, String descripcion, EstadoNovedad estado, LocalDateTime registradaEn,
		String registradaPor) {
}
