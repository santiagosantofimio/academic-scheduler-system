package co.edu.uelbosque.scheduler.entity;

import java.time.*;
import jakarta.persistence.*;
import co.edu.uelbosque.scheduler.util.IdentidadJpa;

@Entity
public class DisponibilidadDocente {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long idDisponibilidad;
	@Enumerated(EnumType.STRING)
	private DayOfWeek diaSemana;
	private LocalTime horaInicio;
	private LocalTime horaFin;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "docente_id", nullable = false)
	private Docente docente;

	public DisponibilidadDocente() {

	}

	public DisponibilidadDocente(DayOfWeek diaSemana, LocalTime horaInicio, LocalTime horaFin) {
		this.diaSemana = diaSemana;
		this.horaInicio = horaInicio;
		this.horaFin = horaFin;
	}

	public DisponibilidadDocente(long idDisponibilidad, DayOfWeek diaSemana, LocalTime horaInicio, LocalTime horaFin) {
		this.idDisponibilidad = idDisponibilidad;
		this.diaSemana = diaSemana;
		this.horaInicio = horaInicio;
		this.horaFin = horaFin;
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

	public Docente getDocente() {
		return docente;
	}

	public void setDocente(Docente docente) {
		this.docente = docente;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || IdentidadJpa.tipo(this) != IdentidadJpa.tipo(obj))
			return false;
		DisponibilidadDocente other = (DisponibilidadDocente) obj;
		return getIdDisponibilidad() != null && getIdDisponibilidad().equals(other.getIdDisponibilidad());
	}

	@Override
	public int hashCode() {
		return IdentidadJpa.tipo(this).hashCode();
	}

	@Override
	public String toString() {
		return "DisponibilidadDocente{idDisponibilidad=" + getIdDisponibilidad() + "}";
	}
}
