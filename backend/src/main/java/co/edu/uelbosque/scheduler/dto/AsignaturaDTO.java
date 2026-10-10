package co.edu.uelbosque.scheduler.dto;

import java.time.*;
import java.util.Objects;
import co.edu.uelbosque.scheduler.entity.*;

public class AsignaturaDTO {
	private Long idAsginatura;
	private String codigo;
	private String nombre;
	private int semestre;
	private int creditos;
	private int sesionesSemanales;
	private String areaConocimiento;
	private boolean estado;
	private Long planEstudiosId;

	public AsignaturaDTO() {

	}

	public AsignaturaDTO(String codigo, String nombre, int semestre, int creditos, int sesionesSemanales,
			String areaConocimiento, boolean estado) {
		this.codigo = codigo;
		this.nombre = nombre;
		this.semestre = semestre;
		this.creditos = creditos;
		this.sesionesSemanales = sesionesSemanales;
		this.areaConocimiento = areaConocimiento;
		this.estado = estado;
	}

	public AsignaturaDTO(long idAsginatura, String codigo, String nombre, int semestre, int creditos,
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

	public Long getPlanEstudiosId() {
		return planEstudiosId;
	}

	public void setPlanEstudiosId(Long planEstudiosId) {
		this.planEstudiosId = planEstudiosId;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;
		AsignaturaDTO other = (AsignaturaDTO) obj;
		return Objects.equals(idAsginatura, other.idAsginatura) && Objects.equals(codigo, other.codigo)
				&& Objects.equals(nombre, other.nombre) && Objects.equals(semestre, other.semestre)
				&& Objects.equals(creditos, other.creditos)
				&& Objects.equals(sesionesSemanales, other.sesionesSemanales)
				&& Objects.equals(areaConocimiento, other.areaConocimiento) && Objects.equals(estado, other.estado)
				&& Objects.equals(planEstudiosId, other.planEstudiosId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(idAsginatura, codigo, nombre, semestre, creditos, sesionesSemanales, areaConocimiento,
				estado, planEstudiosId);
	}

	@Override
	public String toString() {
		return "AsignaturaDTO{idAsginatura=" + idAsginatura + "}";
	}
}
