export type Rol = 'ADMINISTRADOR' | 'DOCENTE';
export type TipoVinculacion =
  | 'TIEMPO_COMPLETO'
  | 'TRES_CUARTOS_TIEMPO'
  | 'MEDIO_TIEMPO'
  | 'CUARTO_TIEMPO';
export type NivelFormacion = 'PREGRADO' | 'ESPECIALIZACION' | 'MAESTRIA' | 'DOCTORADO';
export type TipoExperiencia = 'LABORAL' | 'DOCENTE' | 'INVESTIGATIVA';
export type TipoRequisito =
  | 'FORMACION_ACADEMICA'
  | 'EXPERIENCIA_LABORAL'
  | 'EXPERIENCIA_DOCENTE'
  | 'EXPERIENCIA_INVESTIGATIVA';
export type DiaSemana =
  | 'MONDAY'
  | 'TUESDAY'
  | 'WEDNESDAY'
  | 'THURSDAY'
  | 'FRIDAY'
  | 'SATURDAY'
  | 'SUNDAY';
export interface UsuarioDTO {
  id?: number;
  nombre: string;
  apellido: string;
  numeroDocumento: string;
  correoInstitucional: string;
  contrasenia?: string | null;
  estado: boolean;
}
export interface AdministradorDTO extends UsuarioDTO {
  cargo: string;
}
export interface DocenteDTO extends UsuarioDTO {
  idDocente?: number;
  tipoVinculacion: TipoVinculacion;
  escalafon: string;
  especialidad: string;
  horasMaximasDocenciaSemanal: number;
}
export interface PlanEstudiosDTO {
  idPlan?: number;
  nombrePrograma: string;
  codigoPrograma: string;
  versionPlan: string;
  numerosSemestres: number;
  estado: boolean;
}
export interface AsignaturaDTO {
  // Se conserva la grafia del contrato Java.
  idAsginatura?: number;
  codigo: string;
  nombre: string;
  semestre: number;
  creditos: number;
  sesionesSemanales: number;
  areaConocimiento: string;
  estado: boolean;
  planEstudiosId: number;
}
export interface GrupoDTO {
  idGrupo?: number;
  numeroGrupo: string;
  estado: 'ACTIVO' | 'INACTIVO';
  asignaturaId: number;
  docenteId: number | null;
}
export interface FormacionAcademicaDTO {
  idFormacion?: number;
  tituloObtenido: string;
  nivelFormacion: NivelFormacion;
  institucion: string;
  areaConocimiento: string;
  anioGraduacion: number;
  docenteId: number;
}
export interface ExperienciaDocenteDTO {
  idExperiencia?: number;
  tipoExperiencia: TipoExperiencia;
  institucion: string;
  cargo: string;
  areaConocimiento: string;
  descripcion: string;
  fechaInicio: string;
  fechaFin: string | null;
  docenteId: number;
}
export interface RequisitoAsignaturaDTO {
  idRequisito?: number;
  tipoRequisito: TipoRequisito;
  descripcion: string;
  areaConocimiento: string;
  nivelFormacionMinimo: NivelFormacion | null;
  aniosExperienciaMinima: number;
  asignaturaId: number;
}
export interface SesionHorarioDTO {
  idSesion?: number;
  diaSemana: DiaSemana;
  horaInicio: string;
  horaFin: string;
  estado: 'PROGRAMADA' | 'CANCELADA';
  grupoId: number;
}
export interface ConfiguracionHorariosDTO {
  idConfiguracion?: number;
  horaInicioSemana: string;
  horaFinSemana: string;
  horaInicioSabado: string;
  horaFinSabado: string;
  horaInicioDescanso: string | null;
  horaFinDescanso: string | null;
  duracionSesionMinutos: number;
  maxSesionesSemanales: number;
  evitarDiasConsecutivos: boolean;
}
export interface TokenCsrf {
  headerName: string;
  parameterName: string;
  token: string;
}
export interface SesionUsuario {
  correo: string;
  nombre: string;
  rol: Rol;
}
export interface DatosPlaneacion {
  docentes: DocenteDTO[];
  planes: PlanEstudiosDTO[];
  asignaturas: AsignaturaDTO[];
  grupos: GrupoDTO[];
  sesiones: SesionHorarioDTO[];
  configuracion: ConfiguracionHorariosDTO | null;
}
export const DIAS: { valor: DiaSemana; nombre: string; corto: string }[] = [
  { valor: 'MONDAY', nombre: 'Lunes', corto: 'Lun' },
  { valor: 'TUESDAY', nombre: 'Martes', corto: 'Mar' },
  { valor: 'WEDNESDAY', nombre: 'Miércoles', corto: 'Mié' },
  { valor: 'THURSDAY', nombre: 'Jueves', corto: 'Jue' },
  { valor: 'FRIDAY', nombre: 'Viernes', corto: 'Vie' },
  { valor: 'SATURDAY', nombre: 'Sábado', corto: 'Sáb' },
];
export const VINCULACIONES: TipoVinculacion[] = [
  'TIEMPO_COMPLETO',
  'TRES_CUARTOS_TIEMPO',
  'MEDIO_TIEMPO',
  'CUARTO_TIEMPO',
];
export const NIVELES: NivelFormacion[] = ['PREGRADO', 'ESPECIALIZACION', 'MAESTRIA', 'DOCTORADO'];
export const EXPERIENCIAS: TipoExperiencia[] = ['LABORAL', 'DOCENTE', 'INVESTIGATIVA'];
export const REQUISITOS: TipoRequisito[] = [
  'FORMACION_ACADEMICA',
  'EXPERIENCIA_LABORAL',
  'EXPERIENCIA_DOCENTE',
  'EXPERIENCIA_INVESTIGATIVA',
];
export function etiqueta(valor: unknown): string {
  if (valor === null || valor === undefined || valor === '') return '—';
  if (typeof valor === 'boolean') return valor ? 'Activo' : 'Inactivo';
  return String(valor)
    .replaceAll('_', ' ')
    .toLocaleLowerCase('es')
    .replace(/^./, (x) => x.toLocaleUpperCase('es'));
}
export function minutos(hora: string): number {
  const [h, m, s = '0'] = hora.split(':');
  return Number(h) * 60 + Number(m) + Number(s) / 60;
}
export function hora(minuto: number): string {
  return `${Math.floor(minuto / 60)
    .toString()
    .padStart(2, '0')}:${Math.floor(minuto % 60)
    .toString()
    .padStart(2, '0')}`;
}
