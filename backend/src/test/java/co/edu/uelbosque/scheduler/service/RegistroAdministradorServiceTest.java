package co.edu.uelbosque.scheduler.service;

import org.junit.jupiter.api.Test;
import co.edu.uelbosque.scheduler.dto.*;
import co.edu.uelbosque.scheduler.exception.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RegistroAdministradorServiceTest {
    private RegistroAdministradorDTO datos(String correo, String codigo) {
        return new RegistroAdministradorDTO("Ana", "Rios", "77", correo, "clave-segura", "Coordinadora", codigo);
    }

    @Test void exigeElCorreoInstitucional() {
        var admins = mock(AdministradorService.class);
        var registro = new RegistroAdministradorService(admins, "unbosque.edu.co", "");
        assertThrows(DatosInvalidosException.class, () -> registro.registrar(datos("ana@gmail.com", null)));
        verify(admins, never()).create(any());
        registro.registrar(datos(" ANA@unbosque.edu.co ", null));
        verify(admins).create(argThat(d -> d.getCorreoInstitucional().equals("ana@unbosque.edu.co") && d.isEstado()));
    }

    @Test void siHayCodigoLoExige() {
        var admins = mock(AdministradorService.class);
        var registro = new RegistroAdministradorService(admins, "unbosque.edu.co", "secreto-2026");
        assertTrue(registro.pideCodigo());
        assertThrows(DatosInvalidosException.class, () -> registro.registrar(datos("ana@unbosque.edu.co", "otro")));
        assertThrows(DatosInvalidosException.class, () -> registro.registrar(datos("ana@unbosque.edu.co", null)));
        registro.registrar(datos("ana@unbosque.edu.co", "secreto-2026"));
        verify(admins, times(1)).create(any());
    }
}
