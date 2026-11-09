package co.edu.uelbosque.scheduler.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import co.edu.uelbosque.scheduler.entity.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
	Optional<Usuario> findByNumeroDocumento(String numeroDocumento);

	Optional<Usuario> findByCorreoInstitucional(String correoInstitucional);
}
