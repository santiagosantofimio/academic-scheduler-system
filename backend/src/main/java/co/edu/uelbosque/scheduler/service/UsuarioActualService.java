package co.edu.uelbosque.scheduler.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import co.edu.uelbosque.scheduler.dto.UsuarioActualDTO;
import co.edu.uelbosque.scheduler.entity.Administrador;
import co.edu.uelbosque.scheduler.entity.Usuario;
import co.edu.uelbosque.scheduler.exception.RecursoNoEncontradoException;
import co.edu.uelbosque.scheduler.repository.UsuarioRepository;

@Service
@Transactional(readOnly = true)
public class UsuarioActualService {
	private final UsuarioRepository usuarios;

	public UsuarioActualService(UsuarioRepository usuarios) {
		this.usuarios = usuarios;
	}

	public UsuarioActualDTO consultar(String correo) {
		Usuario usuario = usuarios.findByCorreoInstitucional(correo)
				.orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));
		String rol = usuario instanceof Administrador ? "ADMINISTRADOR" : "DOCENTE";
		return new UsuarioActualDTO(usuario.getId(), usuario.getNombre(), usuario.getApellido(),
				usuario.getCorreoInstitucional(), rol);
	}
}
