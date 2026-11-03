package co.edu.uelbosque.scheduler.configuration;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import co.edu.uelbosque.scheduler.dto.AdministradorDTO;
import co.edu.uelbosque.scheduler.repository.AdministradorRepository;
import co.edu.uelbosque.scheduler.service.AdministradorService;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdministradorInicialTest {
    private final AdministradorRepository administradores = mock(AdministradorRepository.class);
    private final AdministradorService servicio = mock(AdministradorService.class);

    private void arrancar(long existentes, String correo, String contrasenia) {
        when(administradores.count()).thenReturn(existentes);
        new AdministradorInicial(administradores, servicio, correo, contrasenia).run(null);
    }
    @Test void creaElPrimerAdministradorConLasCredencialesConfiguradas() {
        arrancar(0, "admin@example.test", "clave-prueba");
        var creado = ArgumentCaptor.forClass(AdministradorDTO.class);
        verify(servicio).create(creado.capture());
        assertEquals("admin@example.test", creado.getValue().getCorreoInstitucional());
        assertEquals("clave-prueba", creado.getValue().getContrasenia());
        assertTrue(creado.getValue().isEstado());
    }
    @Test void noCreaOtroSiYaExisteUnAdministrador() {
        arrancar(1, "admin@example.test", "clave-prueba");
        verify(servicio, never()).create(any());
    }
    @Test void sinCredencialesNoCreaNadaNiImpideElArranque() {
        arrancar(0, "", "");
        arrancar(0, "admin@example.test", " ");
        verify(servicio, never()).create(any());
    }
}
