package co.edu.uelbosque.scheduler.configuration;

import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import co.edu.uelbosque.scheduler.dto.AdministradorDTO;
import co.edu.uelbosque.scheduler.repository.AdministradorRepository;
import co.edu.uelbosque.scheduler.service.AdministradorService;

@Component
public class AdministradorInicial implements ApplicationRunner {
	private static final Logger LOG = LoggerFactory.getLogger(AdministradorInicial.class);

	private final AdministradorRepository administradores;
	private final AdministradorService servicio;
	private final String correo;
	private final String contrasenia;

	public AdministradorInicial(AdministradorRepository administradores, AdministradorService servicio,
			@Value("${scheduler.admin.correo:}") String correo,
			@Value("${scheduler.admin.contrasenia:}") String contrasenia) {
		this.administradores = administradores;
		this.servicio = servicio;
		this.correo = correo;
		this.contrasenia = contrasenia;
	}

	@Override
	public void run(ApplicationArguments args) {
		if (administradores.count() > 0)
			return;
		if (correo.isBlank() || contrasenia.isBlank()) {
			LOG.warn("No hay administradores: defina SCHEDULER_ADMIN_CORREO y SCHEDULER_ADMIN_CONTRASENIA para crear el primero");
			return;
		}
		servicio.create(new AdministradorDTO("Administrador", "Inicial", "0000000000", correo, contrasenia, true,
				"Coordinador"));
		LOG.info("Administrador inicial creado: {}", correo.strip().toLowerCase(Locale.ROOT));
	}
}
