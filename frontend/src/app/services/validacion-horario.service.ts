import { Injectable } from '@angular/core';
import { DatosPlaneacion, DIAS, minutos } from '../interfaces/api.models';
export interface ObservacionHorario {
  title: string;
  detail: string;
  sesiones: number[];
}
@Injectable({ providedIn: 'root' })
export class ValidacionHorarioService {
  revisar(data: DatosPlaneacion): ObservacionHorario[] {
    const result: ObservacionHorario[] = [];
    const sessions = data.sesiones.filter((s) => s.estado === 'PROGRAMADA');
    const config = data.configuracion;
    const groupName = (id: number): string => {
      const g = data.grupos.find((x) => x.idGrupo === id);
      const a = data.asignaturas.find((x) => x.idAsginatura === g?.asignaturaId);
      return `${a?.nombre || 'Asignatura'} · Grupo ${g?.numeroGrupo || id}`;
    };
    for (let i = 0; i < sessions.length; i++) {
      const a = sessions[i];
      const grupoA = data.grupos.find((g) => g.idGrupo === a.grupoId);
      for (let j = i + 1; j < sessions.length; j++) {
        const b = sessions[j];
        const grupoB = data.grupos.find((g) => g.idGrupo === b.grupoId);
        if (
          a.diaSemana !== b.diaSemana ||
          minutos(a.horaInicio) >= minutos(b.horaFin) ||
          minutos(b.horaInicio) >= minutos(a.horaFin)
        )
          continue;
        const mismoGrupo = a.grupoId === b.grupoId;
        const mismoDocente = !!grupoA?.docenteId && grupoA.docenteId === grupoB?.docenteId;
        if (mismoGrupo || mismoDocente)
          result.push({
            title: mismoGrupo ? 'Cruce de sesiones del grupo' : 'Cruce de docente',
            detail: `${groupName(a.grupoId)} y ${groupName(b.grupoId)} se superponen el ${DIAS.find((d) => d.valor === a.diaSemana)?.nombre.toLowerCase() || a.diaSemana}.`,
            sesiones: [a.idSesion!, b.idSesion!],
          });
      }
      if (config) {
        const inicio = minutos(a.horaInicio),
          fin = minutos(a.horaFin);
        const sabado = a.diaSemana === 'SATURDAY';
        const fuera =
          a.diaSemana === 'SUNDAY' ||
          inicio < minutos(sabado ? config.horaInicioSabado : config.horaInicioSemana) ||
          fin > minutos(sabado ? config.horaFinSabado : config.horaFinSemana);
        const descanso =
          config.horaInicioDescanso &&
          config.horaFinDescanso &&
          inicio < minutos(config.horaFinDescanso) &&
          fin > minutos(config.horaInicioDescanso);
        if (fuera || descanso)
          result.push({
            title: 'Franja institucional incumplida',
            detail: `${groupName(a.grupoId)} tiene una sesión fuera de la franja o durante el descanso.`,
            sesiones: [a.idSesion!],
          });
        if (Math.abs(fin - inicio - config.duracionSesionMinutos) > 0.001)
          result.push({
            title: 'Duración de sesión diferente',
            detail: `${groupName(a.grupoId)} no coincide con la duración configurada.`,
            sesiones: [a.idSesion!],
          });
      }
    }
    for (const group of data.grupos) {
      const propias = sessions.filter((s) => s.grupoId === group.idGrupo);
      if (!propias.length) continue;
      const subject = data.asignaturas.find((a) => a.idAsginatura === group.asignaturaId);
      if (!group.docenteId)
        result.push({
          title: 'Sesiones sin docente asignado',
          detail: groupName(group.idGrupo!),
          sesiones: propias.map((s) => s.idSesion!),
        });
      if (
        propias.length > (subject?.sesionesSemanales ?? Infinity) ||
        (config && propias.length > config.maxSesionesSemanales)
      )
        result.push({
          title: 'Cantidad de sesiones excedida',
          detail: groupName(group.idGrupo!),
          sesiones: propias.map((s) => s.idSesion!),
        });
      const dias = [
        ...new Set(propias.map((s) => DIAS.findIndex((d) => d.valor === s.diaSemana))),
      ].sort((a, b) => a - b);
      if (config?.evitarDiasConsecutivos && dias.some((d, i) => i > 0 && d - dias[i - 1] === 1))
        result.push({
          title: 'Sesiones en días consecutivos',
          detail: groupName(group.idGrupo!),
          sesiones: propias.map((s) => s.idSesion!),
        });
    }
    for (const teacher of data.docentes) {
      const ids = data.grupos.filter((g) => g.docenteId === teacher.id).map((g) => g.idGrupo);
      const propias = sessions.filter((s) => ids.includes(s.grupoId));
      const carga =
        propias.reduce((sum, s) => sum + minutos(s.horaFin) - minutos(s.horaInicio), 0) / 60;
      if (carga > teacher.horasMaximasDocenciaSemanal)
        result.push({
          title: 'Capacidad semanal excedida',
          detail: `${teacher.nombre} ${teacher.apellido}: ${carga.toFixed(1)} de ${teacher.horasMaximasDocenciaSemanal} horas permitidas.`,
          sesiones: propias.map((s) => s.idSesion!),
        });
    }
    return result;
  }
}
