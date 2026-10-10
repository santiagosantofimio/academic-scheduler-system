package co.edu.uelbosque.scheduler.dto;

import java.time.*;
import java.util.Objects;
import co.edu.uelbosque.scheduler.entity.*;

public class PlanEstudiosDTO {
	private Long idPlan;
	private String nombrePrograma;
	private String codigoPrograma;
	private String versionPlan;
	private int numerosSemestres;
	private boolean estado;

	public PlanEstudiosDTO() {

	}

	public PlanEstudiosDTO(String nombrePrograma, String codigoPrograma, String versionPlan, int numerosSemestres,
			boolean estado) {
		this.nombrePrograma = nombrePrograma;
		this.codigoPrograma = codigoPrograma;
		this.versionPlan = versionPlan;
		this.numerosSemestres = numerosSemestres;
		this.estado = estado;
	}

	public PlanEstudiosDTO(long idPlan, String nombrePrograma, String codigoPrograma, String versionPlan,
			int numerosSemestres, boolean estado) {
		this.idPlan = idPlan;
		this.nombrePrograma = nombrePrograma;
		this.codigoPrograma = codigoPrograma;
		this.versionPlan = versionPlan;
		this.numerosSemestres = numerosSemestres;
		this.estado = estado;
	}

	public Long getIdPlan() {
		return idPlan;
	}

	public void setIdPlan(Long idPlan) {
		this.idPlan = idPlan;
	}

	public String getNombrePrograma() {
		return nombrePrograma;
	}

	public void setNombrePrograma(String nombrePrograma) {
		this.nombrePrograma = nombrePrograma;
	}

	public String getCodigoPrograma() {
		return codigoPrograma;
	}

	public void setCodigoPrograma(String codigoPrograma) {
		this.codigoPrograma = codigoPrograma;
	}

	public String getVersionPlan() {
		return versionPlan;
	}

	public void setVersionPlan(String versionPlan) {
		this.versionPlan = versionPlan;
	}

	public int getNumerosSemestres() {
		return numerosSemestres;
	}

	public void setNumerosSemestres(int numerosSemestres) {
		this.numerosSemestres = numerosSemestres;
	}

	public boolean isEstado() {
		return estado;
	}

	public void setEstado(boolean estado) {
		this.estado = estado;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;
		PlanEstudiosDTO other = (PlanEstudiosDTO) obj;
		return Objects.equals(idPlan, other.idPlan) && Objects.equals(nombrePrograma, other.nombrePrograma)
				&& Objects.equals(codigoPrograma, other.codigoPrograma)
				&& Objects.equals(versionPlan, other.versionPlan)
				&& Objects.equals(numerosSemestres, other.numerosSemestres) && Objects.equals(estado, other.estado);
	}

	@Override
	public int hashCode() {
		return Objects.hash(idPlan, nombrePrograma, codigoPrograma, versionPlan, numerosSemestres, estado);
	}

	@Override
	public String toString() {
		return "PlanEstudiosDTO{idPlan=" + idPlan + "}";
	}
}
