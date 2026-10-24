# Entrega del frontend SIS GEN

Fecha: 24 de octubre de 2026. Ubicación: `C:\Users\Jairo\.codex\academic-scheduler-system\frontend`.

## Resultado y alcance

Se sustituyó el scaffold inicial por una aplicación Angular modular con formularios y servicios HTTP reales. Se mantuvieron Angular 20.3 y las dependencias y versiones de `package.json` y `package-lock.json`. No se añadieron bibliotecas visuales ni dependencias de ejecución. El formato del código se normalizó con una herramienta temporal externa al proyecto.

**Verificación final:** compilación de producción correcta, **24 pruebas aprobadas**, 17 componentes no standalone y 6 NgModules. Se inspeccionaron las pantallas en escritorio y a 390 × 844, con tablas que se desplazan dentro de sus contenedores y sin desbordamiento horizontal del documento en las vistas comprobadas.

## Fuentes revisadas

- Las instrucciones proporcionadas por el usuario, el backend Java real, sus DTO, enumeraciones, controladores, validaciones, repositorios, seguridad y respuestas de error.
- Las seis páginas de `Wireframe, mockup y prototipo del sistema de horarios.pdf`: renderizadas e inspeccionadas visualmente. Se conservaron la intención de barra lateral azul, agenda semanal, gestión de datos y revisión de condiciones. Los procesos no respaldados por el backend se dejaron como limitaciones explícitas.
- `SIS GEN.png`: se utiliza el archivo original, idéntico byte por byte a la imagen recibida. CSS encuadra su contenido al presentarlo; el archivo no se recreó ni modificó.
- El frontend Angular existente: era un scaffold con una plantilla raíz que sólo contenía el router outlet y rutas vacías. Se conservaron el proyecto CLI, TypeScript, los scripts y el lockfile.

**Referencia inaccesible:** no fue posible leer `D:\GitHub - Proyectos\ProyectoFinal3rSemestre\PFinalFront`, ni la subcarpeta `src\app` indicada después, incluso tras conceder el permiso de lectura. No se afirma haber analizado ese código. Se adoptó una organización convencional y familiar de módulos, interfaces, servicios, guards e interceptores sobre el proyecto Angular disponible.

## Estructura y archivos

Se añadieron AppModule y AppRoutingModule, los módulos Auth, Layout, Admin y Shared, los componentes de página y seis componentes compartidos. Se retiraron las entradas de arranque y los estilos SCSS del scaffold sustituidos por NgModules y CSS. El código anterior quedó respaldado en la carpeta de trabajo de esta tarea.

```text
src/
├── app
│   ├── guards
│   │   ├── admin.guard.ts
│   │   ├── auth.guard.ts
│   │   └── guards.spec.ts
│   ├── interceptors
│   │   ├── api.interceptor.spec.ts
│   │   └── api.interceptor.ts
│   ├── interfaces
│   │   └── api.models.ts
│   ├── modules
│   │   ├── admin
│   │   │   ├── catalogos
│   │   │   │   ├── catalogo-definitions.ts
│   │   │   │   ├── catalogo.component.html
│   │   │   │   └── catalogo.component.ts
│   │   │   ├── configuracion
│   │   │   │   ├── configuracion.component.html
│   │   │   │   └── configuracion.component.ts
│   │   │   ├── docentes
│   │   │   │   ├── docente-fields.ts
│   │   │   │   ├── docentes.component.html
│   │   │   │   ├── docentes.component.ts
│   │   │   │   ├── ficha-docente.component.html
│   │   │   │   └── ficha-docente.component.ts
│   │   │   ├── horarios
│   │   │   │   ├── horarios.component.html
│   │   │   │   └── horarios.component.ts
│   │   │   ├── inicio
│   │   │   │   ├── inicio.component.html
│   │   │   │   └── inicio.component.ts
│   │   │   ├── validaciones
│   │   │   │   └── validaciones.component.ts
│   │   │   └── admin.module.ts
│   │   ├── auth
│   │   │   ├── auth.module.ts
│   │   │   ├── login.component.html
│   │   │   └── login.component.ts
│   │   └── layout
│   │       ├── cuenta.component.ts
│   │       ├── layout.component.html
│   │       ├── layout.component.ts
│   │       └── layout.module.ts
│   ├── services
│   │   ├── academico.service.spec.ts
│   │   ├── academico.service.ts
│   │   ├── api-error.ts
│   │   ├── auth.service.spec.ts
│   │   ├── auth.service.ts
│   │   ├── recurso-api.ts
│   │   ├── validacion-horario.service.spec.ts
│   │   └── validacion-horario.service.ts
│   ├── shared
│   │   ├── dialog
│   │   │   └── dialog.component.ts
│   │   ├── feedback
│   │   │   └── feedback.component.ts
│   │   ├── field-error
│   │   │   └── field-error.component.ts
│   │   ├── icon
│   │   │   └── icon.component.ts
│   │   ├── registro-form
│   │   │   ├── registro-form.component.html
│   │   │   ├── registro-form.component.spec.ts
│   │   │   └── registro-form.component.ts
│   │   ├── semana
│   │   │   ├── semana.component.html
│   │   │   └── semana.component.ts
│   │   ├── form-validators.ts
│   │   └── shared.module.ts
│   ├── app-routing.module.ts
│   ├── app.component.ts
│   └── app.module.ts
├── environments
│   ├── environment.production.ts
│   └── environment.ts
├── index.html
├── main.ts
└── styles.css
```

Archivos de configuración y recursos relevantes: `angular.json`, `proxy.conf.json`, `karma.conf.cjs`, `public/assets/sis-gen.png`, `README.md` y este informe. Las rutas de Angular se definen en AppRoutingModule y AdminModule. Los módulos Auth y Admin se cargan de forma diferida.

## Funcionalidades implementadas

| Área | Funciones |
|---|---|
| Acceso | Formulario reactivo, credenciales HTTP Basic reales, validaciones, token CSRF y cookies, error 401 y renovación de sesión. |
| Navegación | Menú por permisos, guards de sesión y administración, menú lateral adaptable y cierre local de sesión. |
| Inicio | Cantidades calculadas a partir de datos consultados, avance por grupos, agenda semanal, filtro y comprobación de preparación. |
| Docentes | Listado, búsqueda y filtro local, creación, edición, eliminación con confirmación y ficha profesional. |
| Formación y experiencia | Consulta por docente, registro, edición y eliminación; validación de años y fechas. |
| Catálogos | CRUD de planes, asignaturas, grupos, requisitos académicos y administradores; selección de relaciones mediante IDs reales. |
| Configuración | Franjas entre semana/sábado, descanso opcional, duración, máximo por grupo y evitar días consecutivos. |
| Horarios | Vista semanal y lista, filtros por docente y grupo, registro manual, actualización, cancelación y eliminación de sesiones. |
| Generación | POST de generación por un grupo con docente; muestra los errores reales y el resultado devuelto, conservando las sesiones existentes. |
| Ajuste visual | Arrastre nativo para proponer día/hora y abrir el formulario de confirmación. Cancelar conserva la sesión original; guardar usa PUT. |
| Validaciones | Revisión local de datos consultados: cruces de docente/grupo, franjas, descanso, duración, cantidad, carga y días consecutivos. La autoridad final sigue siendo el backend. |

Los catálogos utilizan un formulario compartido por metadatos para reutilizar campos y validaciones. Las pantallas de docentes, fichas, agenda, inicio, validaciones y configuración tienen componentes propios. La aplicación no está concentrada en AppComponent.

## Endpoints conectados

Cada recurso CRUD consume `GET /api/<recurso>`, `POST /api/<recurso>`, `PUT /api/<recurso>/{id}` y `DELETE /api/<recurso>/{id}` cuando la pantalla ofrece esas operaciones. Se usa también GET por ID para la ficha del docente. Los POST/PUT/DELETE del CRUD se leen como **texto**, según las respuestas reales de los controladores Java; los listados y la generación se leen como JSON.

| Recurso o función | Rutas |
|---|---|
| Administradores | `/api/administradores` y `/api/administradores/por-correo-institucional?valor=...` |
| Docentes | `/api/docentes`, `/api/docentes/{id}` |
| Planes | `/api/planes-estudios` |
| Asignaturas | `/api/asignaturas`; se respeta el atributo `idAsginatura` del contrato Java |
| Grupos | `/api/grupos`, con `asignaturaId` y `docenteId` |
| Requisitos | `/api/requisitos-asignatura` |
| Formación | `/api/formaciones`, `/api/formaciones/por-docente?valor=...` |
| Experiencia | `/api/experiencias`, `/api/experiencias/por-docente?valor=...` |
| Configuración | GET `/api/configuracion-horarios/actual`, POST o PUT en `/api/configuracion-horarios` |
| Sesiones | `/api/sesiones` y `/api/sesiones/{id}` |
| Generación | POST `/api/sesiones/grupo/{id}/generar` |
| Seguridad | GET `/api/csrf` |

Los IDs de padre se incluyen en el DTO al crear/actualizar. Un `docenteId` nulo al editar un grupo conserva la asignación según el comportamiento existente del backend. El servidor no ofrece una operación específica para retirar solamente la asignación.

## Autenticación y seguridad

El frontend usa HTTP Basic y obtiene el token de `GET /api/csrf`; envía su cabecera junto con la cookie en las escrituras. Las credenciales se conservan exclusivamente en memoria y no se escriben en localStorage, sessionStorage ni archivos. Un 401 durante una sesión activa limpia ese estado y vuelve al login. Cerrar sesión realiza esa limpieza local: no se inventó un endpoint de logout del servidor.

No existe `/me` ni un endpoint que devuelva los roles. Después de autenticar, se comprueba la capacidad administrativa mediante la consulta de administrador por correo. Una respuesta 403 después de un CSRF válido identifica el acceso limitado bajo el contrato actual de dos roles; una respuesta exitosa identifica y carga la cuenta administrativa. Los fallos de red y respuestas 401/500 no se convierten en un rol docente. Este mecanismo debe reemplazarse por una consulta de identidad explícita si el backend amplía los roles o cambia su política.

El backend actual reserva `/api/**` al administrador, salvo `/api/csrf`. Por eso el docente obtiene una pantalla autenticada de cuenta, con una explicación clara de que sus consultas personales todavía no están habilitadas. No se reutilizan permisos administrativos ni se permite navegar a esos paneles con el guard del docente.

El proxy de desarrollo y un proxy inverso en producción mantienen la API en el mismo origen. No se modificó CORS, la configuración de seguridad ni la lógica académica del backend. Los enlaces externos y recursos fuera de la ruta de API no reciben credenciales del interceptor. El despliegue real deberá usar HTTPS.

## Pruebas y evidencia

- `npm ci`: instalación correcta con el lockfile existente.
- `npm run build`: correcto; archivos de producción en `dist/frontend/browser`, paquete inicial de aproximadamente 407 kB sin comprimir.
- Compilación TypeScript de aplicación y pruebas: correcta.
- Jasmine/Karma: **24 pruebas ejecutadas y aprobadas**, proceso con código de salida 0. El navegador integrado se conectó al servidor Karma porque el lanzador automático de Edge falló dentro del entorno restringido de Windows. Al cerrar Karma aparece un aviso de desconexión posterior al resumen de éxito, sin fallos de pruebas.
- Contratos comprobados: credenciales rechazadas, identificación de administrador y docente limitado, manejo de errores de permisos, envío de Basic/CSRF/cookies, exclusión de cabeceras en otros destinos, respuestas de texto, parámetros de consulta y generación por grupo.
- Formularios: conservación de datos al guardar, reglas condicionales de requisitos, textos vacíos y límite de contraseña UTF-8.
- Agenda: intervalos adyacentes, cruces, cancelaciones, disposición de sesiones simultáneas y conservación de identidad DOM durante el arrastre. Se corrigió la recreación de elementos que interrumpía el gesto; la prueba protege ese comportamiento.
- Comprobación visual con servidor HTTP de pruebas **externo al proyecto entregado**: login de administrador y docente, validación de campos, menú, edición de docente, alta de plan, ficha de formación/experiencia, configuración, error HTTP 409 al ajustar una sesión, guardado aceptado, generación por grupo, arrastre con confirmación y cancelación que conserva la sesión original.
- Responsividad: escritorio y 390 × 844; formularios y navegación adaptados, tablas con desplazamiento interno.

El backend real no respondió en `http://localhost:8080/actuator/health` durante la verificación. Las comprobaciones HTTP visuales usaron datos sintéticos identificados como pruebas; no constituyen una validación integral con Spring Boot y PostgreSQL. El simulador y sus credenciales no se incluyen en la aplicación, el ZIP ni la compilación de producción. La configuración entregada apunta al backend real en el puerto 8080.

Capturas: `login.jpg` y `login-movil.jpg` muestran el acceso sin datos de usuario. `planeacion-pruebas.jpg`, `horarios-pruebas.jpg` y `ficha-movil-pruebas.jpg` documentan exclusivamente el servidor desechable de verificación, con datos sintéticos.

## Pendientes reales

1. Levantar el backend con su base de datos y cuentas existentes, y verificar el flujo completo con información real. Esta tarea no configuró ni inició una base de datos.
2. Proporcionar consultas y permisos de docente para sus propios datos y horarios. El frontend no puede habilitar operaciones que el servidor devuelve como 403.
3. Incorporar un endpoint explícito de identidad/roles y, si se requiere invalidar la sesión del servidor, uno de logout.
4. Definir e implementar en el backend los procesos del prototipo aún ausentes: comparación de alternativas, publicación, historial, periodos y disponibilidad personal. Tampoco se añadió gestión de aulas.
5. Facilitar una copia accesible de PFinalFront si se necesita una comparación exacta con su estructura de archivos.

## Ejecución y traslado

Consultar README.md. Para usarlo en la otra copia del repositorio, respaldar los cambios propios y sustituir el frontend anterior por esta versión; luego ejecutar `npm ci` y `npm start`. No mezclar las entradas standalone del scaffold anterior con AppModule. El backend se mantuvo idéntico al estado que tenía al iniciar esta tarea, comprobado mediante hashes de sus archivos.

Referencias técnicas oficiales: [NgModules y bootstrapModule](https://angular.dev/guide/ngmodules), [HttpClient en NgModules](https://angular.dev/guide/http/setup) y [compatibilidad de Angular 20.3 con Node y TypeScript](https://angular.dev/reference/versions).
