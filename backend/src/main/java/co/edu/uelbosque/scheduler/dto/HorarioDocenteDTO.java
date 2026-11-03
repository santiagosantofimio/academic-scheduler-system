package co.edu.uelbosque.scheduler.dto;

import java.util.List;

public record HorarioDocenteDTO(ConfiguracionHorariosDTO configuracion, List<AsignaturaDTO> asignaturas,
		List<GrupoDTO> grupos, List<SesionHorarioDTO> sesiones) {
}
