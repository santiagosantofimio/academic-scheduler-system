package co.edu.uelbosque.scheduler.dto;

import java.time.LocalDateTime;

public record AuditoriaCambioDTO(Long idAuditoria, LocalDateTime fecha, String usuario, String accion, String detalle) {
}
