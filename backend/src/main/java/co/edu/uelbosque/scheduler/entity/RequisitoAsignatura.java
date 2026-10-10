package co.edu.uelbosque.scheduler.entity;

import java.time.*;
import java.util.Objects;
import co.edu.uelbosque.scheduler.entity.*;
import jakarta.persistence.*;
import co.edu.uelbosque.scheduler.util.IdentidadJpa;

@Entity
public class RequisitoAsignatura {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long idRequisito;
	@Enumerated(EnumType.STRING)
	private TipoRequisito tipoRequisito;
	private String descripcion;
	private String areaConocimiento;
	@Enumerated(EnumType.STRING)
	private NivelFormacion nivelFormacionMinimo;
	private int aniosExperienciaMinima;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "asignatura_id", nullable = false)
	private Asignatura asignatura;

	public RequisitoAsignatura() {

	}

	public RequisitoAsignatura(TipoRequisito tipoRequisito, String descripcion, String areaConocimiento,
			NivelFormacion nivelFormacionMinimo, int aniosExperienciaMinima) {
		this.tipoRequisito = tipoRequisito;
		this.descripcion = descripcion;
		this.areaConocimiento = areaConocimiento;
		this.nivelFormacionMinimo = nivelFormacionMinimo;
		this.aniosExperienciaMinima = aniosExperienciaMinima;
	}

	public RequisitoAsignatura(long idRequisito, TipoRequisito tipoRequisito, String descripcion,
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
		RequisitoAsignatura other = (RequisitoAsignatura) obj;
		return getIdRequisito() != 0 && getIdRequisito() == other.getIdRequisito();
	}

	@Override
	public int hashCode() {
		return IdentidadJpa.tipo(this).hashCode();
	}

	@Override
	public String toString() {
		return "RequisitoAsignatura{idRequisito=" + getIdRequisito() + "}";
	}
}
