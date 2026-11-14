package co.edu.uelbosque.scheduler.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.*;
import co.edu.uelbosque.scheduler.util.IdentidadJpa;

@Entity
public class VersionHorario {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idVersion;
	@Column(nullable = false, length = 120)
	private String nombre;
	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private EstadoVersion estado;
	@Column(nullable = false)
	private LocalDateTime creadaEn;
	@Column(nullable = false)
	private String creadaPor;
	private LocalDateTime publicadaEn;
	private String publicadaPor;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "origen_id")
	private VersionHorario origen;
	@OneToMany(mappedBy = "version", cascade = CascadeType.ALL, orphanRemoval = true)
	@OrderBy("diaSemana, horaInicio")
	private List<VersionSesion> sesiones = new ArrayList<>();

	public VersionHorario() {
	}

	public VersionHorario(String nombre, EstadoVersion estado, LocalDateTime creadaEn, String creadaPor) {
		this.nombre = nombre;
		this.estado = estado;
		this.creadaEn = creadaEn;
		this.creadaPor = creadaPor;
	}

	public void agregar(VersionSesion sesion) {
		sesion.setVersion(this);
		sesiones.add(sesion);
	}

	public Long getIdVersion() {
		return idVersion;
	}

	public void setIdVersion(Long idVersion) {
		this.idVersion = idVersion;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public EstadoVersion getEstado() {
		return estado;
	}

	public void setEstado(EstadoVersion estado) {
		this.estado = estado;
	}

	public LocalDateTime getCreadaEn() {
		return creadaEn;
	}

	public String getCreadaPor() {
		return creadaPor;
	}

	public LocalDateTime getPublicadaEn() {
		return publicadaEn;
	}

	public String getPublicadaPor() {
		return publicadaPor;
	}

	public void publicar(LocalDateTime fecha, String usuario) {
		this.estado = EstadoVersion.PUBLICADA;
		this.publicadaEn = fecha;
		this.publicadaPor = usuario;
	}

	public VersionHorario getOrigen() {
		return origen;
	}

	public void setOrigen(VersionHorario origen) {
		this.origen = origen;
	}

	public List<VersionSesion> getSesiones() {
		return sesiones;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || IdentidadJpa.tipo(this) != IdentidadJpa.tipo(obj))
			return false;
		VersionHorario other = (VersionHorario) obj;
		return getIdVersion() != null && getIdVersion().equals(other.getIdVersion());
	}

	@Override
	public int hashCode() {
		return IdentidadJpa.tipo(this).hashCode();
	}

	@Override
	public String toString() {
		return "VersionHorario{idVersion=" + getIdVersion() + "}";
	}
}
