package co.edu.uelbosque.scheduler.service;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import co.edu.uelbosque.scheduler.dto.RequisitoAsignaturaDTO;
import co.edu.uelbosque.scheduler.exception.*;
import co.edu.uelbosque.scheduler.util.DtoMapper;
import co.edu.uelbosque.scheduler.entity.*;
import co.edu.uelbosque.scheduler.repository.*;
import static co.edu.uelbosque.scheduler.util.LanzadorDeExcepcion.*;

@Service
@Transactional(readOnly = true)
public class RequisitoAsignaturaService extends AbstractCrudService<RequisitoAsignatura, RequisitoAsignaturaDTO> {
	private final RequisitoAsignaturaRepository rep;
	private final AsignaturaServiceRepository asignaturas;
	private final DocenteRepository docentes;
	private final FormacionAcademicaRepository formaciones;
	private final ExperienciaDocenteRepository experiencias;
	private final GrupoRepository grupos;
	private final Clock clock;

	public RequisitoAsignaturaService(RequisitoAsignaturaRepository rep, AsignaturaServiceRepository asignaturas,
			DocenteRepository docentes, FormacionAcademicaRepository formaciones,
			ExperienciaDocenteRepository experiencias, GrupoRepository grupos, Clock clock,
			ConfiguracionHorariosRepository configuraciones) {
		super(rep, "RequisitoAsignatura", configuraciones);
		this.rep = rep;
		this.asignaturas = asignaturas;
		this.docentes = docentes;
		this.formaciones = formaciones;
		this.experiencias = experiencias;
		this.grupos = grupos;
		this.clock = clock;
	}

	@Override
	protected RequisitoAsignatura nuevaEntidad() {
		return new RequisitoAsignatura();
	}

	@Override
	protected RequisitoAsignaturaDTO toDto(RequisitoAsignatura entity) {
		return DtoMapper.toDto(entity);
	}

	@Override
	protected Long idDto(RequisitoAsignaturaDTO data) {
		return data.getIdRequisito();
	}

	@Override
	protected void aplicar(RequisitoAsignaturaDTO data, RequisitoAsignatura entity, boolean nuevo) {
		if (data.getTipoRequisito() == null)
			throw new DatosInvalidosException("El tipo de requisito es obligatorio");
		texto(data.getDescripcion(), "descripcion");
		verificarAreaConocimiento(data.getAreaConocimiento());
		if (data.getAniosExperienciaMinima() < 0)
			throw new DatosInvalidosException("Los anios de experiencia no pueden ser negativos");
		if (data.getTipoRequisito() == TipoRequisito.FORMACION_ACADEMICA)
			verificarNivelFormacion(data.getNivelFormacionMinimo());
		Long id = data.getAsignaturaId() != null ? data.getAsignaturaId()
				: entity.getAsignatura() == null ? null : entity.getAsignatura().getIdAsginatura();
		verificarId(id);
		Asignatura asignatura = asignaturas.findById(id)
				.orElseThrow(() -> new RecursoNoEncontradoException("Asignatura no encontrada"));
		DtoMapper.copy(data, entity);
		entity.setAsignatura(asignatura);
		for (Grupo grupo : grupos.findByAsignaturaId(id).orElseGet(List::of)) {
			if (grupo.getEstado() == EstadoGrupo.ACTIVO && grupo.getDocente() != null)
				verificarRequisito(entity, grupo.getDocente());
		}
	}

	public List<RequisitoAsignaturaDTO> findByAsignaturaId(Long id) {
		verificarId(id);
		return rep.findByAsignaturaId(id).orElseGet(List::of).stream().map(DtoMapper::toDto).toList();
	}

	public List<RequisitoAsignaturaDTO> findByTipoRequisito(TipoRequisito tipo) {
		return rep.findByTipoRequisito(tipo).orElseGet(List::of).stream().map(DtoMapper::toDto).toList();
	}

	public List<RequisitoAsignaturaDTO> findByAreaConocimiento(String area) {
		return rep.findByAreaConocimiento(area).orElseGet(List::of).stream().map(DtoMapper::toDto).toList();
	}

	@Transactional
	public int registrarRequisitoAsignatura(Long id, RequisitoAsignaturaDTO data) {
		verificarId(id);
		if (data == null)
			throw new DatosInvalidosException("El requisito es obligatorio");
		if (data.getAsignaturaId() != null && !id.equals(data.getAsignaturaId()))
			throw new IdInvalidoException("Asignatura de la ruta y del cuerpo no coinciden");
		data.setAsignaturaId(id);
		return create(data);
	}

	public void validarDocente(Long asignaturaId, Long docenteId) {
		verificarId(asignaturaId);
		verificarId(docenteId);
		Docente docente = docentes.findById(docenteId)
				.orElseThrow(() -> new RecursoNoEncontradoException("Docente no encontrado"));
		verificarAsignacionAcademica(docente.isEstado());
		for (RequisitoAsignatura requisito : rep.findByAsignaturaId(asignaturaId).orElseGet(List::of))
			verificarRequisito(requisito, docente);
	}

	public void validarAsignacionesDocente(Long docenteId) {
		verificarId(docenteId);
		for (Grupo grupo : grupos.buscarPorDocente(docenteId))
			if (grupo.getEstado() == EstadoGrupo.ACTIVO)
				validarDocente(grupo.getAsignatura().getIdAsginatura(), docenteId);
	}

	private void verificarRequisito(RequisitoAsignatura requisito, Docente docente) {
		String area = requisito.getAreaConocimiento();
		if (requisito.getTipoRequisito() == TipoRequisito.FORMACION_ACADEMICA) {
			NivelFormacion nivelMinimo = requisito.getNivelFormacionMinimo();
			boolean cumple = formaciones.findByDocenteId(docente.getId()).orElseGet(List::of).stream()
					.anyMatch(f -> mismaArea(f.getAreaConocimiento(), area)
							&& nivel(f.getNivelFormacion()) >= nivel(nivelMinimo));
			if (!cumple) {
				String nivelRequerido = nivelMinimo == null ? "" : " con nivel mínimo " + nivelMinimo;
				throw requisitoIncumplido(requisito, docente, "formación académica en " + area + nivelRequerido);
			}
			return;
		}

		TipoExperiencia tipo = switch (requisito.getTipoRequisito()) {
		case EXPERIENCIA_LABORAL -> TipoExperiencia.LABORAL;
		case EXPERIENCIA_DOCENTE -> TipoExperiencia.DOCENTE;
		case EXPERIENCIA_INVESTIGATIVA -> TipoExperiencia.INVESTIGATIVA;
		default -> throw new DatosInvalidosException("Tipo de requisito no evaluable");
		};
		List<ExperienciaDocente> registros = experiencias.findByDocenteIdAndTipoExperiencia(docente.getId(), tipo)
				.orElseGet(List::of).stream().filter(e -> mismaArea(e.getAreaConocimiento(), area)).toList();
		int aniosMinimos = requisito.getAniosExperienciaMinima();
		if (registros.isEmpty() || diasExperiencia(registros, LocalDate.now(clock)) < aniosMinimos * 365L) {
			String experiencia = "experiencia " + tipo.name().toLowerCase(Locale.ROOT) + " en " + area;
			String evidencia = aniosMinimos > 0
					? experiencia + " durante al menos " + aniosMinimos + (aniosMinimos == 1 ? " año" : " años")
					: "al menos un registro de " + experiencia;
			throw requisitoIncumplido(requisito, docente, evidencia);
		}
	}

	private static AsignacionAcademicaInvalidaException requisitoIncumplido(
			RequisitoAsignatura requisito, Docente docente, String evidencia) {
		return new AsignacionAcademicaInvalidaException(
				"El docente " + docente.getNombre() + " " + docente.getApellido()
						+ " no cumple el requisito «" + requisito.getDescripcion()
						+ "». En su ficha debe constar " + evidencia + ".");
	}

	private static boolean mismaArea(String a, String b) {
		return a != null && b != null && a.strip().equalsIgnoreCase(b.strip());
	}

	private static int nivel(NivelFormacion nivel) {
		if (nivel == null)
			return -1;
		return switch (nivel) {
		case PREGRADO -> 1;
		case ESPECIALIZACION -> 2;
		case MAESTRIA -> 3;
		case DOCTORADO -> 4;
		};
	}

	static long diasExperiencia(List<ExperienciaDocente> registros, LocalDate hoy) {
		List<ExperienciaDocente> ordenados = registros.stream()
				.sorted(Comparator.comparing(ExperienciaDocente::getFechaInicio)).toList();
		LocalDate inicio = null, fin = null;
		long dias = 0;
		for (ExperienciaDocente experiencia : ordenados) {
			LocalDate siguienteInicio = experiencia.getFechaInicio();
			LocalDate siguienteFin = experiencia.getFechaFin() == null ? hoy : experiencia.getFechaFin();
			if (inicio == null) {
				inicio = siguienteInicio;
				fin = siguienteFin;
			} else if (!siguienteInicio.isAfter(fin)) {
				if (siguienteFin.isAfter(fin))
					fin = siguienteFin;
			} else {
				dias += ChronoUnit.DAYS.between(inicio, fin);
				inicio = siguienteInicio;
				fin = siguienteFin;
			}
		}
		return dias + (inicio == null ? 0 : ChronoUnit.DAYS.between(inicio, fin));
	}
}
