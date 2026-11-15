# Guía de usuarios y base de datos

Cómo entrar al sistema con cada rol, qué cuentas trae la base de demostración y cómo se levanta, consulta y reinicia la base de datos en un equipo de desarrollo.

## Roles

| Rol | Quién lo usa | Qué puede hacer |
|---|---|---|
| Administrador | Coordinador de horarios | Gestión completa: docentes, planes de estudio, asignaturas, grupos, requisitos, administradores, configuración y horarios. Genera, mueve y valida sesiones, guarda borradores, publica el horario, registra novedades de docentes y exporta el horario a CSV y PDF. |
| Docente | Profesor | Solo consulta. Ve en "Mi horario" su parte del último horario publicado y la exporta a CSV y PDF. No crea, modifica ni elimina información y no ve datos de otros docentes. |

El inicio de sesión es el mismo para los dos: correo institucional y contraseña. El sistema reconoce el rol y muestra el menú que corresponde.

| Rol | Menú |
|---|---|
| Administrador | Inicio, Docentes, Planes de estudio, Asignaturas, Grupos, Horarios, Validaciones, Versiones y publicación, Novedades, Requisitos académicos, Administradores y Configuración. |
| Docente | Mi horario y Cuenta. |

## Cuentas

### Administrador

La base empieza sin usuarios. El primer administrador se crea solo la primera vez que arranca el backend, con dos variables de entorno:

| Variable | Valor |
|---|---|
| `SCHEDULER_ADMIN_CORREO` | Correo con el que se va a ingresar, por ejemplo `admin@unbosque.edu.co`. |
| `SCHEDULER_ADMIN_CONTRASENIA` | Contraseña que elija cada uno. No se guarda en el repositorio. |

La cuenta queda como "Administrador Inicial", con documento `0000000000` y cargo "Coordinador". Esos datos y la contraseña se cambian después desde **Administradores**. Desde la misma pantalla se crean más administradores.

Si ya existe algún administrador, las variables no hacen nada. Si faltan, el backend arranca igual y deja un aviso en el log.

### Docentes de demostración

El perfil `dev` carga cuatro docentes:

| Docente | Correo | Documento | Vinculación | Disponibilidad | Grupos |
|---|---|---|---|---|---|
| Laura Méndez | `lmendez@unbosque.edu.co` | 1010101010 | Tiempo completo, 20 h | Sin franjas: disponible en todo el horario | IS105 Desarrollo de sistemas de información 1, IS503 Ingeniería de software 1 |
| Andrés Rojas | `arojas@unbosque.edu.co` | 1020202020 | Tiempo completo, 20 h | Sin franjas: disponible en todo el horario | IS101 Matemáticas 1, IS202 Álgebra lineal |
| Camila Torres | `ctorres@unbosque.edu.co` | 1030303030 | Medio tiempo, 12 h | Lunes y miércoles de 18:00 a 22:00 | IS405 Infraestructura TI 1 |
| Felipe Castaño | `fcastano@unbosque.edu.co` | 1040404040 | Tres cuartos de tiempo, 16 h | Martes y jueves de 16:00 a 22:00, sábado de 07:00 a 12:00 | IS403 Estructuras de sistemas |

También hay un séptimo grupo, IS407 Proyecto núcleo 1, con cupo de 25 y sin docente, para probar la asignación. Los grupos empiezan sin sesiones: se generan o se agregan desde **Horarios**.

Las contraseñas de estos docentes no se conocen. Para entrar como docente:

1. Ingresar como administrador.
2. En **Docentes**, editar al docente y escribir una contraseña nueva.
3. Cerrar sesión e ingresar con el correo del docente y esa contraseña.

Al editar un docente, dejar la contraseña vacía conserva la actual.

### Reglas de las cuentas

- El correo y el número de documento no se pueden repetir entre usuarios, sean administradores o docentes.
- La contraseña debe tener entre 1 y 72 bytes. Se guarda cifrada con BCrypt, así que nadie puede leerla desde la base.
- Un docente con grupos asignados no se puede eliminar. El mensaje dice qué grupos tiene; hay que quitárselos primero.

## Base de datos

### Conexión

MySQL 8.4 corre en Docker con estos datos, que solo sirven para desarrollo local:

| Dato | Valor |
|---|---|
| Servidor | `localhost` |
| Puerto | `3306`, o el de `SCHEDULER_DB_PUERTO` si se cambió |
| Base | `scheduler` |
| Usuario | `scheduler` |
| Contraseña | `scheduler` |
| Usuario administrador de MySQL | `root`, contraseña `root` |

### Arranque completo

Desde la raíz del repositorio, en este orden:

```bash
docker compose up -d
```

Backend, en macOS o Linux:

```bash
cd backend
SCHEDULER_ADMIN_CORREO=admin@unbosque.edu.co SCHEDULER_ADMIN_CONTRASENIA='tu-contraseña' ./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

Backend, en Windows con PowerShell:

```powershell
cd backend
$env:SCHEDULER_ADMIN_CORREO='admin@unbosque.edu.co'
$env:SCHEDULER_ADMIN_CONTRASENIA='tu-contraseña'
.\mvnw spring-boot:run '-Dspring-boot.run.profiles=dev'
```

Frontend, en otra terminal:

```bash
cd frontend
npm ci
npm start
```

La aplicación queda en `http://localhost:4200` y la API en `http://localhost:8080`. La documentación de la API está en `http://localhost:8080/swagger-ui/index.html`.

Quien tenía la versión anterior con PostgreSQL debe ejecutar una vez `docker compose up -d --remove-orphans`, que quita los contenedores viejos de PostgreSQL y pgAdmin.

### Si el equipo ya tiene MySQL instalado

Un MySQL instalado en el equipo, por ejemplo el de las clases, ocupa el puerto 3306 y `docker compose up -d` falla con "address already in use". Hay tres opciones.

**Usar otro puerto para Docker.** Antes de levantar la base y el backend, en la misma terminal:

```bash
export SCHEDULER_DB_PUERTO=3307
```

En PowerShell: `$env:SCHEDULER_DB_PUERTO='3307'`. Docker y el backend leen esa variable, así que no hay que cambiar nada más. Para conectarse desde Workbench o DBeaver se usa ese puerto.

**Detener el MySQL instalado** mientras se trabaja en el proyecto, y usar Docker con el puerto 3306.

**Usar el MySQL instalado en lugar de Docker.** Desde MySQL Workbench o la consola, con el usuario `root`:

```sql
CREATE DATABASE scheduler CHARACTER SET utf8mb4;
CREATE USER 'scheduler'@'localhost' IDENTIFIED BY 'scheduler';
GRANT ALL PRIVILEGES ON scheduler.* TO 'scheduler'@'localhost';
```

Con eso el backend se conecta sin configurar nada más. Para usar otra base u otro usuario, se definen antes de arrancar el backend `SCHEDULER_DB_URL` (por ejemplo `jdbc:mysql://localhost:3306/otra_base`), `SCHEDULER_DB_USUARIO` y `SCHEDULER_DB_CONTRASENIA`.

Se necesita MySQL 8.0.19 o superior. XAMPP trae MariaDB en lugar de MySQL, y con MariaDB los datos de demostración no cargan.

### Cómo se crea el esquema

No hay que correr ningún script a mano. Al arrancar, el backend aplica con Flyway las migraciones de `backend/src/main/resources/db/migration/` (`V1__crear_esquema.sql` y `V2__versiones_novedades_auditoria.sql`), que crean las tablas, y Hibernate solo comprueba que coincidan con las entidades. Flyway anota lo aplicado en la tabla `flyway_schema_history`. Los identificadores de cada tabla se generan con `AUTO_INCREMENT`.

Con el perfil `dev` se aplica además `backend/src/main/resources/db/seed/R__datos_demo.sql`: el plan de estudios 2025 de Ingeniería de Sistemas (58 asignaturas en 9 semestres), la configuración institucional, los cuatro docentes, su disponibilidad y los siete grupos. Sus identificadores empiezan en 900001 y no duplica datos si ya están. Después de cargarlos, MySQL sigue numerando cada tabla desde el identificador más alto, así que lo que se cree desde la aplicación recibe números por encima de 900000.

Sin el perfil `dev` solo se crea el esquema vacío, y el administrador inicial si se definieron las variables.

### Tablas

| Tabla | Contenido | Se relaciona con |
|---|---|---|
| `usuario` | Datos comunes de todas las cuentas: nombre, documento, correo, contraseña cifrada y estado. | Base de `administrador` y `docente`. |
| `administrador` | Cargo del administrador. | Comparte el `id` con `usuario`. |
| `docente` | Vinculación, escalafón, especialidad y horas máximas por semana. | Comparte el `id` con `usuario`. |
| `formacion_academica` | Títulos del docente. | `docente` |
| `experiencia_docente` | Experiencia laboral, docente e investigativa. | `docente` |
| `disponibilidad_docente` | Franjas en que el docente puede dictar clase. | `docente` |
| `plan_estudios` | Programa y versión del plan. | Tiene muchas `asignatura`. |
| `asignatura` | Código, nombre, semestre, créditos y sesiones por semana. | `plan_estudios` |
| `requisito_asignatura` | Formación o experiencia que exige la asignatura al docente. | `asignatura` |
| `grupo` | Número de grupo, estado y cupo esperado. | `asignatura` y, si lo tiene, `docente` |
| `sesion_horario` | Día, hora de inicio, hora de fin y estado de cada clase. | `grupo` |
| `configuracion_horarios` | Horario institucional, descanso, duración de sesión, máximo de sesiones y límites de cupo. Hay una sola fila. | Ninguna |
| `version_horario` | Borradores y versiones publicadas del horario, con quién y cuándo. | Tiene muchas `version_sesion`; `origen_id` apunta a la versión de la que se recuperó. |
| `version_sesion` | Copia de cada sesión dentro de una versión. | `version_horario` y `grupo` |
| `novedad_docente` | Incapacidades o cambios de disponibilidad de un docente, abiertos o cerrados. | `docente` |
| `auditoria_cambio` | Historial de cambios del horario con fecha, correo y detalle. | Ninguna |

La sesión no guarda el docente: se obtiene a través de su grupo.

### Ver los datos

**phpMyAdmin** se levanta con el mismo `docker compose up -d`. Se abre `http://localhost:8081` y se ingresa con usuario `scheduler` y contraseña `scheduler`. Las tablas aparecen en la base `scheduler`, a la izquierda.

**MySQL Workbench**: crear una conexión nueva con servidor `127.0.0.1`, puerto `3306`, usuario `scheduler` y contraseña `scheduler`. Para ver el diagrama de las tablas: Database › Reverse Engineer, elegir la base `scheduler` y seguir el asistente.

También sirve cualquier otro cliente, como DBeaver, con los datos de la sección Conexión.

### Consultas útiles

Usuarios con su rol:

```sql
SELECT u.nombre, u.apellido, u.correo_institucional,
       CASE WHEN a.id IS NOT NULL THEN 'ADMINISTRADOR' ELSE 'DOCENTE' END AS rol
FROM usuario u
LEFT JOIN administrador a ON a.id = u.id
ORDER BY rol, u.apellido;
```

Horario programado, con asignatura, grupo y docente:

```sql
SELECT a.codigo, a.nombre, g.numero_grupo,
       CONCAT(u.nombre, ' ', u.apellido) AS docente,
       s.dia_semana, s.hora_inicio, s.hora_fin
FROM sesion_horario s
JOIN grupo g ON g.id_grupo = s.grupo_id
JOIN asignatura a ON a.id_asginatura = g.asignatura_id
LEFT JOIN usuario u ON u.id = g.docente_id
WHERE s.estado = 'PROGRAMADA'
ORDER BY a.codigo,
         FIELD(s.dia_semana, 'MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY'),
         s.hora_inicio;
```

Migraciones aplicadas:

```sql
SELECT version, description, installed_on, success
FROM flyway_schema_history
ORDER BY installed_rank;
```

La columna `id_asginatura` se llama así en el esquema. En MySQL los nombres se unen con `CONCAT`: `||` no concatena texto. `FIELD` ordena los días de lunes a sábado en lugar de alfabéticamente.

Los datos se cambian desde la aplicación, no con SQL. La aplicación valida cruces, cupos y disponibilidad antes de guardar, y una contraseña escrita directamente en la tabla no funcionaría porque debe ir cifrada.

### Reiniciar la base

Si Flyway falla al arrancar porque la base viene de una versión anterior del esquema, o se quiere empezar de cero, este comando **borra todos los datos locales**:

```bash
docker compose down -v
docker compose up -d
```

Al volver a arrancar el backend se crean de nuevo el esquema, el administrador inicial y, con el perfil `dev`, los datos de demostración.

### Cambios al esquema

`V1__crear_esquema.sql` no se edita, porque Flyway detecta el cambio y el backend no arranca. Para agregar una columna o una tabla se crea una migración nueva en la misma carpeta, con el número siguiente, por ejemplo `V2__agregar_campo.sql`. Después se ajusta la entidad para que Hibernate la valide.
