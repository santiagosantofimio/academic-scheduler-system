package co.edu.uelbosque.scheduler.dto;

import java.util.List;

public record ClaseAfectadaDTO(SesionHorarioDTO sesion, String asignatura, String grupo, List<FranjaDTO> alternativas) {
}
