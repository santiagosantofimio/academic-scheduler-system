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

public interface AdministradorRepository extends JpaRepository<Administrador, Long> {
    Optional<List<Administrador>> findByNombre(String valor);
    Optional<List<Administrador>> findByNumeroDocumento(String valor);
    Optional<List<Administrador>> findByCorreoInstitucional(String valor);
    Optional<List<Administrador>> findByCargo(String valor);
    boolean existsByNumeroDocumento(String numeroDocumento);
}
