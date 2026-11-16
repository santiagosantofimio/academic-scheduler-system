# academic-scheduler-system

Sistema de programación de horarios de clases, desarrollado como Proyecto Núcleo 1 (Ingeniería de Sistemas, Universidad El Bosque). Aborda una instancia acotada del *timetable problem*: gestión de docentes, asignaturas y grupos, validación de conflictos, generación asistida por grupo y ajuste con *drag-and-drop*.

## Stack

- **Backend**: Java 21, Spring Boot 4.1.1, Maven, MySQL (JPA/Hibernate), Flyway, Spring Security, springdoc-openapi, OpenPDF.
- **Frontend**: Angular 20 con NgModules.
- **Base de datos**: MySQL 8.4 (Docker).

## Estructura

```
backend/     API REST: controller, service, repository, entity, dto, security, util
frontend/    SPA Angular
docker-compose.yml   MySQL + phpMyAdmin para desarrollo local
docs/        Informe de ingeniería y diagramas
```

Detalle técnico en [backend/IMPLEMENTACION.md](backend/IMPLEMENTACION.md) y [frontend/IMPLEMENTACION_FRONTEND.md](frontend/IMPLEMENTACION_FRONTEND.md).

## Requisitos

- JDK 21 o superior
- Node.js 20.19+ / npm
- Docker + Docker Compose

## Arranque local

1. Levantar base de datos:
   ```bash
   docker compose up -d
   ```
   MySQL queda en el puerto 3306 con base, usuario y contraseña `scheduler`. Si ese puerto ya lo usa un MySQL instalado en el equipo, `docker compose up -d` falla. En ese caso define otro puerto en la terminal antes de levantar la base y el backend; los dos leen la misma variable:
   ```bash
   export SCHEDULER_DB_PUERTO=3307
   ```
   En PowerShell: `$env:SCHEDULER_DB_PUERTO='3307'`.

   También puedes usar ese MySQL en lugar de Docker: crea la base `scheduler` y, si el usuario no es `scheduler` con contraseña `scheduler`, define `SCHEDULER_DB_URL`, `SCHEDULER_DB_USUARIO` y `SCHEDULER_DB_CONTRASENIA`. Se necesita MySQL 8.0.19 o superior; con MariaDB, la que trae XAMPP, los datos de demostración no cargan.
2. Backend (puerto 8080). Las variables crean el primer administrador la primera vez que arranca sin ninguno; elige tu propia contraseña:
   ```bash
   cd backend
   SCHEDULER_ADMIN_CORREO=admin@unbosque.edu.co \
   SCHEDULER_ADMIN_CONTRASENIA='tu-contraseña' \
   ./mvnw spring-boot:run
   ```
   En PowerShell:
   ```powershell
   cd backend
   $env:SCHEDULER_ADMIN_CORREO='admin@unbosque.edu.co'
   $env:SCHEDULER_ADMIN_CONTRASENIA='tu-contraseña'
   .\mvnw spring-boot:run
   ```
   Al arrancar se aplican las migraciones y se cargan los datos base: el plan de estudios 2025 de Ingeniería de Sistemas (58 asignaturas), la configuración, 12 docentes y un grupo por asignatura. Esos datos solo se insertan si no existen, así que reiniciar o actualizar el aplicativo no borra ni sobrescribe lo que se haya registrado. La base vive en el volumen `mysql_data` de Docker y solo se borra con `docker compose down -v`.

   Health check: `curl http://localhost:8080/actuator/health`. Swagger UI: `http://localhost:8080/swagger-ui/index.html`.
3. Frontend (puerto 4200):
   ```bash
   cd frontend
   npm ci
   npm start
   ```

Los docentes de demostración no tienen contraseña conocida. Para probar el rol de consulta, edita uno desde Docentes con la cuenta de administrador y asígnale una.

phpMyAdmin disponible en `http://localhost:8081` (usuario `scheduler`, contraseña `scheduler`). MySQL Workbench también se conecta a `localhost:3306` con esos datos.

Si antes usabas la versión con PostgreSQL, ejecuta una vez `docker compose up -d --remove-orphans` para quitar los contenedores de PostgreSQL y pgAdmin.

Si tu base local viene de una versión anterior del esquema y Flyway falla al validar, recréala. Este comando **borra los datos locales**:

```bash
docker compose down -v
docker compose up -d
```

## Pruebas

```bash
cd backend && ./mvnw test
cd frontend && npm test -- --watch=false
```

Las pruebas de integración del backend levantan MySQL con Testcontainers y se omiten si Docker no está activo. Las del frontend usan ChromeHeadless y requieren Chrome o Chromium (`CHROME_BIN`).

## Alcance del PMV (12 semanas)

**Incluye**: CRUD de docentes, asignaturas, grupos y franjas; motor de validación de conflictos (choque de docente y grupo; cupo de grupo de 40 con 10 % de tolerancia; disponibilidad docente; franja bloqueada de 12:00 a 13:00); grilla semanal drag-and-drop; generación de horario sin choques por grupo; login con dos roles; exportación CSV y PDF.

**Fuera de alcance**: aulas y espacios físicos, inscripción de estudiantes, reasignación automática de grupos, algoritmos de optimización metaheurística (GRASP, tabú, genéticos).

El diagrama de clases UML aprobado está en [docs/diagrams](docs/diagrams).

## Roles

| Rol del PMV | Cuenta | Permisos |
|---|---|---|
| Coordinador | `Administrador` | Gestión completa. |
| Consulta | `Docente` | Solo lectura de su propio horario, en la pantalla "Mi horario", y su exportación. No crea, modifica ni elimina información. |

## Estado

Implementado: API, frontend, reglas de programación, exportación, migraciones Flyway, administrador inicial y datos de demostración.
