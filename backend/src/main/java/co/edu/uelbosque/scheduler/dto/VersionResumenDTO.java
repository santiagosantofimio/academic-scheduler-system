package co.edu.uelbosque.scheduler.dto;

import java.time.LocalDateTime;
import co.edu.uelbosque.scheduler.entity.EstadoVersion;

public record VersionResumenDTO(Long idVersion, String nombre, EstadoVersion estado, LocalDateTime creadaEn,
		String creadaPor, LocalDateTime publicadaEn, String publicadaPor, Long origenId, String origenNombre,
		IndicadoresDTO indicadores) {
}
