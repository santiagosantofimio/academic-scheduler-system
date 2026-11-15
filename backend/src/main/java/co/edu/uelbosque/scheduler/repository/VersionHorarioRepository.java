package co.edu.uelbosque.scheduler.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import co.edu.uelbosque.scheduler.entity.EstadoVersion;
import co.edu.uelbosque.scheduler.entity.VersionHorario;

public interface VersionHorarioRepository extends JpaRepository<VersionHorario, Long> {
	List<VersionHorario> findAllByOrderByIdVersionDesc();

	List<VersionHorario> findByEstado(EstadoVersion estado);

	@EntityGraph(attributePaths = { "sesiones" })
	Optional<VersionHorario> findFirstByEstadoOrderByIdVersionDesc(EstadoVersion estado);
}
