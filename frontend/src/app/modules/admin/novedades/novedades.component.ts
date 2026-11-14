import { Component, OnInit } from '@angular/core';
import { finalize, forkJoin } from 'rxjs';
import { AcademicoService } from '../../../services/academico.service';
import { mensajeError } from '../../../services/api-error';
import {
  ClaseAfectadaDTO,
  DIAS,
  DiaSemana,
  DocenteDTO,
  FranjaDTO,
  NovedadDocenteDTO,
  TipoNovedad,
} from '../../../interfaces/api.models';

@Component({
  selector: 'app-novedades',
  standalone: false,
  templateUrl: './novedades.component.html',
})
export class NovedadesComponent implements OnInit {
  loading = true;
  busy = false;
  error = '';
  success = '';
  docentes: DocenteDTO[] = [];
  novedades: NovedadDocenteDTO[] = [];
  abierta: number | null = null;
  afectadas: ClaseAfectadaDTO[] = [];
  cargandoAfectadas = false;
  readonly dias = DIAS;
  readonly tipos: { valor: TipoNovedad; nombre: string }[] = [
    { valor: 'INCAPACIDAD', nombre: 'Incapacidad' },
    { valor: 'CAMBIO_DISPONIBILIDAD', nombre: 'Cambio de disponibilidad' },
    { valor: 'OTRA', nombre: 'Otra' },
  ];
  form = this.vacio();
  constructor(private readonly api: AcademicoService) {}
  ngOnInit(): void {
    this.load();
  }
  load(): void {
    this.loading = true;
    this.error = '';
    forkJoin({ docentes: this.api.docentes.listar(), novedades: this.api.novedades() })
      .pipe(finalize(() => (this.loading = false)))
      .subscribe({
        next: (data) => {
          this.docentes = data.docentes;
          this.novedades = [...data.novedades].sort(
            (a, b) => Number(a.estado === 'CERRADA') - Number(b.estado === 'CERRADA'),
          );
        },
        error: (error) => (this.error = mensajeError(error)),
      });
  }
  get formularioValido(): boolean {
    const f = this.form;
    if (!f.docenteId || !f.tipo || !f.diaSemana) return false;
    if (!f.horaInicio && !f.horaFin) return true;
    return !!f.horaInicio && !!f.horaFin && f.horaInicio < f.horaFin;
  }
  registrar(): void {
    if (!this.formularioValido) return;
    const f = this.form;
    const data: NovedadDocenteDTO = {
      docenteId: Number(f.docenteId),
      tipo: f.tipo,
      diaSemana: f.diaSemana,
      horaInicio: f.horaInicio ? this.conSegundos(f.horaInicio) : null,
      horaFin: f.horaFin ? this.conSegundos(f.horaFin) : null,
      descripcion: f.descripcion.trim() || null,
    };
    this.error = '';
    this.success = '';
    this.busy = true;
    this.api
      .registrarNovedad(data)
      .pipe(finalize(() => (this.busy = false)))
      .subscribe({
        next: (n) => {
          this.success = `Novedad registrada para ${n.docente}. Mientras siga abierta, no se le programarán clases en esa franja.`;
          this.form = this.vacio();
          this.load();
          if (n.idNovedad) this.verAfectadas(n.idNovedad);
        },
        error: (error) => (this.error = mensajeError(error)),
      });
  }
  verAfectadas(id: number): void {
    if (this.abierta === id && !this.cargandoAfectadas) {
      this.abierta = null;
      return;
    }
    this.abierta = id;
    this.afectadas = [];
    this.cargandoAfectadas = true;
    this.api
      .clasesAfectadas(id)
      .pipe(finalize(() => (this.cargandoAfectadas = false)))
      .subscribe({
        next: (clases) => (this.afectadas = clases),
        error: (error) => (this.error = mensajeError(error)),
      });
  }
  mover(clase: ClaseAfectadaDTO, franja: FranjaDTO): void {
    const id = clase.sesion.idSesion;
    if (!id || this.abierta === null) return;
    const novedad = this.abierta;
    this.error = '';
    this.success = '';
    this.busy = true;
    this.api.sesiones
      .actualizar(id, {
        ...clase.sesion,
        diaSemana: franja.diaSemana,
        horaInicio: franja.horaInicio,
        horaFin: franja.horaFin,
      })
      .pipe(finalize(() => (this.busy = false)))
      .subscribe({
        next: () => {
          this.success = `${clase.asignatura}, grupo ${clase.grupo}, quedó el ${this.franja(franja)}.`;
          this.abierta = null;
          this.verAfectadas(novedad);
        },
        error: (error) => (this.error = mensajeError(error)),
      });
  }
  cerrar(n: NovedadDocenteDTO): void {
    if (!n.idNovedad) return;
    this.error = '';
    this.success = '';
    this.busy = true;
    this.api
      .cerrarNovedad(n.idNovedad)
      .pipe(finalize(() => (this.busy = false)))
      .subscribe({
        next: () => {
          this.success = `Se cerró la novedad de ${n.docente}. Su franja vuelve a estar disponible.`;
          if (this.abierta === n.idNovedad) this.abierta = null;
          this.load();
        },
        error: (error) => (this.error = mensajeError(error)),
      });
  }
  nombreDia(dia: DiaSemana): string {
    return DIAS.find((d) => d.valor === dia)?.nombre || dia;
  }
  tipo(valor: TipoNovedad): string {
    return this.tipos.find((t) => t.valor === valor)?.nombre || valor;
  }
  franja(f: { diaSemana: DiaSemana; horaInicio: string | null; horaFin: string | null }): string {
    const dia = this.nombreDia(f.diaSemana).toLowerCase();
    return f.horaInicio && f.horaFin
      ? `${dia} de ${f.horaInicio.slice(0, 5)} a ${f.horaFin.slice(0, 5)}`
      : `${dia}, todo el día`;
  }
  fecha(valor?: string): string {
    return valor ? new Date(valor).toLocaleString('es-CO', { dateStyle: 'medium', timeStyle: 'short' }) : '';
  }
  private conSegundos(hora: string): string {
    return hora.length === 5 ? `${hora}:00` : hora;
  }
  private vacio() {
    return {
      docenteId: '' as number | '',
      tipo: 'INCAPACIDAD' as TipoNovedad,
      diaSemana: 'MONDAY' as DiaSemana,
      horaInicio: '',
      horaFin: '',
      descripcion: '',
    };
  }
}
