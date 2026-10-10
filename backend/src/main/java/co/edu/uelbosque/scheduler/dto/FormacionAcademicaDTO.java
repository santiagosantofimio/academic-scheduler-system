package co.edu.uelbosque.scheduler.dto;

import java.time.*;
import java.util.Objects;
import co.edu.uelbosque.scheduler.entity.*;

public class FormacionAcademicaDTO {
	private Long idFormacion;
	private String tituloObtenido;
	private NivelFormacion nivelFormacion;
	private String institucion;
	private String areaConocimiento;
	private int anioGraduacion;
	private Long docenteId;

	public FormacionAcademicaDTO() {

	}

	public FormacionAcademicaDTO(String tituloObtenido, NivelFormacion nivelFormacion, String institucion,
			String areaConocimiento, int anioGraduacion) {
		this.tituloObtenido = tituloObtenido;
		this.nivelFormacion = nivelFormacion;
		this.institucion = institucion;
		this.areaConocimiento = areaConocimiento;
		this.anioGraduacion = anioGraduacion;
	}

	public FormacionAcademicaDTO(long idFormacion, String tituloObtenido, NivelFormacion nivelFormacion,
			String institucion, String areaConocimiento, int anioGraduacion) {
		this.idFormacion = idFormacion;
		this.tituloObtenido = tituloObtenido;
		this.nivelFormacion = nivelFormacion;
		this.institucion = institucion;
		this.areaConocimiento = areaConocimiento;
		this.anioGraduacion = anioGraduacion;
	}

	public Long getIdFormacion() {
		return idFormacion;
	}

	public void setIdFormacion(Long idFormacion) {
		this.idFormacion = idFormacion;
	}

	public String getTituloObtenido() {
		return tituloObtenido;
	}

	public void setTituloObtenido(String tituloObtenido) {
		this.tituloObtenido = tituloObtenido;
	}

	public NivelFormacion getNivelFormacion() {
		return nivelFormacion;
	}

	public void setNivelFormacion(NivelFormacion nivelFormacion) {
		this.nivelFormacion = nivelFormacion;
	}

	public String getInstitucion() {
		return institucion;
	}

	public void setInstitucion(String institucion) {
		this.institucion = institucion;
	}

	public String getAreaConocimiento() {
		return areaConocimiento;
	}

	public void setAreaConocimiento(String areaConocimiento) {
		this.areaConocimiento = areaConocimiento;
	}

	public int getAnioGraduacion() {
		return anioGraduacion;
	}

	public void setAnioGraduacion(int anioGraduacion) {
		this.anioGraduacion = anioGraduacion;
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
		FormacionAcademicaDTO other = (FormacionAcademicaDTO) obj;
		return Objects.equals(idFormacion, other.idFormacion) && Objects.equals(tituloObtenido, other.tituloObtenido)
				&& Objects.equals(nivelFormacion, other.nivelFormacion)
				&& Objects.equals(institucion, other.institucion)
				&& Objects.equals(areaConocimiento, other.areaConocimiento)
				&& Objects.equals(anioGraduacion, other.anioGraduacion) && Objects.equals(docenteId, other.docenteId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(idFormacion, tituloObtenido, nivelFormacion, institucion, areaConocimiento, anioGraduacion,
				docenteId);
	}

	@Override
	public String toString() {
		return "FormacionAcademicaDTO{idFormacion=" + idFormacion + "}";
	}
}
