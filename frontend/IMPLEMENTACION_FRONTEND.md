# Implementación del frontend

Aplicación Angular 20.3 modular para SIS GEN, conectada a la API del backend.

## Estructura

```text
src/app
├── guards          AuthGuard y AdminGuard
├── interceptors    Credenciales, token CSRF y manejo de sesión vencida
├── interfaces      Contratos de la API
├── modules
│   ├── admin       Inicio, docentes, catálogos, configuración, horarios, validaciones
│   ├── auth        Inicio de sesión
│   └── layout      Menú, cuenta y Mi horario
├── services        Acceso a la API y revisión local del horario
└── shared          Agenda semanal, formulario por metadatos, diálogos e iconos
```

Los módulos Auth, Layout y Admin se cargan de forma diferida. Los catálogos comparten un formulario definido por metadatos en `catalogo-definitions.ts`.

## Funciones

| Área | Funciones |
|---|---|
| Acceso | Formulario reactivo, HTTP Basic, token CSRF e identidad obtenida de `GET /api/usuarios/actual`. |
| Navegación | Menú según rol, guards y menú lateral adaptable. |
| Inicio | Cantidades, avance por grupos y agenda semanal. |
| Docentes | Listado, ficha, formación, experiencia y disponibilidad. |
| Catálogos | Planes, asignaturas, grupos con cupo, requisitos y administradores. |
| Configuración | Franjas, descanso, duración, sesiones máximas, días consecutivos y límites de cupo. |
| Horarios | Vista semanal y lista, filtros por grupo y docente, edición y cancelación. |
| Generación | Generación por grupo, conservando las sesiones existentes. |
| Ajuste visual | El arrastre propone día y hora; solo guardar envía el cambio. |
| Validaciones | Revisión local de cruces de docente y grupo, franjas, duración, cantidad, carga y días consecutivos. La decisión final es del servidor. |
| Exportación | Descarga de CSV y PDF con los filtros activos. |
| Mi horario | Vista de solo lectura del horario propio para el rol Docente, con exportación. Usa `GET /api/mi-horario`. |

## Sistema visual

- **Color:** un solo acento, el teal de la malla curricular de la Universidad El Bosque, sobre neutros fríos. Rojo, ámbar y verde se reservan para estados (error, advertencia, éxito). Los cuatro tonos de las sesiones distinguen asignaturas.
- **Modo claro y oscuro:** todos los colores son variables de `styles.css` y cambian solos según `prefers-color-scheme`.
- **Tipografía:** fuente del sistema, con tracking negativo en títulos y cifras tabulares en tablas, horas y totales.
- **Forma:** controles con radio de 10px, paneles con 14px y etiquetas en píldora.
- **Movimiento:** respuesta al presionar botones, diálogos y menú móvil con curva tipo resorte, y barra superior translúcida. Con `prefers-reduced-motion`, `prefers-reduced-transparency` o `prefers-contrast` se quitan animaciones, transparencias o se refuerzan bordes.

## Autenticación

Las credenciales se conservan solo en memoria: recargar la página exige ingresar de nuevo. El interceptor añade credenciales y `X-Requested-With` únicamente a las rutas de la API y envía el token CSRF en las escrituras. Un 401 durante la sesión limpia el estado y vuelve al inicio de sesión.

El rol viene del servidor. El rol Administrador usa las pantallas de gestión. El rol Docente solo accede a Mi horario y a su cuenta; cualquier ruta administrativa lo redirige a Mi horario. Estos guards complementan la autorización del servidor.

## Pruebas

```bash
npm run build
npm test -- --watch=false
```

Las pruebas usan Jasmine y Karma con ChromeHeadless. Si no hay Chrome instalado, definir `CHROME_BIN` con un navegador Chromium.

## Pendientes

- Disponibilidad docente en la revisión local. Hoy solo la comprueba el servidor al guardar o generar.
- Comparación de alternativas, publicación e historial de horarios, que no tienen soporte en el backend.
