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

public interface PlanEstudiosRepository extends JpaRepository<PlanEstudios, Long> {
	Optional<List<PlanEstudios>> findByNombrePrograma(String valor);

	Optional<List<PlanEstudios>> findByCodigoPrograma(String valor);

	Optional<List<PlanEstudios>> findByVersionPlan(String valor);

	Optional<List<PlanEstudios>> findByEstado(boolean valor);

	boolean existsByCodigoPrograma(String codigoPrograma);
}
