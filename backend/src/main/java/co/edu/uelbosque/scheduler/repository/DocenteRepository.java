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

public interface DocenteRepository extends JpaRepository<Docente, Long> {
	Optional<List<Docente>> findByNombre(String valor);

	Optional<List<Docente>> findByNumeroDocumento(String valor);

	Optional<List<Docente>> findByCorreoInstitucional(String valor);

	Optional<List<Docente>> findByTipoVinculacion(TipoVinculacion valor);

	Optional<List<Docente>> findByEspecialidad(String valor);

	Optional<List<Docente>> findByEscalafon(String valor);

	boolean existsByNumeroDocumento(String numeroDocumento);

	boolean existsByCorreoInstitucional(String correoInstitucional);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("select d from Docente d where d.id = :id")
	Optional<Docente> bloquearPorId(@Param("id") Long id);
}
