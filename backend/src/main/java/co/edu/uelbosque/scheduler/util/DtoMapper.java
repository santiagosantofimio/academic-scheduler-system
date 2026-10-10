package co.edu.uelbosque.scheduler.util;

import co.edu.uelbosque.scheduler.entity.*;
import co.edu.uelbosque.scheduler.dto.*;

/** Mapeo explicito: nunca copia hashes ni asociaciones JPA a las respuestas. */
public final class DtoMapper {
	private DtoMapper() {
	}

	public static FormacionAcademicaDTO toDto(FormacionAcademica entity) {
		FormacionAcademicaDTO dto = new FormacionAcademicaDTO();
		dto.setIdFormacion(entity.getIdFormacion());
		dto.setTituloObtenido(entity.getTituloObtenido());
		dto.setNivelFormacion(entity.getNivelFormacion());
		dto.setInstitucion(entity.getInstitucion());
		dto.setAreaConocimiento(entity.getAreaConocimiento());
		dto.setAnioGraduacion(entity.getAnioGraduacion());
		if (entity.getDocente() != null)
			dto.setDocenteId(entity.getDocente().getId());
		return dto;
	}

	public static void copy(FormacionAcademicaDTO dto, FormacionAcademica entity) {
		entity.setTituloObtenido(dto.getTituloObtenido());
		entity.setNivelFormacion(dto.getNivelFormacion());
		entity.setInstitucion(dto.getInstitucion());
		entity.setAreaConocimiento(dto.getAreaConocimiento());
		entity.setAnioGraduacion(dto.getAnioGraduacion());
	}

	public static ExperienciaDocenteDTO toDto(ExperienciaDocente entity) {
		ExperienciaDocenteDTO dto = new ExperienciaDocenteDTO();
		dto.setIdExperiencia(entity.getIdExperiencia());
		dto.setTipoExperiencia(entity.getTipoExperiencia());
		dto.setInstitucion(entity.getInstitucion());
		dto.setCargo(entity.getCargo());
		dto.setAreaConocimiento(entity.getAreaConocimiento());
		dto.setDescripcion(entity.getDescripcion());
		dto.setFechaInicio(entity.getFechaInicio());
		dto.setFechaFin(entity.getFechaFin());
		if (entity.getDocente() != null)
			dto.setDocenteId(entity.getDocente().getId());
		return dto;
	}

	public static void copy(ExperienciaDocenteDTO dto, ExperienciaDocente entity) {
		entity.setTipoExperiencia(dto.getTipoExperiencia());
		entity.setInstitucion(dto.getInstitucion());
		entity.setCargo(dto.getCargo());
		entity.setAreaConocimiento(dto.getAreaConocimiento());
		entity.setDescripcion(dto.getDescripcion());
		entity.setFechaInicio(dto.getFechaInicio());
		entity.setFechaFin(dto.getFechaFin());
	}

	public static PlanEstudiosDTO toDto(PlanEstudios entity) {
		PlanEstudiosDTO dto = new PlanEstudiosDTO();
		dto.setIdPlan(entity.getIdPlan());
		dto.setNombrePrograma(entity.getNombrePrograma());
		dto.setCodigoPrograma(entity.getCodigoPrograma());
		dto.setVersionPlan(entity.getVersionPlan());
		dto.setNumerosSemestres(entity.getNumerosSemestres());
		dto.setEstado(entity.isEstado());
		return dto;
	}

	public static void copy(PlanEstudiosDTO dto, PlanEstudios entity) {
		entity.setNombrePrograma(dto.getNombrePrograma());
		entity.setCodigoPrograma(dto.getCodigoPrograma());
		entity.setVersionPlan(dto.getVersionPlan());
		entity.setNumerosSemestres(dto.getNumerosSemestres());
		entity.setEstado(dto.isEstado());
	}

	public static AsignaturaDTO toDto(Asignatura entity) {
		AsignaturaDTO dto = new AsignaturaDTO();
		dto.setIdAsginatura(entity.getIdAsginatura());
		dto.setCodigo(entity.getCodigo());
		dto.setNombre(entity.getNombre());
		dto.setSemestre(entity.getSemestre());
		dto.setCreditos(entity.getCreditos());
		dto.setSesionesSemanales(entity.getSesionesSemanales());
		dto.setAreaConocimiento(entity.getAreaConocimiento());
		dto.setEstado(entity.isEstado());
		if (entity.getPlanEstudios() != null)
			dto.setPlanEstudiosId(entity.getPlanEstudios().getIdPlan());
		return dto;
	}

	public static void copy(AsignaturaDTO dto, Asignatura entity) {
		entity.setCodigo(dto.getCodigo());
		entity.setNombre(dto.getNombre());
		entity.setSemestre(dto.getSemestre());
		entity.setCreditos(dto.getCreditos());
		entity.setSesionesSemanales(dto.getSesionesSemanales());
		entity.setAreaConocimiento(dto.getAreaConocimiento());
		entity.setEstado(dto.isEstado());
	}

	public static GrupoDTO toDto(Grupo entity) {
		GrupoDTO dto = new GrupoDTO();
		if (entity.getDocente() != null)
			dto.setDocenteId(entity.getDocente().getId());
		dto.setIdGrupo(entity.getIdGrupo());
		dto.setNumeroGrupo(entity.getNumeroGrupo());
		dto.setEstado(entity.getEstado());
		if (entity.getAsignatura() != null)
			dto.setAsignaturaId(entity.getAsignatura().getIdAsginatura());
		return dto;
	}

	public static void copy(GrupoDTO dto, Grupo entity) {
		entity.setNumeroGrupo(dto.getNumeroGrupo());
		entity.setEstado(dto.getEstado());
	}

	public static RequisitoAsignaturaDTO toDto(RequisitoAsignatura entity) {
		RequisitoAsignaturaDTO dto = new RequisitoAsignaturaDTO();
		dto.setIdRequisito(entity.getIdRequisito());
		dto.setTipoRequisito(entity.getTipoRequisito());
		dto.setDescripcion(entity.getDescripcion());
		dto.setAreaConocimiento(entity.getAreaConocimiento());
		dto.setNivelFormacionMinimo(entity.getNivelFormacionMinimo());
		dto.setAniosExperienciaMinima(entity.getAniosExperienciaMinima());
		if (entity.getAsignatura() != null)
			dto.setAsignaturaId(entity.getAsignatura().getIdAsginatura());
		return dto;
	}

	public static void copy(RequisitoAsignaturaDTO dto, RequisitoAsignatura entity) {
		entity.setTipoRequisito(dto.getTipoRequisito());
		entity.setDescripcion(dto.getDescripcion());
		entity.setAreaConocimiento(dto.getAreaConocimiento());
		entity.setNivelFormacionMinimo(dto.getNivelFormacionMinimo());
		entity.setAniosExperienciaMinima(dto.getAniosExperienciaMinima());
	}

	public static SesionHorarioDTO toDto(SesionHorario entity) {
		SesionHorarioDTO dto = new SesionHorarioDTO();
		dto.setIdSesion(entity.getIdSesion());
		dto.setDiaSemana(entity.getDiaSemana());
		dto.setHoraInicio(entity.getHoraInicio());
		dto.setHoraFin(entity.getHoraFin());
		dto.setEstado(entity.getEstado());
		if (entity.getGrupo() != null)
			dto.setGrupoId(entity.getGrupo().getIdGrupo());
		return dto;
	}

	public static void copy(SesionHorarioDTO dto, SesionHorario entity) {
		entity.setDiaSemana(dto.getDiaSemana());
		entity.setHoraInicio(dto.getHoraInicio());
		entity.setHoraFin(dto.getHoraFin());
		entity.setEstado(dto.getEstado());
	}

	public static ConfiguracionHorariosDTO toDto(ConfiguracionHorarios entity) {
		ConfiguracionHorariosDTO dto = new ConfiguracionHorariosDTO();
		dto.setIdConfiguracion(entity.getIdConfiguracion());
		dto.setHoraInicioSemana(entity.getHoraInicioSemana());
		dto.setHoraFinSemana(entity.getHoraFinSemana());
		dto.setHoraInicioSabado(entity.getHoraInicioSabado());
		dto.setHoraFinSabado(entity.getHoraFinSabado());
		dto.setHoraInicioDescanso(entity.getHoraInicioDescanso());
		dto.setHoraFinDescanso(entity.getHoraFinDescanso());
		dto.setDuracionSesionMinutos(entity.getDuracionSesionMinutos());
		dto.setMaxSesionesSemanales(entity.getMaxSesionesSemanales());
		dto.setEvitarDiasConsecutivos(entity.isEvitarDiasConsecutivos());
		return dto;
	}

	public static void copy(ConfiguracionHorariosDTO dto, ConfiguracionHorarios entity) {
		entity.setHoraInicioSemana(dto.getHoraInicioSemana());
		entity.setHoraFinSemana(dto.getHoraFinSemana());
		entity.setHoraInicioSabado(dto.getHoraInicioSabado());
		entity.setHoraFinSabado(dto.getHoraFinSabado());
		entity.setHoraInicioDescanso(dto.getHoraInicioDescanso());
		entity.setHoraFinDescanso(dto.getHoraFinDescanso());
		entity.setDuracionSesionMinutos(dto.getDuracionSesionMinutos());
		entity.setMaxSesionesSemanales(dto.getMaxSesionesSemanales());
		entity.setEvitarDiasConsecutivos(dto.isEvitarDiasConsecutivos());
	}

	public static AdministradorDTO toDto(Administrador entity) {
		AdministradorDTO dto = new AdministradorDTO();
		dto.setId(entity.getId());
		dto.setNombre(entity.getNombre());
		dto.setApellido(entity.getApellido());
		dto.setNumeroDocumento(entity.getNumeroDocumento());
		dto.setCorreoInstitucional(entity.getCorreoInstitucional());
		dto.setEstado(entity.isEstado());
		dto.setCargo(entity.getCargo());
		return dto;
	}

	public static void copy(AdministradorDTO dto, Administrador entity) {
		entity.setNombre(dto.getNombre());
		entity.setApellido(dto.getApellido());
		entity.setNumeroDocumento(dto.getNumeroDocumento());
		entity.setCorreoInstitucional(dto.getCorreoInstitucional());
		entity.setEstado(dto.isEstado());
		entity.setCargo(dto.getCargo());
	}

	public static DocenteDTO toDto(Docente entity) {
		DocenteDTO dto = new DocenteDTO();
		dto.setId(entity.getId());
		dto.setNombre(entity.getNombre());
		dto.setApellido(entity.getApellido());
		dto.setNumeroDocumento(entity.getNumeroDocumento());
		dto.setCorreoInstitucional(entity.getCorreoInstitucional());
		dto.setEstado(entity.isEstado());
		dto.setTipoVinculacion(entity.getTipoVinculacion());
		dto.setEscalafon(entity.getEscalafon());
		dto.setEspecialidad(entity.getEspecialidad());
		dto.setHorasMaximasDocenciaSemanal(entity.getHorasMaximasDocenciaSemanal());
		return dto;
	}

	public static void copy(DocenteDTO dto, Docente entity) {
		entity.setNombre(dto.getNombre());
		entity.setApellido(dto.getApellido());
		entity.setNumeroDocumento(dto.getNumeroDocumento());
		entity.setCorreoInstitucional(dto.getCorreoInstitucional());
		entity.setEstado(dto.isEstado());
		entity.setTipoVinculacion(dto.getTipoVinculacion());
		entity.setEscalafon(dto.getEscalafon());
		entity.setEspecialidad(dto.getEspecialidad());
		entity.setHorasMaximasDocenciaSemanal(dto.getHorasMaximasDocenciaSemanal());
	}
}
