package co.edu.uelbosque.scheduler.entity;

import java.time.*;
import java.util.Objects;
import co.edu.uelbosque.scheduler.entity.*;
import jakarta.persistence.*;
import co.edu.uelbosque.scheduler.util.IdentidadJpa;

@Entity
public class ConfiguracionHorarios {
	// Clave tecnica constante: la unicidad evita dos configuraciones en altas
	// concurrentes.
	@Column(nullable = false, unique = true, updatable = false, length = 20)
	private String claveInstitucional = "INSTITUCIONAL";
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
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

	public ConfiguracionHorarios() {

	}

	public ConfiguracionHorarios(LocalTime horaInicioSemana, LocalTime horaFinSemana, LocalTime horaInicioSabado,
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

	public ConfiguracionHorarios(long idConfiguracion, LocalTime horaInicioSemana, LocalTime horaFinSemana,
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
		if (obj == null || IdentidadJpa.tipo(this) != IdentidadJpa.tipo(obj))
			return false;
		ConfiguracionHorarios other = (ConfiguracionHorarios) obj;
		return getIdConfiguracion() != null && getIdConfiguracion().equals(other.getIdConfiguracion());
	}

	@Override
	public int hashCode() {
		return IdentidadJpa.tipo(this).hashCode();
	}

	@Override
	public String toString() {
		return "ConfiguracionHorarios{idConfiguracion=" + getIdConfiguracion() + "}";
	}
}
