package co.edu.uelbosque.scheduler.entity;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import co.edu.uelbosque.scheduler.util.IdentidadJpa;

@Entity
public class AuditoriaCambio {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idAuditoria;
	@Column(nullable = false)
	private LocalDateTime fecha;
	@Column(nullable = false)
	private String usuario;
	@Column(nullable = false, length = 40)
	private String accion;
	@Column(nullable = false, length = 500)
	private String detalle;

	public AuditoriaCambio() {
	}

	public AuditoriaCambio(LocalDateTime fecha, String usuario, String accion, String detalle) {
		this.fecha = fecha;
		this.usuario = usuario;
		this.accion = accion;
		this.detalle = detalle.length() > 500 ? detalle.substring(0, 500) : detalle;
	}

	public Long getIdAuditoria() {
		return idAuditoria;
	}

	public LocalDateTime getFecha() {
		return fecha;
	}

	public String getUsuario() {
		return usuario;
	}

	public String getAccion() {
		return accion;
	}

	public String getDetalle() {
		return detalle;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || IdentidadJpa.tipo(this) != IdentidadJpa.tipo(obj))
			return false;
		AuditoriaCambio other = (AuditoriaCambio) obj;
		return getIdAuditoria() != null && getIdAuditoria().equals(other.getIdAuditoria());
	}

	@Override
	public int hashCode() {
		return IdentidadJpa.tipo(this).hashCode();
	}

	@Override
	public String toString() {
		return "AuditoriaCambio{idAuditoria=" + getIdAuditoria() + "}";
	}
}
