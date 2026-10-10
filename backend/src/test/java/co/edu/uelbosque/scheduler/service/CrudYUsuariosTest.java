package co.edu.uelbosque.scheduler.service;

import java.time.*;
import java.util.*;
import org.junit.jupiter.api.Test;
import co.edu.uelbosque.scheduler.dto.*;
import co.edu.uelbosque.scheduler.exception.*;
import co.edu.uelbosque.scheduler.util.DtoMapper;
import co.edu.uelbosque.scheduler.util.DatosUsuario;
import co.edu.uelbosque.scheduler.entity.*;
import co.edu.uelbosque.scheduler.security.SecurityConfig;
import co.edu.uelbosque.scheduler.repository.*;
import tools.jackson.databind.json.JsonMapper;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CrudYUsuariosTest {
    @Test void inexistenteNoSeActualizaNiSeCreaAccidentalmente() {
        AdministradorRepository rep=mock(AdministradorRepository.class);
        var servicio=new AdministradorService(rep,mock(DatosUsuario.class),mock(ConfiguracionHorariosRepository.class));
        assertThrows(RecursoNoEncontradoException.class, () -> servicio.updateById(7L,new AdministradorDTO()));
        assertThrows(RecursoNoEncontradoException.class, () -> servicio.deleteById(7L));
        assertThrows(IdInvalidoException.class, () -> servicio.getById(-1L));
        verify(rep,never()).saveAndFlush(any()); verify(rep,never()).delete(any());
    }
    private AdministradorDTO entrada() {
        var dto=new AdministradorDTO(); dto.setNombre("Ana"); dto.setApellido("Lopez");
        dto.setCorreoInstitucional(" ANA@UNIVERSIDAD.EDU "); dto.setNumeroDocumento(" 123 ");
        dto.setContrasenia("Una clave de prueba"); dto.setEstado(true); return dto;
    }
    @Test void passwordSeCifraYNoApareceEnDtoNiJsonNiToString() {
        var encoder=new SecurityConfig().passwordEncoder();
        var datos=new DatosUsuario(mock(UsuarioRepository.class),encoder);
        var usuario=new Administrador(); datos.aplicar(entrada(),usuario,true); usuario.setId(1L);
        assertTrue(encoder.matches("Una clave de prueba",usuario.getContrasenia()));
        assertEquals("ana@universidad.edu",usuario.getCorreoInstitucional());
        assertEquals("123",usuario.getNumeroDocumento());
        assertNull(DtoMapper.toDto(usuario).getContrasenia());
        String json=JsonMapper.builder().build().writeValueAsString(usuario);
        assertFalse(json.contains("contrasenia")); assertFalse(json.contains(usuario.getContrasenia()));
        assertFalse(usuario.toString().contains(usuario.getContrasenia()));
    }
    @Test void nullAlActualizarPreservaHashYPasswordExcesivoNoSeTrunca() {
        var datos=new DatosUsuario(mock(UsuarioRepository.class),new SecurityConfig().passwordEncoder());
        var entidad=new Administrador(); entidad.setContrasenia("hash-existente");
        var dto=entrada(); dto.setContrasenia(null); datos.aplicar(dto,entidad,false);
        assertEquals("hash-existente",entidad.getContrasenia());
        dto.setContrasenia("á".repeat(37));
        assertThrows(DatosInvalidosException.class, () -> datos.aplicar(dto,entidad,false));
    }
    @Test void documentoDuplicadoEntreSubclasesSeRechaza() {
        var rep=mock(UsuarioRepository.class); var existente=new Docente(); existente.setId(12L);
        when(rep.findByNumeroDocumento("123")).thenReturn(Optional.of(existente));
        var datos=new DatosUsuario(rep,new SecurityConfig().passwordEncoder());
        assertThrows(DuplicadoException.class, () -> datos.aplicar(entrada(),new Administrador(),true));
    }
    @Test void dtoHeredaUsuarioYAliasDocenteMantieneUnSoloIdentificador() {
        DocenteDTO dto=new DocenteDTO(); dto.setIdDocente(17L);
        assertEquals(17L,dto.getId()); dto.setId(18L); assertEquals(18L,dto.getIdDocente());
        assertEquals(dto.hashCode(),dto.HashCode());
        assertTrue(dto instanceof Usuario); assertTrue(new AdministradorDTO() instanceof Usuario);
        dto.setContrasenia("secreto");
        assertFalse(JsonMapper.builder().build().writeValueAsString(dto).contains("secreto"));
    }
    @Test void identidadJpaNoDependeDeCamposMutables() {
        Docente docente=new Docente(); Set<Docente> docentes=new HashSet<>(); docentes.add(docente);
        docente.setId(7L); docente.setNombre("Cambio");
        assertTrue(docentes.contains(docente)); Docente otro=new Docente(); otro.setId(7L);
        assertEquals(docente,otro); assertNotEquals(new Docente(),new Docente());
        Administrador admin=new Administrador(); admin.setId(7L); assertNotEquals(docente,admin);
    }
    @Test void actualizacionFormacionConservaPadreYRevalidaAsignaciones() {
        var rep=mock(FormacionAcademicaRepository.class); var docentes=mock(DocenteRepository.class);
        var requisitos=mock(RequisitoAsignaturaService.class); var docente=new Docente(); docente.setId(2L);
        var entidad=new FormacionAcademica(); entidad.setIdFormacion(1L); entidad.setDocente(docente);
        when(rep.findById(1L)).thenReturn(Optional.of(entidad)); when(docentes.findById(2L)).thenReturn(Optional.of(docente));
        var servicio=new FormacionAcademicaService(rep,docentes,Clock.systemUTC(),mock(ConfiguracionHorariosRepository.class),requisitos);
        var dto=new FormacionAcademicaDTO("Ingenieria",NivelFormacion.PREGRADO,"Universidad","Sistemas",2020);
        assertEquals(1,servicio.updateById(1L,dto)); assertSame(docente,entidad.getDocente());
        verify(requisitos).validarAsignacionesDocente(2L);
        assertThrows(IdInvalidoException.class, () -> servicio.registrarFormacionDocente(null,dto));
    }
    @Test void eliminarCredencialNoPermiteDejarAsignacionesInvalidas() {
        var rep=mock(FormacionAcademicaRepository.class); var requisitos=mock(RequisitoAsignaturaService.class);
        var entidad=new FormacionAcademica(); var docente=new Docente(); docente.setId(2L); entidad.setDocente(docente);
        when(rep.findById(1L)).thenReturn(Optional.of(entidad));
        doThrow(new AsignacionAcademicaInvalidaException("Requisito incumplido")).when(requisitos).validarAsignacionesDocente(2L);
        var servicio=new FormacionAcademicaService(rep,mock(DocenteRepository.class),Clock.systemUTC(),mock(ConfiguracionHorariosRepository.class),requisitos);
        assertThrows(AsignacionAcademicaInvalidaException.class, () -> servicio.deleteById(1L));
        // El rollback fisico se verificara en integracion con BD; aqui se verifica la excepcion que lo activa.
    }
}
