package co.edu.uelbosque.scheduler.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import co.edu.uelbosque.scheduler.dto.AdministradorDTO;
import co.edu.uelbosque.scheduler.dto.RegistroAdministradorDTO;
import co.edu.uelbosque.scheduler.exception.DatosInvalidosException;

@Service
public class RegistroAdministradorService {
	private final AdministradorService administradores;
	private final String dominio;
	private final String codigo;

	public RegistroAdministradorService(AdministradorService administradores,
			@Value("${scheduler.registro.dominio:unbosque.edu.co}") String dominio,
			@Value("${scheduler.registro.codigo:}") String codigo) {
		this.administradores = administradores;
		this.dominio = dominio.strip().toLowerCase(Locale.ROOT);
		this.codigo = codigo.strip();
	}

	public boolean pideCodigo() {
		return !codigo.isEmpty();
	}

	@Transactional
	public void registrar(RegistroAdministradorDTO data) {
		if (data == null)
			throw new DatosInvalidosException("Los datos del registro son obligatorios");
		String correo = data.correoInstitucional() == null ? "" : data.correoInstitucional().strip().toLowerCase(Locale.ROOT);
		if (!dominio.isEmpty() && !correo.endsWith("@" + dominio))
			throw new DatosInvalidosException("Use su correo institucional @" + dominio);
		if (pideCodigo() && (data.codigo() == null || !MessageDigest.isEqual(codigo.getBytes(StandardCharsets.UTF_8),
				data.codigo().strip().getBytes(StandardCharsets.UTF_8))))
			throw new DatosInvalidosException("El codigo de registro no es valido");
		AdministradorDTO dto = new AdministradorDTO();
		dto.setNombre(data.nombre());
		dto.setApellido(data.apellido());
		dto.setNumeroDocumento(data.numeroDocumento());
		dto.setCorreoInstitucional(correo);
		dto.setContrasenia(data.contrasenia());
		dto.setCargo(data.cargo());
		dto.setEstado(true);
		administradores.create(dto);
	}
}
