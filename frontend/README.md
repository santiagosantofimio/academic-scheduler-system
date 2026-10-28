# SIS GEN · Frontend Angular

Aplicación del sistema de horarios de Ingeniería de Sistemas de la Universidad El Bosque. Angular 20.3, TypeScript, HTML, CSS y **NgModules**. Los 17 componentes declaran `standalone: false`; el arranque utiliza `AppModule`.

## Ejecutar desde Visual Studio Code

Requiere una versión de Node compatible con Angular 20.3: Node 20.19+, 22.12+ o 24.x. La última comprobación usó Node 22.20.0.

```powershell
Set-Location -LiteralPath 'D:\GitHub - Proyecto final\academic-scheduler-system\frontend'
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

La última verificación ejecutó 33 pruebas satisfactoriamente con Edge en modo headless, en el repositorio definitivo y con las dependencias fijadas en el lockfile.

## Autenticación y acceso

HTTP Basic mediante correo institucional y contraseña, con token CSRF obtenido de `GET /api/csrf`. Las credenciales sólo permanecen en memoria mientras la aplicación está abierta: recargar la página exige ingresar otra vez. Cerrar sesión elimina las credenciales y el token de la memoria del frontend.

El backend no ofrece un endpoint de perfil/roles ni de logout explícito. Tras validar las credenciales, se consulta el acceso administrativo usando la búsqueda de administrador por correo. La implementación interpreta una respuesta 403 como acceso docente limitado; este código, por sí solo, no confirma el rol y requiere un contrato explícito de identidad. No se interpretan errores 401, 500 o fallos de conexión como un inicio de sesión exitoso. Si se cambia la política de roles del servidor, este flujo debe adaptarse; es preferible añadir posteriormente un endpoint de identidad explícito.

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

## Recursos visuales

El frontend usa Bootstrap Grid 5.3.8 y Font Awesome Free 6.7.2 junto con los estilos propios. El módulo de navegación se carga de forma diferida para reducir el paquete inicial. El logo transparente SIS GEN se conserva sin recortes en `public/images/logos/sis-gen.png` y se muestra en acceso y navegación. El favicon permanece vacío: la marca es demasiado pequeña y clara para resultar legible en el tamaño de una pestaña.
## Aclaración sobre roles y alcance

El README de la raíz menciona los roles Coordinador y Consulta; la implementación actual distingue Administrador y Docente. Su equivalencia y sus permisos deben confirmarse con los requisitos antes de modificar la seguridad. La inferencia actual de Docente ante una respuesta HTTP 403 debe revisarse: ese código por sí solo no confirma el rol, y aún no existe un contrato explícito de identidad. Por indicación del proyecto, el frontend no incorpora gestión de aulas ni espacios físicos.

## Correspondencia con los prototipos

Los wireframes y mockups de planeación corresponden a `modules/admin/inicio`, con datos consultados y calendario compartido. El editor semanal corresponde a `modules/admin/horarios` y `shared/semana`; el resumen lateral usa las observaciones de `ValidacionHorarioService` y enlaza a `modules/admin/validaciones`. No se incorporan los ejemplos numéricos, personas o periodos del prototipo como datos reales.

Los pasos de comparación de alternativas y publicación no están conectados porque no se cuenta con contratos implementados. El PDF no especifica una pantalla de acceso ni las funciones detalladas del Docente. El logo transparente entregado se muestra sin recortes; se mantiene el favicon vacío porque la marca horizontal, dentro de un PNG cuadrado con mucho margen transparente, no resulta legible en el tamaño de una pestaña.
