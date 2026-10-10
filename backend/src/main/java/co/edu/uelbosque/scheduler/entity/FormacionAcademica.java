package co.edu.uelbosque.scheduler.entity;

import java.time.*;
import java.util.Objects;
import co.edu.uelbosque.scheduler.entity.*;
import jakarta.persistence.*;
import co.edu.uelbosque.scheduler.util.IdentidadJpa;

@Entity
public class FormacionAcademica {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long idFormacion;
	private String tituloObtenido;
	@Enumerated(EnumType.STRING)
	private NivelFormacion nivelFormacion;
	private String institucion;
	private String areaConocimiento;
	private int anioGraduacion;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "docente_id", nullable = false)
	private Docente docente;

	public FormacionAcademica() {

	}

	public FormacionAcademica(String tituloObtenido, NivelFormacion nivelFormacion, String institucion,
			String areaConocimiento, int anioGraduacion) {
		this.tituloObtenido = tituloObtenido;
		this.nivelFormacion = nivelFormacion;
		this.institucion = institucion;
		this.areaConocimiento = areaConocimiento;
		this.anioGraduacion = anioGraduacion;
	}

	public FormacionAcademica(long idFormacion, String tituloObtenido, NivelFormacion nivelFormacion,
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

	public Docente getDocente() {
		return docente;
	}

	public void setDocente(Docente docente) {
		this.docente = docente;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || IdentidadJpa.tipo(this) != IdentidadJpa.tipo(obj))
			return false;
		FormacionAcademica other = (FormacionAcademica) obj;
		return getIdFormacion() != null && getIdFormacion().equals(other.getIdFormacion());
	}

	@Override
	public int hashCode() {
		return IdentidadJpa.tipo(this).hashCode();
	}

	@Override
	public String toString() {
		return "FormacionAcademica{idFormacion=" + getIdFormacion() + "}";
	}
}
