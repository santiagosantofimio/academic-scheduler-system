package co.edu.uelbosque.scheduler.repository;

import java.util.List;
import java.util.Optional;
import java.time.DayOfWeek;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.repository.query.Param;
import co.edu.uelbosque.scheduler.entity.*;

public interface AsignaturaServiceRepository extends JpaRepository<Asignatura, Long> {
	Optional<List<Asignatura>> findByCodigo(String valor);

	Optional<List<Asignatura>> findByNombre(String valor);

	Optional<List<Asignatura>> findBySemestre(int valor);

	Optional<List<Asignatura>> findByAreaConocimiento(String valor);

	Optional<List<Asignatura>> findByEstado(boolean valor);

	@Query("select e from Asignatura e where e.planEstudios.idPlan = :id")
	Optional<List<Asignatura>> findByPlanEstudiosId(@Param("id") Long id);

	@Query("select e from Asignatura e where e.planEstudios.idPlan = :id and e.semestre = :valor")
	Optional<List<Asignatura>> findByPlanEstudiosIdAndSemestre(@Param("id") Long id, @Param("valor") int valor);

	boolean existsByCodigo(String codigo);
}
