package co.edu.uelbosque.scheduler.dto;

import java.time.*;
import java.util.Objects;
import co.edu.uelbosque.scheduler.entity.*;

public class RequisitoAsignaturaDTO {
	private long idRequisito;
	private TipoRequisito tipoRequisito;
	private String descripcion;
	private String areaConocimiento;
	private NivelFormacion nivelFormacionMinimo;
	private int aniosExperienciaMinima;
	private Long asignaturaId;

	public RequisitoAsignaturaDTO() {

	}

	public RequisitoAsignaturaDTO(TipoRequisito tipoRequisito, String descripcion, String areaConocimiento,
			NivelFormacion nivelFormacionMinimo, int aniosExperienciaMinima) {
		this.tipoRequisito = tipoRequisito;
		this.descripcion = descripcion;
		this.areaConocimiento = areaConocimiento;
		this.nivelFormacionMinimo = nivelFormacionMinimo;
		this.aniosExperienciaMinima = aniosExperienciaMinima;
	}

	public RequisitoAsignaturaDTO(long idRequisito, TipoRequisito tipoRequisito, String descripcion,
			String areaConocimiento, NivelFormacion nivelFormacionMinimo, int aniosExperienciaMinima) {
		this.idRequisito = idRequisito;
		this.tipoRequisito = tipoRequisito;
		this.descripcion = descripcion;
		this.areaConocimiento = areaConocimiento;
		this.nivelFormacionMinimo = nivelFormacionMinimo;
		this.aniosExperienciaMinima = aniosExperienciaMinima;
	}

	public long getIdRequisito() {
		return idRequisito;
	}

	public void setIdRequisito(long idRequisito) {
		this.idRequisito = idRequisito;
	}

	public TipoRequisito getTipoRequisito() {
		return tipoRequisito;
	}

	public void setTipoRequisito(TipoRequisito tipoRequisito) {
		this.tipoRequisito = tipoRequisito;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getAreaConocimiento() {
		return areaConocimiento;
	}

	public void setAreaConocimiento(String areaConocimiento) {
		this.areaConocimiento = areaConocimiento;
	}

	public NivelFormacion getNivelFormacionMinimo() {
		return nivelFormacionMinimo;
	}

	public void setNivelFormacionMinimo(NivelFormacion nivelFormacionMinimo) {
		this.nivelFormacionMinimo = nivelFormacionMinimo;
	}

	public int getAniosExperienciaMinima() {
		return aniosExperienciaMinima;
	}

	public void setAniosExperienciaMinima(int aniosExperienciaMinima) {
		this.aniosExperienciaMinima = aniosExperienciaMinima;
	}

	public Long getAsignaturaId() {
		return asignaturaId;
	}

	public void setAsignaturaId(Long asignaturaId) {
		this.asignaturaId = asignaturaId;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;
		RequisitoAsignaturaDTO other = (RequisitoAsignaturaDTO) obj;
		return Objects.equals(idRequisito, other.idRequisito) && Objects.equals(tipoRequisito, other.tipoRequisito)
				&& Objects.equals(descripcion, other.descripcion)
				&& Objects.equals(areaConocimiento, other.areaConocimiento)
				&& Objects.equals(nivelFormacionMinimo, other.nivelFormacionMinimo)
				&& Objects.equals(aniosExperienciaMinima, other.aniosExperienciaMinima)
				&& Objects.equals(asignaturaId, other.asignaturaId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(idRequisito, tipoRequisito, descripcion, areaConocimiento, nivelFormacionMinimo,
				aniosExperienciaMinima, asignaturaId);
	}

	@Override
	public String toString() {
		return "RequisitoAsignaturaDTO{idRequisito=" + idRequisito + "}";
	}
}
