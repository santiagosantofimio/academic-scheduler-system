package co.edu.uelbosque.scheduler.entity;

import java.time.*;
import java.util.Objects;
import co.edu.uelbosque.scheduler.entity.*;
import jakarta.persistence.*;
import co.edu.uelbosque.scheduler.util.IdentidadJpa;

@Entity
public class Asignatura {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long idAsginatura;
	@Column(nullable = false, unique = true)
	private String codigo;
	private String nombre;
	private int semestre;
	private int creditos;
	private int sesionesSemanales;
	private String areaConocimiento;
	private boolean estado;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "plan_estudios_id", nullable = false)
	private PlanEstudios planEstudios;

	public Asignatura() {

	}

	public Asignatura(String codigo, String nombre, int semestre, int creditos, int sesionesSemanales,
			String areaConocimiento, boolean estado) {
		this.codigo = codigo;
		this.nombre = nombre;
		this.semestre = semestre;
		this.creditos = creditos;
		this.sesionesSemanales = sesionesSemanales;
		this.areaConocimiento = areaConocimiento;
		this.estado = estado;
	}

	public Asignatura(long idAsginatura, String codigo, String nombre, int semestre, int creditos,
			int sesionesSemanales, String areaConocimiento, boolean estado) {
		this.idAsginatura = idAsginatura;
		this.codigo = codigo;
		this.nombre = nombre;
		this.semestre = semestre;
		this.creditos = creditos;
		this.sesionesSemanales = sesionesSemanales;
		this.areaConocimiento = areaConocimiento;
		this.estado = estado;
	}

	public Long getIdAsginatura() {
		return idAsginatura;
	}

	public void setIdAsginatura(Long idAsginatura) {
		this.idAsginatura = idAsginatura;
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public int getSemestre() {
		return semestre;
	}

	public void setSemestre(int semestre) {
		this.semestre = semestre;
	}

	public int getCreditos() {
		return creditos;
	}

	public void setCreditos(int creditos) {
		this.creditos = creditos;
	}

	public int getSesionesSemanales() {
		return sesionesSemanales;
	}

	public void setSesionesSemanales(int sesionesSemanales) {
		this.sesionesSemanales = sesionesSemanales;
	}

	public String getAreaConocimiento() {
		return areaConocimiento;
	}

	public void setAreaConocimiento(String areaConocimiento) {
		this.areaConocimiento = areaConocimiento;
	}

	public boolean isEstado() {
		return estado;
	}

	public void setEstado(boolean estado) {
		this.estado = estado;
	}

	public PlanEstudios getPlanEstudios() {
		return planEstudios;
	}

	public void setPlanEstudios(PlanEstudios planEstudios) {
		this.planEstudios = planEstudios;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || IdentidadJpa.tipo(this) != IdentidadJpa.tipo(obj))
			return false;
		Asignatura other = (Asignatura) obj;
		return getIdAsginatura() != null && getIdAsginatura().equals(other.getIdAsginatura());
	}

	@Override
	public int hashCode() {
		return IdentidadJpa.tipo(this).hashCode();
	}

	@Override
	public String toString() {
		return "Asignatura{idAsginatura=" + getIdAsginatura() + "}";
	}
}
