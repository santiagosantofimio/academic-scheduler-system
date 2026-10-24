import { Campo } from '../../../shared/registro-form/registro-form.component';
import { campoNumero, campoTexto, opciones } from '../catalogos/catalogo-definitions';
import { EXPERIENCIAS, NIVELES, VINCULACIONES } from '../../../interfaces/api.models';
export function camposDocente(editando: boolean): Campo[] {
  return [
    campoTexto('nombre', 'Nombre'),
    campoTexto('apellido', 'Apellido'),
    campoTexto('numeroDocumento', 'Número de documento'),
    { key: 'correoInstitucional', label: 'Correo institucional', type: 'email', required: true },
    {
      key: 'tipoVinculacion',
      label: 'Tipo de vinculación',
      type: 'select',
      required: true,
      options: opciones(VINCULACIONES),
    },
    campoNumero('horasMaximasDocenciaSemanal', 'Horas máximas de docencia / semana', 0),
    campoTexto('escalafon', 'Escalafón'),
    campoTexto('especialidad', 'Especialidad'),
    {
      key: 'contrasenia',
      label: 'Contraseña',
      type: 'password',
      required: !editando,
      full: true,
      hint: editando
        ? 'Déjala vacía para conservar la contraseña actual.'
        : 'La contraseña se asigna a la cuenta del docente.',
    },
    { key: 'estado', label: 'Docente activo', type: 'checkbox' },
  ];
}
export const CAMPOS_FORMACION: Campo[] = [
  campoTexto('tituloObtenido', 'Título obtenido', true),
  {
    key: 'nivelFormacion',
    label: 'Nivel de formación',
    type: 'select',
    required: true,
    options: opciones(NIVELES),
  },
  {
    ...campoNumero('anioGraduacion', 'Año de graduación'),
    max: Number(
      new Intl.DateTimeFormat('en', { timeZone: 'America/Bogota', year: 'numeric' }).format(
        new Date(),
      ),
    ),
  },
  campoTexto('institucion', 'Institución'),
  campoTexto('areaConocimiento', 'Área de conocimiento'),
];
export const CAMPOS_EXPERIENCIA: Campo[] = [
  {
    key: 'tipoExperiencia',
    label: 'Tipo de experiencia',
    type: 'select',
    required: true,
    options: opciones(EXPERIENCIAS),
  },
  campoTexto('institucion', 'Institución'),
  campoTexto('cargo', 'Cargo'),
  campoTexto('areaConocimiento', 'Área de conocimiento'),
  { key: 'fechaInicio', label: 'Fecha inicial', type: 'date', required: true },
  {
    key: 'fechaFin',
    label: 'Fecha final',
    type: 'date',
    hint: 'Déjala vacía si la experiencia sigue vigente.',
  },
  { key: 'descripcion', label: 'Descripción', type: 'textarea', required: true, full: true },
];
