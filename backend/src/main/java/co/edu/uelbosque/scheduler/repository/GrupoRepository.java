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

public interface GrupoRepository extends JpaRepository<Grupo, Long> {
	Optional<List<Grupo>> findByNumeroGrupo(String valor);

	Optional<List<Grupo>> findByEstado(EstadoGrupo valor);

	@Query("select e from Grupo e where e.asignatura.idAsginatura = :id")
	Optional<List<Grupo>> findByAsignaturaId(@Param("id") Long id);

	@Query("select e from Grupo e where e.asignatura.idAsginatura = :id and e.estado = :valor")
	Optional<List<Grupo>> findByAsignaturaIdAndEstado(@Param("id") Long id, @Param("valor") EstadoGrupo valor);

	@Query("select e from Grupo e where e.asignatura.idAsginatura = :id and e.numeroGrupo = :valor")
	Optional<List<Grupo>> findByAsignaturaIdAndNumeroGrupo(@Param("id") Long id, @Param("valor") String valor);

	@Query("select (count(g) > 0) from Grupo g where g.asignatura.idAsginatura = :id and g.numeroGrupo = :numero")
	boolean existsByAsignaturaIdAndNumeroGrupo(@Param("id") Long id, @Param("numero") String numero);

	@Query("select g from Grupo g where g.docente.id = :id")
	List<Grupo> buscarPorDocente(@Param("id") Long id);
}
