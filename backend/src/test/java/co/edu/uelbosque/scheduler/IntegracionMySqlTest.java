package co.edu.uelbosque.scheduler;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;
import co.edu.uelbosque.scheduler.dto.AsignaturaDTO;
import co.edu.uelbosque.scheduler.dto.SesionHorarioDTO;
import co.edu.uelbosque.scheduler.exception.DuplicadoException;
import co.edu.uelbosque.scheduler.service.AsignaturaService;
import co.edu.uelbosque.scheduler.service.DocenteService;
import co.edu.uelbosque.scheduler.exception.AsignacionAcademicaInvalidaException;
import co.edu.uelbosque.scheduler.service.GrupoService;
import co.edu.uelbosque.scheduler.service.HorarioDocenteService;
import co.edu.uelbosque.scheduler.service.SesionHorarioService;
import co.edu.uelbosque.scheduler.service.VersionHorarioService;
import co.edu.uelbosque.scheduler.service.NovedadDocenteService;
import co.edu.uelbosque.scheduler.service.AuditoriaService;
import co.edu.uelbosque.scheduler.dto.NombreVersionDTO;
import co.edu.uelbosque.scheduler.dto.NovedadDocenteDTO;
import co.edu.uelbosque.scheduler.entity.EstadoSesion;
import co.edu.uelbosque.scheduler.entity.EstadoVersion;
import co.edu.uelbosque.scheduler.entity.TipoNovedad;
import co.edu.uelbosque.scheduler.exception.DisponibilidadInvalidaException;
import co.edu.uelbosque.scheduler.exception.PublicacionRechazadaException;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import java.time.DayOfWeek;
import static org.junit.jupiter.api.Assertions.*;

@Testcontainers(disabledWithoutDocker = true)
@SpringBootTest(properties = { "scheduler.admin.correo=admin@example.test", "scheduler.admin.contrasenia=clave-prueba" })
@ActiveProfiles("dev")
@Import(TestcontainersConfiguration.class)
class IntegracionMySqlTest {
    private static final long GRUPO_PROGRAMACION = 900001L;
    private static final long GRUPO_CALCULO = 900002L;

    @Autowired private JdbcTemplate jdbc;
    @Autowired private UserDetailsService usuarios;
    @Autowired private PasswordEncoder encoder;
    @Autowired private SesionHorarioService sesiones;
    @Autowired private AsignaturaService asignaturas;
    @Autowired private GrupoService grupos;
    @Autowired private HorarioDocenteService miHorario;
    @Autowired private DocenteService docentes;
    @Autowired private VersionHorarioService versiones;
    @Autowired private NovedadDocenteService novedades;
    @Autowired private AuditoriaService auditoria;

    @BeforeEach void autenticar() {
        SecurityContextHolder.getContext().setAuthentication(new TestingAuthenticationToken("admin@example.test", null));
    }
    @AfterEach void limpiar() {
        SecurityContextHolder.clearContext();
        jdbc.update("delete from sesion_horario");
        jdbc.update("delete from version_horario");
        jdbc.update("delete from novedad_docente");
        jdbc.update("delete from auditoria_cambio");
    }
    private long contar(String sql) {
        return jdbc.queryForObject(sql, Long.class);
    }
    @Test void aplicaLaMigracionYLosDatosDeDemostracion() {
        assertEquals(3, contar("select count(*) from flyway_schema_history where success"));
        assertEquals(4, contar("select count(*) from docente"));
        assertEquals(7, contar("select count(*) from grupo"));
        assertEquals(58, contar("select count(*) from asignatura"));
        assertEquals(148, contar("select sum(creditos) from asignatura"));
    }
    @Test void elAdministradorInicialPuedeAutenticarseYNoComparteIdConDocentes() {
        var cuenta = usuarios.loadUserByUsername("admin@example.test");
        assertTrue(encoder.matches("clave-prueba", cuenta.getPassword()));
        assertEquals(0, contar("select count(*) from administrador a join docente d using (id)"));
    }
    @Test void generaYGuardaUnHorarioEnLaBaseReal() {
        var generadas = sesiones.generarHorarioGrupo(GRUPO_PROGRAMACION);
        assertEquals(2, generadas.size());
        assertEquals(2, generadas.stream().map(SesionHorarioDTO::getDiaSemana).distinct().count());
        assertEquals(generadas, sesiones.generarHorarioGrupo(GRUPO_PROGRAMACION));
    }
    @Test void generacionesSimultaneasDelMismoDocenteNoSeCruzan() throws Exception {
        try (var hilos = Executors.newFixedThreadPool(2)) {
            List<Callable<List<SesionHorarioDTO>>> tareas = List.of(
                    () -> sesiones.generarHorarioGrupo(GRUPO_PROGRAMACION),
                    () -> sesiones.generarHorarioGrupo(GRUPO_CALCULO));
            for (var resultado : hilos.invokeAll(tareas))
                assertFalse(resultado.get().isEmpty());
        }
        assertEquals(3, contar("select count(*) from sesion_horario"));
        assertEquals(0, contar("""
                select count(*) from sesion_horario a join sesion_horario b
                on a.id_sesion < b.id_sesion and a.dia_semana = b.dia_semana
                and a.hora_inicio < b.hora_fin and b.hora_inicio < a.hora_fin
                where a.grupo_id = b.grupo_id
                or (select docente_id from grupo where id_grupo = a.grupo_id) = (select docente_id from grupo where id_grupo = b.grupo_id)
                """));
    }
    @Test void laBaseRechazaCodigosDuplicadosYGruposConSesiones() {
        var duplicada = new AsignaturaDTO("IS101", "Otra", 1, 3, 2, "Software", true);
        duplicada.setPlanEstudiosId(900001L);
        assertThrows(DuplicadoException.class, () -> asignaturas.create(duplicada));
        sesiones.generarHorarioGrupo(GRUPO_PROGRAMACION);
        assertThrows(DataIntegrityViolationException.class, () -> grupos.deleteById(GRUPO_PROGRAMACION));
        assertEquals(1, contar("select count(*) from grupo where id_grupo = " + GRUPO_PROGRAMACION));
    }
    @Test void elDocenteSoloRecibeSuPropioHorarioPublicado() {
        sesiones.generarHorarioGrupo(GRUPO_PROGRAMACION);
        sesiones.generarHorarioGrupo(900003L);
        assertTrue(miHorario.consultar("lmendez@unbosque.edu.co").sesiones().isEmpty());
        versiones.publicar(new NombreVersionDTO("Horario 2026-2"));
        var propio = miHorario.consultar("lmendez@unbosque.edu.co");
        assertEquals(2, propio.sesiones().size());
        assertTrue(propio.sesiones().stream().allMatch(s -> s.getGrupoId() == GRUPO_PROGRAMACION));
        assertEquals(2, propio.grupos().size());
        assertNotNull(propio.configuracion());
        var csv = new String(miHorario.csv("lmendez@unbosque.edu.co"), java.nio.charset.StandardCharsets.UTF_8);
        assertEquals(3, csv.lines().count());
        assertFalse(csv.contains("Rojas"));
    }
    @Test void eliminarDocenteExplicaElBloqueoYBorraSuPerfilCuandoNoTieneGrupos() {
        var error = assertThrows(AsignacionAcademicaInvalidaException.class, () -> docentes.deleteById(900003L));
        assertTrue(error.getMessage().contains("IS405 grupo 01"));
        jdbc.update("insert into usuario (id, nombre, apellido, numero_documento, correo_institucional, contrasenia, estado) values (777001, 'Sin', 'Grupos', '777001', 'singrupos@example.test', '{noop}x', true)");
        jdbc.update("insert into docente (id, tipo_vinculacion, escalafon, especialidad, horas_maximas_docencia_semanal) values (777001, 'MEDIO_TIEMPO', 'A', 'B', 10)");
        jdbc.update("insert into disponibilidad_docente (id_disponibilidad, dia_semana, hora_inicio, hora_fin, docente_id) values (777001, 'MONDAY', '07:00', '09:00', 777001)");
        docentes.deleteById(777001L);
        assertEquals(0, contar("select count(*) from usuario where id = 777001"));
        assertEquals(0, contar("select count(*) from disponibilidad_docente where docente_id = 777001"));
        assertEquals(4, contar("select count(*) from docente"));
    }
    @Test void publicarExigeUnHorarioSinConflictosYConservaLasVersionesAnteriores() {
        sesiones.generarHorarioGrupo(GRUPO_PROGRAMACION);
        var primera = versiones.publicar(new NombreVersionDTO("Version 1"));
        assertEquals(EstadoVersion.PUBLICADA, primera.estado());
        assertEquals("admin@example.test", primera.publicadaPor());
        var clase = sesiones.findByGrupoId(GRUPO_PROGRAMACION).getFirst();
        jdbc.update("insert into novedad_docente (docente_id, tipo, dia_semana, estado, registrada_en, registrada_por) values (900001, 'INCAPACIDAD', ?, 'ABIERTA', now(), 'prueba')",
                clase.getDiaSemana().name());
        var rechazo = assertThrows(PublicacionRechazadaException.class, () -> versiones.publicar(new NombreVersionDTO("Version 2")));
        assertTrue(rechazo.getMessage().contains("novedad"));
        assertEquals(1, versiones.conflictos().size());
        assertEquals(1, contar("select count(*) from version_horario where estado = 'PUBLICADA'"));
        jdbc.update("delete from novedad_docente");
        versiones.publicar(new NombreVersionDTO("Version 2"));
        assertEquals(1, contar("select count(*) from version_horario where estado = 'PUBLICADA' and nombre = 'Version 2'"));
        assertEquals(1, contar("select count(*) from version_horario where estado = 'HISTORICA' and nombre = 'Version 1'"));
    }
    @Test void losBorradoresSeComparanYSeRecuperanSinModificarLosDemas() {
        sesiones.generarHorarioGrupo(GRUPO_PROGRAMACION);
        sesiones.generarHorarioGrupo(GRUPO_CALCULO);
        var completo = versiones.guardarBorrador(new NombreVersionDTO("Completo"));
        sesiones.deleteById(sesiones.findByGrupoId(GRUPO_CALCULO).getFirst().getIdSesion());
        var parcial = versiones.guardarBorrador(new NombreVersionDTO("Parcial"));
        assertEquals(3, completo.indicadores().sesiones());
        assertEquals(2, parcial.indicadores().sesiones());
        assertEquals(6.0, completo.indicadores().cargaMaxima());
        assertEquals(4.0, parcial.indicadores().cargaMaxima());
        var carga = versiones.cargar(completo.idVersion());
        assertEquals(3, carga.sesiones());
        assertNull(carga.versionCreadaId());
        assertTrue(carga.conflictos().isEmpty());
        assertEquals(3, contar("select count(*) from sesion_horario"));
        assertEquals(2, versiones.detalle(parcial.idVersion()).sesiones().size());
        var publicada = versiones.publicar(new NombreVersionDTO("Oficial"));
        var recuperada = versiones.cargar(publicada.idVersion());
        assertNotNull(recuperada.versionCreadaId());
        assertEquals(4, contar("select count(*) from version_horario"));
        assertTrue(auditoria.recientes().stream().anyMatch(a -> a.accion().equals("PUBLICAR") && a.usuario().equals("admin@example.test")));
        assertTrue(auditoria.recientes().stream().anyMatch(a -> a.accion().equals("ELIMINAR_SESION")));
    }
    @Test void unaNovedadListaLasClasesAfectadasYSugiereFranjasValidas() {
        sesiones.generarHorarioGrupo(GRUPO_PROGRAMACION);
        var clase = sesiones.findByGrupoId(GRUPO_PROGRAMACION).getFirst();
        var novedad = novedades.registrar(new NovedadDocenteDTO(null, 900001L, null, TipoNovedad.INCAPACIDAD,
                clase.getDiaSemana(), null, null, "Cita medica", null, null, null));
        var afectadas = novedades.afectadas(novedad.idNovedad());
        assertEquals(1, afectadas.size());
        var franja = afectadas.getFirst().alternativas().getFirst();
        assertNotEquals(clase.getDiaSemana(), franja.diaSemana());
        var ajuste = new SesionHorarioDTO(franja.diaSemana(), franja.horaInicio(), franja.horaFin(), EstadoSesion.PROGRAMADA);
        ajuste.setGrupoId(GRUPO_PROGRAMACION);
        sesiones.updateById(clase.getIdSesion(), ajuste);
        assertTrue(novedades.afectadas(novedad.idNovedad()).isEmpty());
        assertTrue(versiones.conflictos().isEmpty());
        var regreso = new SesionHorarioDTO(clase.getDiaSemana(), clase.getHoraInicio(), clase.getHoraFin(), EstadoSesion.PROGRAMADA);
        regreso.setGrupoId(GRUPO_PROGRAMACION);
        assertThrows(DisponibilidadInvalidaException.class, () -> sesiones.updateById(clase.getIdSesion(), regreso));
        novedades.cerrar(novedad.idNovedad());
        sesiones.updateById(clase.getIdSesion(), regreso);
    }
    @Test void validarYGenerarRespondenEnMenosDeDosSegundos() {
        long inicio = System.nanoTime();
        sesiones.generarHorarioGrupo(GRUPO_PROGRAMACION);
        long generar = (System.nanoTime() - inicio) / 1_000_000;
        inicio = System.nanoTime();
        versiones.conflictos();
        long validar = (System.nanoTime() - inicio) / 1_000_000;
        System.out.println("Tiempo de generacion: " + generar + " ms; tiempo de validacion: " + validar + " ms");
        assertTrue(generar < 2000);
        assertTrue(validar < 2000);
    }
}
