package co.edu.uelbosque.scheduler.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import co.edu.uelbosque.scheduler.entity.DisponibilidadDocente;

public interface DisponibilidadDocenteRepository extends JpaRepository<DisponibilidadDocente, Long> {
	@Query("select d from DisponibilidadDocente d where d.docente.id = :id")
	List<DisponibilidadDocente> findByDocenteId(@Param("id") Long id);
}
