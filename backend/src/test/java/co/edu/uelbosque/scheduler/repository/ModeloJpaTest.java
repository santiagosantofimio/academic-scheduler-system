package co.edu.uelbosque.scheduler.repository;

import java.sql.Connection;
import java.util.List;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import co.edu.uelbosque.scheduler.entity.*;
import co.edu.uelbosque.scheduler.repository.*;
import static org.junit.jupiter.api.Assertions.*;

/** Construye metadatos y analiza JPQL sin URL JDBC, base de datos ni arranque de Spring Boot. */
class ModeloJpaTest {
    @Test void herenciaRelacionesYConsultasSonValidasSinAccederAJdbc() {
        ConnectionProvider prohibido = new ConnectionProvider() {
            @Override public Connection getConnection() { throw new AssertionError("Esta prueba no debe abrir conexiones JDBC"); }
            @Override public void closeConnection(Connection connection) { throw new AssertionError("No debe existir conexion"); }
            @Override public boolean supportsAggressiveRelease() { return false; }
            @Override public boolean isUnwrappableAs(Class<?> tipo) { return false; }
            @Override public <T> T unwrap(Class<T> tipo) { throw new UnsupportedOperationException("No hay conexion"); }
        };
        var registro = new StandardServiceRegistryBuilder()
                .applySetting("hibernate.boot.allow_jdbc_metadata_access", false)
                .applySetting("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect")
                .applySetting("hibernate.hbm2ddl.auto", "none")
                .addService(ConnectionProvider.class, prohibido).build();
        try {
            var origen = new MetadataSources(registro);
            List.of(Usuario.class,Administrador.class,Docente.class,FormacionAcademica.class,ExperienciaDocente.class,
                    PlanEstudios.class,Asignatura.class,Grupo.class,RequisitoAsignatura.class,SesionHorario.class,ConfiguracionHorarios.class)
                    .forEach(origen::addAnnotatedClass);
            try (var fabrica = origen.buildMetadata().buildSessionFactory(); var em = fabrica.createEntityManager()) {
                assertEquals(11, fabrica.getMetamodel().getEntities().size());
                var repositorios = new JpaRepositoryFactory(em);
                for (Class<?> tipo : List.of(UsuarioRepository.class,AdministradorRepository.class,DocenteRepository.class,
                        FormacionAcademicaRepository.class,ExperienciaDocenteRepository.class,PlanEstudiosRepository.class,
                        AsignaturaServiceRepository.class,GrupoRepository.class,RequisitoAsignaturaRepository.class,
                        SesionHorarioRepository.class,ConfiguracionHorariosRepository.class))
                    assertNotNull(repositorios.getRepository(tipo), tipo.getSimpleName());
            }
        } finally { StandardServiceRegistryBuilder.destroy(registro); }
    }
}
