package co.edu.uelbosque.scheduler.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import co.edu.uelbosque.scheduler.entity.EstadoNovedad;
import co.edu.uelbosque.scheduler.entity.NovedadDocente;

public interface NovedadDocenteRepository extends JpaRepository<NovedadDocente, Long> {
	@Query("select n from NovedadDocente n where n.docente.id = :id and n.estado = :estado")
	List<NovedadDocente> buscarPorDocente(@Param("id") Long id, @Param("estado") EstadoNovedad estado);

	@Query("select n from NovedadDocente n where n.docente.id = :id")
	List<NovedadDocente> findByDocenteId(@Param("id") Long id);

	List<NovedadDocente> findAllByOrderByIdNovedadDesc();
}
