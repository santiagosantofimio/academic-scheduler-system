package co.edu.uelbosque.scheduler.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import co.edu.uelbosque.scheduler.entity.AuditoriaCambio;

public interface AuditoriaCambioRepository extends JpaRepository<AuditoriaCambio, Long> {
	List<AuditoriaCambio> findTop200ByOrderByIdAuditoriaDesc();
}
