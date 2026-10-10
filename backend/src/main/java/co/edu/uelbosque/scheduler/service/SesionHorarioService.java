package co.edu.uelbosque.scheduler.service;

import java.time.*;
import java.util.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import co.edu.uelbosque.scheduler.dto.SesionHorarioDTO;
import co.edu.uelbosque.scheduler.exception.*;
import co.edu.uelbosque.scheduler.util.DtoMapper;
import co.edu.uelbosque.scheduler.util.ReglasHorario;
import co.edu.uelbosque.scheduler.entity.*;
import co.edu.uelbosque.scheduler.repository.*;
import static co.edu.uelbosque.scheduler.util.LanzadorDeExcepcion.*;

@Service
@Transactional(readOnly = true)
public class SesionHorarioService extends AbstractCrudService<SesionHorario, SesionHorarioDTO> {
	private final SesionHorarioRepository rep;
	private final GrupoRepository grupos;
	private final DocenteRepository docentes;
	private final ConfiguracionHorariosRepository configuraciones;
	private final RequisitoAsignaturaService requisitos;

	public SesionHorarioService(SesionHorarioRepository rep, GrupoRepository grupos, DocenteRepository docentes,
			ConfiguracionHorariosRepository configuraciones, RequisitoAsignaturaService requisitos) {
		super(rep, "SesionHorario", configuraciones);
		this.rep = rep;
		this.grupos = grupos;
		this.docentes = docentes;
		this.configuraciones = configuraciones;
		this.requisitos = requisitos;
	}

	@Override
	protected SesionHorario nuevaEntidad() {
		return new SesionHorario();
	}

	@Override
	protected SesionHorarioDTO toDto(SesionHorario entity) {
		return DtoMapper.toDto(entity);
	}

	@Override
	protected Long idDto(SesionHorarioDTO data) {
		return data.getIdSesion();
	}

	@Override
	protected void aplicar(SesionHorarioDTO data, SesionHorario entity, boolean nuevo) {
		// Todas las escrituras de programacion toman el mismo bloqueo institucional
		// primero.
		List<ConfiguracionHorarios> configuracion = configuraciones.bloquearConfiguraciones();
		verificarHorario(data.getHoraInicio(), data.getHoraFin());
		verificarEstado(data.getEstado());
		if (data.getDiaSemana() == null)
			throw new HorarioInvalidoException("El dia es obligatorio");
		Long grupoId = data.getGrupoId() != null ? data.getGrupoId()
				: entity.getGrupo() == null ? null : entity.getGrupo().getIdGrupo();
		Grupo grupo = grupoRequerido(grupoId);
		if (!nuevo && entity.getGrupo().getIdGrupo() != grupoId)
			throw new DatosInvalidosException("Una sesion no se traslada a otro grupo; cancelela y registre otra");
		SesionHorario candidata = new SesionHorario(data.getDiaSemana(), data.getHoraInicio(), data.getHoraFin(),
				data.getEstado());
		candidata.setIdSesion(entity.getIdSesion());
		candidata.setGrupo(grupo);
		if (data.getEstado() == EstadoSesion.PROGRAMADA) {
			ConfiguracionHorarios config = ConfiguracionHorariosService.unica(configuracion);
			Docente docente = docenteParaProgramar(grupo, true);
			validarCandidata(candidata, config, grupo, docente, activasGrupo(grupoId), activasDocente(docente.getId()));
		}
		DtoMapper.copy(data, entity);
		entity.setGrupo(grupo);
	}

	@Transactional
	public int registrarSesion(Long grupoId, SesionHorarioDTO data) {
		verificarGrupo(grupoId);
		if (data == null)
			throw new DatosInvalidosException("La sesion es obligatoria");
		if (data.getGrupoId() != null && !grupoId.equals(data.getGrupoId()))
			throw new IdInvalidoException("Grupo de la ruta y del cuerpo no coinciden");
		data.setGrupoId(grupoId);
		return create(data);
	}

	@Override
	@Transactional
	public int deleteById(Long id) {
		configuraciones.bloquearConfiguraciones();
		return super.deleteById(id);
	}

	public List<SesionHorarioDTO> findByGrupoId(Long id) {
		verificarGrupo(id);
		return ordenadas(rep.findByGrupoId(id).orElseGet(List::of));
	}

	public List<SesionHorarioDTO> findByDiaSemana(DayOfWeek dia) {
		if (dia == null)
			throw new HorarioInvalidoException("El dia es obligatorio");
		return ordenadas(rep.findByDiaSemana(dia).orElseGet(List::of));
	}

	public List<SesionHorarioDTO> findByDocenteId(Long id) {
		verificarId(id);
		return ordenadas(rep.findByDocenteId(id));
	}

	public boolean validarDisponibilidadDocente(Long id, DayOfWeek dia, LocalTime inicio, LocalTime fin) {
		verificarId(id);
		verificarHorario(inicio, fin);
		Docente docente = docentes.findById(id)
				.orElseThrow(() -> new RecursoNoEncontradoException("Docente no encontrado"));
		ConfiguracionHorarios config = ConfiguracionHorariosService.unica(configuraciones.findAll());
		List<SesionHorario> actuales = activasDocente(id);
		return docente.isEstado() && ReglasHorario.franja(config, dia, inicio, fin)
				&& actuales.stream().noneMatch(s -> cruce(s, dia, inicio, fin)) && segundos(actuales) + Duration
						.between(inicio, fin).getSeconds() <= docente.getHorasMaximasDocenciaSemanal() * 3600L;
	}

	/**
	 * true significa que existe un cruce, consistente con verificarCruceHorario.
	 */
	public boolean validarCruceDocente(Long id, DayOfWeek dia, LocalTime inicio, LocalTime fin) {
		verificarId(id);
		verificarHorario(inicio, fin);
		if (dia == null)
			throw new HorarioInvalidoException("El dia es obligatorio");
		if (!docentes.existsById(id))
			throw new RecursoNoEncontradoException("Docente no encontrado");
		return activasDocente(id).stream().anyMatch(s -> cruce(s, dia, inicio, fin));
	}

	public boolean validarFranjaInstitucional(DayOfWeek dia, LocalTime inicio, LocalTime fin) {
		return ReglasHorario.franja(ConfiguracionHorariosService.unica(configuraciones.findAll()), dia, inicio, fin);
	}

	/** El Long identifica al grupo; la restriccion se aplica a sus sesiones. */
	public boolean validarDiasConsecutivos(Long grupoId, DayOfWeek dia) {
		grupoRequerido(grupoId);
		if (dia == null)
			throw new HorarioInvalidoException("El dia es obligatorio");
		ConfiguracionHorarios config = ConfiguracionHorariosService.unica(configuraciones.findAll());
		return !config.isEvitarDiasConsecutivos()
				|| activasGrupo(grupoId).stream().noneMatch(s -> ReglasHorario.consecutivos(s.getDiaSemana(), dia));
	}

	/**
	 * Completa sesiones faltantes, conserva las existentes y persiste solo una
	 * solucion completa.
	 */
	@Transactional
	public List<SesionHorarioDTO> generarHorarioGrupo(Long grupoId) {
		verificarGrupo(grupoId);
		ConfiguracionHorarios config = ConfiguracionHorariosService.unica(configuraciones.bloquearConfiguraciones());
		Grupo grupo = grupoRequerido(grupoId);
		Docente docente = docenteParaProgramar(grupo, true);
		List<SesionHorario> existentes = activasGrupo(grupoId);
		List<SesionHorario> delDocente = activasDocente(docente.getId());
		int necesarias = grupo.getAsignatura().getSesionesSemanales();
		verificarSesionesSemanales(necesarias);
		if (necesarias > config.getMaxSesionesSemanales() || existentes.size() > necesarias)
			throw new SesionesSemanalesInvalidasException(
					"La cantidad requerida no es compatible con la configuracion o las sesiones existentes");
		for (SesionHorario sesion : existentes)
			validarCandidata(sesion, config, grupo, docente, existentes, delDocente);
		int faltantes = necesarias - existentes.size();
		if (faltantes == 0)
			return ordenadas(existentes);
		long segundosNuevos = (long) faltantes * config.getDuracionSesionMinutos() * 60;
		if (segundos(delDocente) + segundosNuevos > docente.getHorasMaximasDocenciaSemanal() * 3600L)
			throw new DisponibilidadInvalidaException("La generacion excederia la carga semanal del docente");

		List<List<SesionHorario>> opciones = new ArrayList<>();
		boolean[] ocupados = new boolean[6];
		for (int d = 1; d <= 6; d++) {
			DayOfWeek dia = DayOfWeek.of(d);
			ocupados[d - 1] = existentes.stream().anyMatch(s -> s.getDiaSemana() == dia);
			boolean bloqueado = config.isEvitarDiasConsecutivos()
					&& existentes.stream().anyMatch(s -> ReglasHorario.consecutivos(s.getDiaSemana(), dia));
			opciones.add(bloqueado ? List.of() : espaciosLibres(config, grupo, dia, delDocente));
		}
		List<SesionHorario> nuevas = new ArrayList<>();
		if (!seleccionar(0, faltantes, false, config.isEvitarDiasConsecutivos(), ocupados, opciones, nuevas,
				new HashSet<>()))
			throw new DisponibilidadInvalidaException(
					"No existe espacio suficiente para completar el grupo con las restricciones actuales; no se guardaron sesiones parciales");
		List<SesionHorario> completas = new ArrayList<>(existentes);
		completas.addAll(rep.saveAllAndFlush(nuevas));
		return ordenadas(completas);
	}

	private void validarCandidata(SesionHorario candidata, ConfiguracionHorarios config, Grupo grupo, Docente docente,
			List<SesionHorario> delGrupo, List<SesionHorario> delDocente) {
		if (!ReglasHorario.franja(config, candidata.getDiaSemana(), candidata.getHoraInicio(), candidata.getHoraFin()))
			throw new HorarioInvalidoException("La sesion esta fuera de la franja institucional o invade el descanso");
		if (!Duration.between(candidata.getHoraInicio(), candidata.getHoraFin())
				.equals(Duration.ofMinutes(config.getDuracionSesionMinutos())))
			throw new HorarioInvalidoException("La sesion no tiene la duracion institucional");
		List<SesionHorario> otrasGrupo = sinActual(delGrupo, candidata);
		List<SesionHorario> otrasDocente = sinActual(delDocente, candidata);
		verificarCruceHorario(otrasGrupo.stream()
				.anyMatch(s -> cruce(s, candidata.getDiaSemana(), candidata.getHoraInicio(), candidata.getHoraFin()))
				|| otrasDocente.stream().anyMatch(
						s -> cruce(s, candidata.getDiaSemana(), candidata.getHoraInicio(), candidata.getHoraFin())));
		if (otrasGrupo.size() + 1 > grupo.getAsignatura().getSesionesSemanales()
				|| otrasGrupo.size() + 1 > config.getMaxSesionesSemanales())
			throw new SesionesSemanalesInvalidasException("La sesion supera el total permitido para el grupo");
		if (config.isEvitarDiasConsecutivos() && otrasGrupo.stream()
				.anyMatch(s -> ReglasHorario.consecutivos(s.getDiaSemana(), candidata.getDiaSemana())))
			throw new HorarioInvalidoException("El grupo ya tiene una sesion en un dia consecutivo");
		if (segundos(otrasDocente) + Duration.between(candidata.getHoraInicio(), candidata.getHoraFin())
				.getSeconds() > docente.getHorasMaximasDocenciaSemanal() * 3600L)
			throw new DisponibilidadInvalidaException("Se excede la carga semanal maxima del docente");
	}

	private Grupo grupoRequerido(Long id) {
		verificarGrupo(id);
		return grupos.findById(id).orElseThrow(() -> new RecursoNoEncontradoException("Grupo no encontrado: " + id));
	}

	private Docente docenteParaProgramar(Grupo grupo, boolean bloquear) {
		if (grupo.getEstado() != EstadoGrupo.ACTIVO || !grupo.getAsignatura().isEstado()
				|| !grupo.getAsignatura().getPlanEstudios().isEstado())
			throw new EstadoInvalidoException("Grupo, asignatura y plan deben estar activos");
		verificarAsignacionAcademica(grupo.getDocente() != null);
		Long id = grupo.getDocente().getId();
		Docente docente = (bloquear ? docentes.bloquearPorId(id) : docentes.findById(id))
				.orElseThrow(() -> new RecursoNoEncontradoException("Docente no encontrado"));
		verificarAsignacionAcademica(docente.isEstado());
		requisitos.validarDocente(grupo.getAsignatura().getIdAsginatura(), id);
		return docente;
	}

	private List<SesionHorario> activasGrupo(Long id) {
		return rep.findByGrupoIdAndEstado(id, EstadoSesion.PROGRAMADA).orElseGet(List::of);
	}

	private List<SesionHorario> activasDocente(Long id) {
		return rep.findByDocenteId(id).stream().filter(s -> s.getEstado() == EstadoSesion.PROGRAMADA).toList();
	}

	private static List<SesionHorario> sinActual(List<SesionHorario> sesiones, SesionHorario actual) {
		return sesiones.stream()
				.filter(s -> actual.getIdSesion() == null || !actual.getIdSesion().equals(s.getIdSesion())).toList();
	}

	private static long segundos(List<SesionHorario> sesiones) {
		return sesiones.stream().mapToLong(s -> Duration.between(s.getHoraInicio(), s.getHoraFin()).getSeconds()).sum();
	}

	private static boolean cruce(SesionHorario s, DayOfWeek dia, LocalTime inicio, LocalTime fin) {
		return s.getDiaSemana() == dia && ReglasHorario.seCruzan(s.getHoraInicio(), s.getHoraFin(), inicio, fin);
	}

	private static List<SesionHorarioDTO> ordenadas(List<SesionHorario> sesiones) {
		return sesiones.stream()
				.sorted(Comparator.comparing(SesionHorario::getDiaSemana).thenComparing(SesionHorario::getHoraInicio))
				.map(DtoMapper::toDto).toList();
	}

	private record Franja(LocalTime inicio, LocalTime fin) {
	}

	private static List<SesionHorario> espaciosLibres(ConfiguracionHorarios config, Grupo grupo, DayOfWeek dia,
			List<SesionHorario> existentes) {
		LocalTime comienzo = ReglasHorario.inicio(config, dia), termino = ReglasHorario.fin(config, dia);
		List<Franja> ocupadas = new ArrayList<>();
		for (SesionHorario sesion : existentes)
			if (sesion.getDiaSemana() == dia)
				ocupadas.add(new Franja(sesion.getHoraInicio(), sesion.getHoraFin()));
		if (config.getHoraInicioDescanso() != null)
			ocupadas.add(new Franja(config.getHoraInicioDescanso(), config.getHoraFinDescanso()));
		ocupadas.sort(Comparator.comparing(Franja::inicio));
		List<SesionHorario> libres = new ArrayList<>();
		LocalTime cursor = comienzo;
		for (Franja ocupada : ocupadas) {
			if (!ocupada.fin().isAfter(cursor) || !ocupada.inicio().isBefore(termino))
				continue;
			if (ocupada.inicio().isAfter(cursor))
				agregarBloques(libres, grupo, dia, cursor, ocupada.inicio(), config.getDuracionSesionMinutos());
			cursor = ocupada.fin().isAfter(cursor) ? ocupada.fin() : cursor;
			if (!cursor.isBefore(termino))
				return libres;
		}
		agregarBloques(libres, grupo, dia, cursor, termino, config.getDuracionSesionMinutos());
		return libres;
	}

	private static void agregarBloques(List<SesionHorario> destino, Grupo grupo, DayOfWeek dia, LocalTime inicio,
			LocalTime fin, int minutos) {
		Duration duracion = Duration.ofMinutes(minutos);
		if (minutos <= 0)
			throw new HorarioInvalidoException("La duracion debe ser positiva");
		LocalTime cursor = inicio;
		while (Duration.between(cursor, fin).compareTo(duracion) >= 0) {
			LocalTime hasta = cursor.plusMinutes(minutos);
			SesionHorario sesion = new SesionHorario(dia, cursor, hasta, EstadoSesion.PROGRAMADA);
			sesion.setGrupo(grupo);
			destino.add(sesion);
			cursor = hasta;
		}
	}

	private record EstadoBusqueda(int dia, int restantes, boolean previo) {
	}

	private static boolean seleccionar(int dia, int restantes, boolean previo, boolean evitar, boolean[] existentes,
			List<List<SesionHorario>> opciones, List<SesionHorario> seleccionadas, Set<EstadoBusqueda> descartados) {
		if (restantes == 0)
			return true;
		if (dia == opciones.size())
			return false;
		EstadoBusqueda estado = new EstadoBusqueda(dia, restantes, previo);
		if (descartados.contains(estado))
			return false;
		int disponibles = 0;
		for (int i = dia; i < opciones.size(); i++)
			disponibles += opciones.get(i).size();
		if (disponibles < restantes)
			return false;
		for (int cantidad = Math.min(restantes, opciones.get(dia).size()); cantidad >= 0; cantidad--) {
			boolean usado = existentes[dia] || cantidad > 0;
			if (evitar && previo && usado)
				continue;
			int anterior = seleccionadas.size();
			seleccionadas.addAll(opciones.get(dia).subList(0, cantidad));
			if (seleccionar(dia + 1, restantes - cantidad, usado, evitar, existentes, opciones, seleccionadas,
					descartados))
				return true;
			seleccionadas.subList(anterior, seleccionadas.size()).clear();
		}
		descartados.add(estado);
		return false;
	}
}
