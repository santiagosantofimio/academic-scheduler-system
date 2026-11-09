package co.edu.uelbosque.scheduler.dto;

import java.time.*;
import java.util.Objects;

public class DisponibilidadDocenteDTO {
	private Long idDisponibilidad;
	private DayOfWeek diaSemana;
	private LocalTime horaInicio;
	private LocalTime horaFin;
	private Long docenteId;

	public DisponibilidadDocenteDTO() {

	}

	public DisponibilidadDocenteDTO(DayOfWeek diaSemana, LocalTime horaInicio, LocalTime horaFin, Long docenteId) {
		this.diaSemana = diaSemana;
		this.horaInicio = horaInicio;
		this.horaFin = horaFin;
		this.docenteId = docenteId;
	}

	public Long getIdDisponibilidad() {
		return idDisponibilidad;
	}

	public void setIdDisponibilidad(Long idDisponibilidad) {
		this.idDisponibilidad = idDisponibilidad;
	}

	public DayOfWeek getDiaSemana() {
		return diaSemana;
	}

	public void setDiaSemana(DayOfWeek diaSemana) {
		this.diaSemana = diaSemana;
	}

	public LocalTime getHoraInicio() {
		return horaInicio;
	}

	public void setHoraInicio(LocalTime horaInicio) {
		this.horaInicio = horaInicio;
	}

	public LocalTime getHoraFin() {
		return horaFin;
	}

	public void setHoraFin(LocalTime horaFin) {
		this.horaFin = horaFin;
	}

	public Long getDocenteId() {
		return docenteId;
	}

	public void setDocenteId(Long docenteId) {
		this.docenteId = docenteId;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;
		DisponibilidadDocenteDTO other = (DisponibilidadDocenteDTO) obj;
		return Objects.equals(idDisponibilidad, other.idDisponibilidad) && Objects.equals(diaSemana, other.diaSemana)
				&& Objects.equals(horaInicio, other.horaInicio) && Objects.equals(horaFin, other.horaFin)
				&& Objects.equals(docenteId, other.docenteId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(idDisponibilidad, diaSemana, horaInicio, horaFin, docenteId);
	}

	@Override
	public String toString() {
		return "DisponibilidadDocenteDTO{idDisponibilidad=" + idDisponibilidad + "}";
	}
}
