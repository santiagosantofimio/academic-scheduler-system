package co.edu.uelbosque.scheduler.dto;

import java.util.List;

public record ResultadoCargaDTO(int sesiones, int omitidas, Long versionCreadaId, List<ConflictoDTO> conflictos) {
}
