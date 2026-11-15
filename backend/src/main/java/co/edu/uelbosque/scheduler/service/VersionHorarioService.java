package co.edu.uelbosque.scheduler.service;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import co.edu.uelbosque.scheduler.dto.*;
import co.edu.uelbosque.scheduler.entity.*;
import co.edu.uelbosque.scheduler.exception.*;
import co.edu.uelbosque.scheduler.repository.*;
import co.edu.uelbosque.scheduler.util.DtoMapper;
import co.edu.uelbosque.scheduler.util.ReglasHorario;
import static co.edu.uelbosque.scheduler.util.LanzadorDeExcepcion.*;

@Service
@Transactional(readOnly = true)
public class VersionHorarioService {
	private final VersionHorarioRepository rep;
	private final SesionHorarioRepository sesiones;
	private final SesionHorarioService horario;
	private final ConfiguracionHorariosRepository configuraciones;
	private final AuditoriaService auditoria;

	public VersionHorarioService(VersionHorarioRepository rep, SesionHorarioRepository sesiones,
			SesionHorarioService horario, ConfiguracionHorariosRepository configuraciones,
			AuditoriaService auditoria) {
		this.rep = rep;
		this.sesiones = sesiones;
		this.horario = horario;
		this.configuraciones = configuraciones;
		this.auditoria = auditoria;
	}

	public List<VersionResumenDTO> listar() {
		Optional<ConfiguracionHorarios> config = configuracion();
		return rep.findAllByOrderByIdVersionDesc().stream().map(v -> resumen(v, config)).toList();
	}

	public VersionDetalleDTO detalle(Long id) {
		VersionHorario version = requerida(id);
		return new VersionDetalleDTO(resumen(version, configuracion()), version.getSesiones().stream()
				.filter(s -> s.getGrupo() != null).map(s -> DtoMapper.toDto(s.comoSesion())).toList());
	}

	public Optional<VersionHorario> vigente() {
		return rep.findFirstByEstadoOrderByIdVersionDesc(EstadoVersion.PUBLICADA);
	}

	public List<ConflictoDTO> conflictos() {
		return horario.conflictos();
	}

	@Transactional
	public VersionResumenDTO guardarBorrador(NombreVersionDTO data) {
		configuraciones.bloquearConfiguraciones();
		String nombre = nombre(data);
		List<SesionHorario> actuales = horario.programadas();
		if (actuales.isEmpty())
			throw new EstadoInvalidoException("No hay sesiones programadas para guardar como borrador");
		VersionHorario version = copia(nombre, EstadoVersion.BORRADOR, actuales);
		auditoria.registrar("GUARDAR_BORRADOR", "Borrador \"" + nombre + "\" con " + actuales.size() + " sesiones");
		return resumen(version, configuracion());
	}

	@Transactional
	public VersionResumenDTO publicar(NombreVersionDTO data) {
		configuraciones.bloquearConfiguraciones();
		String nombre = nombre(data);
		List<SesionHorario> actuales = horario.programadas();
		if (actuales.isEmpty())
			throw new PublicacionRechazadaException("No hay sesiones programadas para publicar");
		List<ConflictoDTO> conflictos = horario.conflictos();
		if (!conflictos.isEmpty())
			throw new PublicacionRechazadaException("El horario tiene " + conflictos.size()
					+ " conflictos con reglas obligatorias; resuelvalos antes de publicar. Primero: "
					+ conflictos.getFirst().descripcion() + " - " + conflictos.getFirst().regla());
		for (VersionHorario anterior : rep.findByEstado(EstadoVersion.PUBLICADA))
			anterior.setEstado(EstadoVersion.HISTORICA);
		VersionHorario version = copia(nombre, EstadoVersion.PUBLICADA, actuales);
		version.publicar(auditoria.ahora(), auditoria.usuarioActual());
		rep.saveAndFlush(version);
		auditoria.registrar("PUBLICAR", "Version \"" + nombre + "\" publicada con " + actuales.size() + " sesiones");
		return resumen(version, configuracion());
	}

	@Transactional
	public ResultadoCargaDTO cargar(Long id) {
		configuraciones.bloquearConfiguraciones();
		VersionHorario version = requerida(id);
		List<VersionSesion> disponibles = version.getSesiones().stream().filter(s -> s.getGrupo() != null).toList();
		sesiones.deleteAllInBatch();
		sesiones.saveAllAndFlush(disponibles.stream().map(VersionSesion::comoSesion).toList());
		Long creada = null;
		if (version.getEstado() != EstadoVersion.BORRADOR) {
			VersionHorario recuperada = copia("Recuperada de " + version.getNombre(), EstadoVersion.BORRADOR,
					horario.programadas());
			recuperada.setOrigen(version);
			creada = recuperada.getIdVersion();
		}
		auditoria.registrar(version.getEstado() == EstadoVersion.BORRADOR ? "CARGAR_BORRADOR" : "RECUPERAR_VERSION",
				"Se cargo \"" + version.getNombre() + "\" como horario de trabajo (" + disponibles.size()
						+ " sesiones)");
		return new ResultadoCargaDTO(disponibles.size(), version.getSesiones().size() - disponibles.size(), creada,
				horario.conflictos());
	}

	@Transactional
	public void eliminarBorrador(Long id) {
		VersionHorario version = requerida(id);
		if (version.getEstado() != EstadoVersion.BORRADOR)
			throw new EstadoInvalidoException("Solo se eliminan borradores; las versiones publicadas se conservan");
		rep.delete(version);
		auditoria.registrar("ELIMINAR_BORRADOR", "Borrador \"" + version.getNombre() + "\" eliminado");
	}

	private VersionHorario copia(String nombre, EstadoVersion estado, List<SesionHorario> origen) {
		VersionHorario version = new VersionHorario(nombre, estado, auditoria.ahora(), auditoria.usuarioActual());
		for (SesionHorario s : origen)
			version.agregar(new VersionSesion(s.getGrupo(), s.getDiaSemana(), s.getHoraInicio(), s.getHoraFin()));
		return rep.saveAndFlush(version);
	}

	private VersionHorario requerida(Long id) {
		verificarId(id);
		return rep.findById(id).orElseThrow(() -> new RecursoNoEncontradoException("Version no encontrada: " + id));
	}

	private Optional<ConfiguracionHorarios> configuracion() {
		List<ConfiguracionHorarios> registros = configuraciones.findAll();
		return registros.size() == 1 ? Optional.of(registros.getFirst()) : Optional.empty();
	}

	private static String nombre(NombreVersionDTO data) {
		String nombre = data == null || data.nombre() == null ? "" : data.nombre().strip();
		if (nombre.isEmpty())
			throw new DatosInvalidosException("El nombre de la version es obligatorio");
		if (nombre.length() > 120)
			throw new DatosInvalidosException("El nombre de la version admite maximo 120 caracteres");
		return nombre;
	}

	private static VersionResumenDTO resumen(VersionHorario v, Optional<ConfiguracionHorarios> config) {
		VersionHorario origen = v.getOrigen();
		return new VersionResumenDTO(v.getIdVersion(), v.getNombre(), v.getEstado(), v.getCreadaEn(),
				v.getCreadaPor(), v.getPublicadaEn(), v.getPublicadaPor(),
				origen == null ? null : origen.getIdVersion(), origen == null ? null : origen.getNombre(),
				indicadores(v.getSesiones().stream().filter(s -> s.getGrupo() != null).map(VersionSesion::comoSesion)
						.toList(), config));
	}

	public static IndicadoresDTO indicadores(List<SesionHorario> lista, Optional<ConfiguracionHorarios> config) {
		Map<Long, List<SesionHorario>> porDocente = lista.stream().filter(s -> s.getGrupo().getDocente() != null)
				.collect(Collectors.groupingBy(s -> s.getGrupo().getDocente().getId()));
		List<Double> cargas = porDocente.values().stream().map(VersionHorarioService::horas).toList();
		double maxima = cargas.stream().mapToDouble(Double::doubleValue).max().orElse(0);
		double minima = cargas.stream().mapToDouble(Double::doubleValue).min().orElse(0);
		long muertos = 0;
		long dias = 0;
		for (List<SesionHorario> propias : porDocente.values()) {
			Map<DayOfWeek, List<SesionHorario>> porDia = propias.stream()
					.collect(Collectors.groupingBy(SesionHorario::getDiaSemana));
			dias += porDia.size();
			for (List<SesionHorario> delDia : porDia.values())
				muertos += minutosMuertos(delDia, config);
		}
		int grupos = (int) lista.stream().map(s -> s.getGrupo().getIdGrupo()).distinct().count();
		return new IndicadoresDTO(lista.size(), grupos, porDocente.size(), redondear(maxima), redondear(minima),
				redondear(maxima - minima), redondear(muertos / 60.0),
				porDocente.isEmpty() ? 0 : redondear((double) dias / porDocente.size()));
	}

	private static long minutosMuertos(List<SesionHorario> delDia, Optional<ConfiguracionHorarios> config) {
		List<SesionHorario> ordenadas = delDia.stream().sorted(Comparator.comparing(SesionHorario::getHoraInicio))
				.toList();
		long total = 0;
		for (int i = 1; i < ordenadas.size(); i++) {
			LocalTime desde = ordenadas.get(i - 1).getHoraFin();
			LocalTime hasta = ordenadas.get(i).getHoraInicio();
			if (!hasta.isAfter(desde))
				continue;
			long hueco = Duration.between(desde, hasta).toMinutes();
			if (config.isPresent() && config.get().getHoraInicioDescanso() != null) {
				LocalTime di = config.get().getHoraInicioDescanso(), df = config.get().getHoraFinDescanso();
				if (ReglasHorario.seCruzan(desde, hasta, di, df)) {
					LocalTime a = desde.isAfter(di) ? desde : di;
					LocalTime b = hasta.isBefore(df) ? hasta : df;
					hueco -= Duration.between(a, b).toMinutes();
				}
			}
			total += hueco;
		}
		return total;
	}

	private static double horas(List<SesionHorario> lista) {
		return lista.stream().mapToLong(s -> Duration.between(s.getHoraInicio(), s.getHoraFin()).toMinutes()).sum()
				/ 60.0;
	}

	private static double redondear(double valor) {
		return Math.round(valor * 10) / 10.0;
	}
}
