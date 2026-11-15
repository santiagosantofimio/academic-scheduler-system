package co.edu.uelbosque.scheduler.service;

import java.util.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import co.edu.uelbosque.scheduler.dto.*;
import co.edu.uelbosque.scheduler.exception.*;
import co.edu.uelbosque.scheduler.util.DatosUsuario;
import co.edu.uelbosque.scheduler.entity.*;
import co.edu.uelbosque.scheduler.security.SecurityConfig;
import co.edu.uelbosque.scheduler.repository.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ValidacionesLimiteTest {
    @AfterEach void limpiar() { SecurityContextHolder.clearContext(); }

    private AdministradorDTO usuario(String clave) {
        var dto = new AdministradorDTO(); dto.setNombre("Ana"); dto.setApellido("Rios");
        dto.setCorreoInstitucional("ana@unbosque.edu.co"); dto.setNumeroDocumento("55");
        dto.setContrasenia(clave); dto.setEstado(true); dto.setCargo("Coordinadora"); return dto;
    }

    @Test void laContraseniaExigeAlMenosOchoCaracteres() {
        var datos = new DatosUsuario(mock(UsuarioRepository.class), new SecurityConfig().passwordEncoder());
        assertThrows(DatosInvalidosException.class, () -> datos.aplicar(usuario("1"), new Administrador(), true));
        assertThrows(DatosInvalidosException.class, () -> datos.aplicar(usuario("1234567"), new Administrador(), true));
        assertDoesNotThrow(() -> datos.aplicar(usuario("12345678"), new Administrador(), true));
    }

    @Test void elAdministradorNoPuedeEliminarseNiDesactivarseASiMismo() {
        var rep = mock(AdministradorRepository.class);
        var yo = new Administrador("Ana", "Rios", "55", "ana@unbosque.edu.co", "h", true, "C"); yo.setId(1L);
        var otro = new Administrador("Luis", "Paz", "56", "luis@unbosque.edu.co", "h", true, "C"); otro.setId(2L);
        when(rep.findById(1L)).thenReturn(Optional.of(yo));
        when(rep.findAll()).thenReturn(List.of(yo, otro));
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("ana@unbosque.edu.co", null, List.of()));
        var servicio = new AdministradorService(rep, mock(DatosUsuario.class), mock(ConfiguracionHorariosRepository.class));
        assertThrows(AsignacionAcademicaInvalidaException.class, () -> servicio.deleteById(1L));
        var dto = usuario(null); dto.setEstado(false);
        assertThrows(AsignacionAcademicaInvalidaException.class, () -> servicio.updateById(1L, dto));
        verify(rep, never()).delete(any()); verify(rep, never()).saveAndFlush(any());
    }

    @Test void noSePuedeEliminarAlUnicoAdministradorActivo() {
        var rep = mock(AdministradorRepository.class);
        var unico = new Administrador("Luis", "Paz", "56", "luis@unbosque.edu.co", "h", true, "C"); unico.setId(2L);
        var inactivo = new Administrador("Eva", "Sol", "57", "eva@unbosque.edu.co", "h", false, "C"); inactivo.setId(3L);
        when(rep.findById(2L)).thenReturn(Optional.of(unico));
        when(rep.findById(3L)).thenReturn(Optional.of(inactivo));
        when(rep.findAll()).thenReturn(List.of(unico, inactivo));
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("otro@unbosque.edu.co", null, List.of()));
        var servicio = new AdministradorService(rep, mock(DatosUsuario.class), mock(ConfiguracionHorariosRepository.class));
        assertThrows(AsignacionAcademicaInvalidaException.class, () -> servicio.deleteById(2L));
        assertDoesNotThrow(() -> servicio.deleteById(3L));
    }
}
