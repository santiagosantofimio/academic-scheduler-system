package co.edu.uelbosque.scheduler.service;

import java.time.DayOfWeek;
import java.util.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import co.edu.uelbosque.scheduler.dto.DisponibilidadDocenteDTO;
import co.edu.uelbosque.scheduler.exception.*;
import co.edu.uelbosque.scheduler.util.DtoMapper;
import co.edu.uelbosque.scheduler.util.ReglasHorario;
import co.edu.uelbosque.scheduler.entity.*;
import co.edu.uelbosque.scheduler.repository.*;
import static co.edu.uelbosque.scheduler.util.LanzadorDeExcepcion.*;

@Service
@Transactional(readOnly = true)
public class DisponibilidadDocenteService extends AbstractCrudService<DisponibilidadDocente, DisponibilidadDocenteDTO> {
	private final DisponibilidadDocenteRepository rep;
	private final DocenteRepository docentes;
	private final SesionHorarioRepository sesiones;
	private final ConfiguracionHorariosRepository configuraciones;

	public DisponibilidadDocenteService(DisponibilidadDocenteRepository rep, DocenteRepository docentes,
			SesionHorarioRepository sesiones, ConfiguracionHorariosRepository configuraciones) {
		super(rep, "DisponibilidadDocente", configuraciones);
		this.rep = rep;
		this.docentes = docentes;
		this.sesiones = sesiones;
		this.configuraciones = configuraciones;
	}

	@Override
	protected DisponibilidadDocente nuevaEntidad() {
		return new DisponibilidadDocente();
	}

	@Override
	protected DisponibilidadDocenteDTO toDto(DisponibilidadDocente entity) {
		return DtoMapper.toDto(entity);
	}

	@Override
	protected Long idDto(DisponibilidadDocenteDTO data) {
		return data.getIdDisponibilidad();
	}

	@Override
	protected void aplicar(DisponibilidadDocenteDTO data, DisponibilidadDocente entity, boolean nuevo) {
		verificarHorario(data.getHoraInicio(), data.getHoraFin());
		if (data.getDiaSemana() == null || data.getDiaSemana() == DayOfWeek.SUNDAY)
			throw new HorarioInvalidoException("La disponibilidad debe ser de lunes a sabado");
		Long docenteId = data.getDocenteId() != null ? data.getDocenteId()
				: entity.getDocente() == null ? null : entity.getDocente().getId();
		verificarId(docenteId);
		if (!nuevo && !docenteId.equals(entity.getDocente().getId()))
			throw new DatosInvalidosException("Una disponibilidad no se traslada a otro docente");
		Docente docente = docentes.findById(docenteId)
				.orElseThrow(() -> new RecursoNoEncontradoException("Docente no encontrado"));
		List<DisponibilidadDocente> otras = rep.findByDocenteId(docenteId).stream()
				.filter(d -> !Objects.equals(d.getIdDisponibilidad(), entity.getIdDisponibilidad())).toList();
		if (otras.stream().anyMatch(d -> d.getDiaSemana() == data.getDiaSemana()
				&& ReglasHorario.seCruzan(d.getHoraInicio(), d.getHoraFin(), data.getHoraInicio(), data.getHoraFin())))
			throw new DisponibilidadInvalidaException("La franja se superpone con otra disponibilidad del docente");
		DisponibilidadDocente propuesta = new DisponibilidadDocente(data.getDiaSemana(), data.getHoraInicio(),
				data.getHoraFin());
		List<DisponibilidadDocente> ventanas = new ArrayList<>(otras);
		ventanas.add(propuesta);
		verificarSesiones(docenteId, ventanas);
		DtoMapper.copy(data, entity);
		entity.setDocente(docente);
	}

	@Override
	@Transactional
	public int deleteById(Long id) {
		configuraciones.bloquearConfiguraciones();
		DisponibilidadDocente actual = requerido(id);
		Long docenteId = actual.getDocente().getId();
		verificarSesiones(docenteId, rep.findByDocenteId(docenteId).stream()
				.filter(d -> !Objects.equals(d.getIdDisponibilidad(), id)).toList());
		return super.deleteById(id);
	}

	public List<DisponibilidadDocenteDTO> findByDocenteId(Long id) {
		verificarId(id);
		return rep.findByDocenteId(id).stream()
				.sorted(Comparator.comparing(DisponibilidadDocente::getDiaSemana)
						.thenComparing(DisponibilidadDocente::getHoraInicio))
				.map(DtoMapper::toDto).toList();
	}

	private void verificarSesiones(Long docenteId, List<DisponibilidadDocente> ventanas) {
		boolean fuera = sesiones.findByDocenteId(docenteId).stream()
				.filter(s -> s.getEstado() == EstadoSesion.PROGRAMADA).anyMatch(s -> !ReglasHorario
						.dentroDeDisponibilidad(ventanas, s.getDiaSemana(), s.getHoraInicio(), s.getHoraFin()));
		if (fuera)
			throw new DisponibilidadInvalidaException("El cambio deja sesiones programadas fuera de la disponibilidad");
	}
}
