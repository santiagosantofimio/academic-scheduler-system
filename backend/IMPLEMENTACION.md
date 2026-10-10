# Informe de implementación del backend

Fecha: 9 de octubre de 2026. Repositorio: `C:\Users\Jairo\.codex\academic-scheduler-system`.

**Resultado:** backend implementado directamente en `backend`, compilado y empaquetado con **Java 21**, conservando **Spring Boot 4.1.1**. Se ejecutaron **31 pruebas satisfactorias**, con cero fallos y cero errores. La prueba original de arranque está conservada y condicionada a disponer de una base de datos. No se inició la aplicación completa.

`application.properties` conserva exactamente su contenido original. No se añadieron conexiones, migraciones, precargas ni un frontend. La lectura de Revista-Back se utilizó como referencia de estilo; su código no se modificó.

## A. Estructura final

Se conservó el paquete base `co.edu.uelbosque.scheduler`. Por la petición final, todas las clases quedaron directamente en `configuration`, `controller`, `dto`, `entity`, `exception`, `repository`, `security`, `service` y `util`, como en la imagen de referencia. Hay **91 archivos Java de producción** y **7 archivos Java de pruebas**. El siguiente árbol se obtuvo de los archivos reales. Se eliminaron los marcadores vacíos de las carpetas sustituidas. Se omite `target` por contener resultados regenerables de Maven.

```text
backend/
├── .mvn/wrapper/maven-wrapper.properties  [conservado]
├── .gitattributes                        [conservado]
├── .gitignore                            [conservado]
├── mvnw                                 [conservado]
├── mvnw.cmd                             [conservado]
├── pom.xml                              [sin cambios en la reorganización]
├── IMPLEMENTACION.md
└── src/
    ├── main
    │   ├── java
    │   │   └── co
    │   │       └── edu
    │   │           └── uelbosque
    │   │               └── scheduler
    │   │                   ├── configuration
    │   │                   │   ├── ApplicationConfig.java
    │   │                   │   └── OpenApiConfig.java
    │   │                   ├── controller
    │   │                   │   ├── AdministradorController.java
    │   │                   │   ├── AsignaturaController.java
    │   │                   │   ├── ConfiguracionHorariosController.java
    │   │                   │   ├── DocenteController.java
    │   │                   │   ├── ExperienciaDocenteController.java
    │   │                   │   ├── FormacionAcademicaController.java
    │   │                   │   ├── GrupoController.java
    │   │                   │   ├── PlanEstudiosController.java
    │   │                   │   ├── RequisitoAsignaturaController.java
    │   │                   │   ├── SeguridadController.java
    │   │                   │   └── SesionHorarioController.java
    │   │                   ├── dto
    │   │                   │   ├── AdministradorDTO.java
    │   │                   │   ├── AsignaturaDTO.java
    │   │                   │   ├── ConfiguracionHorariosDTO.java
    │   │                   │   ├── DocenteDTO.java
    │   │                   │   ├── ExperienciaDocenteDTO.java
    │   │                   │   ├── FormacionAcademicaDTO.java
    │   │                   │   ├── GrupoDTO.java
    │   │                   │   ├── PlanEstudiosDTO.java
    │   │                   │   ├── RequisitoAsignaturaDTO.java
    │   │                   │   └── SesionHorarioDTO.java
    │   │                   ├── entity
    │   │                   │   ├── Administrador.java
    │   │                   │   ├── Asignatura.java
    │   │                   │   ├── ConfiguracionHorarios.java
    │   │                   │   ├── Docente.java
    │   │                   │   ├── EstadoGrupo.java
    │   │                   │   ├── EstadoSesion.java
    │   │                   │   ├── ExperienciaDocente.java
    │   │                   │   ├── FormacionAcademica.java
    │   │                   │   ├── Grupo.java
    │   │                   │   ├── NivelFormacion.java
    │   │                   │   ├── PlanEstudios.java
    │   │                   │   ├── RequisitoAsignatura.java
    │   │                   │   ├── SesionHorario.java
    │   │                   │   ├── TipoExperiencia.java
    │   │                   │   ├── TipoRequisito.java
    │   │                   │   ├── TipoVinculacion.java
    │   │                   │   └── Usuario.java
    │   │                   ├── exception
    │   │                   │   ├── AreaConocimientoInvalidaException.java
    │   │                   │   ├── AsignacionAcademicaInvalidaException.java
    │   │                   │   ├── CorreoInstitucionalInvalidoException.java
    │   │                   │   ├── CreditosInvalidosException.java
    │   │                   │   ├── CruceHorarioException.java
    │   │                   │   ├── DatosInvalidosException.java
    │   │                   │   ├── DisponibilidadInvalidaException.java
    │   │                   │   ├── DuplicadoException.java
    │   │                   │   ├── EstadoInvalidoException.java
    │   │                   │   ├── GlobalExceptionHandler.java
    │   │                   │   ├── GrupoValidoException.java
    │   │                   │   ├── HorarioInvalidoException.java
    │   │                   │   ├── IdInvalidoException.java
    │   │                   │   ├── NivelFormacionInvalidoException.java
    │   │                   │   ├── NombreInvalidoException.java
    │   │                   │   ├── NumeroDocumentoInvalidoException.java
    │   │                   │   ├── RecursoNoEncontradoException.java
    │   │                   │   ├── SemestreInvalidoException.java
    │   │                   │   ├── SesionesSemanalesInvalidasException.java
    │   │                   │   └── TipoVinculacionInvalidoException.java
    │   │                   ├── repository
    │   │                   │   ├── AdministradorRepository.java
    │   │                   │   ├── AsignaturaServiceRepository.java
    │   │                   │   ├── ConfiguracionHorariosRepository.java
    │   │                   │   ├── DocenteRepository.java
    │   │                   │   ├── ExperienciaDocenteRepository.java
    │   │                   │   ├── FormacionAcademicaRepository.java
    │   │                   │   ├── GrupoRepository.java
    │   │                   │   ├── PlanEstudiosRepository.java
    │   │                   │   ├── RequisitoAsignaturaRepository.java
    │   │                   │   ├── SesionHorarioRepository.java
    │   │                   │   └── UsuarioRepository.java
    │   │                   ├── security
    │   │                   │   ├── SecurityConfig.java
    │   │                   │   └── UserDetailsServiceImpl.java
    │   │                   ├── service
    │   │                   │   ├── AbstractCrudService.java
    │   │                   │   ├── AdministradorService.java
    │   │                   │   ├── AsignaturaService.java
    │   │                   │   ├── ConfiguracionHorariosService.java
    │   │                   │   ├── CRUDOperation.java
    │   │                   │   ├── DocenteService.java
    │   │                   │   ├── ExperienciaDocenteService.java
    │   │                   │   ├── FormacionAcademicaService.java
    │   │                   │   ├── GrupoService.java
    │   │                   │   ├── PlanEstudiosService.java
    │   │                   │   ├── RequisitoAsignaturaService.java
    │   │                   │   └── SesionHorarioService.java
    │   │                   ├── util
    │   │                   │   ├── DatosUsuario.java
    │   │                   │   ├── DtoMapper.java
    │   │                   │   ├── IdentidadJpa.java
    │   │                   │   ├── LanzadorDeExcepcion.java
    │   │                   │   └── ReglasHorario.java
    │   │                   └── AcademicSchedulerApplication.java
    │   └── resources
    │       └── application.properties
    └── test
        ├── java
        │   └── co
        │       └── edu
        │           └── uelbosque
        │               └── scheduler
        │                   ├── controller
        │                   │   └── ContratoHttpTest.java
        │                   ├── repository
        │                   │   └── ModeloJpaTest.java
        │                   ├── security
        │                   │   └── SeguridadTest.java
        │                   ├── service
        │                   │   ├── CrudYUsuariosTest.java
        │                   │   ├── RequisitoAsignaturaServiceTest.java
        │                   │   └── SesionHorarioServiceTest.java
        │                   └── AcademicSchedulerApplicationTests.java
        └── resources
            └── mockito-extensions
                ├── org.mockito.plugins.MemberAccessor
                └── org.mockito.plugins.MockMaker
```

Además, Maven produjo `backend/target/academic-scheduler-0.0.1-SNAPSHOT.jar` y sus reportes en `backend/target/surefire-reports`. La clase principal original permanece en su paquete.

## B. Clases implementadas

| Capa | Implementación |
|---|---|
| `entity` | Usuario, Administrador, Docente, FormacionAcademica, ExperienciaDocente, PlanEstudios, Asignatura, Grupo, RequisitoAsignatura, SesionHorario, ConfiguracionHorarios: 11 entidades. |
| `entity` (enumeraciones) | NivelFormacion, TipoVinculacion, TipoExperiencia, EstadoGrupo, EstadoSesion y TipoRequisito. |
| `dto` | Los 10 DTO correspondientes a las entidades concretas del UML, con sus constructores, accesores y métodos de objeto. |
| `repository` | Los 10 repositorios del UML y UsuarioRepository como soporte técnico de autenticación y unicidad entre subclases. |
| `service` | 10 servicios concretos, CRUDOperation y AbstractCrudService. |
| `controller` | 10 controladores del dominio y SeguridadController para obtener el token CSRF. |
| Soporte | DtoMapper, DatosUsuario, LanzadorDeExcepcion, ReglasHorario, IdentidadJpa, 19 excepciones, GlobalExceptionHandler, SecurityConfig, UserDetailsServiceImpl, ApplicationConfig y OpenApiConfig. |

Se emplea inyección por constructor, conversión explícita Entity–DTO y anotaciones Jakarta compatibles con Spring Boot 4. Los repositorios trabajan con persistencia JPA real; los dobles de prueba están únicamente en `src/test`.

## C. Herencia

`Usuario` es una entidad abstracta. `Administrador` y `Docente` la extienden y utilizan `InheritanceType.JOINED`: los atributos comunes pertenecen a la tabla de Usuario y los específicos a las tablas de las subclases.

En el PDF, `AdministradorDTO` y `DocenteDTO` también heredan directamente de `Usuario`. Se conservó esa relación aunque acopla los DTO a la entidad base. Los DTO no llevan `@Entity` ni se registran como entidades JPA; no se inventó una clase UsuarioDTO ausente del diagrama analizado.

`DocenteDTO.idDocente` es un alias de `Usuario.id`: ambos accesores operan sobre un único identificador. El JSON mantiene ambos nombres por compatibilidad. `HashCode()` delega en `hashCode()` y conserva la firma del UML. La igualdad de entidades utiliza clase persistente e identificador, con hash estable y sin incluir asociaciones, contraseñas o atributos mutables. Los DTO ordinarios comparan sus datos.

## D. Relaciones JPA

El UML no dejaba inequívocas todas las cardinalidades. Se implementaron las asociaciones mínimas necesarias para sus operaciones:

| Entidad propietaria | Relación | Obligatoriedad |
|---|---|---|
| FormacionAcademica | Muchas formaciones → un Docente | Obligatoria |
| ExperienciaDocente | Muchas experiencias → un Docente | Obligatoria |
| Asignatura | Muchas asignaturas → un PlanEstudios | Obligatoria |
| Grupo | Muchos grupos → una Asignatura | Obligatoria |
| RequisitoAsignatura | Muchos requisitos → una Asignatura | Obligatoria |
| SesionHorario | Muchas sesiones → un Grupo | Obligatoria |
| Grupo | Muchos grupos → un Docente | Opcional al registrar; obligatoria para programar |

Todas son asociaciones unidireccionales `@ManyToOne(fetch = LAZY)`, con columna de referencia y sin borrado en cascada. Los DTO transportan los IDs de las relaciones, evitando grafos recursivos y exposición accidental de entidades asociadas. Los identificadores usan la generación JPA `AUTO`; las enumeraciones se almacenan con `EnumType.STRING`.

Hay restricciones de unicidad para documento y correo de Usuario, códigos de programa y asignatura, y número de grupo dentro de su asignatura. La configuración institucional tiene una clave técnica constante y única para impedir altas concurrentes duplicadas. Estas son declaraciones del modelo; el esquema físico y sus migraciones quedan para la fase de base de datos.

## E. Services

Los diez servicios implementan creación, listado, consulta por ID, actualización, eliminación, conteo, existencia y las búsquedas que les corresponden. `CRUDOperation` conserva sus seis firmas originales: las escrituras devuelven `1` cuando afectan correctamente al registro y lanzan una excepción ante errores; no se usan valores de éxito ficticios.

- **Administrador y Docente:** validación de identidad, correo y vinculación; unicidad entre ambas subclases; hash de contraseña; preservación del hash cuando se actualiza con contraseña nula. Las horas máximas proceden del dato del docente, sin deducir límites de su tipo de vinculación. No se permite reducir su capacidad por debajo de la carga ya programada ni desactivarlo con sesiones activas.
- **Formación y experiencia:** registro por docente, fechas y año válidos, nivel/tipo/área obligatorios. La experiencia puede seguir vigente con fecha final nula. Modificaciones y eliminaciones revalidan los requisitos de sus grupos activos; una incompatibilidad provoca la reversión transaccional.
- **Planes y asignaturas:** códigos únicos, semestres y créditos positivos, semestre dentro del plan y coherencia de estados. Se rechazan cambios del plan o de la programación que invaliden sesiones existentes.
- **Grupos:** número único dentro de la asignatura, asociación explícita a docente y validación de requisitos. Para cambiar docente/asignatura o desactivar un grupo programado deben cancelarse antes sus sesiones.
- **Requisitos:** validación por nivel y área de formación o por tipo, área y duración acumulada de experiencia. Todos los requisitos de la asignatura deben cumplirse. Los periodos laborales superpuestos no se cuentan dos veces.
- **Configuración:** una configuración institucional, franjas válidas, descanso opcional con sus dos extremos, duración y máximo positivos. Se comprueba que los cambios conserven la validez de sesiones existentes.
- **Sesiones:** registro manual, actualización/cancelación, eliminación, consultas, validación de cruces y generación completa por grupo.

Las escrituras usan transacciones y adquieren primero un bloqueo pesimista sobre la configuración existente. La programación bloquea también el docente antes de comprobar su agenda. Esto establece un orden común de bloqueo; su comportamiento efectivo con transacciones concurrentes debe verificarse al conectar la base de datos.

## F. Controllers y endpoints

Hay **116 combinaciones de método y ruta** declaradas en los controladores. Cada ruta base de la tabla implementa:

| Método y sufijo | Operación | Éxito |
|---|---|---|
| `POST` en la ruta base | Crear mediante JSON DTO | 201 y mensaje |
| `GET` en la ruta base | Listar | 200 y lista DTO |
| `GET /{id}` | Consultar uno | 200 y DTO |
| `PUT /{id}` | Actualizar mediante JSON DTO | 200 y mensaje |
| `DELETE /{id}` | Eliminar | 204, sin cuerpo |
| `GET /cantidad` | Contar | 200 y número |
| `GET /{id}/existe` | Comprobar existencia | 200 y booleano |

Los filtros se invocan como `GET <ruta-base>/por-<filtro>?valor=...`. Por ejemplo, `/api/docentes/por-tipo-vinculacion?valor=TIEMPO_COMPLETO`. No son búsquedas parciales: se comparan los campos mediante las consultas correspondientes. Una colección sin coincidencias produce una lista vacía; consultar por ID un recurso inexistente produce 404.

| Controller | Ruta base | Filtros |
|---|---|---|
| AdministradorController | `/api/administradores` | `nombre`, `numero-documento`, `correo-institucional`, `cargo` |
| DocenteController | `/api/docentes` | `nombre`, `numero-documento`, `correo-institucional`, `tipo-vinculacion`, `especialidad`, `escalafon` |
| FormacionAcademicaController | `/api/formaciones` | `docente`, `nivel-formacion`, `institucion`, `area-conocimiento` |
| ExperienciaDocenteController | `/api/experiencias` | `docente`, `tipo-experiencia`, `institucion`, `area-conocimiento` |
| PlanEstudiosController | `/api/planes-estudios` | `nombre-programa`, `codigo-programa`, `version-plan`, `estado` |
| AsignaturaController | `/api/asignaturas` | `codigo`, `nombre`, `semestre`, `area-conocimiento`, `estado`, `plan-estudios` |
| GrupoController | `/api/grupos` | `numero-grupo`, `estado`, `asignatura` |
| RequisitoAsignaturaController | `/api/requisitos-asignatura` | `asignatura`, `tipo-requisito`, `area-conocimiento` |
| SesionHorarioController | `/api/sesiones` | `grupo`, `dia-semana`, `docente` |
| ConfiguracionHorariosController | `/api/configuracion-horarios` | — |

Rutas adicionales, extraídas de las anotaciones:

| Método | Ruta |
|---|---|
| POST | `/api/asignaturas/planEstudios/{id}` |
| GET | `/api/configuracion-horarios/actual` |
| POST | `/api/experiencias/docente/{id}` |
| POST | `/api/formaciones/docente/{id}` |
| POST | `/api/grupos/asignatura/{id}` |
| POST | `/api/requisitos-asignatura/asignatura/{id}` |
| GET | `/api/csrf` |
| POST | `/api/sesiones/grupo/{id}` |
| POST | `/api/sesiones/grupo/{id}/generar` |

Los registros por padre reciben el mismo DTO y fijan su relación desde la ruta. Si el cuerpo contiene otro ID de padre, se rechaza la solicitud. `PUT` actualiza los atributos completos; un ID de padre omitido conserva la relación actual. En Grupo, `docenteId` permite asignar o reemplazar al docente; un valor nulo conserva la asignación existente en una actualización. No hay operación específica para retirar solamente esa asignación.

Fechas: `AAAA-MM-DD`. Horas: `HH:mm:ss` o representación ISO de LocalTime. Días: valores de `DayOfWeek`, como `MONDAY` o `SATURDAY`. Enumeraciones: nombres exactos de las constantes. No se aceptan IDs nuevos suministrados por el cliente al crear recursos. Las sobrecargas escalares del UML se conservan como métodos Java y delegan en el DTO; no son rutas HTTP adicionales.

Swagger UI estará disponible en `/swagger-ui/index.html` y OpenAPI en `/v3/api-docs` una vez que la aplicación pueda arrancar con su base de datos. Los esquemas se derivan de los DTO y se documentaron operaciones y respuestas; la autenticación es HTTP Basic.

## G. Excepciones

Clases implementadas: `AreaConocimientoInvalidaException`, `AsignacionAcademicaInvalidaException`, `CorreoInstitucionalInvalidoException`, `CreditosInvalidosException`, `CruceHorarioException`, `DatosInvalidosException`, `DisponibilidadInvalidaException`, `DuplicadoException`, `EstadoInvalidoException`, `GrupoValidoException`, `HorarioInvalidoException`, `IdInvalidoException`, `NivelFormacionInvalidoException`, `NombreInvalidoException`, `NumeroDocumentoInvalidoException`, `RecursoNoEncontradoException`, `SemestreInvalidoException`, `SesionesSemanalesInvalidasException`, `TipoVinculacionInvalidoException`.

`GlobalExceptionHandler` responde con `ProblemDetail`: 400 para datos o formatos inválidos, 404 para recursos inexistentes, y 409 para duplicados, cruces, falta de capacidad, asignaciones incompatibles, integridad referencial o conflictos de concurrencia. Los filtros de seguridad producen 401 o 403. La escritura de una operación fallida no devuelve 200. Los errores de integridad no publican SQL ni mensajes internos del controlador de base de datos.

No se añadió un `catch` genérico que convierta errores inesperados en éxito. Los errores imprevistos siguen el manejo estándar de Spring Boot y requieren diagnóstico en el servidor.

## H. Seguridad

Se conservó y completó HTTP Basic, presente en la configuración inicial. `UserDetailsServiceImpl` consulta Usuario por correo institucional normalizado, obtiene la contraseña cifrada y rechaza cuentas inactivas. Administrador recibe `ROLE_ADMINISTRADOR`; Docente recibe `ROLE_DOCENTE`.

Las rutas de gestión `/api/**` requieren administrador. `/api/csrf` requiere cualquier usuario autenticado. Swagger y los endpoints de salud/información conservan acceso público; las demás rutas se deniegan. Este permiso administrativo es una decisión conservadora ante la ausencia de una matriz de permisos del docente; no se presenta como una matriz académica aprobada.

Las contraseñas se guardan mediante `DelegatingPasswordEncoder` con BCrypt. Se valida su límite técnico de 72 bytes UTF-8 para evitar truncamiento. El mapper no devuelve el hash y `contrasenia` es de escritura exclusiva en JSON. No se copiaron JWT, AES, secretos ni cuentas precargadas de Revista.

CSRF permanece habilitado porque el navegador puede enviar HTTP Basic automáticamente. Para escribir: autenticar `GET /api/csrf`, conservar la cookie de sesión y enviar el token recibido en la cabecera indicada por `headerName`, junto con la autenticación. Esta secuencia se verificó con Spring Security y MockMvc. El despliegue deberá proporcionar HTTPS y definir el aprovisionamiento seguro del primer administrador; no hay un endpoint público de autorregistro administrativo.

## I. Generación de horarios

`POST /api/sesiones/grupo/{id}/generar` genera las sesiones pendientes de **un grupo**:

1. Obtiene la configuración institucional y comprueba que plan, asignatura, grupo y docente estén activos.
2. Valida formación y experiencia del docente explícitamente asignado.
3. Consulta todas sus sesiones programadas, conserva las del grupo y calcula las faltantes según la asignatura.
4. Comprueba el máximo institucional por grupo y la carga semanal del docente, medida en horas de 60 minutos.
5. Calcula intervalos libres, descontando descansos y sesiones de otros grupos, incluso si comienzan en minutos no alineados con la hora institucional.
6. Busca una combinación completa con retroceso y memorización cuando evitar días consecutivos exige reconsiderar una elección anterior.
7. Persiste conjuntamente las sesiones nuevas sólo si encontró la solución completa. Si no cabe, responde con conflicto sin guardar una solución parcial.

Repetir la operación sobre un horario completo conserva las sesiones y sus IDs. Las sesiones canceladas no ocupan agenda ni consumen capacidad. El registro manual y la actualización comprueban también duración, franjas, descansos, cruces, carga y cantidad. Los intervalos son `[inicio, fin)`: finalizar a las 09:00 y comenzar otra sesión a las 09:00 no es un cruce.

Decisiones adoptadas para cerrar ambigüedades del UML: semana de lunes a viernes y franja separada del sábado; domingo sin franja definida; máximo de sesiones y restricción de días consecutivos por grupo; varias sesiones en un mismo día permitidas si cumplen el resto; selección determinista en orden cronológico. La experiencia acumulada usa años de 365 días y el nivel mínimo usa PREGRADO < ESPECIALIZACION < MAESTRIA < DOCTORADO. Estas son convenciones de implementación, no reglas institucionales encontradas en el PDF.

**Límites:** no existe en el UML suministrado un calendario de disponibilidad personal. La disponibilidad implementada significa compatibilidad con franjas institucionales, sesiones existentes y capacidad semanal. El sistema no selecciona automáticamente al docente, no optimiza globalmente todos los grupos y no incluye aulas, periodos académicos, festivos ni preferencias personales no modelados. El grupo debe llevar `docenteId` antes de generar.

## J. Dependencias y Eclipse

Cambios en `pom.xml`: `java.version` se fijó en **21** por la última instrucción expresa; se mantuvo Spring Boot **4.1.1**; se agregó solamente `springdoc-openapi-starter-webmvc-ui:3.1.1` para la documentación. Los starters JPA, MVC, Security, Validation, Actuator, Flyway, PostgreSQL y pruebas ya estaban declarados y se conservaron. No se añadió otra base de datos ni ModelMapper, Lombok, JWT o un sistema de construcción distinto.

Java 21 está dentro del intervalo soportado por Spring Boot 4.1.1, y springdoc 3.x corresponde a Spring Boot 4.x. Fuentes oficiales consultadas: [requisitos de Spring Boot](https://docs.spring.io/spring-boot/system-requirements.html) y [compatibilidad de springdoc](https://springdoc.org/). Además de esa revisión documental, se compiló y ejecutó la suite con JDK 21 real.

Para Eclipse IDE for Enterprise Java and Web Developers 2026-03:

1. Registrar un **JDK 21** en `Window > Preferences > Java > Installed JREs` y asociarlo a `JavaSE-21`.
2. Importar `C:\Users\Jairo\.codex\academic-scheduler-system\backend` mediante `File > Import > Maven > Existing Maven Projects`.
3. Ejecutar `Maven > Update Project` y comprobar que `JRE System Library` use JavaSE-21.
4. Ejecutar Maven `test` o `package` con JDK 21. El proyecto no usa funcionalidades preview ni procesadores de anotaciones adicionales.

Si ya estaba importado el proyecto, ejecutar primero Refresh (F5), Maven > Update Project y Project > Clean. Si se transporta el código a otra copia del repositorio, sustituir el contenido anterior de `src/main/java` y `src/test/java` por estas versiones, después de respaldar cambios propios: mezclar carpetas dejaría las clases antiguas duplicadas. No se modificaron preferencias globales de Eclipse ni se ejecutó una comprobación gráfica del IDE. La compatibilidad comprobada corresponde al proyecto Maven estándar, sus fuentes y bytecode Java 21.

En una terminal PowerShell con Maven instalado puede verificarse sin arrancar la aplicación:

```powershell
$env:JAVA_HOME = 'C:\Program Files\Eclipse Adoptium\jdk-21.0.11.10-hotspot'
Set-Location -LiteralPath 'C:\Users\Jairo\.codex\academic-scheduler-system\backend'
mvn -B -ntp test
mvn -B -ntp '-DskipTests' package
```

También está conservado `mvnw.cmd`, configurado para Maven 3.9.16. Las verificaciones de esta entrega se ejecutaron con esa misma versión de Maven descargada en la carpeta de trabajo; no se cambió la instalación global.

## K. Verificación real

Entorno ejecutado: Eclipse Temurin **21.0.11**, Maven **3.9.16**, compilación `release 21`.

| Comprobación | Resultado |
|---|---|
| Resolución de dependencias Maven | BUILD SUCCESS |
| `mvn -B -ntp clean verify` después de reorganizar | BUILD SUCCESS; compilación desde cero, 32 pruebas detectadas, 31 aprobadas, 1 omitida, 0 fallos y 0 errores; JAR Spring Boot generado |
| Paquetes dentro del JAR | Verificado: no contiene clases en las carpetas sustituidas |
| Bytecode propio empaquetado | Todas las clases con versión mayor 65: Java 21 |
| Dependencias del JAR | 100 JAR y 35,798 clases aplicables a Java 21 inspeccionadas; ninguna exige bytecode posterior a Java 21 |
| Configuración original | SHA-256 de application.properties coincide con el original |

Se excluyeron de la inspección de compatibilidad las variantes multi-release destinadas exclusivamente a JVM posteriores a 21. Esta inspección de bytecode y las pruebas no sustituyen una futura integración con la base de datos ni garantizan cada combinación de ejecución posible.

| Suite | Detectadas | Aprobadas | Omitidas |
|---|---:|---:|---:|
| AcademicSchedulerApplicationTests | 1 | 0 | 1 |
| ContratoHttpTest | 4 | 4 | 0 |
| ModeloJpaTest | 1 | 1 | 0 |
| SeguridadTest | 3 | 3 | 0 |
| CrudYUsuariosTest | 8 | 8 | 0 |
| RequisitoAsignaturaServiceTest | 3 | 3 | 0 |
| SesionHorarioServiceTest | 12 | 12 | 0 |

Cobertura funcional: generación completa e idempotente, conservación de sesiones, retroceso, franjas no alineadas, descansos, carga de otros grupos, cancelaciones, falta de espacio sin guardado parcial, requisitos, igualdad de entidades, contrato HTTP, unicidad entre tipos de usuario, contraseña segura y no serializada, herencia DTO, rechazo de recursos inexistentes, revalidación de credenciales, HTTP Basic, cuentas inactivas, permisos y ciclo CSRF con cookie.

`ModeloJpaTest` construye metadatos Hibernate y los once repositorios de Spring Data para validar entidades, relaciones y consultas. No carga la aplicación Spring Boot y utiliza un proveedor que falla si se intenta abrir una conexión JDBC. El dialecto se establece sólo en esa prueba para analizar metadatos sin consultar un servidor; no hay URL JDBC ni creación de esquema. El rollback físico, los bloqueos efectivos y las restricciones SQL requieren pruebas futuras con base de datos.

La prueba original `AcademicSchedulerApplicationTests.contextLoads` permanece anotada con `@SpringBootTest`; sólo se habilita expresamente mediante `-Dscheduler.integration=true` cuando exista una configuración de base de datos adecuada. No se eliminó ni se convirtió en una falsa prueba de arranque.

Mockito se configuró sólo en `src/test/resources` con `mock-maker-subclass` y `member-accessor-reflection`: estas pruebas no necesitan instrumentación de clases finales. Así se evita la carga dinámica del agente que bloqueaba la ejecución en este entorno Windows. Los reportes finales corresponden a la ejecución satisfactoria posterior a ese ajuste.

Hash preservado de `application.properties`:

```text
D7A2FB379B246A163BF6E3BCC23FE1932939509EDF09ED150344191CB473BB13
```

No se inició un servidor HTTP real, no se conectó a PostgreSQL, no se aplicaron migraciones y no se validó el funcionamiento integral contra una base de datos. El análisis JPA y las pruebas HTTP utilizaron exclusivamente el entorno de pruebas descrito.

## L. Pendientes

- Definir y configurar posteriormente la base de datos, sus credenciales externas y el esquema/migraciones correspondientes a estas entidades. El driver y Flyway existentes aún no constituyen una conexión configurada por esta entrega.
- Preparar el alta segura del primer administrador y el despliegue HTTPS.
- Ejecutar la prueba de arranque conservada y pruebas de integración de persistencia, restricciones, carga diferida, rollback y concurrencia.
- Acordar los permisos concretos del docente y, si se amplía el modelo, representar su disponibilidad personal, la retirada de asignaciones y los periodos académicos.
- Revisar con la institución las convenciones de estados, requisitos y cómputo de experiencia aquí documentadas si sus reglas difieren.

El README de la raíz del repositorio describe el scaffold anterior y puede mencionar Java 26 u otro alcance. No se modificó ese archivo fuera de `backend`; para esta implementación, `pom.xml` y este informe establecen Java 21 y el alcance basado en el UML.

## M. Cambios respecto al UML

1. **Paquetes:** por petición final, se sustituyeron las carpetas intermedias por los nueve paquetes directos de la imagen de referencia. Las enumeraciones comparten `entity`; DtoMapper, IdentidadJpa, DatosUsuario, LanzadorDeExcepcion y ReglasHorario están en `util`; GlobalExceptionHandler está en `exception`; SecurityConfig y UserDetailsServiceImpl están en `security`. La clase principal permanece en el paquete base. Se actualizaron declaraciones, imports y paquetes de pruebas sin alterar la lógica de negocio.
2. **Nombres peculiares preservados:** `idAsginatura`, `AsignaturaServiceRepository`, `GrupoValidoException` y `DocenteDTO.HashCode()`. Las referencias inconsistentes a otro nombre de repositorio se alinearon con `AsignaturaServiceRepository`; los constructores de excepciones llevan necesariamente el nombre de su propia clase. La excepción SemestreInvalido se declara una sola vez.
3. **Firmas ambiguas:** el constructor de Usuario con un `int` adicional conserva su firma y delega en el constructor común, sin inventar un atributo para ese parámetro sin definición. La firma de dos cadenas de AdministradorDTO se interpreta como nombre y cargo. Los constructores auxiliares necesarios para delegar a la superclase se incorporaron explícitamente. Los métodos comunes se heredan donde corresponde.
4. **Identificador de DocenteDTO:** se sustituyó la duplicación del ID por accesores alias de la propiedad heredada. Los DTO de Administrador y Docente conservan la herencia directa de Usuario mostrada en el PDF y no se registran como entidades.
5. **Enumeraciones originales:** NivelFormacion = PREGRADO, ESPECIALIZACION, MAESTRIA, DOCTORADO; TipoVinculacion = TIEMPO_COMPLETO, TRES_CUARTOS_TIEMPO, MEDIO_TIEMPO, CUARTO_TIEMPO; TipoExperiencia = LABORAL, DOCENTE, INVESTIGATIVA.
6. **Enumeraciones incompletas resueltas:** EstadoGrupo = ACTIVO, INACTIVO; EstadoSesion = PROGRAMADA, CANCELADA; TipoRequisito = FORMACION_ACADEMICA, EXPERIENCIA_LABORAL, EXPERIENCIA_DOCENTE, EXPERIENCIA_INVESTIGATIVA. Estos valores se eligieron para hacer ejecutables las operaciones tras la autorización de resolver las ambigüedades con base en el UML.
7. **Relaciones e IDs de asociación:** se concretaron las cardinalidades de la sección D y se agregaron sus IDs en los DTO para registrar asociaciones sin transportar entidades completas. No se añadieron colecciones bidireccionales ni cascadas destructivas.
8. **Asignación sin entidad ficticia:** la referencia a AsignacionAcademicaRepository carecía de una entidad completa en el material. Se resolvió mediante `Grupo.docente`, consultas por docente y `GrupoDTO.docenteId`, con un docente por grupo. No se creó un repositorio vacío ni una entidad AsignacionAcademica inventada.
9. **Consultas incoherentes:** las consultas copiadas de sesiones en RequisitoAsignaturaRepository se reemplazaron por consultas del propio requisito. Se añadieron consultas de pertenencia, agenda y carga necesarias para los métodos de servicio; `@Query` enlaza explícitamente los nombres reales de los identificadores. Se conservaron los contratos `Optional<List<...>>` de los repositorios originales.
10. **Código técnico necesario:** AbstractCrudService, DtoMapper, DatosUsuario, IdentidadJpa, ReglasHorario, UsuarioRepository, dos excepciones de soporte, manejo de errores, reloj inyectable, seguridad y OpenAPI. Ninguno introduce un concepto académico nuevo.
11. **Persistencia:** JOINED, generación AUTO, enums como texto, unicidad, referencias LAZY y clave institucional constante. Se declaran en Java y no mediante una configuración o migración ejecutada.
12. **Contrato HTTP:** se añadieron rutas coherentes, DTO JSON, consulta por ID, conteo/existencia, manejo centralizado y el endpoint técnico CSRF. Las firmas escalares del UML se mantienen como sobrecargas Java.
13. **Decisiones funcionales documentadas:** convenciones de las secciones H–I para roles, estados, requisitos, franjas, unidades, días consecutivos y solución cronológica. No se atribuyen al UML reglas que no figuraban allí.
14. **Referencias de Revista adaptadas:** no se trasladaron sus noticias, comentarios, horóscopos, cifrado AES, JWT ni precarga de datos. No se añadió ServletInitializer porque el proyecto actual empaqueta un JAR con servidor embebido, no un WAR para un contenedor externo.
