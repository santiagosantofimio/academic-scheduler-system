package co.edu.uelbosque.scheduler.service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import co.edu.uelbosque.scheduler.dto.AuditoriaCambioDTO;
import co.edu.uelbosque.scheduler.entity.AuditoriaCambio;
import co.edu.uelbosque.scheduler.repository.AuditoriaCambioRepository;

@Service
@Transactional(readOnly = true)
public class AuditoriaService {
	private final AuditoriaCambioRepository rep;
	private final Clock reloj;

	public AuditoriaService(AuditoriaCambioRepository rep, Clock reloj) {
		this.rep = rep;
		this.reloj = reloj;
	}

	@Transactional(propagation = Propagation.MANDATORY)
	public void registrar(String accion, String detalle) {
		rep.save(new AuditoriaCambio(ahora(), usuarioActual(), accion, detalle));
	}

	public List<AuditoriaCambioDTO> recientes() {
		return rep.findTop200ByOrderByIdAuditoriaDesc().stream().map(a -> new AuditoriaCambioDTO(a.getIdAuditoria(),
				a.getFecha(), a.getUsuario(), a.getAccion(), a.getDetalle())).toList();
	}

	public LocalDateTime ahora() {
		return LocalDateTime.now(reloj).withNano(0);
	}

	public String usuarioActual() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		return auth == null || auth.getName() == null ? "sistema" : auth.getName();
	}
}
