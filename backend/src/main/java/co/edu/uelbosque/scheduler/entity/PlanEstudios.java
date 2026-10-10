package co.edu.uelbosque.scheduler.entity;

import java.time.*;
import java.util.Objects;
import co.edu.uelbosque.scheduler.entity.*;
import jakarta.persistence.*;
import co.edu.uelbosque.scheduler.util.IdentidadJpa;

@Entity
public class PlanEstudios {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long idPlan;
	private String nombrePrograma;
	@Column(nullable = false, unique = true)
	private String codigoPrograma;
	private String versionPlan;
	private int numerosSemestres;
	private boolean estado;

	public PlanEstudios() {

	}

	public PlanEstudios(String nombrePrograma, String codigoPrograma, String versionPlan, int numerosSemestres,
			boolean estado) {
		this.nombrePrograma = nombrePrograma;
		this.codigoPrograma = codigoPrograma;
		this.versionPlan = versionPlan;
		this.numerosSemestres = numerosSemestres;
		this.estado = estado;
	}

	public PlanEstudios(long idPlan, String nombrePrograma, String codigoPrograma, String versionPlan,
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
		if (obj == null || IdentidadJpa.tipo(this) != IdentidadJpa.tipo(obj))
			return false;
		PlanEstudios other = (PlanEstudios) obj;
		return getIdPlan() != null && getIdPlan().equals(other.getIdPlan());
	}

	@Override
	public int hashCode() {
		return IdentidadJpa.tipo(this).hashCode();
	}

	@Override
	public String toString() {
		return "PlanEstudios{idPlan=" + getIdPlan() + "}";
	}
}
