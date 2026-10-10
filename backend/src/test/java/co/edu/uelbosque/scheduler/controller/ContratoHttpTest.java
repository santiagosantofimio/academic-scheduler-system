package co.edu.uelbosque.scheduler.controller;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import co.edu.uelbosque.scheduler.controller.GrupoController;
import co.edu.uelbosque.scheduler.exception.GlobalExceptionHandler;
import co.edu.uelbosque.scheduler.service.GrupoService;
import co.edu.uelbosque.scheduler.exception.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ContratoHttpTest {
    private GrupoService servicio;
    private MockMvc mvc;
    @BeforeEach void preparar() {
        servicio=mock(GrupoService.class);
        mvc=MockMvcBuilders.standaloneSetup(new GrupoController(servicio)).setControllerAdvice(new GlobalExceptionHandler()).build();
    }
    @Test void altaListaYEliminacionTienenCodigosCoherentes() throws Exception {
        when(servicio.create(any())).thenReturn(1); when(servicio.getAll()).thenReturn(List.of());
        mvc.perform(post("/api/grupos").contentType(MediaType.APPLICATION_JSON).content("{\"numeroGrupo\":\"01\",\"estado\":\"ACTIVO\",\"asignaturaId\":1}"))
                .andExpect(status().isCreated());
        mvc.perform(get("/api/grupos")).andExpect(status().isOk()).andExpect(content().json("[]"));
        mvc.perform(delete("/api/grupos/1")).andExpect(status().isNoContent()).andExpect(content().string(""));
    }
    @Test void erroresDeDominioSon404Y409SinExitoFalso() throws Exception {
        when(servicio.getById(1L)).thenThrow(new RecursoNoEncontradoException("Grupo no encontrado"));
        when(servicio.create(any())).thenThrow(new DuplicadoException("Numero duplicado"));
        mvc.perform(get("/api/grupos/1")).andExpect(status().isNotFound()).andExpect(jsonPath("$.status").value(404));
        mvc.perform(post("/api/grupos").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isConflict()).andExpect(jsonPath("$.detail").value("Numero duplicado"));
    }
    @Test void enumOIdInvalidoSeResponde400() throws Exception {
        mvc.perform(get("/api/grupos/por-estado").param("valor","INVENTADO")).andExpect(status().isBadRequest());
        mvc.perform(get("/api/grupos/no-es-un-numero")).andExpect(status().isBadRequest());
        mvc.perform(post("/api/grupos").contentType(MediaType.APPLICATION_JSON).content("{\"estado\":\"INVENTADO\"}"))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(servicio);
    }
    @Test void restriccionDePersistenciaNoExponeSql() throws Exception {
        when(servicio.deleteById(1L)).thenThrow(new org.springframework.dao.DataIntegrityViolationException("SQL interno secreto"));
        mvc.perform(delete("/api/grupos/1")).andExpect(status().isConflict())
                .andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("SQL interno secreto"))));
    }
}
