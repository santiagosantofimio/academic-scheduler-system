package co.edu.uelbosque.scheduler.security;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockServletContext;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.bind.annotation.*;
import co.edu.uelbosque.scheduler.controller.SeguridadController;
import tools.jackson.databind.json.JsonMapper;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class SeguridadTest {
    private AnnotationConfigWebApplicationContext contexto;
    private MockMvc mvc;
    @Configuration @EnableWebMvc @Import({SecurityConfig.class,SeguridadController.class,RecursoDePrueba.class})
    static class Contexto {
        @Bean UserDetailsService usuariosSoloDePrueba(PasswordEncoder encoder) {
            return new InMemoryUserDetailsManager(
                    User.withUsername("admin-prueba").password(encoder.encode("clave-prueba")).roles("ADMINISTRADOR").build(),
                    User.withUsername("docente-prueba").password(encoder.encode("clave-prueba")).roles("DOCENTE").build(),
                    User.withUsername("inactivo-prueba").password(encoder.encode("clave-prueba")).roles("ADMINISTRADOR").disabled(true).build());
        }
    }
    @RestController static class RecursoDePrueba {
        @GetMapping("/api/prueba") String leer() { return "ok"; }
        @PostMapping("/api/prueba") String escribir() { return "guardado"; }
        @GetMapping("/api/sesiones") String sesiones() { return "ok"; }
        @PostMapping("/api/sesiones") String crearSesion() { return "guardado"; }
        @GetMapping("/api/docentes") String docentes() { return "ok"; }
        @GetMapping("/api/usuarios/actual") String actual() { return "ok"; }
        @GetMapping("/api/mi-horario") String miHorario() { return "ok"; }
        @GetMapping("/api/mi-horario/exportar/pdf") String miPdf() { return "ok"; }
        @PostMapping("/api/mi-horario") String escribirMiHorario() { return "guardado"; }
    }
    @BeforeEach void preparar() {
        contexto=new AnnotationConfigWebApplicationContext(); contexto.setServletContext(new MockServletContext());
        contexto.register(Contexto.class); contexto.refresh();
        mvc=MockMvcBuilders.webAppContextSetup(contexto).apply(springSecurity()).build();
    }
    @AfterEach void cerrar() { contexto.close(); }
    @Test void anonimoRecibe401YDocenteNoPuedeGestionar() throws Exception {
        mvc.perform(get("/api/prueba")).andExpect(status().isUnauthorized());
        mvc.perform(get("/api/prueba").with(httpBasic("docente-prueba","clave-prueba"))).andExpect(status().isForbidden());
        mvc.perform(get("/api/prueba").with(httpBasic("admin-prueba","clave-prueba"))).andExpect(status().isOk());
    }
    @Test void docenteSoloLeeSuHorario() throws Exception {
        mvc.perform(get("/api/mi-horario").with(httpBasic("docente-prueba","clave-prueba"))).andExpect(status().isOk());
        mvc.perform(get("/api/mi-horario/exportar/pdf").with(httpBasic("docente-prueba","clave-prueba"))).andExpect(status().isOk());
        mvc.perform(get("/api/usuarios/actual").with(httpBasic("docente-prueba","clave-prueba"))).andExpect(status().isOk());
        mvc.perform(get("/api/sesiones").with(httpBasic("docente-prueba","clave-prueba"))).andExpect(status().isForbidden());
        mvc.perform(get("/api/docentes").with(httpBasic("docente-prueba","clave-prueba"))).andExpect(status().isForbidden());
        mvc.perform(post("/api/mi-horario").with(httpBasic("docente-prueba","clave-prueba")).with(csrf())).andExpect(status().isForbidden());
        mvc.perform(post("/api/sesiones").with(httpBasic("docente-prueba","clave-prueba")).with(csrf())).andExpect(status().isForbidden());
    }
    @Test void administradorGestionaPeroNoTieneHorarioPropio() throws Exception {
        mvc.perform(post("/api/sesiones").with(httpBasic("admin-prueba","clave-prueba")).with(csrf())).andExpect(status().isOk());
        mvc.perform(get("/api/sesiones").with(httpBasic("admin-prueba","clave-prueba"))).andExpect(status().isOk());
        mvc.perform(get("/api/mi-horario").with(httpBasic("admin-prueba","clave-prueba"))).andExpect(status().isForbidden());
        mvc.perform(get("/api/mi-horario")).andExpect(status().isUnauthorized());
        mvc.perform(get("/api/usuarios/actual")).andExpect(status().isUnauthorized());
    }
    @Test void laAplicacionWebNoRecibeRetoQueAbraElDialogoDelNavegador() throws Exception {
        mvc.perform(get("/api/sesiones").with(httpBasic("admin-prueba","incorrecta")).header("X-Requested-With","XMLHttpRequest"))
                .andExpect(status().isUnauthorized()).andExpect(header().doesNotExist("WWW-Authenticate"))
                .andExpect(jsonPath("$.status").value(401));
        mvc.perform(get("/api/sesiones").with(httpBasic("admin-prueba","incorrecta")))
                .andExpect(status().isUnauthorized()).andExpect(header().exists("WWW-Authenticate"));
    }
    @Test void inactivoYPasswordIncorrectoNoAutentican() throws Exception {
        mvc.perform(get("/api/prueba").with(httpBasic("inactivo-prueba","clave-prueba"))).andExpect(status().isUnauthorized());
        mvc.perform(get("/api/prueba").with(httpBasic("admin-prueba","incorrecta"))).andExpect(status().isUnauthorized());
    }
    @Test void escrituraRequiereTokenCsrfYCookieCorrespondiente() throws Exception {
        mvc.perform(post("/api/prueba").with(httpBasic("admin-prueba","clave-prueba"))).andExpect(status().isForbidden());
        var respuesta=mvc.perform(get("/api/csrf").with(httpBasic("admin-prueba","clave-prueba")))
                .andExpect(status().isOk()).andReturn();
        var token=JsonMapper.builder().build().readTree(respuesta.getResponse().getContentAsString());
        var sesion=(MockHttpSession) respuesta.getRequest().getSession(false);
        mvc.perform(post("/api/prueba").with(httpBasic("admin-prueba","clave-prueba")).session(sesion)
                .header(token.get("headerName").asString(),token.get("token").asString()))
                .andExpect(status().isOk()).andExpect(content().string("guardado"));
    }
}
