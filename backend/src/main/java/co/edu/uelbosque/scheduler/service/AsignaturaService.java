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
public class AsignaturaService extends AbstractCrudService<Asignatura, AsignaturaDTO> {
	private final AsignaturaServiceRepository rep;
	private final PlanEstudiosRepository planes;
	private final SesionHorarioRepository sesiones;

	public AsignaturaService(AsignaturaServiceRepository rep, PlanEstudiosRepository planes,
			SesionHorarioRepository sesiones, ConfiguracionHorariosRepository configuraciones) {
		super(rep, "Asignatura", configuraciones);
		this.rep = rep;
		this.planes = planes;
		this.sesiones = sesiones;
	}

	@Override
	protected Asignatura nuevaEntidad() {
		return new Asignatura();
	}

	@Override
	protected AsignaturaDTO toDto(Asignatura entity) {
		return DtoMapper.toDto(entity);
	}

	@Override
	protected Long idDto(AsignaturaDTO data) {
		return data.getIdAsginatura();
	}

	@Override
	protected void aplicar(AsignaturaDTO data, Asignatura entity, boolean nuevo) {
		texto(data.getCodigo(), "codigo");
		verificarNombre(data.getNombre());
		verificarAreaConocimiento(data.getAreaConocimiento());
		verificarSemestre(data.getSemestre());
		verificarCreditos(data.getCreditos());
		verificarSesionesSemanales(data.getSesionesSemanales());
		Long padre = data.getPlanEstudiosId() != null ? data.getPlanEstudiosId()
				: entity.getPlanEstudios() == null ? null : entity.getPlanEstudios().getIdPlan();
		verificarId(padre);
		PlanEstudios plan = planes.findById(padre)
				.orElseThrow(() -> new RecursoNoEncontradoException("Plan de estudios no encontrado"));
		if (data.getSemestre() > plan.getNumerosSemestres())
			throw new SemestreInvalidoException("El semestre supera el numero de semestres del plan");
		if (data.isEstado() && !plan.isEstado())
			throw new EstadoInvalidoException("El plan esta inactivo");
		verificarDuplicado(rep.findByCodigo(data.getCodigo()).orElseGet(List::of).stream()
				.anyMatch(a -> !Objects.equals(a.getIdAsginatura(), entity.getIdAsginatura())), "Codigo de asignatura");
		if (!nuevo && !sesiones.buscarPorAsignatura(entity.getIdAsginatura(), EstadoSesion.PROGRAMADA).isEmpty()
				&& (!data.isEstado() || data.getSesionesSemanales() != entity.getSesionesSemanales()
						|| !Objects.equals(padre, entity.getPlanEstudios().getIdPlan())))
			throw new EstadoInvalidoException(
					"Cancele primero las sesiones antes de cambiar la programacion de la asignatura");
		DtoMapper.copy(data, entity);
		entity.setPlanEstudios(plan);
	}

	public List<AsignaturaDTO> findByCodigo(String valor) {
		return rep.findByCodigo(valor).orElseGet(List::of).stream().map(DtoMapper::toDto).toList();
	}

	public List<AsignaturaDTO> findByNombre(String valor) {
		return rep.findByNombre(valor).orElseGet(List::of).stream().map(DtoMapper::toDto).toList();
	}

	public List<AsignaturaDTO> findBySemestre(int valor) {
		return rep.findBySemestre(valor).orElseGet(List::of).stream().map(DtoMapper::toDto).toList();
	}

	public List<AsignaturaDTO> findByAreaConocimiento(String valor) {
		return rep.findByAreaConocimiento(valor).orElseGet(List::of).stream().map(DtoMapper::toDto).toList();
	}

	public List<AsignaturaDTO> findByEstado(boolean valor) {
		return rep.findByEstado(valor).orElseGet(List::of).stream().map(DtoMapper::toDto).toList();
	}

	public List<AsignaturaDTO> findByPlanEstudiosId(Long valor) {
		return rep.findByPlanEstudiosId(valor).orElseGet(List::of).stream().map(DtoMapper::toDto).toList();
	}

	@Transactional
	public int registrarAsignaturaEnPlan(Long id, AsignaturaDTO data) {
		verificarId(id);
		if (data == null)
			throw new DatosInvalidosException("La asignatura es obligatoria");
		if (data.getPlanEstudiosId() != null && !id.equals(data.getPlanEstudiosId()))
			throw new IdInvalidoException("Plan de la ruta y del cuerpo no coinciden");
		data.setPlanEstudiosId(id);
		return create(data);
	}
}
