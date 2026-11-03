package co.edu.uelbosque.scheduler.service;

import java.util.List;

public interface CRUDOperation<T> {
	int create(T data);

	List<T> getAll();

	int deleteById(Long id);

	int updateById(Long id, T newData);

	long count();

	boolean exist(Long id);
}
