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
public class DocenteService extends AbstractCrudService<Docente, DocenteDTO> {
	private final DocenteRepository rep;
	private final DatosUsuario datosUsuario;
	private final SesionHorarioRepository sesiones;
	private final GrupoRepository grupos;
	private final FormacionAcademicaRepository formaciones;
	private final ExperienciaDocenteRepository experiencias;
	private final DisponibilidadDocenteRepository disponibilidades;
	private final ConfiguracionHorariosRepository configuraciones;

	public DocenteService(DocenteRepository rep, DatosUsuario datosUsuario, SesionHorarioRepository sesiones,
			GrupoRepository grupos, FormacionAcademicaRepository formaciones, ExperienciaDocenteRepository experiencias,
			DisponibilidadDocenteRepository disponibilidades, ConfiguracionHorariosRepository configuraciones) {
		super(rep, "Docente", configuraciones);
		this.rep = rep;
		this.datosUsuario = datosUsuario;
		this.sesiones = sesiones;
		this.grupos = grupos;
		this.formaciones = formaciones;
		this.experiencias = experiencias;
		this.disponibilidades = disponibilidades;
		this.configuraciones = configuraciones;
	}

	@Override
	@Transactional
	public int deleteById(Long id) {
		configuraciones.bloquearConfiguraciones();
		requerido(id);
		List<Grupo> asignados = grupos.buscarPorDocente(id);
		if (!asignados.isEmpty())
			throw new AsignacionAcademicaInvalidaException("No se puede eliminar: el docente esta asignado a "
					+ String.join(", ", asignados.stream()
							.map(g -> g.getAsignatura().getCodigo() + " grupo " + g.getNumeroGrupo()).toList())
					+ ". En Grupos asigne otro docente o deje el grupo sin docente, o desactive al docente");
		disponibilidades.deleteAll(disponibilidades.findByDocenteId(id));
		formaciones.deleteAll(formaciones.findByDocenteId(id).orElseGet(List::of));
		experiencias.deleteAll(experiencias.findByDocenteId(id).orElseGet(List::of));
		return super.deleteById(id);
	}

	@Override
	protected Docente nuevaEntidad() {
		return new Docente();
	}

	@Override
	protected DocenteDTO toDto(Docente entity) {
		return DtoMapper.toDto(entity);
	}

	@Override
	protected Long idDto(DocenteDTO data) {
		return data.getId();
	}

	@Override
	protected void aplicar(DocenteDTO data, Docente entity, boolean nuevo) {
		verificarTipoVinculacion(data.getTipoVinculacion());
		if (data.getHorasMaximasDocenciaSemanal() < 0)
			throw new DatosInvalidosException("Las horas maximas no pueden ser negativas");
		if (!nuevo) {
			List<SesionHorario> activas = sesiones.findByDocenteId(entity.getId()).stream()
					.filter(s -> s.getEstado() == EstadoSesion.PROGRAMADA).toList();
			long segundos = activas.stream()
					.mapToLong(s -> Duration.between(s.getHoraInicio(), s.getHoraFin()).getSeconds()).sum();
			if ((!data.isEstado() && !activas.isEmpty()) || segundos > data.getHorasMaximasDocenciaSemanal() * 3600L)
				throw new DisponibilidadInvalidaException("El cambio invalida sesiones programadas del docente");
		}
		datosUsuario.aplicar(data, entity, nuevo);
		entity.setTipoVinculacion(data.getTipoVinculacion());
		entity.setEscalafon(texto(data.getEscalafon(), "escalafon"));
		entity.setEspecialidad(texto(data.getEspecialidad(), "especialidad"));
		entity.setHorasMaximasDocenciaSemanal(data.getHorasMaximasDocenciaSemanal());
	}

	public List<DocenteDTO> findByNombre(String valor) {
		return rep.findByNombre(valor).orElseGet(List::of).stream().map(DtoMapper::toDto).toList();
	}

	public List<DocenteDTO> findByNumeroDocumento(String valor) {
		return rep.findByNumeroDocumento(valor).orElseGet(List::of).stream().map(DtoMapper::toDto).toList();
	}

	public List<DocenteDTO> findByCorreoInstitucional(String valor) {
		return rep.findByCorreoInstitucional(valor).orElseGet(List::of).stream().map(DtoMapper::toDto).toList();
	}

	public List<DocenteDTO> findByTipoVinculacion(TipoVinculacion valor) {
		return rep.findByTipoVinculacion(valor).orElseGet(List::of).stream().map(DtoMapper::toDto).toList();
	}

	public List<DocenteDTO> findByEspecialidad(String valor) {
		return rep.findByEspecialidad(valor).orElseGet(List::of).stream().map(DtoMapper::toDto).toList();
	}

	public List<DocenteDTO> findByEscalafon(String valor) {
		return rep.findByEscalafon(valor).orElseGet(List::of).stream().map(DtoMapper::toDto).toList();
	}
}
