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

public interface FormacionAcademicaRepository extends JpaRepository<FormacionAcademica, Long> {
	Optional<List<FormacionAcademica>> findByNivelFormacion(NivelFormacion valor);

	Optional<List<FormacionAcademica>> findByInstitucion(String valor);

	Optional<List<FormacionAcademica>> findByAreaConocimiento(String valor);

	@Query("select e from FormacionAcademica e where e.docente.id = :id")
	Optional<List<FormacionAcademica>> findByDocenteId(@Param("id") Long id);
}
