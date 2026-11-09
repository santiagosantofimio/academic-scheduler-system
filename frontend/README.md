# SIS GEN · Frontend Angular

Aplicación del sistema de horarios de Ingeniería de Sistemas de la Universidad El Bosque. Angular 20.3, TypeScript, HTML, CSS y NgModules. Los componentes declaran `standalone: false` y el arranque utiliza `AppModule`.

## Ejecutar

Requiere Node 20.19+, 22.12+ o 24.x.

```bash
npm ci
npm start
```

Abrir `http://127.0.0.1:4200`. El backend debe estar activo en `http://localhost:8080`. La cuenta inicial se crea al arrancarlo; ver el README de la raíz.

`proxy.conf.json` reenvía `/api/**` al backend durante el desarrollo. Si el backend usa otro puerto, cambiar el `target` de ese archivo. La ruta base está en `src/environments`.

## Compilar y probar

```bash
npm run build
npm test -- --watch=false
```

La salida de producción queda en `dist/frontend/browser`. Las pruebas usan Jasmine y Karma con ChromeHeadless; requieren Chrome o un navegador Chromium configurado mediante `CHROME_BIN`.

## Autenticación y acceso

HTTP Basic con correo institucional y contraseña, y token CSRF de `GET /api/csrf`. Las credenciales solo permanecen en memoria. El rol se consulta en `GET /api/usuarios/actual`.

| Rol | Acceso |
|---|---|
| Administrador | Gestión académica completa. |
| Docente | Solo lectura de su propio horario en Mi horario, con exportación. |

## Funciones

- Docentes con ficha de formación, experiencia y disponibilidad.
- CRUD de planes, asignaturas, grupos, requisitos y administradores.
- Configuración de franjas, descanso, duración, distribución y cupos.
- Agenda de lunes a sábado, vista de lista y filtros por grupo y docente.
- Arrastre con confirmación: el movimiento propone un cambio y solo guardar lo envía.
- Generación por grupo, conservando las sesiones previas.
- Revisión local de cruces, franjas, cantidad, duración y carga.
- Exportación a CSV y PDF.

## Producción

Publicar `dist/frontend/browser` y configurar el servidor web para entregar `index.html` en las rutas de Angular. Publicar `/api` con un proxy inverso en el mismo origen, conservando cookies y la cabecera Authorization, y usar HTTPS.

## Recursos visuales

Usa Bootstrap Grid 5.3.8 y Font Awesome Free 6.7.2 junto con los estilos propios. El logo está en `public/images/logos/sis-gen.png`.

Más detalle en [IMPLEMENTACION_FRONTEND.md](IMPLEMENTACION_FRONTEND.md).
