import { DatosPlaneacion, GrupoDTO } from '../interfaces/api.models';

export function numeroGrupoVisible(numero: string | number): string {
  return String(numero).trim().replace(/\s+(?:diurno|nocturno)$/i, '');
}

export function detalleGrupo(data: DatosPlaneacion | null, id: number) {
  const grupo = data?.grupos.find((registro) => registro.idGrupo === id);
  const asignatura = data?.asignaturas.find((registro) => registro.idAsginatura === grupo?.asignaturaId);
  return {
    asignatura: asignatura?.nombre || 'Asignatura',
    numero: numeroGrupoVisible(grupo?.numeroGrupo || id),
  };
}

export interface GruposPorSemestre {
  semestre: number | null;
  grupos: GrupoDTO[];
}

export function agruparGruposPorSemestre({ asignaturas, grupos }: DatosPlaneacion): GruposPorSemestre[] {
  const asignaturasPorId = new Map(
    asignaturas.map((asignatura) => [asignatura.idAsginatura, asignatura]),
  );
  const gruposPorSemestre = new Map<number | null, GrupoDTO[]>();
  for (const grupo of grupos) {
    const semestre = asignaturasPorId.get(grupo.asignaturaId)?.semestre ?? null;
    const gruposDelSemestre = gruposPorSemestre.get(semestre) ?? [];
    gruposDelSemestre.push(grupo);
    gruposPorSemestre.set(semestre, gruposDelSemestre);
  }

  const orden = new Intl.Collator('es', { numeric: true });
  return [...gruposPorSemestre]
    .sort(
      ([primero], [segundo]) =>
        (primero ?? Number.MAX_SAFE_INTEGER) - (segundo ?? Number.MAX_SAFE_INTEGER),
    )
    .map(([semestre, gruposDelSemestre]) => ({
      semestre,
      grupos: gruposDelSemestre.sort((primero, segundo) => {
        const codigoPrimero = asignaturasPorId.get(primero.asignaturaId)?.codigo ?? '';
        const codigoSegundo = asignaturasPorId.get(segundo.asignaturaId)?.codigo ?? '';
        return (
          orden.compare(codigoPrimero, codigoSegundo) ||
          orden.compare(primero.numeroGrupo, segundo.numeroGrupo)
        );
      }),
    }));
}
