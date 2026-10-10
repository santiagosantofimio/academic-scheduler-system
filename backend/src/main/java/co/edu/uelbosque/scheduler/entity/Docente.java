package co.edu.uelbosque.scheduler.entity;

import java.time.*;
import java.util.Objects;
import co.edu.uelbosque.scheduler.entity.*;
import jakarta.persistence.*;
import co.edu.uelbosque.scheduler.util.IdentidadJpa;

@Entity
public class Docente extends Usuario {
	@Enumerated(EnumType.STRING)
	private TipoVinculacion tipoVinculacion;
	private String escalafon;
	private String especialidad;
	private int horasMaximasDocenciaSemanal;

	public Docente() {

	}

	public Docente(TipoVinculacion tipoVinculacion, String escalafon, String especialidad,
			int horasMaximasDocenciaSemanal) {
		this.tipoVinculacion = tipoVinculacion;
		this.escalafon = escalafon;
		this.especialidad = especialidad;
		this.horasMaximasDocenciaSemanal = horasMaximasDocenciaSemanal;
	}

	public Docente(String nombre, String apellido, String numeroDocumento, String correoInstitucional,
			String contrasenia, boolean estado, TipoVinculacion tipoVinculacion, String escalafon, String especialidad,
			int horasMaximasDocenciaSemanal) {
		super(nombre, apellido, numeroDocumento, correoInstitucional, contrasenia, estado);
		this.tipoVinculacion = tipoVinculacion;
		this.escalafon = escalafon;
		this.especialidad = especialidad;
		this.horasMaximasDocenciaSemanal = horasMaximasDocenciaSemanal;
	}

	public Docente(long id, String nombre, String apellido, String numeroDocumento, String correoInstitucional,
			String contrasenia, boolean estado, TipoVinculacion tipoVinculacion, String escalafon, String especialidad,
			int horasMaximasDocenciaSemanal) {
		super(id, nombre, apellido, numeroDocumento, correoInstitucional, contrasenia, estado);
		this.tipoVinculacion = tipoVinculacion;
		this.escalafon = escalafon;
		this.especialidad = especialidad;
		this.horasMaximasDocenciaSemanal = horasMaximasDocenciaSemanal;
	}

	public TipoVinculacion getTipoVinculacion() {
		return tipoVinculacion;
	}

	public void setTipoVinculacion(TipoVinculacion tipoVinculacion) {
		this.tipoVinculacion = tipoVinculacion;
	}

	public String getEscalafon() {
		return escalafon;
	}

	public void setEscalafon(String escalafon) {
		this.escalafon = escalafon;
	}

	public String getEspecialidad() {
		return especialidad;
	}

	public void setEspecialidad(String especialidad) {
		this.especialidad = especialidad;
	}

	public int getHorasMaximasDocenciaSemanal() {
		return horasMaximasDocenciaSemanal;
	}

	public void setHorasMaximasDocenciaSemanal(int horasMaximasDocenciaSemanal) {
		this.horasMaximasDocenciaSemanal = horasMaximasDocenciaSemanal;
	}

	@Override
	public String toString() {
		return "Docente{id=" + getId() + "}";
	}
}
