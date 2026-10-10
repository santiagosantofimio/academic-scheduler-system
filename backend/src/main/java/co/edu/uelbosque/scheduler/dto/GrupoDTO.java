package co.edu.uelbosque.scheduler.dto;

import java.time.*;
import java.util.Objects;
import co.edu.uelbosque.scheduler.entity.*;

public class GrupoDTO {
	private Long docenteId;

	public Long getDocenteId() {
		return docenteId;
	}

	public void setDocenteId(Long docenteId) {
		this.docenteId = docenteId;
	}

	private long idGrupo;
	private String numeroGrupo;
	private EstadoGrupo estado;
	private Long asignaturaId;

	public GrupoDTO() {

	}

	public GrupoDTO(String numeroGrupo, EstadoGrupo estado) {
		this.numeroGrupo = numeroGrupo;
		this.estado = estado;
	}

	public GrupoDTO(long idGrupo, String numeroGrupo, EstadoGrupo estado) {
		this.idGrupo = idGrupo;
		this.numeroGrupo = numeroGrupo;
		this.estado = estado;
	}

	public long getIdGrupo() {
		return idGrupo;
	}

	public void setIdGrupo(long idGrupo) {
		this.idGrupo = idGrupo;
	}

	public String getNumeroGrupo() {
		return numeroGrupo;
	}

	public void setNumeroGrupo(String numeroGrupo) {
		this.numeroGrupo = numeroGrupo;
	}

	public EstadoGrupo getEstado() {
		return estado;
	}

	public void setEstado(EstadoGrupo estado) {
		this.estado = estado;
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
		GrupoDTO other = (GrupoDTO) obj;
		return Objects.equals(idGrupo, other.idGrupo) && Objects.equals(numeroGrupo, other.numeroGrupo)
				&& Objects.equals(estado, other.estado) && Objects.equals(asignaturaId, other.asignaturaId)
				&& Objects.equals(docenteId, other.docenteId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(idGrupo, numeroGrupo, estado, asignaturaId, docenteId);
	}

	@Override
	public String toString() {
		return "GrupoDTO{idGrupo=" + idGrupo + "}";
	}
}
