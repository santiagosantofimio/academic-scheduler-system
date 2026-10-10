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
public class GrupoService extends AbstractCrudService<Grupo, GrupoDTO> {
	private final GrupoRepository rep;
	private final AsignaturaServiceRepository asignaturas;
	private final DocenteRepository docentes;
	private final SesionHorarioRepository sesiones;
	private final RequisitoAsignaturaService requisitos;

	public GrupoService(GrupoRepository rep, AsignaturaServiceRepository asignaturas, DocenteRepository docentes,
			SesionHorarioRepository sesiones, RequisitoAsignaturaService requisitos,
			ConfiguracionHorariosRepository configuraciones) {
		super(rep, "Grupo", configuraciones);
		this.rep = rep;
		this.asignaturas = asignaturas;
		this.docentes = docentes;
		this.sesiones = sesiones;
		this.requisitos = requisitos;
	}

	@Override
	protected Grupo nuevaEntidad() {
		return new Grupo();
	}

	@Override
	protected GrupoDTO toDto(Grupo entity) {
		return DtoMapper.toDto(entity);
	}

	@Override
	protected Long idDto(GrupoDTO data) {
		return data.getIdGrupo();
	}

	@Override
	protected void aplicar(GrupoDTO data, Grupo entity, boolean nuevo) {
		texto(data.getNumeroGrupo(), "numeroGrupo");
		verificarEstado(data.getEstado());
		Long padre = data.getAsignaturaId() != null ? data.getAsignaturaId()
				: entity.getAsignatura() == null ? null : entity.getAsignatura().getIdAsginatura();
		verificarId(padre);
		Asignatura asignatura = asignaturas.findById(padre)
				.orElseThrow(() -> new RecursoNoEncontradoException("Asignatura no encontrada"));
		verificarDuplicado(rep.findByAsignaturaIdAndNumeroGrupo(padre, data.getNumeroGrupo()).orElseGet(List::of)
				.stream().anyMatch(g -> g.getIdGrupo() != entity.getIdGrupo()), "Numero de grupo de la asignatura");
		Long docenteId = data.getDocenteId() != null ? data.getDocenteId()
				: entity.getDocente() == null ? null : entity.getDocente().getId();
		Docente docente = null;
		if (docenteId != null) {
			verificarId(docenteId);
			docente = docentes.findById(docenteId)
					.orElseThrow(() -> new RecursoNoEncontradoException("Docente no encontrado"));
			if (data.getEstado() == EstadoGrupo.ACTIVO)
				requisitos.validarDocente(padre, docenteId);
		}
		if (data.getEstado() == EstadoGrupo.ACTIVO
				&& (!asignatura.isEstado() || !asignatura.getPlanEstudios().isEstado()))
			throw new EstadoInvalidoException("La asignatura y su plan deben estar activos");
		if (!nuevo && sesiones.existenSesionesDeGrupo(entity.getIdGrupo(), EstadoSesion.PROGRAMADA)
				&& (data.getEstado() != EstadoGrupo.ACTIVO
						|| !Objects.equals(padre, entity.getAsignatura().getIdAsginatura()) || !Objects
								.equals(docenteId, entity.getDocente() == null ? null : entity.getDocente().getId())))
			throw new AsignacionAcademicaInvalidaException(
					"Cancele las sesiones antes de cambiar la asignatura, el docente o desactivar el grupo");
		DtoMapper.copy(data, entity);
		entity.setAsignatura(asignatura);
		entity.setDocente(docente);
	}

	public List<GrupoDTO> findByNumeroGrupo(String valor) {
		return rep.findByNumeroGrupo(valor).orElseGet(List::of).stream().map(DtoMapper::toDto).toList();
	}

	public List<GrupoDTO> findByEstado(EstadoGrupo valor) {
		return rep.findByEstado(valor).orElseGet(List::of).stream().map(DtoMapper::toDto).toList();
	}

	public List<GrupoDTO> findByAsignaturaId(Long valor) {
		return rep.findByAsignaturaId(valor).orElseGet(List::of).stream().map(DtoMapper::toDto).toList();
	}

	@Transactional
	public int registrarGrupoAsignatura(Long id, GrupoDTO data) {
		verificarId(id);
		if (data == null)
			throw new DatosInvalidosException("El grupo es obligatorio");
		if (data.getAsignaturaId() != null && !id.equals(data.getAsignaturaId()))
			throw new IdInvalidoException("Asignatura de la ruta y del cuerpo no coinciden");
		data.setAsignaturaId(id);
		return create(data);
	}
}
