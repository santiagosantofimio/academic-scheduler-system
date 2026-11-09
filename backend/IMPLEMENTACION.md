# Implementación del backend

API REST en Java 21 y Spring Boot 4.1.1 para gestionar la información académica y programar sesiones de clase.

El modelo parte del diagrama de clases UML aprobado (`docs/diagrams/diagrama-clases-uml.pdf`). Aulas y espacios físicos están fuera del alcance.

## Estructura

Paquete base `co.edu.uelbosque.scheduler`:

| Paquete | Contenido |
|---|---|
| `controller` | Rutas REST de cada recurso. |
| `service` | Reglas de negocio, CRUD común en `AbstractCrudService` y exportación. |
| `repository` | Repositorios Spring Data JPA. |
| `entity` | Entidades JPA y enumeraciones. |
| `dto` | Objetos de transporte. Llevan los IDs de sus relaciones, no las entidades. |
| `exception` | Excepciones de dominio y `GlobalExceptionHandler`. |
| `security` | `SecurityConfig` y `UserDetailsServiceImpl`. |
| `configuration` | Reloj inyectable, OpenAPI y creación del administrador inicial. |
| `util` | `DtoMapper`, `ReglasHorario`, `LanzadorDeExcepcion`, `DatosUsuario`, `IdentidadJpa`. |

## Modelo

`Usuario` es abstracta y usa herencia `JOINED`. `Administrador` y `Docente` la extienden.

| Entidad propietaria | Relación | Obligatoriedad |
|---|---|---|
| FormacionAcademica | Muchas a un Docente | Obligatoria |
| ExperienciaDocente | Muchas a un Docente | Obligatoria |
| DisponibilidadDocente | Muchas a un Docente | Obligatoria |
| Asignatura | Muchas a un PlanEstudios | Obligatoria |
| RequisitoAsignatura | Muchos a una Asignatura | Obligatoria |
| Grupo | Muchos a una Asignatura | Obligatoria |
| Grupo | Muchos a un Docente | Opcional al registrar, obligatoria para programar |
| SesionHorario | Muchas a un Grupo | Obligatoria |

Todas las relaciones son `@ManyToOne(fetch = LAZY)`, unidireccionales y sin borrado en cascada. Hay unicidad en documento y correo de usuario, código de programa y de asignatura, y número de grupo dentro de su asignatura.

`ConfiguracionHorarios` es única en el sistema y guarda franjas de lunes a viernes y de sábado, descanso, duración de sesión, máximo de sesiones por grupo, si se evitan días consecutivos y los límites de cupo.

## Base de datos

Flyway aplica las migraciones al arrancar y Hibernate solo valida (`ddl-auto=validate`). Los cambios de esquema se hacen con una migración nueva, nunca editando una ya aplicada.

| Ubicación | Contenido | Se aplica |
|---|---|---|
| `db/migration/V1__crear_esquema.sql` | Tablas, secuencias, índices y restricciones | Siempre |
| `db/seed/R__datos_demo.sql` | Datos de demostración | Solo con el perfil `dev` |

Los datos de demostración cargan el plan de estudios 2025 de Ingeniería de Sistemas: 58 asignaturas en 9 semestres y 148 créditos. Los códigos `ISsnn` (semestre y orden) son internos; el plan oficial no los publica. Cada asignatura tiene una sesión semanal por cada 2 créditos, salvo Trabajo de grado, que tiene una. También incluyen 4 docentes, disponibilidad para dos de ellos y 7 grupos, uno sin docente.

Los datos de demostración usan identificadores desde 900001, lejos de los que asignan las secuencias, y las contraseñas de sus docentes no se conocen. Se pueden activar sobre una base que ya tenga datos.

## Administrador inicial

Al arrancar sin ningún administrador, `AdministradorInicial` crea uno con `SCHEDULER_ADMIN_CORREO` y `SCHEDULER_ADMIN_CONTRASENIA`. Si ya existe alguno no hace nada, y si faltan las variables deja un aviso en el log y la aplicación arranca igual. La contraseña no se escribe en el repositorio ni en el log, y se puede cambiar después desde Administradores. Los demás datos de la cuenta también se editan ahí.

## Reglas de programación

Una sesión programada debe cumplir todas estas condiciones. Los intervalos son `[inicio, fin)`: terminar a las 09:00 y empezar otra a las 09:00 no es un cruce.

- Estar dentro de la franja institucional y no invadir el descanso.
- Durar exactamente lo configurado.
- No cruzarse con otra sesión del grupo ni del docente.
- Tener un grupo con cupo válido: entre el mínimo y el máximo más la tolerancia (con 40 y 10 % el límite es 44).
- Quedar dentro de la disponibilidad del docente. Un docente sin franjas registradas se considera disponible en todo el horario institucional. Las franjas contiguas se unen.
- No superar las sesiones de la asignatura, el máximo institucional ni la carga semanal del docente.
- Respetar la regla de días consecutivos cuando esté activa.

Los cambios que dejarían inválidas sesiones ya programadas se rechazan: quitar disponibilidad usada, reducir la carga de un docente o cambiar la configuración.

Las escrituras toman primero un bloqueo pesimista sobre la configuración, y la programación bloquea además al docente. Así todas siguen el mismo orden.

## Generación de horarios

`POST /api/sesiones/grupo/{id}/generar` completa las sesiones faltantes de un grupo:

1. Valida grupo, asignatura, plan, docente, requisitos y sesiones existentes.
2. Calcula por día los bloques libres, descontando agenda del docente, descanso y franjas fuera de su disponibilidad.
3. Busca una solución con retroceso, empezando por el menor número de sesiones por día y separando los días cuando es posible.
4. Guarda solo si encontró la solución completa. Si no, responde 409 sin sesiones parciales.

Repetir la operación sobre un grupo completo no cambia nada. El sistema genera por grupo: no elige docente ni optimiza todos los grupos a la vez.

## API

Cada recurso ofrece `POST` (201), `GET`, `GET /{id}`, `PUT /{id}`, `DELETE /{id}` (204), `GET /cantidad` y `GET /{id}/existe`. Los filtros son `GET <ruta>/por-<filtro>?valor=...` y comparan por igualdad.

| Ruta base | Filtros |
|---|---|
| `/api/administradores` | `nombre`, `numero-documento`, `correo-institucional`, `cargo` |
| `/api/docentes` | `nombre`, `numero-documento`, `correo-institucional`, `tipo-vinculacion`, `especialidad`, `escalafon` |
| `/api/formaciones` | `docente`, `nivel-formacion`, `institucion`, `area-conocimiento` |
| `/api/experiencias` | `docente`, `tipo-experiencia`, `institucion`, `area-conocimiento` |
| `/api/disponibilidades` | `docente` |
| `/api/planes-estudios` | `nombre-programa`, `codigo-programa`, `version-plan`, `estado` |
| `/api/asignaturas` | `codigo`, `nombre`, `semestre`, `area-conocimiento`, `estado`, `plan-estudios` |
| `/api/grupos` | `numero-grupo`, `estado`, `asignatura` |
| `/api/requisitos-asignatura` | `asignatura`, `tipo-requisito`, `area-conocimiento` |
| `/api/sesiones` | `grupo`, `dia-semana`, `docente` |
| `/api/configuracion-horarios` | Sin filtros; `GET /actual` devuelve la configuración vigente |

Rutas adicionales:

| Método | Ruta | Uso |
|---|---|---|
| POST | `/api/sesiones/grupo/{id}/generar` | Generar sesiones del grupo |
| GET | `/api/sesiones/exportar/csv` | CSV con `;` como separador y UTF-8 con BOM, para Excel en español |
| GET | `/api/sesiones/exportar/pdf` | PDF horizontal en A4 |
| GET | `/api/usuarios/actual` | Identidad y rol del usuario autenticado |
| GET | `/api/mi-horario` | Horario propio del docente autenticado |
| GET | `/api/mi-horario/exportar/csv` y `/pdf` | Exportación del horario propio |
| GET | `/api/csrf` | Token CSRF |

Las exportaciones aceptan los parámetros opcionales `grupo` y `docente`, e incluyen solo sesiones programadas.

Fechas `AAAA-MM-DD`, horas `HH:mm:ss`, días como `DayOfWeek` (`MONDAY`). Los errores usan `ProblemDetail`: 400 para datos inválidos, 404 para recursos inexistentes y 409 para duplicados, cruces o conflictos de capacidad. Swagger UI está en `/swagger-ui/index.html`.

## Seguridad

HTTP Basic con el correo institucional. Contraseñas con BCrypt mediante `DelegatingPasswordEncoder`, límite de 72 bytes. CSRF activo: para escribir se obtiene el token en `GET /api/csrf`, se conserva la cookie de sesión y se envía el token en la cabecera `headerName`.

Las peticiones con `X-Requested-With: XMLHttpRequest` reciben el 401 sin `WWW-Authenticate`, para que el navegador no abra su diálogo nativo y la aplicación muestre el error en la página. Swagger y curl conservan el reto de HTTP Basic.

| Ruta | Acceso |
|---|---|
| `/actuator/health`, `/actuator/info`, Swagger | Público |
| `/api/csrf`, `/api/usuarios/actual` | Cualquier usuario autenticado |
| `GET /api/mi-horario` y `GET /api/mi-horario/exportar/{csv,pdf}` | `DOCENTE` |
| Resto de `/api/**` | `ADMINISTRADOR` |

El rol `DOCENTE` solo lee su propio horario: sus grupos, sus asignaturas, sus sesiones programadas y la configuración. No crea, modifica ni elimina información, y no accede a datos de otros docentes.

## Pruebas

```bash
./mvnw test
```

Cubren servicios con repositorios simulados, contrato HTTP, seguridad y metadatos JPA. `AcademicSchedulerApplicationTests` e `IntegracionPostgresTest` levantan PostgreSQL 17 con Testcontainers y comprueban migraciones, datos de demostración, administrador inicial, restricciones de la base y generaciones simultáneas. Se omiten si Docker no está activo.

## Pendientes

- HTTPS en despliegue.
- Cuentas de docentes con contraseña inicial. Hoy la asigna el administrador.
