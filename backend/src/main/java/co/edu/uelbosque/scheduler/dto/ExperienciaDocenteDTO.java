package co.edu.uelbosque.scheduler.dto;

import java.time.*;
import java.util.Objects;
import co.edu.uelbosque.scheduler.entity.*;

public class ExperienciaDocenteDTO {
	private Long idExperiencia;
	private TipoExperiencia tipoExperiencia;
	private String institucion;
	private String cargo;
	private String areaConocimiento;
	private String descripcion;
	private LocalDate fechaInicio;
	private LocalDate fechaFin;
	private Long docenteId;

	public ExperienciaDocenteDTO() {

	}

	public ExperienciaDocenteDTO(TipoExperiencia tipoExperiencia, String institucion, String cargo,
			String areaConocimiento, String descripcion, LocalDate fechaInicio, LocalDate fechaFin) {
		this.tipoExperiencia = tipoExperiencia;
		this.institucion = institucion;
		this.cargo = cargo;
		this.areaConocimiento = areaConocimiento;
		this.descripcion = descripcion;
		this.fechaInicio = fechaInicio;
		this.fechaFin = fechaFin;
	}

	public ExperienciaDocenteDTO(long idExperiencia, TipoExperiencia tipoExperiencia, String institucion, String cargo,
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
		ExperienciaDocenteDTO other = (ExperienciaDocenteDTO) obj;
		return Objects.equals(idExperiencia, other.idExperiencia)
				&& Objects.equals(tipoExperiencia, other.tipoExperiencia)
				&& Objects.equals(institucion, other.institucion) && Objects.equals(cargo, other.cargo)
				&& Objects.equals(areaConocimiento, other.areaConocimiento)
				&& Objects.equals(descripcion, other.descripcion) && Objects.equals(fechaInicio, other.fechaInicio)
				&& Objects.equals(fechaFin, other.fechaFin) && Objects.equals(docenteId, other.docenteId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(idExperiencia, tipoExperiencia, institucion, cargo, areaConocimiento, descripcion,
				fechaInicio, fechaFin, docenteId);
	}

	@Override
	public String toString() {
		return "ExperienciaDocenteDTO{idExperiencia=" + idExperiencia + "}";
	}
}
