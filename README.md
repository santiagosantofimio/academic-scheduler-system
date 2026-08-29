# academic-scheduler-system

Sistema automatizado de programación de horarios de clases, desarrollado como Proyecto Núcleo 1 (Ingeniería de Sistemas, Universidad El Bosque). Aborda una instancia acotada del *timetable problem*: gestión paramétrica de docentes, cursos, aulas y franjas horarias, con motor de validación de conflictos y armado de horario asistido mediante *drag-and-drop*.

## Stack

- **Backend**: Java 26, Spring Boot 4.1.1, Maven, PostgreSQL (JPA/Hibernate), Flyway, Spring Security.
- **Frontend**: Angular 20 (standalone components).
- **Base de datos**: PostgreSQL 17 (Docker).

## Estructura

```
backend/     API REST — capas domain / application / infrastructure / api
frontend/    SPA Angular
docker-compose.yml   PostgreSQL + pgAdmin para desarrollo local
docs/        Informe de ingeniería, ADRs
```

## Requisitos

- JDK 26
- Node.js 20+ / npm
- Docker + Docker Compose

## Arranque local

1. Levantar base de datos:
   ```bash
   docker compose up -d
   ```
2. Backend (puerto 8080):
   ```bash
   cd backend
   ./mvnw spring-boot:run
   ```
   Health check: `curl -u user:<password-generado-en-log> http://localhost:8080/actuator/health` (los endpoints `/actuator/health` e `/info` están abiertos sin auth en esta etapa de scaffolding).
3. Frontend (puerto 4200):
   ```bash
   cd frontend
   npm start
   ```

pgAdmin disponible en `http://localhost:5050` (usuario `admin@scheduler.local`, contraseña `admin`).

## Alcance del PMV (12 semanas)

**Incluye**: CRUD paramétrico de docentes/cursos/aulas/horarios, motor de validación de conflictos (choque docente/aula/grupo, capacidad, disponibilidad), grilla semanal drag-and-drop, heurística de sugerencia de horario sin choques, login básico con roles Coordinador/Consulta, exportación CSV/PDF.

**Fuera de alcance**: inscripción de estudiantes, reasignación automática de grupos, algoritmos de optimización metaheurística (GRASP, tabú, genéticos).

## Estado

Fase A — scaffolding (en curso).
