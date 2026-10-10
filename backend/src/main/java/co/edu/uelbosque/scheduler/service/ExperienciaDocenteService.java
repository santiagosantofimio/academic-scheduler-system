package co.edu.uelbosque.scheduler.service;

import java.util.*;
import java.time.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import co.edu.uelbosque.scheduler.dto.*;
import co.edu.uelbosque.scheduler.exception.*;
import co.edu.uelbosque.scheduler.util.DtoMapper;
import co.edu.uelbosque.scheduler.util.*;
import co.edu.uelbosque.scheduler.entity.*;
import co.edu.uelbosque.scheduler.repository.*;
import static co.edu.uelbosque.scheduler.util.LanzadorDeExcepcion.*;

@Service
@Transactional(readOnly = true)
public class ExperienciaDocenteService extends AbstractCrudService<ExperienciaDocente, ExperienciaDocenteDTO> {
	private final ExperienciaDocenteRepository rep;
	private final DocenteRepository docentes;
	private final Clock clock;
	private final RequisitoAsignaturaService requisitos;
	private final ConfiguracionHorariosRepository configuraciones;

	public ExperienciaDocenteService(ExperienciaDocenteRepository rep, DocenteRepository docentes, Clock clock,
			ConfiguracionHorariosRepository configuraciones, RequisitoAsignaturaService requisitos) {
		super(rep, "ExperienciaDocente", configuraciones);
		this.rep = rep;
		this.docentes = docentes;
		this.clock = clock;
		this.requisitos = requisitos;
		this.configuraciones = configuraciones;
	}

	@Override
	@Transactional
	public int create(ExperienciaDocenteDTO data) {
		int resultado = super.create(data);
		requisitos.validarAsignacionesDocente(data.getDocenteId());
		return resultado;
	}

	@Override
	@Transactional
	public int updateById(Long id, ExperienciaDocenteDTO data) {
		configuraciones.bloquearConfiguraciones();
		Long docenteAnterior = requerido(id).getDocente().getId();
		int resultado = super.updateById(id, data);
		requisitos.validarAsignacionesDocente(docenteAnterior);
		if (data.getDocenteId() != null && !data.getDocenteId().equals(docenteAnterior))
			requisitos.validarAsignacionesDocente(data.getDocenteId());
		return resultado;
	}

	@Override
	@Transactional
	public int deleteById(Long id) {
		configuraciones.bloquearConfiguraciones();
		Long docenteId = requerido(id).getDocente().getId();
		int resultado = super.deleteById(id);
		// Una eliminacion incompatible revierte la transaccion, incluido el DELETE ya
		// enviado.
		requisitos.validarAsignacionesDocente(docenteId);
		return resultado;
	}

	@Override
	protected ExperienciaDocente nuevaEntidad() {
		return new ExperienciaDocente();
	}

	@Override
	protected ExperienciaDocenteDTO toDto(ExperienciaDocente entity) {
		return DtoMapper.toDto(entity);
	}

	@Override
	protected Long idDto(ExperienciaDocenteDTO data) {
		return data.getIdExperiencia();
	}

	@Override
	protected void aplicar(ExperienciaDocenteDTO data, ExperienciaDocente entity, boolean nuevo) {
		verificarTipoExperiencia(data.getTipoExperiencia());
		verificarAreaConocimiento(data.getAreaConocimiento());
		texto(data.getInstitucion(), "institucion");
		texto(data.getCargo(), "cargo");
		texto(data.getDescripcion(), "descripcion");
		LocalDate hoy = LocalDate.now(clock);
		if (data.getFechaInicio() == null || data.getFechaInicio().isAfter(hoy) || (data.getFechaFin() != null
				&& (data.getFechaFin().isBefore(data.getFechaInicio()) || data.getFechaFin().isAfter(hoy))))
			throw new DatosInvalidosException(
					"Las fechas de experiencia son inconsistentes; fechaFin nula indica experiencia vigente");
		Long padre = data.getDocenteId() != null ? data.getDocenteId()
				: entity.getDocente() == null ? null : entity.getDocente().getId();
		verificarId(padre);
		Docente docente = docentes.findById(padre)
				.orElseThrow(() -> new RecursoNoEncontradoException("Docente no encontrado"));
		DtoMapper.copy(data, entity);
		entity.setDocente(docente);
	}

	public List<ExperienciaDocenteDTO> findByDocenteId(Long valor) {
		return rep.findByDocenteId(valor).orElseGet(List::of).stream().map(DtoMapper::toDto).toList();
	}

	public List<ExperienciaDocenteDTO> findByTipoExperiencia(TipoExperiencia valor) {
		return rep.findByTipoExperiencia(valor).orElseGet(List::of).stream().map(DtoMapper::toDto).toList();
	}

	public List<ExperienciaDocenteDTO> findByInstitucion(String valor) {
		return rep.findByInstitucion(valor).orElseGet(List::of).stream().map(DtoMapper::toDto).toList();
	}

	public List<ExperienciaDocenteDTO> findByAreaConocimiento(String valor) {
		return rep.findByAreaConocimiento(valor).orElseGet(List::of).stream().map(DtoMapper::toDto).toList();
	}

	@Transactional
	public int registrarExperienciaDocente(Long id, ExperienciaDocenteDTO data) {
		verificarId(id);
		if (data == null)
			throw new DatosInvalidosException("La experiencia es obligatoria");
		if (data.getDocenteId() != null && !id.equals(data.getDocenteId()))
			throw new IdInvalidoException("Docente de la ruta y del cuerpo no coinciden");
		data.setDocenteId(id);
		return create(data);
	}
}
