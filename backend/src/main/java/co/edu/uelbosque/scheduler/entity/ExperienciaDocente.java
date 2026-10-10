package co.edu.uelbosque.scheduler.entity;

import java.time.*;
import java.util.Objects;
import co.edu.uelbosque.scheduler.entity.*;
import jakarta.persistence.*;
import co.edu.uelbosque.scheduler.util.IdentidadJpa;

@Entity
public class ExperienciaDocente {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long idExperiencia;
	@Enumerated(EnumType.STRING)
	private TipoExperiencia tipoExperiencia;
	private String institucion;
	private String cargo;
	private String areaConocimiento;
	private String descripcion;
	private LocalDate fechaInicio;
	private LocalDate fechaFin;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "docente_id", nullable = false)
	private Docente docente;

	public ExperienciaDocente() {

	}

	public ExperienciaDocente(TipoExperiencia tipoExperiencia, String institucion, String cargo,
			String areaConocimiento, String descripcion, LocalDate fechaInicio, LocalDate fechaFin) {
		this.tipoExperiencia = tipoExperiencia;
		this.institucion = institucion;
		this.cargo = cargo;
		this.areaConocimiento = areaConocimiento;
		this.descripcion = descripcion;
		this.fechaInicio = fechaInicio;
		this.fechaFin = fechaFin;
	}

	public ExperienciaDocente(long idExperiencia, TipoExperiencia tipoExperiencia, String institucion, String cargo,
			String areaConocimiento, String descripcion, LocalDate fechaInicio, LocalDate fechaFin) {
		this.idExperiencia = idExperiencia;
		this.tipoExperiencia = tipoExperiencia;
		this.institucion = institucion;
		this.cargo = cargo;
		this.areaConocimiento = areaConocimiento;
		this.descripcion = descripcion;
		this.fechaInicio = fechaInicio;
		this.fechaFin = fechaFin;
	}

	public Long getIdExperiencia() {
		return idExperiencia;
	}

	public void setIdExperiencia(Long idExperiencia) {
		this.idExperiencia = idExperiencia;
	}

	public TipoExperiencia getTipoExperiencia() {
		return tipoExperiencia;
	}

	public void setTipoExperiencia(TipoExperiencia tipoExperiencia) {
		this.tipoExperiencia = tipoExperiencia;
	}

	public String getInstitucion() {
		return institucion;
	}

	public void setInstitucion(String institucion) {
		this.institucion = institucion;
	}

	public String getCargo() {
		return cargo;
	}

	public void setCargo(String cargo) {
		this.cargo = cargo;
	}

	public String getAreaConocimiento() {
		return areaConocimiento;
	}

	public void setAreaConocimiento(String areaConocimiento) {
		this.areaConocimiento = areaConocimiento;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public LocalDate getFechaInicio() {
		return fechaInicio;
	}

	public void setFechaInicio(LocalDate fechaInicio) {
		this.fechaInicio = fechaInicio;
	}

	public LocalDate getFechaFin() {
		return fechaFin;
	}

	public void setFechaFin(LocalDate fechaFin) {
		this.fechaFin = fechaFin;
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
		ExperienciaDocente other = (ExperienciaDocente) obj;
		return getIdExperiencia() != null && getIdExperiencia().equals(other.getIdExperiencia());
	}

	@Override
	public int hashCode() {
		return IdentidadJpa.tipo(this).hashCode();
	}

	@Override
	public String toString() {
		return "ExperienciaDocente{idExperiencia=" + getIdExperiencia() + "}";
	}
}
