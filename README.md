# academic-scheduler-system

Sistema de programación de horarios de clases, desarrollado como Proyecto Núcleo 1 (Ingeniería de Sistemas, Universidad El Bosque). Aborda una instancia acotada del *timetable problem*: gestión de docentes, asignaturas y grupos, validación de conflictos, generación asistida por grupo y ajuste con *drag-and-drop*.

## Stack

- **Backend**: Java 21, Spring Boot 4.1.1, Maven, PostgreSQL (JPA/Hibernate), Flyway, Spring Security, springdoc-openapi, OpenPDF.
- **Frontend**: Angular 20 con NgModules.
- **Base de datos**: PostgreSQL 17 (Docker).

## Estructura

```
backend/     API REST: controller, service, repository, entity, dto, security, util
frontend/    SPA Angular
docker-compose.yml   PostgreSQL + pgAdmin para desarrollo local
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
2. Backend (puerto 8080). Las variables crean el primer administrador la primera vez que arranca sin ninguno; elige tu propia contraseña:
   ```bash
   cd backend
   SCHEDULER_ADMIN_CORREO=admin@unbosque.edu.co \
   SCHEDULER_ADMIN_CONTRASENIA='tu-contraseña' \
   ./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
   ```
   En PowerShell:
   ```powershell
   cd backend
   $env:SCHEDULER_ADMIN_CORREO='admin@unbosque.edu.co'
   $env:SCHEDULER_ADMIN_CONTRASENIA='tu-contraseña'
   .\mvnw spring-boot:run '-Dspring-boot.run.profiles=dev'
   ```
   El perfil `dev` carga datos de demostración: el plan de estudios 2025 de Ingeniería de Sistemas (58 asignaturas), configuración, docentes, disponibilidad y grupos. Sin perfil solo se crea el esquema. Las migraciones se aplican solas al arrancar.

   Health check: `curl http://localhost:8080/actuator/health`. Swagger UI: `http://localhost:8080/swagger-ui/index.html`.
3. Frontend (puerto 4200):
   ```bash
   cd frontend
   npm ci
   npm start
   ```

Los docentes de demostración no tienen contraseña conocida. Para probar el rol de consulta, edita uno desde Docentes con la cuenta de administrador y asígnale una.

pgAdmin disponible en `http://localhost:5050` (usuario `admin@scheduler.local`, contraseña `admin`).

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

Las pruebas de integración del backend levantan PostgreSQL con Testcontainers y se omiten si Docker no está activo. Las del frontend usan ChromeHeadless y requieren Chrome o Chromium (`CHROME_BIN`).

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
