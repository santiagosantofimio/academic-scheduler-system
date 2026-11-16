import { Component, Input, Output, EventEmitter, OnChanges } from '@angular/core';
import {
  DatosPlaneacion,
  DIAS,
  DiaSemana,
  SesionHorarioDTO,
  minutos,
  hora,
} from '../../interfaces/api.models';
import { numeroGrupoVisible } from '../grupo';
interface SesionVisual {
  sesion: SesionHorarioDTO;
  top: number;
  height: number;
  left: number;
  width: number;
  title: string;
  numeroGrupo: string;
  docente: string;
  color: number;
}
export interface MovimientoSesion {
  sesion: SesionHorarioDTO;
  diaSemana: DiaSemana;
  horaInicio: string;
  horaFin: string;
}
interface DestinoVisual {
  movimiento: MovimientoSesion;
  top: number;
  height: number;
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
  dropPreview: DestinoVisual | null = null;
  private draggedSession: { id: number; grabOffset: number } | null = null;
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
          numeroGrupo: numeroGrupoVisible(group?.numeroGrupo || s.grupoId),
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
    this.endDrag();
    const transfer = event.dataTransfer;
    if (!this.editable || session.idSesion == null || !transfer) {
      event.preventDefault();
      return;
    }
    const source = event.currentTarget as HTMLElement;
    this.draggedSession = {
      id: session.idSesion,
      grabOffset: Math.max(0, event.clientY - source.getBoundingClientRect().top),
    };
    transfer.effectAllowed = 'move';
    transfer.setData('application/x-scheduler-session', String(session.idSesion));
  }
  allowDrop(event: DragEvent, dia: DiaSemana): void {
    this.dropPreview = this.destino(event, dia);
    if (event.dataTransfer) event.dataTransfer.dropEffect = this.dropPreview ? 'move' : 'none';
    if (this.dropPreview) event.preventDefault();
  }
  leaveDrop(event: DragEvent, dia: DiaSemana): void {
    const column = event.currentTarget as HTMLElement;
    if (
      this.dropPreview?.movimiento.diaSemana === dia &&
      !column.contains(event.relatedTarget as Node | null)
    ) {
      this.dropPreview = null;
    }
  }
  endDrag(): void {
    this.draggedSession = null;
    this.dropPreview = null;
  }
  drop(event: DragEvent, dia: DiaSemana): void {
    const destino = this.destino(event, dia);
    this.endDrag();
    if (!destino) return;
    event.preventDefault();
    this.moveSession.emit(destino.movimiento);
  }
  private destino(event: DragEvent, dia: DiaSemana): DestinoVisual | null {
    const drag = this.draggedSession;
    if (!this.editable || !drag) return null;
    const session = this.sessions.find((s) => s.idSesion === drag.id);
    if (!session) return null;

    const column = event.currentTarget as HTMLElement;
    const offset = event.clientY - column.getBoundingClientRect().top - drag.grabOffset;
    const dropTop = this.start + Math.round((offset / this.scale) * 60) / 60;
    const config = this.data?.configuracion;
    const sabado = dia === 'SATURDAY';
    let apertura = config
      ? minutos(sabado ? config.horaInicioSabado : config.horaInicioSemana)
      : this.start;
    let cierre = config
      ? minutos(sabado ? config.horaFinSabado : config.horaFinSemana)
      : this.end;
    if (config?.horaInicioDescanso && config.horaFinDescanso) {
      const finDescanso = minutos(config.horaFinDescanso);
      if (dropTop >= finDescanso) apertura = Math.max(apertura, finDescanso);
      else cierre = Math.min(cierre, minutos(config.horaInicioDescanso));
    }
    if (dropTop < apertura || dropTop >= cierre) return null;

    const inicio = Math.max(apertura, Math.floor(dropTop / 60) * 60);
    const duracion = Math.round((minutos(session.horaFin) - minutos(session.horaInicio)) * 60) / 60;
    const fin = inicio + duracion;
    if (duracion <= 0 || fin > cierre) return null;

    return {
      movimiento: {
        sesion: session,
        diaSemana: dia,
        horaInicio: this.horaDestino(inicio),
        horaFin: this.horaDestino(fin),
      },
      top: (inicio - this.start) * this.scale,
      height: duracion * this.scale,
    };
  }
  private horaDestino(minuto: number): string {
    const totalSegundos = Math.round(minuto * 60);
    const base = hora(Math.floor(totalSegundos / 60));
    const segundos = totalSegundos % 60;
    return segundos ? base + ':' + String(segundos).padStart(2, '0') : base;
  }
}
