# SIS GEN · Frontend Angular

Aplicación del sistema de horarios de Ingeniería de Sistemas de la Universidad El Bosque. Angular 20.3, TypeScript, HTML, CSS y **NgModules**. Los 17 componentes declaran `standalone: false`; el arranque utiliza `AppModule`.

## Ejecutar desde Visual Studio Code

Requiere una versión de Node compatible con Angular 20.3: Node 20.19+, 22.12+ o 24.x. Se verificó con Node 24.22.1.

```powershell
Set-Location -LiteralPath 'C:\Users\Jairo\.codex\academic-scheduler-system\frontend'
npm ci
npm start
```

Abrir `http://127.0.0.1:4200`. El backend debe estar operativo en `http://localhost:8080`; para ingresar se requiere una cuenta existente en el servidor. El frontend no crea usuarios iniciales ni contiene credenciales de demostración.

`proxy.conf.json` reenvía `/api/**` al backend durante el desarrollo. Si el backend usa otro puerto, cambiar el `target` de ese archivo. La ruta base está centralizada en `src/environments/environment.ts` y su variante de producción.

## Compilar y probar

```powershell
npm run build
npm test -- --watch=false
```

La salida de producción está en `dist/frontend/browser`. Las pruebas utilizan Jasmine y Karma con ChromeHeadless; requieren Chrome o un navegador Chromium compatible configurado mediante `CHROME_BIN`.

Se ejecutaron 24 pruebas satisfactorias en el navegador integrado de Codex, conectado al servidor Karma. El lanzador automático de Edge no pudo ejecutarse dentro del entorno restringido de Windows; no se deshabilitaron medidas de seguridad para resolverlo.

## Autenticación y acceso

HTTP Basic mediante correo institucional y contraseña, con token CSRF obtenido de `GET /api/csrf`. Las credenciales sólo permanecen en memoria mientras la aplicación está abierta: recargar la página exige ingresar otra vez. Cerrar sesión elimina las credenciales y el token de la memoria del frontend.

El backend no ofrece un endpoint de perfil/roles ni de logout explícito. Tras validar las credenciales, se consulta el acceso administrativo usando la búsqueda de administrador por correo. Una respuesta 403 permite identificar el acceso docente limitado según los únicos dos roles de la implementación actual. No se interpretan errores 401, 500 o fallos de conexión como un inicio de sesión exitoso. Si se cambia la política de roles del servidor, este flujo debe adaptarse; es preferible añadir posteriormente un endpoint de identidad explícito.

Los administradores acceden a la gestión académica. Los docentes tienen una pantalla de cuenta autenticada que explica la limitación actual del servidor: las consultas y escrituras de información académica están protegidas para ADMINISTRADOR. La aplicación no intenta saltarse esa restricción. Los guards de Angular complementan la autorización del servidor, no la sustituyen.

## Funciones

- Gestión de docentes y ficha con formación académica y experiencia.
- CRUD de planes de estudio, asignaturas, grupos, requisitos y administradores.
- Configuración institucional de franjas, descanso, duración y distribución de sesiones.
- Agenda de lunes a sábado, filtros por grupo y docente, vista de lista, edición y cancelación de sesiones.
- Arrastre con confirmación: el movimiento propone un cambio; sólo Guardar ajuste envía el PUT.
- Generación por grupo mediante el endpoint existente; conserva las sesiones previas.
- Resumen con datos consultados y revisión local de cruces, franjas, cantidad, duración y carga.
- Estados de carga, errores HTTP, validación de formularios, diálogos de confirmación y navegación adaptable.

No existen servicios de comparación de alternativas, publicación, historial, periodos, aulas ni disponibilidad personal en el backend revisado. No se añadieron botones de éxito ficticio para esos procesos.

## Producción

Publicar el contenido de `dist/frontend/browser` y configurar el servidor web para entregar `index.html` al acceder a rutas de Angular. Publicar `/api` mediante un proxy inverso al backend en el mismo origen, conservar cookies y la cabecera Authorization, y usar HTTPS. Esta solución evita depender de una configuración CORS inexistente en el backend actual. No se modificó la seguridad ni la lógica de negocio del servidor.

Consultar `IMPLEMENTACION_FRONTEND.md` para el inventario de archivos, endpoints conectados, pruebas visuales y límites de verificación.

## Trasladar a otra copia del repositorio

Después de respaldar cambios propios, sustituir la carpeta `frontend` anterior por esta versión y ejecutar `npm ci`. No copiar `node_modules`, `.angular` ni `dist` como código fuente. El ZIP de entrega incluye los archivos fuente, el logo, el lockfile y la configuración; no incluye el servidor aislado usado para las pruebas visuales.
