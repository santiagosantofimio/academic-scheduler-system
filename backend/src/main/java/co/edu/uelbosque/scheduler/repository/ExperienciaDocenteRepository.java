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

public interface ExperienciaDocenteRepository extends JpaRepository<ExperienciaDocente, Long> {
	Optional<List<ExperienciaDocente>> findByTipoExperiencia(TipoExperiencia valor);

	Optional<List<ExperienciaDocente>> findByInstitucion(String valor);

	Optional<List<ExperienciaDocente>> findByAreaConocimiento(String valor);

	@Query("select e from ExperienciaDocente e where e.docente.id = :id")
	Optional<List<ExperienciaDocente>> findByDocenteId(@Param("id") Long id);

	@Query("select e from ExperienciaDocente e where e.docente.id = :id and e.tipoExperiencia = :valor")
	Optional<List<ExperienciaDocente>> findByDocenteIdAndTipoExperiencia(@Param("id") Long id,
			@Param("valor") TipoExperiencia valor);
}
