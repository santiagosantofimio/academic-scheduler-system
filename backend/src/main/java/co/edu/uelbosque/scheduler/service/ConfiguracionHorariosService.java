package co.edu.uelbosque.scheduler.service;

import java.time.Duration;
import java.util.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import co.edu.uelbosque.scheduler.dto.ConfiguracionHorariosDTO;
import co.edu.uelbosque.scheduler.exception.*;
import co.edu.uelbosque.scheduler.util.DtoMapper;
import co.edu.uelbosque.scheduler.util.ReglasHorario;
import co.edu.uelbosque.scheduler.entity.*;
import co.edu.uelbosque.scheduler.entity.EstadoSesion;
import co.edu.uelbosque.scheduler.repository.*;
import static co.edu.uelbosque.scheduler.util.LanzadorDeExcepcion.*;

@Service
@Transactional(readOnly = true)
public class ConfiguracionHorariosService extends AbstractCrudService<ConfiguracionHorarios, ConfiguracionHorariosDTO> {
	private final ConfiguracionHorariosRepository rep;
	private final SesionHorarioRepository sesiones;

	public ConfiguracionHorariosService(ConfiguracionHorariosRepository rep, SesionHorarioRepository sesiones) {
		super(rep, "ConfiguracionHorarios", rep);
		this.rep = rep;
		this.sesiones = sesiones;
	}

	@Override
	protected ConfiguracionHorarios nuevaEntidad() {
		return new ConfiguracionHorarios();
	}

	@Override
	protected ConfiguracionHorariosDTO toDto(ConfiguracionHorarios entity) {
		return DtoMapper.toDto(entity);
	}

	@Override
	protected Long idDto(ConfiguracionHorariosDTO data) {
		return data.getIdConfiguracion();
	}

	@Override
	protected void aplicar(ConfiguracionHorariosDTO data, ConfiguracionHorarios entity, boolean nuevo) {
		List<ConfiguracionHorarios> existentes = rep.bloquearConfiguraciones();
		if (nuevo && !existentes.isEmpty())
			throw new DuplicadoException("Existe una configuracion institucional; actualicela");
		verificarHorario(data.getHoraInicioSemana(), data.getHoraFinSemana());
		verificarHorario(data.getHoraInicioSabado(), data.getHoraFinSabado());
		if (data.getHoraInicioDescanso() != null || data.getHoraFinDescanso() != null)
			verificarHorario(data.getHoraInicioDescanso(), data.getHoraFinDescanso());
		if (data.getDuracionSesionMinutos() <= 0)
			throw new HorarioInvalidoException("La duracion debe ser positiva");
		verificarSesionesSemanales(data.getMaxSesionesSemanales());
		DtoMapper.copy(data, entity);
		List<SesionHorario> activas = sesiones.findByEstado(EstadoSesion.PROGRAMADA).orElseGet(List::of);
		Map<Long, List<SesionHorario>> porGrupo = new HashMap<>();
		for (SesionHorario sesion : activas) {
			if (!ReglasHorario.franja(entity, sesion.getDiaSemana(), sesion.getHoraInicio(), sesion.getHoraFin())
					|| !Duration.between(sesion.getHoraInicio(), sesion.getHoraFin())
							.equals(Duration.ofMinutes(entity.getDuracionSesionMinutos())))
				throw new HorarioInvalidoException("La configuracion invalida sesiones existentes; cancelelas primero");
			porGrupo.computeIfAbsent(sesion.getGrupo().getIdGrupo(), k -> new ArrayList<>()).add(sesion);
		}
		for (List<SesionHorario> grupo : porGrupo.values()) {
			if (grupo.size() > entity.getMaxSesionesSemanales())
				throw new SesionesSemanalesInvalidasException(
						"El maximo es inferior a las sesiones existentes de un grupo");
			if (entity.isEvitarDiasConsecutivos())
				for (SesionHorario a : grupo)
					for (SesionHorario b : grupo)
						if (ReglasHorario.consecutivos(a.getDiaSemana(), b.getDiaSemana()))
							throw new HorarioInvalidoException("Hay sesiones existentes en dias consecutivos");
		}
	}

	public ConfiguracionHorariosDTO getConfiguracion() {
		return toDto(unica(rep.findAll()));
	}

	static ConfiguracionHorarios unica(List<ConfiguracionHorarios> registros) {
		if (registros.isEmpty())
			throw new RecursoNoEncontradoException("Debe registrar la configuracion institucional antes de programar");
		if (registros.size() != 1)
			throw new DatosInvalidosException("La configuracion institucional es ambigua: debe existir una sola");
		return registros.getFirst();
	}

	@Override
	@Transactional
	public int deleteById(Long id) {
		rep.bloquearConfiguraciones();
		if (!sesiones.findByEstado(EstadoSesion.PROGRAMADA).orElseGet(List::of).isEmpty())
			throw new EstadoInvalidoException("No puede eliminar la configuracion con sesiones programadas");
		return super.deleteById(id);
	}
}
