package co.edu.uelbosque.scheduler.dto;

import java.time.*;
import java.util.Objects;
import co.edu.uelbosque.scheduler.entity.*;
import co.edu.uelbosque.scheduler.entity.Usuario;

/**
 * Conserva la generalizacion a Usuario dibujada en el UML; no es una entidad
 * JPA.
 */
public class AdministradorDTO extends Usuario {
	private String cargo;

	public AdministradorDTO() {

	}

	public AdministradorDTO(String cargo) {
		this.cargo = cargo;
	}

	public AdministradorDTO(long id, String cargo) {
		setId(id);
		this.cargo = cargo;
	}

	/**
	 * La exportacion omite nombres: se interpreta esta sobrecarga como nombre y
	 * cargo.
	 */
	public AdministradorDTO(String nombre, String cargo) {
		setNombre(nombre);
		this.cargo = cargo;
	}

	public AdministradorDTO(String nombre, String apellido, String numeroDocumento, String correoInstitucional,
			String contrasenia, boolean estado) {
		super(nombre, apellido, numeroDocumento, correoInstitucional, contrasenia, estado);
	}

	public AdministradorDTO(String nombre, String apellido, String numeroDocumento, String correoInstitucional,
			String contrasenia, boolean estado, String cargo) {
		super(nombre, apellido, numeroDocumento, correoInstitucional, contrasenia, estado);
		this.cargo = cargo;
	}

	public AdministradorDTO(long id, String nombre, String apellido, String numeroDocumento, String correoInstitucional,
			String contrasenia, boolean estado) {
		super(id, nombre, apellido, numeroDocumento, correoInstitucional, contrasenia, estado);
	}

	public AdministradorDTO(long id, String nombre, String apellido, String numeroDocumento, String correoInstitucional,
			String contrasenia, boolean estado, String cargo) {
		super(id, nombre, apellido, numeroDocumento, correoInstitucional, contrasenia, estado);
		this.cargo = cargo;
	}

	public String getCargo() {
		return cargo;
	}

	public void setCargo(String cargo) {
		this.cargo = cargo;
	}

	@Override
	public String toString() {
		return "AdministradorDTO{id=" + getId() + "}";
	}
}
