package co.edu.uelbosque.scheduler.service;

import java.util.List;

/**
 * Las escrituras retornan la cantidad de recursos afectados (1); los fallos
 * lanzan excepciones.
 */
public interface CRUDOperation<T> {
	int create(T data);

	List<T> getAll();

	int deleteById(Long id);

	int updateById(Long id, T newData);

	long count();

	boolean exist(Long id);
}
