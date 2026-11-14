package co.edu.uelbosque.scheduler.dto;

public record IndicadoresDTO(int sesiones, int grupos, int docentes, double cargaMaxima, double cargaMinima,
		double diferenciaCarga, double horasMuertas, double diasConClasePromedio) {
}
