package co.edu.uelbosque.scheduler.entity;

import java.time.DayOfWeek;
import java.time.LocalTime;
import jakarta.persistence.*;
import co.edu.uelbosque.scheduler.util.IdentidadJpa;

@Entity
public class VersionSesion {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idVersionSesion;
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "version_id", nullable = false)
	private VersionHorario version;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "grupo_id")
	private Grupo grupo;
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private DayOfWeek diaSemana;
	@Column(nullable = false)
	private LocalTime horaInicio;
	@Column(nullable = false)
	private LocalTime horaFin;

	public VersionSesion() {
	}

	public VersionSesion(Grupo grupo, DayOfWeek diaSemana, LocalTime horaInicio, LocalTime horaFin) {
		this.grupo = grupo;
		this.diaSemana = diaSemana;
		this.horaInicio = horaInicio;
		this.horaFin = horaFin;
	}

	public Long getIdVersionSesion() {
		return idVersionSesion;
	}

	public VersionHorario getVersion() {
		return version;
	}

	public void setVersion(VersionHorario version) {
		this.version = version;
	}

	public Grupo getGrupo() {
		return grupo;
	}

	public DayOfWeek getDiaSemana() {
		return diaSemana;
	}

	public LocalTime getHoraInicio() {
		return horaInicio;
	}

	public LocalTime getHoraFin() {
		return horaFin;
	}

	public SesionHorario comoSesion() {
		SesionHorario sesion = new SesionHorario(diaSemana, horaInicio, horaFin, EstadoSesion.PROGRAMADA);
		sesion.setGrupo(grupo);
		return sesion;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || IdentidadJpa.tipo(this) != IdentidadJpa.tipo(obj))
			return false;
		VersionSesion other = (VersionSesion) obj;
		return getIdVersionSesion() != null && getIdVersionSesion().equals(other.getIdVersionSesion());
	}

	@Override
	public int hashCode() {
		return IdentidadJpa.tipo(this).hashCode();
	}

	@Override
	public String toString() {
		return "VersionSesion{idVersionSesion=" + getIdVersionSesion() + "}";
	}
}
