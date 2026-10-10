package co.edu.uelbosque.scheduler.entity;

import java.time.*;
import java.util.Objects;
import co.edu.uelbosque.scheduler.entity.*;
import jakarta.persistence.*;
import co.edu.uelbosque.scheduler.util.IdentidadJpa;

@Entity
@Table(name = "grupo", uniqueConstraints = @UniqueConstraint(columnNames = { "asignatura_id", "numero_grupo" }))
public class Grupo {
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "docente_id")
	private Docente docente;

	public Docente getDocente() {
		return docente;
	}

	public void setDocente(Docente docente) {
		this.docente = docente;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long idGrupo;
	@Column(name = "numero_grupo", nullable = false)
	private String numeroGrupo;
	@Enumerated(EnumType.STRING)
	private EstadoGrupo estado;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "asignatura_id", nullable = false)
	private Asignatura asignatura;

	public Grupo() {

	}

	public Grupo(String numeroGrupo, EstadoGrupo estado) {
		this.numeroGrupo = numeroGrupo;
		this.estado = estado;
	}

	public Grupo(long idGrupo, String numeroGrupo, EstadoGrupo estado) {
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

	public Asignatura getAsignatura() {
		return asignatura;
	}

	public void setAsignatura(Asignatura asignatura) {
		this.asignatura = asignatura;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || IdentidadJpa.tipo(this) != IdentidadJpa.tipo(obj))
			return false;
		Grupo other = (Grupo) obj;
		return getIdGrupo() != 0 && getIdGrupo() == other.getIdGrupo();
	}

	@Override
	public int hashCode() {
		return IdentidadJpa.tipo(this).hashCode();
	}

	@Override
	public String toString() {
		return "Grupo{idGrupo=" + getIdGrupo() + "}";
	}
}
