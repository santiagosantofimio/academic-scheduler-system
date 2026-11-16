package co.edu.uelbosque.scheduler.security;

import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.jackson.autoconfigure.JacksonAutoConfiguration;
import org.springframework.boot.security.autoconfigure.web.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.tomcat.autoconfigure.servlet.TomcatServletWebServerAutoConfiguration;
import org.springframework.boot.webmvc.autoconfigure.DispatcherServletAutoConfiguration;
import org.springframework.boot.webmvc.autoconfigure.WebMvcAutoConfiguration;
import org.springframework.boot.webmvc.autoconfigure.error.ErrorMvcAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import co.edu.uelbosque.scheduler.controller.SeguridadController;
import co.edu.uelbosque.scheduler.controller.SeguridadController.TokenCsrf;
import co.edu.uelbosque.scheduler.controller.SesionHorarioController;
import co.edu.uelbosque.scheduler.service.SesionHorarioService;
import tools.jackson.databind.json.JsonMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@SpringBootTest(classes = SeguridadHttpTest.Contexto.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = { "server.address=127.0.0.1", "spring.main.banner-mode=off" })
class SeguridadHttpTest {
    private static final String ADMIN = "admin-prueba";
    private static final String DOCENTE = "docente-prueba";
    private static final String CLAVE = "clave-prueba";
    private static final String RUTA_MANUAL = "/api/sesiones/grupo/1/manual";
    private static final String SESIONES = """
            [{"diaSemana":"MONDAY","horaInicio":"16:00","horaFin":"18:00","estado":"PROGRAMADA","grupoId":1},
             {"diaSemana":"WEDNESDAY","horaInicio":"16:00","horaFin":"18:00","estado":"PROGRAMADA","grupoId":1}]
            """;

    @LocalServerPort
    private int puerto;

    @Autowired
    private SesionHorarioService servicio;

    @Test
    void registraSesionesPorHttpConLaCookieYElTokenCorrespondientes() throws Exception {
        var cookies = new CookieManager();
        var cookieAjena = new HttpCookie("JSESSIONID", "otra-aplicacion");
        cookieAjena.setPath("/api/sesiones");
        cookieAjena.setVersion(0);
        cookies.getCookieStore().add(url("/"), cookieAjena);
        try (var administrador = HttpClient.newBuilder().cookieHandler(cookies).build();
                var clienteSinCookie = HttpClient.newHttpClient();
                var docente = HttpClient.newBuilder().cookieHandler(new CookieManager()).build()) {
            var tokenAdministrador = obtenerToken(administrador, ADMIN);
            var cookieScheduler = cookies.getCookieStore().getCookies().stream()
                    .filter(cookie -> cookie.getName().equals("ACADEMIC_SCHEDULER_SESSION"))
                    .findFirst().orElseThrow();
            assertEquals("/api", cookieScheduler.getPath());
            assertTrue(cookieScheduler.isHttpOnly());

            var faltaCookie = registrar(clienteSinCookie, ADMIN, tokenAdministrador);
            assertError(faltaCookie, "CSRF_INVALIDO");

            var tokenDocente = obtenerToken(docente, DOCENTE);
            var faltaPermiso = registrar(docente, DOCENTE, tokenDocente);
            assertError(faltaPermiso, "PERMISO_INSUFICIENTE");
            verifyNoInteractions(servicio);

            var respuesta = registrar(administrador, ADMIN, tokenAdministrador);
            assertEquals(201, respuesta.statusCode(), respuesta::body);
            verify(servicio).registrarSesiones(eq(1L), anyList());
        }
    }

    private void assertError(HttpResponse<String> respuesta, String codigo) {
        assertEquals(403, respuesta.statusCode(), respuesta::body);
        var problema = JsonMapper.builder().build().readTree(respuesta.body());
        assertEquals(codigo, problema.get("code").asString());
    }

    private TokenCsrf obtenerToken(HttpClient cliente, String usuario) throws Exception {
        var solicitud = peticion("/api/csrf", usuario).GET().build();
        var respuesta = cliente.send(solicitud, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, respuesta.statusCode(), respuesta::body);
        return JsonMapper.builder().build().readValue(respuesta.body(), TokenCsrf.class);
    }

    private HttpResponse<String> registrar(HttpClient cliente, String usuario, TokenCsrf token) throws Exception {
        var solicitud = peticion(RUTA_MANUAL, usuario)
                .header("Content-Type", "application/json")
                .header(token.headerName(), token.token())
                .POST(HttpRequest.BodyPublishers.ofString(SESIONES))
                .build();
        return cliente.send(solicitud, HttpResponse.BodyHandlers.ofString());
    }

    private URI url(String ruta) {
        return URI.create("http://127.0.0.1:" + puerto + ruta);
    }

    private HttpRequest.Builder peticion(String ruta, String usuario) {
        var credenciales = Base64.getEncoder().encodeToString((usuario + ":" + CLAVE).getBytes(StandardCharsets.UTF_8));
        return HttpRequest.newBuilder(url(ruta))
                .timeout(Duration.ofSeconds(10))
                .header("Authorization", "Basic " + credenciales)
                .header("X-Requested-With", "XMLHttpRequest");
    }

    @Configuration(proxyBeanMethods = false)
    @Import({ SecurityConfig.class, SeguridadController.class, SesionHorarioController.class })
    @ImportAutoConfiguration({ TomcatServletWebServerAutoConfiguration.class, DispatcherServletAutoConfiguration.class,
            WebMvcAutoConfiguration.class, JacksonAutoConfiguration.class, SecurityFilterAutoConfiguration.class,
            ErrorMvcAutoConfiguration.class })
    static class Contexto {
        @Bean
        SesionHorarioService servicioSesiones() {
            return mock(SesionHorarioService.class);
        }

        @Bean
        UserDetailsService usuarios(PasswordEncoder encoder) {
            return new InMemoryUserDetailsManager(
                    User.withUsername(ADMIN).password(encoder.encode(CLAVE)).roles("ADMINISTRADOR").build(),
                    User.withUsername(DOCENTE).password(encoder.encode(CLAVE)).roles("DOCENTE").build());
        }
    }
}
