import { Campo, Opcion } from '../../../shared/registro-form/registro-form.component';
import { DatosPlaneacion, etiqueta, NIVELES, REQUISITOS } from '../../../interfaces/api.models';
export type CatalogoKey = 'planes' | 'asignaturas' | 'grupos' | 'requisitos' | 'administradores';
export interface Columna {
  key: string;
  label: string;
}
export interface CatalogoDefinicion {
  key: CatalogoKey;
  title: string;
  singular: string;
  description: string;
  idKey: string;
  icon: string;
  columns: Columna[];
  fields: Campo[];
}
export function opciones(values: readonly string[]): Opcion[] {
  return values.map((value) => ({ value, label: etiqueta(value) }));
}
export const campoTexto = (key: string, label: string, full = false): Campo => ({
  key,
  label,
  required: true,
  type: 'text',
  full,
});
export const campoNumero = (key: string, label: string, min = 1): Campo => ({
  key,
  label,
  type: 'number',
  required: true,
  min,
});
const estado: Campo = { key: 'estado', label: 'Registro activo', type: 'checkbox' };
export const CATALOGOS: Record<CatalogoKey, CatalogoDefinicion> = {
  planes: {
    key: 'planes',
    title: 'Planes de estudio',
    singular: 'plan de estudios',
    description: 'Organiza los programas, sus versiones y el número de semestres.',
    idKey: 'idPlan',
    icon: 'book',
    columns: [
      { key: 'nombrePrograma', label: 'Programa' },
      { key: 'codigoPrograma', label: 'Código' },
      { key: 'versionPlan', label: 'Versión' },
      { key: 'numerosSemestres', label: 'Semestres' },
      { key: 'estado', label: 'Estado' },
    ],
    fields: [
      campoTexto('nombrePrograma', 'Nombre del programa', true),
      campoTexto('codigoPrograma', 'Código del programa'),
      campoTexto('versionPlan', 'Versión del plan'),
      campoNumero('numerosSemestres', 'Número de semestres'),
      estado,
    ],
  },
  asignaturas: {
    key: 'asignaturas',
    title: 'Asignaturas',
    singular: 'asignatura',
    description: 'Define las asignaturas y las sesiones semanales de cada plan.',
    idKey: 'idAsginatura',
    icon: 'layers',
    columns: [
      { key: 'nombre', label: 'Asignatura' },
      { key: 'codigo', label: 'Código' },
      { key: 'planEstudiosId', label: 'Plan de estudios' },
      { key: 'semestre', label: 'Semestre' },
      { key: 'creditos', label: 'Créditos' },
      { key: 'sesionesSemanales', label: 'Sesiones / semana' },
      { key: 'estado', label: 'Estado' },
    ],
    fields: [
      campoTexto('codigo', 'Código'),
      campoTexto('nombre', 'Nombre'),
      {
        key: 'planEstudiosId',
        label: 'Plan de estudios',
        type: 'select',
        required: true,
        full: true,
      },
      campoNumero('semestre', 'Semestre'),
      campoNumero('creditos', 'Créditos'),
      campoNumero('sesionesSemanales', 'Sesiones semanales'),
      campoTexto('areaConocimiento', 'Área de conocimiento'),
      estado,
    ],
  },
  grupos: {
    key: 'grupos',
    title: 'Grupos',
    singular: 'grupo',
    description: 'Asocia las asignaturas a sus grupos y asigna un docente antes de generar.',
    idKey: 'idGrupo',
    icon: 'users',
    columns: [
      { key: 'numeroGrupo', label: 'Grupo' },
      { key: 'asignaturaId', label: 'Asignatura' },
      { key: 'docenteId', label: 'Docente asignado' },
      { key: 'estado', label: 'Estado' },
    ],
    fields: [
      campoTexto('numeroGrupo', 'Número de grupo'),
      {
        key: 'estado',
        label: 'Estado',
        type: 'select',
        required: true,
        options: opciones(['ACTIVO', 'INACTIVO']),
      },
      { key: 'asignaturaId', label: 'Asignatura', type: 'select', required: true, full: true },
      {
        key: 'docenteId',
        label: 'Docente',
        type: 'select',
        full: true,
        hint: 'Puedes registrar un grupo sin docente. Para generar su horario debes asignarle uno.',
      },
    ],
  },
  requisitos: {
    key: 'requisitos',
    title: 'Requisitos académicos',
    singular: 'requisito',
    description: 'Especifica la formación y experiencia que requiere cada asignatura.',
    idKey: 'idRequisito',
    icon: 'graduation',
    columns: [
      { key: 'asignaturaId', label: 'Asignatura' },
      { key: 'tipoRequisito', label: 'Tipo de requisito' },
      { key: 'areaConocimiento', label: 'Área' },
      { key: 'descripcion', label: 'Descripción' },
    ],
    fields: [
      { key: 'asignaturaId', label: 'Asignatura', type: 'select', required: true, full: true },
      {
        key: 'tipoRequisito',
        label: 'Tipo de requisito',
        type: 'select',
        required: true,
        options: opciones(REQUISITOS),
        full: true,
      },
      campoTexto('areaConocimiento', 'Área de conocimiento'),
      {
        key: 'nivelFormacionMinimo',
        label: 'Nivel mínimo de formación',
        type: 'select',
        options: opciones(NIVELES),
        hint: 'Obligatorio para requisitos de formación académica.',
      },
      campoNumero('aniosExperienciaMinima', 'Años mínimos de experiencia', 0),
      { key: 'descripcion', label: 'Descripción', type: 'textarea', required: true, full: true },
    ],
  },
  administradores: {
    key: 'administradores',
    title: 'Administradores',
    singular: 'administrador',
    description: 'Gestiona las cuentas que tienen acceso a la planeación académica.',
    idKey: 'id',
    icon: 'shield',
    columns: [
      { key: 'nombre', label: 'Nombre' },
      { key: 'apellido', label: 'Apellido' },
      { key: 'numeroDocumento', label: 'Documento' },
      { key: 'correoInstitucional', label: 'Correo institucional' },
      { key: 'cargo', label: 'Cargo' },
      { key: 'estado', label: 'Estado' },
    ],
    fields: [
      campoTexto('nombre', 'Nombre'),
      campoTexto('apellido', 'Apellido'),
      campoTexto('numeroDocumento', 'Número de documento'),
      { key: 'correoInstitucional', label: 'Correo institucional', type: 'email', required: true },
      campoTexto('cargo', 'Cargo'),
      {
        key: 'contrasenia',
        label: 'Contraseña',
        type: 'password',
        required: true,
        hint: 'En edición, deja este campo vacío para conservar la contraseña.',
      },
      estado,
    ],
  },
};
export type Referencias = Pick<DatosPlaneacion, 'planes' | 'asignaturas' | 'docentes'>;
export function camposCatalogo(
  def: CatalogoDefinicion,
  refs: Referencias,
  editando: boolean,
): Campo[] {
  return def.fields.map((campo) => {
    const field = { ...campo };
    if (field.key === 'planEstudiosId')
      field.options = refs.planes.map((p) => ({
        value: p.idPlan!,
        label: `${p.nombrePrograma} · ${p.versionPlan}`,
      }));
    if (field.key === 'asignaturaId')
      field.options = refs.asignaturas.map((a) => ({
        value: a.idAsginatura!,
        label: `${a.codigo} · ${a.nombre}`,
      }));
    if (field.key === 'docenteId')
      field.options = refs.docentes.map((d) => ({
        value: d.id!,
        label: `${d.nombre} ${d.apellido}${d.estado ? '' : ' (inactivo)'}`,
      }));
    if (field.key === 'contrasenia') field.required = !editando;
    return field;
  });
}
