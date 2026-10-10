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
public class FormacionAcademicaService extends AbstractCrudService<FormacionAcademica, FormacionAcademicaDTO> {
	private final FormacionAcademicaRepository rep;
	private final DocenteRepository docentes;
	private final Clock clock;
	private final RequisitoAsignaturaService requisitos;
	private final ConfiguracionHorariosRepository configuraciones;

	public FormacionAcademicaService(FormacionAcademicaRepository rep, DocenteRepository docentes, Clock clock,
			ConfiguracionHorariosRepository configuraciones, RequisitoAsignaturaService requisitos) {
		super(rep, "FormacionAcademica", configuraciones);
		this.rep = rep;
		this.docentes = docentes;
		this.clock = clock;
		this.requisitos = requisitos;
		this.configuraciones = configuraciones;
	}

	@Override
	@Transactional
	public int create(FormacionAcademicaDTO data) {
		int resultado = super.create(data);
		requisitos.validarAsignacionesDocente(data.getDocenteId());
		return resultado;
	}

	@Override
	@Transactional
	public int updateById(Long id, FormacionAcademicaDTO data) {
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
	protected FormacionAcademica nuevaEntidad() {
		return new FormacionAcademica();
	}

	@Override
	protected FormacionAcademicaDTO toDto(FormacionAcademica entity) {
		return DtoMapper.toDto(entity);
	}

	@Override
	protected Long idDto(FormacionAcademicaDTO data) {
		return data.getIdFormacion();
	}

	@Override
	protected void aplicar(FormacionAcademicaDTO data, FormacionAcademica entity, boolean nuevo) {
		verificarNombre(data.getTituloObtenido());
		verificarNivelFormacion(data.getNivelFormacion());
		verificarAreaConocimiento(data.getAreaConocimiento());
		texto(data.getInstitucion(), "institucion");
		if (data.getAnioGraduacion() <= 0 || data.getAnioGraduacion() > Year.now(clock).getValue())
			throw new DatosInvalidosException("El anio de graduacion debe ser valido y no futuro");
		Long padre = data.getDocenteId() != null ? data.getDocenteId()
				: entity.getDocente() == null ? null : entity.getDocente().getId();
		verificarId(padre);
		Docente docente = docentes.findById(padre)
				.orElseThrow(() -> new RecursoNoEncontradoException("Docente no encontrado"));
		DtoMapper.copy(data, entity);
		entity.setDocente(docente);
	}

	public List<FormacionAcademicaDTO> findByDocenteId(Long valor) {
		return rep.findByDocenteId(valor).orElseGet(List::of).stream().map(DtoMapper::toDto).toList();
	}

	public List<FormacionAcademicaDTO> findByNivelFormacion(NivelFormacion valor) {
		return rep.findByNivelFormacion(valor).orElseGet(List::of).stream().map(DtoMapper::toDto).toList();
	}

	public List<FormacionAcademicaDTO> findByInstitucion(String valor) {
		return rep.findByInstitucion(valor).orElseGet(List::of).stream().map(DtoMapper::toDto).toList();
	}

	public List<FormacionAcademicaDTO> findByAreaConocimiento(String valor) {
		return rep.findByAreaConocimiento(valor).orElseGet(List::of).stream().map(DtoMapper::toDto).toList();
	}

	@Transactional
	public int registrarFormacionDocente(Long id, FormacionAcademicaDTO data) {
		verificarId(id);
		if (data == null)
			throw new DatosInvalidosException("La formacion es obligatoria");
		if (data.getDocenteId() != null && !id.equals(data.getDocenteId()))
			throw new IdInvalidoException("Docente de la ruta y del cuerpo no coinciden");
		data.setDocenteId(id);
		return create(data);
	}
}
