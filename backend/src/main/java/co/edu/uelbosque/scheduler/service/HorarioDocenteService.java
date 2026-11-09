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

	public HorarioDocenteService(UsuarioRepository usuarios, GrupoRepository grupos, SesionHorarioRepository sesiones,
			ConfiguracionHorariosRepository configuraciones, ExportacionHorarioService exportacion) {
		this.usuarios = usuarios;
		this.grupos = grupos;
		this.sesiones = sesiones;
		this.configuraciones = configuraciones;
		this.exportacion = exportacion;
	}

	public HorarioDocenteDTO consultar(String correo) {
		Long docenteId = docenteId(correo);
		List<Grupo> propios = grupos.buscarPorDocente(docenteId);
		List<ConfiguracionHorarios> registros = configuraciones.findAll();
		return new HorarioDocenteDTO(registros.size() == 1 ? DtoMapper.toDto(registros.getFirst()) : null,
				propios.stream().map(Grupo::getAsignatura).distinct().map(DtoMapper::toDto).toList(),
				propios.stream().map(DtoMapper::toDto).toList(),
				sesiones.findByDocenteId(docenteId).stream().filter(s -> s.getEstado() == EstadoSesion.PROGRAMADA)
						.sorted(Comparator.comparing(SesionHorario::getDiaSemana)
								.thenComparing(SesionHorario::getHoraInicio))
						.map(DtoMapper::toDto).toList());
	}

	public byte[] csv(String correo) {
		return exportacion.csv(null, docenteId(correo));
	}

	public byte[] pdf(String correo) {
		return exportacion.pdf(null, docenteId(correo));
	}

	private Long docenteId(String correo) {
		return usuarios.findByCorreoInstitucional(correo).filter(Docente.class::isInstance).map(Usuario::getId)
				.orElseThrow(() -> new RecursoNoEncontradoException("La cuenta no corresponde a un docente"));
	}
}
