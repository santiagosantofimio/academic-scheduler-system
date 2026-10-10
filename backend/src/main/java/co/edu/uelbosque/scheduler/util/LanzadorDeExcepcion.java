package co.edu.uelbosque.scheduler.util;

import java.time.LocalTime;
import java.util.regex.Pattern;
import co.edu.uelbosque.scheduler.exception.*;
import co.edu.uelbosque.scheduler.entity.*;

public final class LanzadorDeExcepcion {
	private static final Pattern CORREO = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

	private LanzadorDeExcepcion() {
	}

	public static void verificarId(Long id) {
		if (id == null || id <= 0)
			throw new IdInvalidoException("El ID debe ser positivo");
	}

	public static String texto(String valor, String campo) {
		if (valor == null || valor.isBlank())
			throw new DatosInvalidosException(campo + " es obligatorio");
		if (valor.length() > 255)
			throw new DatosInvalidosException(campo + " supera los 255 caracteres del campo persistente");
		return valor.strip();
	}

	public static void verificarNombre(String valor) {
		if (valor == null || valor.isBlank())
			throw new NombreInvalidoException("El nombre es obligatorio");
		texto(valor, "nombre");
	}

	public static void verificarNumeroDocumento(String valor) {
		if (valor == null || valor.isBlank())
			throw new NumeroDocumentoInvalidoException("El documento es obligatorio");
		texto(valor, "numeroDocumento");
	}

	public static boolean verificarCorreoInstitucional(String valor) {
		if (valor == null || !CORREO.matcher(valor.strip()).matches())
			throw new CorreoInstitucionalInvalidoException("El correo no tiene un formato valido");
		texto(valor, "correoInstitucional");
		return true;
	}

	public static void verificarTipoVinculacion(TipoVinculacion valor) {
		if (valor == null)
			throw new TipoVinculacionInvalidoException("La vinculacion es obligatoria");
	}

	public static void verificarNivelFormacion(NivelFormacion valor) {
		if (valor == null)
			throw new NivelFormacionInvalidoException("El nivel de formacion es obligatorio");
	}

	public static void verificarTipoExperiencia(TipoExperiencia valor) {
		if (valor == null)
			throw new DatosInvalidosException("El tipo de experiencia es obligatorio");
	}

	public static void verificarAreaConocimiento(String valor) {
		if (valor == null || valor.isBlank())
			throw new AreaConocimientoInvalidaException("El area de conocimiento es obligatoria");
		texto(valor, "areaConocimiento");
	}

	public static void verificarSemestre(int valor) {
		if (valor <= 0)
			throw new SemestreInvalidoException("El semestre debe ser positivo");
	}

	public static void verificarCreditos(int valor) {
		if (valor <= 0)
			throw new CreditosInvalidosException("Los creditos deben ser positivos");
	}

	public static void verificarSesionesSemanales(int valor) {
		if (valor <= 0)
			throw new SesionesSemanalesInvalidasException("Las sesiones semanales deben ser positivas");
	}

	public static void verificarHorario(LocalTime inicio, LocalTime fin) {
		if (inicio == null || fin == null || !inicio.isBefore(fin))
			throw new HorarioInvalidoException("La hora inicial debe ser anterior a la final en el mismo dia");
	}

	public static void verificarDisponibilidad(boolean disponible) {
		if (!disponible)
			throw new DisponibilidadInvalidaException("El docente no tiene disponibilidad operativa");
	}

	/** true significa que se encontro un cruce. */
	public static void verificarCruceHorario(boolean hayCruce) {
		if (hayCruce)
			throw new CruceHorarioException("La franja se superpone con otra sesion programada");
	}

	public static void verificarGrupo(Long id) {
		if (id == null || id <= 0)
			throw new GrupoValidoException("El identificador del grupo no es valido");
	}

	public static void verificarAsignacionAcademica(boolean valida) {
		if (!valida)
			throw new AsignacionAcademicaInvalidaException("El grupo debe tener un docente activo asignado");
	}

	public static void verificarDuplicado(boolean existe, String recurso) {
		if (existe)
			throw new DuplicadoException(recurso + " ya existe");
	}

	public static void verificarEstado(Object estado) {
		if (estado == null)
			throw new EstadoInvalidoException("El estado es obligatorio");
	}
}
