package co.edu.uelbosque.scheduler.dto;

import java.time.*;
import java.util.Objects;
import co.edu.uelbosque.scheduler.entity.*;

public class ConfiguracionHorariosDTO {
	private Long idConfiguracion;
	private LocalTime horaInicioSemana;
	private LocalTime horaFinSemana;
	private LocalTime horaInicioSabado;
	private LocalTime horaFinSabado;
	private LocalTime horaInicioDescanso;
	private LocalTime horaFinDescanso;
	private int duracionSesionMinutos;
	private int maxSesionesSemanales;
	private boolean evitarDiasConsecutivos;

	public ConfiguracionHorariosDTO() {

	}

	public ConfiguracionHorariosDTO(LocalTime horaInicioSemana, LocalTime horaFinSemana, LocalTime horaInicioSabado,
			LocalTime horaFinSabado, LocalTime horaInicioDescanso, LocalTime horaFinDescanso, int duracionSesionMinutos,
			int maxSesionesSemanales, boolean evitarDiasConsecutivos) {
		this.horaInicioSemana = horaInicioSemana;
		this.horaFinSemana = horaFinSemana;
		this.horaInicioSabado = horaInicioSabado;
		this.horaFinSabado = horaFinSabado;
		this.horaInicioDescanso = horaInicioDescanso;
		this.horaFinDescanso = horaFinDescanso;
		this.duracionSesionMinutos = duracionSesionMinutos;
		this.maxSesionesSemanales = maxSesionesSemanales;
		this.evitarDiasConsecutivos = evitarDiasConsecutivos;
	}

	public ConfiguracionHorariosDTO(long idConfiguracion, LocalTime horaInicioSemana, LocalTime horaFinSemana,
			LocalTime horaInicioSabado, LocalTime horaFinSabado, LocalTime horaInicioDescanso,
			LocalTime horaFinDescanso, int duracionSesionMinutos, int maxSesionesSemanales,
			boolean evitarDiasConsecutivos) {
		this.idConfiguracion = idConfiguracion;
		this.horaInicioSemana = horaInicioSemana;
		this.horaFinSemana = horaFinSemana;
		this.horaInicioSabado = horaInicioSabado;
		this.horaFinSabado = horaFinSabado;
		this.horaInicioDescanso = horaInicioDescanso;
		this.horaFinDescanso = horaFinDescanso;
		this.duracionSesionMinutos = duracionSesionMinutos;
		this.maxSesionesSemanales = maxSesionesSemanales;
		this.evitarDiasConsecutivos = evitarDiasConsecutivos;
	}

	public Long getIdConfiguracion() {
		return idConfiguracion;
	}

	public void setIdConfiguracion(Long idConfiguracion) {
		this.idConfiguracion = idConfiguracion;
	}

	public LocalTime getHoraInicioSemana() {
		return horaInicioSemana;
	}

	public void setHoraInicioSemana(LocalTime horaInicioSemana) {
		this.horaInicioSemana = horaInicioSemana;
	}

	public LocalTime getHoraFinSemana() {
		return horaFinSemana;
	}

	public void setHoraFinSemana(LocalTime horaFinSemana) {
		this.horaFinSemana = horaFinSemana;
	}

	public LocalTime getHoraInicioSabado() {
		return horaInicioSabado;
	}

	public void setHoraInicioSabado(LocalTime horaInicioSabado) {
		this.horaInicioSabado = horaInicioSabado;
	}

	public LocalTime getHoraFinSabado() {
		return horaFinSabado;
	}

	public void setHoraFinSabado(LocalTime horaFinSabado) {
		this.horaFinSabado = horaFinSabado;
	}

	public LocalTime getHoraInicioDescanso() {
		return horaInicioDescanso;
	}

	public void setHoraInicioDescanso(LocalTime horaInicioDescanso) {
		this.horaInicioDescanso = horaInicioDescanso;
	}

	public LocalTime getHoraFinDescanso() {
		return horaFinDescanso;
	}

	public void setHoraFinDescanso(LocalTime horaFinDescanso) {
		this.horaFinDescanso = horaFinDescanso;
	}

	public int getDuracionSesionMinutos() {
		return duracionSesionMinutos;
	}

	public void setDuracionSesionMinutos(int duracionSesionMinutos) {
		this.duracionSesionMinutos = duracionSesionMinutos;
	}

	public int getMaxSesionesSemanales() {
		return maxSesionesSemanales;
	}

	public void setMaxSesionesSemanales(int maxSesionesSemanales) {
		this.maxSesionesSemanales = maxSesionesSemanales;
	}

	public boolean isEvitarDiasConsecutivos() {
		return evitarDiasConsecutivos;
	}

	public void setEvitarDiasConsecutivos(boolean evitarDiasConsecutivos) {
		this.evitarDiasConsecutivos = evitarDiasConsecutivos;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;
		ConfiguracionHorariosDTO other = (ConfiguracionHorariosDTO) obj;
		return Objects.equals(idConfiguracion, other.idConfiguracion)
				&& Objects.equals(horaInicioSemana, other.horaInicioSemana)
				&& Objects.equals(horaFinSemana, other.horaFinSemana)
				&& Objects.equals(horaInicioSabado, other.horaInicioSabado)
				&& Objects.equals(horaFinSabado, other.horaFinSabado)
				&& Objects.equals(horaInicioDescanso, other.horaInicioDescanso)
				&& Objects.equals(horaFinDescanso, other.horaFinDescanso)
				&& Objects.equals(duracionSesionMinutos, other.duracionSesionMinutos)
				&& Objects.equals(maxSesionesSemanales, other.maxSesionesSemanales)
				&& Objects.equals(evitarDiasConsecutivos, other.evitarDiasConsecutivos);
	}

	@Override
	public int hashCode() {
		return Objects.hash(idConfiguracion, horaInicioSemana, horaFinSemana, horaInicioSabado, horaFinSabado,
				horaInicioDescanso, horaFinDescanso, duracionSesionMinutos, maxSesionesSemanales,
				evitarDiasConsecutivos);
	}

	@Override
	public String toString() {
		return "ConfiguracionHorariosDTO{idConfiguracion=" + idConfiguracion + "}";
	}
}
