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
public class PlanEstudiosService extends AbstractCrudService<PlanEstudios, PlanEstudiosDTO> {
	private final PlanEstudiosRepository rep;
	private final AsignaturaServiceRepository asignaturas;

	public PlanEstudiosService(PlanEstudiosRepository rep, AsignaturaServiceRepository asignaturas,
			ConfiguracionHorariosRepository configuraciones) {
		super(rep, "PlanEstudios", configuraciones);
		this.rep = rep;
		this.asignaturas = asignaturas;
	}

	@Override
	protected PlanEstudios nuevaEntidad() {
		return new PlanEstudios();
	}

	@Override
	protected PlanEstudiosDTO toDto(PlanEstudios entity) {
		return DtoMapper.toDto(entity);
	}

	@Override
	protected Long idDto(PlanEstudiosDTO data) {
		return data.getIdPlan();
	}

	@Override
	protected void aplicar(PlanEstudiosDTO data, PlanEstudios entity, boolean nuevo) {
		texto(data.getNombrePrograma(), "nombrePrograma");
		texto(data.getCodigoPrograma(), "codigoPrograma");
		texto(data.getVersionPlan(), "versionPlan");
		verificarSemestre(data.getNumerosSemestres());
		verificarDuplicado(rep.findByCodigoPrograma(data.getCodigoPrograma()).orElseGet(List::of).stream()
				.anyMatch(p -> !Objects.equals(p.getIdPlan(), entity.getIdPlan())), "Codigo del programa");
		if (!nuevo) {
			List<Asignatura> existentes = asignaturas.findByPlanEstudiosId(entity.getIdPlan()).orElseGet(List::of);
			if (existentes.stream().anyMatch(a -> a.getSemestre() > data.getNumerosSemestres()))
				throw new SemestreInvalidoException("El plan contiene asignaturas de semestres superiores");
			if (!data.isEstado() && existentes.stream().anyMatch(Asignatura::isEstado))
				throw new EstadoInvalidoException("Desactive primero las asignaturas del plan");
		}
		DtoMapper.copy(data, entity);
	}

	public List<PlanEstudiosDTO> findByNombrePrograma(String valor) {
		return rep.findByNombrePrograma(valor).orElseGet(List::of).stream().map(DtoMapper::toDto).toList();
	}

	public List<PlanEstudiosDTO> findByCodigoPrograma(String valor) {
		return rep.findByCodigoPrograma(valor).orElseGet(List::of).stream().map(DtoMapper::toDto).toList();
	}

	public List<PlanEstudiosDTO> findByVersionPlan(String valor) {
		return rep.findByVersionPlan(valor).orElseGet(List::of).stream().map(DtoMapper::toDto).toList();
	}

	public List<PlanEstudiosDTO> findByEstado(boolean valor) {
		return rep.findByEstado(valor).orElseGet(List::of).stream().map(DtoMapper::toDto).toList();
	}
}
