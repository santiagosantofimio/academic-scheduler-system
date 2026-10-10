package co.edu.uelbosque.scheduler.util;

import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Objects;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import co.edu.uelbosque.scheduler.entity.Usuario;
import co.edu.uelbosque.scheduler.repository.UsuarioRepository;
import co.edu.uelbosque.scheduler.exception.DatosInvalidosException;

@Component
public class DatosUsuario {
	private final UsuarioRepository usuarios;
	private final PasswordEncoder encoder;

	public DatosUsuario(UsuarioRepository usuarios, PasswordEncoder encoder) {
		this.usuarios = usuarios;
		this.encoder = encoder;
	}

	public void aplicar(Usuario data, Usuario entity, boolean nuevo) {
		LanzadorDeExcepcion.verificarNombre(data.getNombre());
		LanzadorDeExcepcion.verificarNumeroDocumento(data.getNumeroDocumento());
		LanzadorDeExcepcion.verificarCorreoInstitucional(data.getCorreoInstitucional());
		String documento = data.getNumeroDocumento().strip();
		String correo = data.getCorreoInstitucional().strip().toLowerCase(Locale.ROOT);
		LanzadorDeExcepcion.verificarDuplicado(usuarios.findByNumeroDocumento(documento)
				.filter(u -> !Objects.equals(u.getId(), entity.getId())).isPresent(), "Documento");
		LanzadorDeExcepcion.verificarDuplicado(usuarios.findByCorreoInstitucional(correo)
				.filter(u -> !Objects.equals(u.getId(), entity.getId())).isPresent(), "Correo institucional");
		entity.setNombre(data.getNombre().strip());
		entity.setApellido(LanzadorDeExcepcion.texto(data.getApellido(), "apellido"));
		entity.setNumeroDocumento(documento);
		entity.setCorreoInstitucional(correo);
		entity.setEstado(data.isEstado());
		String password = data.getContrasenia();
		if (nuevo && (password == null || password.isBlank()))
			throw new DatosInvalidosException("La contrasenia es obligatoria al crear un usuario");
		if (password != null) {
			if (password.isBlank() || password.getBytes(StandardCharsets.UTF_8).length > 72)
				throw new DatosInvalidosException(
						"La contrasenia debe contener entre 1 y 72 bytes UTF-8, limite del codificador BCrypt");
			entity.setContrasenia(encoder.encode(password));
		}
	}
}
