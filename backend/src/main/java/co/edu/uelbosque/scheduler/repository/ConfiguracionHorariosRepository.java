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

public interface ConfiguracionHorariosRepository extends JpaRepository<ConfiguracionHorarios, Long> {
	Optional<List<ConfiguracionHorarios>> findByEvitarDiasConsecutivos(boolean valor);

	Optional<List<ConfiguracionHorarios>> findByDuracionSesionMinutos(int valor);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("select c from ConfiguracionHorarios c order by c.idConfiguracion")
	List<ConfiguracionHorarios> bloquearConfiguraciones();
}
