package co.edu.uelbosque.scheduler.dto;

import java.util.List;

public record VersionDetalleDTO(VersionResumenDTO version, List<SesionHorarioDTO> sesiones) {
}
