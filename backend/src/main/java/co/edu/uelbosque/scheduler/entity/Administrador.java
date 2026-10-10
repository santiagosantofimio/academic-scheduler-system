package co.edu.uelbosque.scheduler.entity;

import java.time.*;
import java.util.Objects;
import co.edu.uelbosque.scheduler.entity.*;
import jakarta.persistence.*;
import co.edu.uelbosque.scheduler.util.IdentidadJpa;

@Entity
public class Administrador extends Usuario {
	private String cargo;

	public Administrador() {

	}

	public Administrador(String cargo) {
		this.cargo = cargo;
	}

	public Administrador(String nombre, String apellido, String numeroDocumento, String correoInstitucional,
			String contrasenia, boolean estado, String cargo) {
		super(nombre, apellido, numeroDocumento, correoInstitucional, contrasenia, estado);
		this.cargo = cargo;
	}

	public Administrador(long id, String nombre, String apellido, String numeroDocumento, String correoInstitucional,
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
		return "Administrador{id=" + getId() + "}";
	}
}
