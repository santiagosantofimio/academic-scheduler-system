package co.edu.uelbosque.scheduler.entity;

import java.time.*;
import java.util.Objects;
import co.edu.uelbosque.scheduler.entity.*;
import jakarta.persistence.*;
import co.edu.uelbosque.scheduler.util.IdentidadJpa;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Usuario {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	private String nombre;
	private String apellido;
	@Column(nullable = false, unique = true)
	private String numeroDocumento;
	@Column(nullable = false, unique = true)
	private String correoInstitucional;
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	@Column(nullable = false)
	private String contrasenia;
	private boolean estado;

	public Usuario() {

	}

	public Usuario(String nombre, String apellido, String numeroDocumento, String correoInstitucional,
			String contrasenia, boolean estado) {
		this.nombre = nombre;
		this.apellido = apellido;
		this.numeroDocumento = numeroDocumento;
		this.correoInstitucional = correoInstitucional;
		this.contrasenia = contrasenia;
		this.estado = estado;
	}

	public Usuario(long id, String nombre, String apellido, String numeroDocumento, String correoInstitucional,
			String contrasenia, boolean estado) {
		this.id = id;
		this.nombre = nombre;
		this.apellido = apellido;
		this.numeroDocumento = numeroDocumento;
		this.correoInstitucional = correoInstitucional;
		this.contrasenia = contrasenia;
		this.estado = estado;
	}

	/**
	 * Firma conservada del UML. El int legado no se persiste porque no tiene
	 * atributo asociado.
	 */
	public Usuario(long id, int parametroSinDefinir, String nombre, String apellido, String numeroDocumento,
			String correoInstitucional, String contrasenia, boolean estado) {
		this(id, nombre, apellido, numeroDocumento, correoInstitucional, contrasenia, estado);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getApellido() {
		return apellido;
	}

	public void setApellido(String apellido) {
		this.apellido = apellido;
	}

	public String getNumeroDocumento() {
		return numeroDocumento;
	}

	public void setNumeroDocumento(String numeroDocumento) {
		this.numeroDocumento = numeroDocumento;
	}

	public String getCorreoInstitucional() {
		return correoInstitucional;
	}

	public void setCorreoInstitucional(String correoInstitucional) {
		this.correoInstitucional = correoInstitucional;
	}

	public String getContrasenia() {
		return contrasenia;
	}

	public void setContrasenia(String contrasenia) {
		this.contrasenia = contrasenia;
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
		Usuario other = (Usuario) obj;
		return getId() != null && getId().equals(other.getId());
	}

	@Override
	public int hashCode() {
		return IdentidadJpa.tipo(this).hashCode();
	}

	@Override
	public String toString() {
		return "Usuario{id=" + getId() + "}";
	}
}
