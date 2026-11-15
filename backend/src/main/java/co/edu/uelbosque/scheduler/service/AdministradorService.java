package co.edu.uelbosque.scheduler.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
public class AdministradorService extends AbstractCrudService<Administrador, AdministradorDTO> {
	private final AdministradorRepository rep;
	private final DatosUsuario datosUsuario;

	public AdministradorService(AdministradorRepository rep, DatosUsuario datosUsuario,
			ConfiguracionHorariosRepository configuraciones) {
		super(rep, "Administrador", configuraciones);
		this.rep = rep;
		this.datosUsuario = datosUsuario;
	}

	@Override
	protected Administrador nuevaEntidad() {
		return new Administrador();
	}

	@Override
	protected AdministradorDTO toDto(Administrador entity) {
		return DtoMapper.toDto(entity);
	}

	@Override
	protected Long idDto(AdministradorDTO data) {
		return data.getId();
	}

	@Override
	@Transactional
	public int deleteById(Long id) {
		Administrador entity = requerido(id);
		protegerAcceso(entity, "eliminar");
		return super.deleteById(id);
	}

	@Override
	protected void aplicar(AdministradorDTO data, Administrador entity, boolean nuevo) {
		if (!nuevo && entity.isEstado() && !data.isEstado())
			protegerAcceso(entity, "desactivar");
		datosUsuario.aplicar(data, entity, nuevo);
		entity.setCargo(texto(data.getCargo(), "cargo"));
	}

	public List<AdministradorDTO> findByNombre(String valor) {
		return rep.findByNombre(valor).orElseGet(List::of).stream().map(DtoMapper::toDto).toList();
	}

	public List<AdministradorDTO> findByNumeroDocumento(String valor) {
		return rep.findByNumeroDocumento(valor).orElseGet(List::of).stream().map(DtoMapper::toDto).toList();
	}

	public List<AdministradorDTO> findByCorreoInstitucional(String valor) {
		return rep.findByCorreoInstitucional(valor).orElseGet(List::of).stream().map(DtoMapper::toDto).toList();
	}

	public List<AdministradorDTO> findByCargo(String valor) {
		return rep.findByCargo(valor).orElseGet(List::of).stream().map(DtoMapper::toDto).toList();
	}

	private void protegerAcceso(Administrador entity, String accion) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null && entity.getCorreoInstitucional().equalsIgnoreCase(auth.getName()))
			throw new AsignacionAcademicaInvalidaException("No puede " + accion + " su propia cuenta de administrador");
		long otrosActivos = rep.findAll().stream()
				.filter(a -> a.isEstado() && !Objects.equals(a.getId(), entity.getId())).count();
		if (entity.isEstado() && otrosActivos == 0)
			throw new AsignacionAcademicaInvalidaException(
					"No puede " + accion + " al unico administrador activo; el sistema quedaria sin acceso");
	}
}
