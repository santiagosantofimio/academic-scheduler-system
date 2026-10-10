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

public interface RequisitoAsignaturaRepository extends JpaRepository<RequisitoAsignatura, Long> {
	Optional<List<RequisitoAsignatura>> findByTipoRequisito(TipoRequisito valor);

	Optional<List<RequisitoAsignatura>> findByAreaConocimiento(String valor);

	@Query("select e from RequisitoAsignatura e where e.asignatura.idAsginatura = :id")
	Optional<List<RequisitoAsignatura>> findByAsignaturaId(@Param("id") Long id);
}
