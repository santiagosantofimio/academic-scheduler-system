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

public interface SesionHorarioRepository extends JpaRepository<SesionHorario, Long> {
	Optional<List<SesionHorario>> findByDiaSemana(DayOfWeek valor);

	Optional<List<SesionHorario>> findByEstado(EstadoSesion valor);

	@Query("select e from SesionHorario e where e.grupo.idGrupo = :id")
	Optional<List<SesionHorario>> findByGrupoId(@Param("id") Long id);

	@Query("select e from SesionHorario e where e.grupo.idGrupo = :id and e.diaSemana = :valor")
	Optional<List<SesionHorario>> findByGrupoIdAndDiaSemana(@Param("id") Long id, @Param("valor") DayOfWeek valor);

	@Query("select e from SesionHorario e where e.grupo.idGrupo = :id and e.estado = :valor")
	Optional<List<SesionHorario>> findByGrupoIdAndEstado(@Param("id") Long id, @Param("valor") EstadoSesion valor);

	@Query("select s from SesionHorario s join s.grupo g where g.docente.id = :id")
	List<SesionHorario> findByDocenteId(@Param("id") Long id);

	@Query("select (count(s) > 0) from SesionHorario s where s.grupo.idGrupo = :id and s.estado = :estado")
	boolean existenSesionesDeGrupo(@Param("id") Long id, @Param("estado") EstadoSesion estado);

	@Query("select s from SesionHorario s where s.grupo.asignatura.idAsginatura = :id and s.estado = :estado")
	List<SesionHorario> buscarPorAsignatura(@Param("id") Long id, @Param("estado") EstadoSesion estado);
}
