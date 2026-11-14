package co.edu.uelbosque.scheduler.service;

import java.time.DayOfWeek;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import co.edu.uelbosque.scheduler.dto.ClaseAfectadaDTO;
import co.edu.uelbosque.scheduler.dto.NovedadDocenteDTO;
import co.edu.uelbosque.scheduler.entity.*;
import co.edu.uelbosque.scheduler.exception.*;
import co.edu.uelbosque.scheduler.repository.*;
import co.edu.uelbosque.scheduler.util.DtoMapper;
import static co.edu.uelbosque.scheduler.util.LanzadorDeExcepcion.*;

@Service
@Transactional(readOnly = true)
public class NovedadDocenteService {
	private static final int ALTERNATIVAS = 8;

	private final NovedadDocenteRepository rep;
	private final DocenteRepository docentes;
	private final SesionHorarioRepository sesiones;
	private final SesionHorarioService horario;
	private final ConfiguracionHorariosRepository configuraciones;
	private final AuditoriaService auditoria;

	public NovedadDocenteService(NovedadDocenteRepository rep, DocenteRepository docentes,
			SesionHorarioRepository sesiones, SesionHorarioService horario,
			ConfiguracionHorariosRepository configuraciones, AuditoriaService auditoria) {
		this.rep = rep;
		this.docentes = docentes;
		this.sesiones = sesiones;
		this.horario = horario;
		this.configuraciones = configuraciones;
		this.auditoria = auditoria;
	}

	public List<NovedadDocenteDTO> listar() {
		return rep.findAllByOrderByIdNovedadDesc().stream().map(NovedadDocenteService::toDto).toList();
	}

	@Transactional
	public NovedadDocenteDTO registrar(NovedadDocenteDTO data) {
		configuraciones.bloquearConfiguraciones();
		if (data == null)
			throw new DatosInvalidosException("La novedad es obligatoria");
		verificarId(data.docenteId());
		Docente docente = docentes.findById(data.docenteId())
				.orElseThrow(() -> new RecursoNoEncontradoException("Docente no encontrado"));
		if (data.tipo() == null)
			throw new DatosInvalidosException("El tipo de novedad es obligatorio");
		if (data.diaSemana() == null || data.diaSemana() == DayOfWeek.SUNDAY)
			throw new HorarioInvalidoException("El dia de la novedad debe estar entre lunes y sabado");
		if ((data.horaInicio() == null) != (data.horaFin() == null))
			throw new HorarioInvalidoException("Indique la hora de inicio y la de fin, o ninguna para todo el dia");
		if (data.horaInicio() != null)
			verificarHorario(data.horaInicio(), data.horaFin());
		String descripcion = data.descripcion() == null ? null : data.descripcion().strip();
		if (descripcion != null && descripcion.length() > 500)
			throw new DatosInvalidosException("La descripcion admite maximo 500 caracteres");
		NovedadDocente novedad = new NovedadDocente();
		novedad.setDocente(docente);
		novedad.setTipo(data.tipo());
		novedad.setDiaSemana(data.diaSemana());
		novedad.setHoraInicio(data.horaInicio());
		novedad.setHoraFin(data.horaFin());
		novedad.setDescripcion(descripcion == null || descripcion.isEmpty() ? null : descripcion);
		novedad.setEstado(EstadoNovedad.ABIERTA);
		novedad.setRegistradaEn(auditoria.ahora());
		novedad.setRegistradaPor(auditoria.usuarioActual());
		rep.saveAndFlush(novedad);
		auditoria.registrar("REGISTRAR_NOVEDAD", texto(novedad));
		return toDto(novedad);
	}

	@Transactional
	public NovedadDocenteDTO cerrar(Long id) {
		NovedadDocente novedad = requerida(id);
		if (novedad.getEstado() == EstadoNovedad.CERRADA)
			throw new EstadoInvalidoException("La novedad ya esta cerrada");
		novedad.setEstado(EstadoNovedad.CERRADA);
		auditoria.registrar("CERRAR_NOVEDAD", texto(novedad));
		return toDto(novedad);
	}

	public List<ClaseAfectadaDTO> afectadas(Long id) {
		NovedadDocente novedad = requerida(id);
		if (novedad.getEstado() == EstadoNovedad.CERRADA)
			return List.of();
		return sesiones.findByDocenteId(novedad.getDocente().getId()).stream()
				.filter(s -> s.getEstado() == EstadoSesion.PROGRAMADA)
				.filter(s -> novedad.bloquea(s.getDiaSemana(), s.getHoraInicio(), s.getHoraFin()))
				.sorted(Comparator.comparing(SesionHorario::getDiaSemana).thenComparing(SesionHorario::getHoraInicio))
				.map(s -> new ClaseAfectadaDTO(DtoMapper.toDto(s),
						s.getGrupo().getAsignatura().getCodigo() + " " + s.getGrupo().getAsignatura().getNombre(),
						s.getGrupo().getNumeroGrupo(), horario.alternativas(s, ALTERNATIVAS)))
				.toList();
	}

	private NovedadDocente requerida(Long id) {
		verificarId(id);
		return rep.findById(id).orElseThrow(() -> new RecursoNoEncontradoException("Novedad no encontrada: " + id));
	}

	private static String texto(NovedadDocente n) {
		Docente d = n.getDocente();
		String tipo = switch (n.getTipo()) {
		case INCAPACIDAD -> "incapacidad";
		case CAMBIO_DISPONIBILIDAD -> "cambio de disponibilidad";
		case OTRA -> "otra novedad";
		};
		return d.getNombre() + " " + d.getApellido() + ", " + tipo + ", "
				+ n.getDiaSemana().getDisplayName(java.time.format.TextStyle.FULL, java.util.Locale.of("es"))
				+ (n.getHoraInicio() == null ? " todo el dia" : " " + n.getHoraInicio() + "-" + n.getHoraFin());
	}

	private static NovedadDocenteDTO toDto(NovedadDocente n) {
		Docente d = n.getDocente();
		return new NovedadDocenteDTO(n.getIdNovedad(), d.getId(), d.getNombre() + " " + d.getApellido(), n.getTipo(),
				n.getDiaSemana(), n.getHoraInicio(), n.getHoraFin(), n.getDescripcion(), n.getEstado(),
				n.getRegistradaEn(), n.getRegistradaPor());
	}
}
