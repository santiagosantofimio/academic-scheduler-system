package co.edu.uelbosque.scheduler.entity;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import jakarta.persistence.*;
import co.edu.uelbosque.scheduler.util.IdentidadJpa;
import co.edu.uelbosque.scheduler.util.ReglasHorario;

@Entity
public class NovedadDocente {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idNovedad;
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "docente_id", nullable = false)
	private Docente docente;
	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 30)
	private TipoNovedad tipo;
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private DayOfWeek diaSemana;
	private LocalTime horaInicio;
	private LocalTime horaFin;
	@Column(length = 500)
	private String descripcion;
	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private EstadoNovedad estado;
	@Column(nullable = false)
	private LocalDateTime registradaEn;
	@Column(nullable = false)
	private String registradaPor;

	public boolean bloquea(DayOfWeek dia, LocalTime inicio, LocalTime fin) {
		if (estado != EstadoNovedad.ABIERTA || diaSemana != dia)
			return false;
		return horaInicio == null || ReglasHorario.seCruzan(horaInicio, horaFin, inicio, fin);
	}

	public Long getIdNovedad() {
		return idNovedad;
	}

	public void setIdNovedad(Long idNovedad) {
		this.idNovedad = idNovedad;
	}

	public Docente getDocente() {
		return docente;
	}

	public void setDocente(Docente docente) {
		this.docente = docente;
	}

	public TipoNovedad getTipo() {
		return tipo;
	}

	public void setTipo(TipoNovedad tipo) {
		this.tipo = tipo;
	}

	public DayOfWeek getDiaSemana() {
		return diaSemana;
	}

	public void setDiaSemana(DayOfWeek diaSemana) {
		this.diaSemana = diaSemana;
	}

	public LocalTime getHoraInicio() {
		return horaInicio;
	}

	public void setHoraInicio(LocalTime horaInicio) {
		this.horaInicio = horaInicio;
	}

	public LocalTime getHoraFin() {
		return horaFin;
	}

	public void setHoraFin(LocalTime horaFin) {
		this.horaFin = horaFin;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public EstadoNovedad getEstado() {
		return estado;
	}

	public void setEstado(EstadoNovedad estado) {
		this.estado = estado;
	}

	public LocalDateTime getRegistradaEn() {
		return registradaEn;
	}

	public void setRegistradaEn(LocalDateTime registradaEn) {
		this.registradaEn = registradaEn;
	}

	public String getRegistradaPor() {
		return registradaPor;
	}

	public void setRegistradaPor(String registradaPor) {
		this.registradaPor = registradaPor;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || IdentidadJpa.tipo(this) != IdentidadJpa.tipo(obj))
			return false;
		NovedadDocente other = (NovedadDocente) obj;
		return getIdNovedad() != null && getIdNovedad().equals(other.getIdNovedad());
	}

	@Override
	public int hashCode() {
		return IdentidadJpa.tipo(this).hashCode();
	}

	@Override
	public String toString() {
		return "NovedadDocente{idNovedad=" + getIdNovedad() + "}";
	}
}
