package co.edu.uelbosque.scheduler.security;

import java.util.Locale;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import co.edu.uelbosque.scheduler.entity.Administrador;
import co.edu.uelbosque.scheduler.entity.Docente;
import co.edu.uelbosque.scheduler.entity.Usuario;
import co.edu.uelbosque.scheduler.repository.UsuarioRepository;

@Service
@Transactional(readOnly = true)
public class UserDetailsServiceImpl implements UserDetailsService {
	private final UsuarioRepository usuarios;

	public UserDetailsServiceImpl(UsuarioRepository usuarios) {
		this.usuarios = usuarios;
	}

	@Override
	public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {
		if (correo == null)
			throw new UsernameNotFoundException("Credenciales invalidas");
		Usuario usuario = usuarios.findByCorreoInstitucional(correo.strip().toLowerCase(Locale.ROOT))
				.orElseThrow(() -> new UsernameNotFoundException("Credenciales invalidas"));
		String rol;
		if (usuario instanceof Administrador)
			rol = "ADMINISTRADOR";
		else if (usuario instanceof Docente)
			rol = "DOCENTE";
		else
			throw new UsernameNotFoundException("Tipo de usuario no autorizado");
		return User.withUsername(usuario.getCorreoInstitucional()).password(usuario.getContrasenia()).roles(rol)
				.disabled(!usuario.isEstado()).build();
	}
}
