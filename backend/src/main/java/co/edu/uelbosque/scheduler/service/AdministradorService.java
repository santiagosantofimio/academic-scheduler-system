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
	protected void aplicar(AdministradorDTO data, Administrador entity, boolean nuevo) {
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
}
