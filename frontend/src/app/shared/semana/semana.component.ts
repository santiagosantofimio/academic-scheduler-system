import { Component, Input, Output, EventEmitter, OnChanges } from '@angular/core';
import {
  DatosPlaneacion,
  DIAS,
  DiaSemana,
  SesionHorarioDTO,
  minutos,
  hora,
} from '../../interfaces/api.models';
interface SesionVisual {
  sesion: SesionHorarioDTO;
  top: number;
  height: number;
  left: number;
  width: number;
  title: string;
  subtitle: string;
  docente: string;
  color: number;
}
export interface MovimientoSesion {
  sesion: SesionHorarioDTO;
  diaSemana: DiaSemana;
  horaInicio: string;
  horaFin: string;
}
@Component({ selector: 'app-semana', standalone: false, templateUrl: './semana.component.html' })
export class SemanaComponent implements OnChanges {
  @Input() data: DatosPlaneacion | null = null;
  @Input() sessions: SesionHorarioDTO[] = [];
  @Input() editable = false;
  @Output() readonly selectSession = new EventEmitter<SesionHorarioDTO>();
  @Output() readonly moveSession = new EventEmitter<MovimientoSesion>();
  readonly dias = DIAS;
  readonly hora = hora;
  readonly scale = 1.05;
  start = 0;
  end = 0;
  ticks: number[] = [];
  days: SesionVisual[][] = [];
  ngOnChanges(): void {
    const config = this.data?.configuracion;
    const starts = this.sessions.map((s) => minutos(s.horaInicio));
    const ends = this.sessions.map((s) => minutos(s.horaFin));
    if (config) {
      starts.push(minutos(config.horaInicioSemana), minutos(config.horaInicioSabado));
      ends.push(minutos(config.horaFinSemana), minutos(config.horaFinSabado));
    }
    if (!starts.length || !ends.length) {
      this.ticks = [];
      this.days = [];
      return;
    }
    this.start = Math.floor(Math.min(...starts) / 60) * 60;
    this.end = Math.ceil(Math.max(...ends) / 60) * 60;
    this.ticks = Array.from(
      { length: (this.end - this.start) / 60 },
      (_, i) => this.start + i * 60,
    );
    this.days = this.dias.map((d) => {
      const sessions = this.sessions
        .filter((s) => s.diaSemana === d.valor)
        .sort((a, b) => minutos(a.horaInicio) - minutos(b.horaInicio));
      const laneEnds: number[] = [];
      const assigned = sessions.map((s) => {
        let lane = laneEnds.findIndex((end) => end <= minutos(s.horaInicio));
        if (lane < 0) lane = laneEnds.length;
        laneEnds[lane] = minutos(s.horaFin);
        return { s, lane };
      });
      return assigned.map(({ s, lane }) => {
        const group = this.data?.grupos.find((g) => g.idGrupo === s.grupoId);
        const subject = this.data?.asignaturas.find((a) => a.idAsginatura === group?.asignaturaId);
        const teacher = this.data?.docentes.find((t) => t.id === group?.docenteId);
        return {
          sesion: s,
          top: (minutos(s.horaInicio) - this.start) * this.scale,
          height: Math.max(22, (minutos(s.horaFin) - minutos(s.horaInicio)) * this.scale - 3),
          left: (lane / laneEnds.length) * 100,
          width: 100 / laneEnds.length,
          title: subject?.nombre || `Grupo #${s.grupoId}`,
          subtitle: `Grupo ${group?.numeroGrupo || s.grupoId}`,
          docente: teacher ? `${teacher.nombre} ${teacher.apellido}` : 'Sin docente asignado',
          color: (group?.asignaturaId || s.grupoId) % 4,
        };
      });
    });
  }
  get height(): number {
    return (this.end - this.start) * this.scale;
  }
  get breakTop(): number {
    return this.data?.configuracion?.horaInicioDescanso
      ? (minutos(this.data.configuracion.horaInicioDescanso) - this.start) * this.scale
      : 0;
  }
  get breakHeight(): number {
    const c = this.data?.configuracion;
    return c?.horaInicioDescanso && c.horaFinDescanso
      ? (minutos(c.horaFinDescanso) - minutos(c.horaInicioDescanso)) * this.scale
      : 0;
  }
  blockedTop(day: number): number {
    const c = this.data?.configuracion;
    return c
      ? (minutos(day === 5 ? c.horaInicioSabado : c.horaInicioSemana) - this.start) * this.scale
      : 0;
  }
  blockedBottom(day: number): number {
    const c = this.data?.configuracion;
    return c ? (this.end - minutos(day === 5 ? c.horaFinSabado : c.horaFinSemana)) * this.scale : 0;
  }
  trackSesion(_index: number, item: SesionVisual): number {
    return item.sesion.idSesion!;
  }
  drag(event: DragEvent, session: SesionHorarioDTO): void {
    if (this.editable && session.idSesion)
      event.dataTransfer?.setData('text/plain', String(session.idSesion));
  }
  allowDrop(event: DragEvent): void {
    if (this.editable) event.preventDefault();
  }
  drop(event: DragEvent, dia: DiaSemana): void {
    if (!this.editable) return;
    event.preventDefault();
    const id = Number(event.dataTransfer?.getData('text/plain'));
    const session = this.sessions.find((s) => s.idSesion === id);
    if (!session) return;
    const element = event.currentTarget as HTMLElement;
    const duration = minutos(session.horaFin) - minutos(session.horaInicio);
    const inicio = Math.max(
      this.start,
      Math.min(
        this.end - duration,
        Math.round((event.clientY - element.getBoundingClientRect().top) / this.scale) + this.start,
      ),
    );
    this.moveSession.emit({
      sesion: session,
      diaSemana: dia,
      horaInicio: hora(inicio),
      horaFin: hora(inicio + duration),
    });
  }
}
