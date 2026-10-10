package co.edu.uelbosque.scheduler.service;

import java.util.List;
import co.edu.uelbosque.scheduler.repository.ConfiguracionHorariosRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import co.edu.uelbosque.scheduler.exception.*;
import co.edu.uelbosque.scheduler.util.LanzadorDeExcepcion;

/**
 * Infraestructura comun del contrato CRUD, sin reglas particulares del dominio.
 */
@Transactional(readOnly = true)
public abstract class AbstractCrudService<E, D> implements CRUDOperation<D> {
	protected final JpaRepository<E, Long> repository;
	private final String recurso;
	private final ConfiguracionHorariosRepository configuraciones;

	protected AbstractCrudService(JpaRepository<E, Long> repository, String recurso,
			ConfiguracionHorariosRepository configuraciones) {
		this.repository = repository;
		this.recurso = recurso;
		this.configuraciones = configuraciones;
	}

	protected abstract E nuevaEntidad();

	protected abstract D toDto(E entity);

	protected abstract Long idDto(D data);

	protected abstract void aplicar(D data, E entity, boolean nuevo);

	protected E requerido(Long id) {
		LanzadorDeExcepcion.verificarId(id);
		return repository.findById(id)
				.orElseThrow(() -> new RecursoNoEncontradoException(recurso + " no encontrado: " + id));
	}

	@Override
	@Transactional
	public int create(D data) {
		configuraciones.bloquearConfiguraciones();
		validarEntrada(data);
		if (idDto(data) != null && idDto(data) != 0) {
			throw new IdInvalidoException("El identificador se genera al crear el recurso");
		}
		E entity = nuevaEntidad();
		aplicar(data, entity, true);
		repository.saveAndFlush(entity);
		return 1;
	}

	@Override
	public List<D> getAll() {
		return repository.findAll().stream().map(this::toDto).toList();
	}

	public D getById(Long id) {
		return toDto(requerido(id));
	}

	@Override
	@Transactional
	public int updateById(Long id, D data) {
		configuraciones.bloquearConfiguraciones();
		validarEntrada(data);
		E entity = requerido(id);
		if (idDto(data) != null && idDto(data) != 0 && !id.equals(idDto(data))) {
			throw new IdInvalidoException("El ID del cuerpo no coincide con el ID de la ruta");
		}
		aplicar(data, entity, false);
		repository.saveAndFlush(entity);
		return 1;
	}

	@Override
	@Transactional
	public int deleteById(Long id) {
		configuraciones.bloquearConfiguraciones();
		repository.delete(requerido(id));
		repository.flush();
		return 1;
	}

	@Override
	public long count() {
		return repository.count();
	}

	@Override
	public boolean exist(Long id) {
		LanzadorDeExcepcion.verificarId(id);
		return repository.existsById(id);
	}

	private void validarEntrada(D data) {
		if (data == null)
			throw new DatosInvalidosException("El cuerpo de la solicitud es obligatorio");
	}
}
