package co.edu.uelbosque.scheduler.dto;

import java.time.*;
import java.util.Objects;
import co.edu.uelbosque.scheduler.entity.*;
import co.edu.uelbosque.scheduler.entity.Usuario;

/**
 * Conserva la generalizacion a Usuario dibujada en el UML; no es una entidad
 * JPA.
 */
public class DocenteDTO extends Usuario {
	private TipoVinculacion tipoVinculacion;
	private String escalafon;
	private String especialidad;
	private int horasMaximasDocenciaSemanal;

	public DocenteDTO() {

	}

	public DocenteDTO(TipoVinculacion tipoVinculacion) {
		this.tipoVinculacion = tipoVinculacion;
	}

	public DocenteDTO(TipoVinculacion tipoVinculacion, String escalafon, String especialidad,
			int horasMaximasDocenciaSemanal) {
		this.tipoVinculacion = tipoVinculacion;
		this.escalafon = escalafon;
		this.especialidad = especialidad;
		this.horasMaximasDocenciaSemanal = horasMaximasDocenciaSemanal;
	}

	public DocenteDTO(long idDocente, TipoVinculacion tipoVinculacion, String escalafon, String especialidad,
			int horasMaximasDocenciaSemanal) {
		setId(idDocente);
		this.tipoVinculacion = tipoVinculacion;
		this.escalafon = escalafon;
		this.especialidad = especialidad;
		this.horasMaximasDocenciaSemanal = horasMaximasDocenciaSemanal;
	}

	public DocenteDTO(String nombre, String apellido, String numeroDocumento, String correoInstitucional,
			String contrasenia, boolean estado) {
		super(nombre, apellido, numeroDocumento, correoInstitucional, contrasenia, estado);
	}

	public DocenteDTO(String nombre, String apellido, String numeroDocumento, String correoInstitucional,
			String contrasenia, boolean estado, TipoVinculacion tipoVinculacion, String escalafon, String especialidad,
			int horasMaximasDocenciaSemanal) {
		super(nombre, apellido, numeroDocumento, correoInstitucional, contrasenia, estado);
		this.tipoVinculacion = tipoVinculacion;
		this.escalafon = escalafon;
		this.especialidad = especialidad;
		this.horasMaximasDocenciaSemanal = horasMaximasDocenciaSemanal;
	}

	public DocenteDTO(long id, String nombre, String apellido, String numeroDocumento, String correoInstitucional,
			String contrasenia, boolean estado) {
		super(id, nombre, apellido, numeroDocumento, correoInstitucional, contrasenia, estado);
	}

	public DocenteDTO(long id, String nombre, String apellido, String numeroDocumento, String correoInstitucional,
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

	/** Alias de la identidad heredada: evita dos IDs contradictorios. */
	public Long getIdDocente() {
		return getId();
	}

	public void setIdDocente(Long idDocente) {
		setId(idDocente);
	}

	public int HashCode() {
		return hashCode();
	}

	@Override
	public String toString() {
		return "DocenteDTO{id=" + getId() + "}";
	}
}
