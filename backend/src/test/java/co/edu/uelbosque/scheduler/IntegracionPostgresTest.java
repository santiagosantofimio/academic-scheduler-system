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
import static org.junit.jupiter.api.Assertions.*;

@Testcontainers(disabledWithoutDocker = true)
@SpringBootTest(properties = { "scheduler.admin.correo=admin@example.test", "scheduler.admin.contrasenia=clave-prueba" })
@ActiveProfiles("dev")
@Import(TestcontainersConfiguration.class)
class IntegracionPostgresTest {
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

    @AfterEach void limpiar() {
        jdbc.update("delete from sesion_horario");
    }
    private long contar(String sql) {
        return jdbc.queryForObject(sql, Long.class);
    }
    @Test void aplicaLaMigracionYLosDatosDeDemostracion() {
        assertEquals(2, contar("select count(*) from flyway_schema_history where success"));
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
    @Test void elDocenteSoloRecibeSuPropioHorario() {
        sesiones.generarHorarioGrupo(GRUPO_PROGRAMACION);
        sesiones.generarHorarioGrupo(900003L);
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
}
