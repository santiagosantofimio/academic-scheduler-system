package co.edu.uelbosque.scheduler.dto;

import java.time.*;
import java.util.Objects;
import co.edu.uelbosque.scheduler.entity.*;

public class SesionHorarioDTO {
	private Long idSesion;
	private DayOfWeek diaSemana;
	private LocalTime horaInicio;
	private LocalTime horaFin;
	private EstadoSesion estado;
	private Long grupoId;

	public SesionHorarioDTO() {

	}

	public SesionHorarioDTO(DayOfWeek diaSemana, LocalTime horaInicio, LocalTime horaFin, EstadoSesion estado) {
		this.diaSemana = diaSemana;
		this.horaInicio = horaInicio;
		this.horaFin = horaFin;
		this.estado = estado;
	}

	public SesionHorarioDTO(long idSesion, DayOfWeek diaSemana, LocalTime horaInicio, LocalTime horaFin,
			EstadoSesion estado) {
		this.idSesion = idSesion;
		this.diaSemana = diaSemana;
		this.horaInicio = horaInicio;
		this.horaFin = horaFin;
		this.estado = estado;
	}

	public Long getIdSesion() {
		return idSesion;
	}

	public void setIdSesion(Long idSesion) {
		this.idSesion = idSesion;
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

	public EstadoSesion getEstado() {
		return estado;
	}

	public void setEstado(EstadoSesion estado) {
		this.estado = estado;
	}

	public Long getGrupoId() {
		return grupoId;
	}

	public void setGrupoId(Long grupoId) {
		this.grupoId = grupoId;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;
		SesionHorarioDTO other = (SesionHorarioDTO) obj;
		return Objects.equals(idSesion, other.idSesion) && Objects.equals(diaSemana, other.diaSemana)
				&& Objects.equals(horaInicio, other.horaInicio) && Objects.equals(horaFin, other.horaFin)
				&& Objects.equals(estado, other.estado) && Objects.equals(grupoId, other.grupoId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(idSesion, diaSemana, horaInicio, horaFin, estado, grupoId);
	}

	@Override
	public String toString() {
		return "SesionHorarioDTO{idSesion=" + idSesion + "}";
	}
}
