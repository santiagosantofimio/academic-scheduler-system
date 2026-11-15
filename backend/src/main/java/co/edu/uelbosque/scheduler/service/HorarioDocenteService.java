package co.edu.uelbosque.scheduler.service;

import java.util.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import co.edu.uelbosque.scheduler.dto.HorarioDocenteDTO;
import co.edu.uelbosque.scheduler.entity.*;
import co.edu.uelbosque.scheduler.exception.RecursoNoEncontradoException;
import co.edu.uelbosque.scheduler.repository.*;
import co.edu.uelbosque.scheduler.util.DtoMapper;

@Service
@Transactional(readOnly = true)
public class HorarioDocenteService {
	private final UsuarioRepository usuarios;
	private final GrupoRepository grupos;
	private final SesionHorarioRepository sesiones;
	private final ConfiguracionHorariosRepository configuraciones;
	private final ExportacionHorarioService exportacion;
	private final VersionHorarioRepository versiones;

	public HorarioDocenteService(UsuarioRepository usuarios, GrupoRepository grupos, SesionHorarioRepository sesiones,
			ConfiguracionHorariosRepository configuraciones, ExportacionHorarioService exportacion,
			VersionHorarioRepository versiones) {
		this.usuarios = usuarios;
		this.grupos = grupos;
		this.sesiones = sesiones;
		this.configuraciones = configuraciones;
		this.exportacion = exportacion;
		this.versiones = versiones;
	}

	public HorarioDocenteDTO consultar(String correo) {
		Long docenteId = docenteId(correo);
		List<Grupo> propios = grupos.buscarPorDocente(docenteId);
		List<ConfiguracionHorarios> registros = configuraciones.findAll();
		Optional<VersionHorario> vigente = vigente();
		List<SesionHorario> publicadas = publicadas(vigente, docenteId);
		List<Grupo> visibles = new ArrayList<>(propios);
		publicadas.stream().map(SesionHorario::getGrupo).filter(g -> !visibles.contains(g)).distinct()
				.forEach(visibles::add);
		return new HorarioDocenteDTO(registros.size() == 1 ? DtoMapper.toDto(registros.getFirst()) : null,
				visibles.stream().map(Grupo::getAsignatura).distinct().map(DtoMapper::toDto).toList(),
				visibles.stream().map(DtoMapper::toDto).toList(),
				publicadas.stream().sorted(Comparator.comparing(SesionHorario::getDiaSemana)
						.thenComparing(SesionHorario::getHoraInicio)).map(DtoMapper::toDto).toList(),
				vigente.map(VersionHorario::getNombre).orElse(null),
				vigente.map(VersionHorario::getPublicadaEn).orElse(null));
	}

	public byte[] csv(String correo) {
		Long id = docenteId(correo);
		return exportacion.csv(publicadas(vigente(), id));
	}

	public byte[] pdf(String correo) {
		Long id = docenteId(correo);
		return exportacion.pdf(publicadas(vigente(), id));
	}

	private Optional<VersionHorario> vigente() {
		return versiones.findFirstByEstadoOrderByIdVersionDesc(EstadoVersion.PUBLICADA);
	}

	private static List<SesionHorario> publicadas(Optional<VersionHorario> vigente, Long docenteId) {
		return vigente.map(v -> v.getSesiones().stream()
				.filter(s -> s.getGrupo() != null && s.getGrupo().getDocente() != null
						&& docenteId.equals(s.getGrupo().getDocente().getId()))
				.map(VersionSesion::comoSesion).toList()).orElseGet(List::of);
	}

	private Long docenteId(String correo) {
		return usuarios.findByCorreoInstitucional(correo).filter(Docente.class::isInstance).map(Usuario::getId)
				.orElseThrow(() -> new RecursoNoEncontradoException("La cuenta no corresponde a un docente"));
	}
}
