import { Component, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { finalize } from 'rxjs';
import { AcademicoService } from '../../../services/academico.service';
import { mensajeError } from '../../../services/api-error';
import { DatosPlaneacion, DIAS, DiaSemana, SesionHorarioDTO } from '../../../interfaces/api.models';
import { MovimientoSesion } from '../../../shared/semana/semana.component';
@Component({
  selector: 'app-horarios',
  standalone: false,
  templateUrl: './horarios.component.html',
})
export class HorariosComponent implements OnInit {
  data: DatosPlaneacion | null = null;
  loading = true;
  busy = false;
  error = '';
  success = '';
  modalError = '';
  groupFilter = 0;
  teacherFilter = 0;
  listView = false;
  generationOpen = false;
  generationGroup: number | null = null;
  editorOpen = false;
  editing: SesionHorarioDTO | null = null;
  deleting: SesionHorarioDTO | null = null;
  readonly dias = DIAS;
  readonly form;
  constructor(
    private readonly api: AcademicoService,
    private readonly route: ActivatedRoute,
    fb: FormBuilder,
  ) {
    this.form = fb.group(
      {
        grupoId: [null as number | null, Validators.required],
        diaSemana: ['MONDAY' as DiaSemana, Validators.required],
        horaInicio: ['', Validators.required],
        horaFin: ['', Validators.required],
        estado: ['PROGRAMADA' as 'PROGRAMADA' | 'CANCELADA', Validators.required],
      },
      {
        validators: (control) =>
          control.value.horaInicio &&
          control.value.horaFin &&
          control.value.horaInicio >= control.value.horaFin
            ? { horas: true }
            : null,
      },
    );
  }
  ngOnInit(): void {
    this.load(true);
  }
  load(initial = false): void {
    this.loading = true;
    this.error = '';
    this.api
      .planeacion()
      .pipe(finalize(() => (this.loading = false)))
      .subscribe({
        next: (data) => {
          this.data = data;
          if (initial) {
            const id = Number(this.route.snapshot.queryParamMap.get('sesion'));
            const session = data.sesiones.find((s) => s.idSesion === id);
            if (session) this.open(session);
            if (this.route.snapshot.queryParamMap.has('generar')) this.generationOpen = true;
          }
        },
        error: (error) => (this.error = mensajeError(error)),
      });
  }
  get filtered(): SesionHorarioDTO[] {
    return (
      this.data?.sesiones.filter(
        (s) =>
          (!this.groupFilter || s.grupoId === Number(this.groupFilter)) &&
          (!this.teacherFilter ||
            this.data?.grupos.find((g) => g.idGrupo === s.grupoId)?.docenteId ===
              Number(this.teacherFilter)),
      ) || []
    );
  }
  get calendarSessions(): SesionHorarioDTO[] {
    return this.filtered.filter((s) => s.estado === 'PROGRAMADA');
  }
  get eligibleGroups() {
    return this.data?.grupos.filter((g) => g.estado === 'ACTIVO' && g.docenteId) || [];
  }
  grupo(id: number): string {
    const g = this.data?.grupos.find((x) => x.idGrupo === id);
    const a = this.data?.asignaturas.find((x) => x.idAsginatura === g?.asignaturaId);
    return `${a?.nombre || 'Asignatura'} · Grupo ${g?.numeroGrupo || id}`;
  }
  docente(id: number): string {
    const g = this.data?.grupos.find((x) => x.idGrupo === id);
    const d = this.data?.docentes.find((x) => x.id === g?.docenteId);
    return d ? `${d.nombre} ${d.apellido}` : 'Sin docente';
  }
  nombreDia(day: DiaSemana): string {
    return DIAS.find((d) => d.valor === day)?.nombre || 'Domingo';
  }
  open(session: SesionHorarioDTO | null = null): void {
    this.editing = session;
    this.modalError = '';
    this.form.reset(
      session
        ? { ...session }
        : {
            grupoId: Number(this.groupFilter) || null,
            diaSemana: 'MONDAY',
            horaInicio: '',
            horaFin: '',
            estado: 'PROGRAMADA',
          },
    );
    this.editorOpen = true;
  }
  move(m: MovimientoSesion): void {
    this.open(m.sesion);
    this.form.patchValue({ diaSemana: m.diaSemana, horaInicio: m.horaInicio, horaFin: m.horaFin });
  }
  save(): void {
    this.form.markAllAsTouched();
    if (this.form.invalid || this.busy) return;
    this.busy = true;
    this.modalError = '';
    const values = this.form.getRawValue();
    const payload: SesionHorarioDTO = {
      grupoId: Number(values.grupoId),
      diaSemana: values.diaSemana!,
      horaInicio: values.horaInicio!,
      horaFin: values.horaFin!,
      estado: values.estado!,
    };
    const editing = this.editing;
    const request = editing
      ? this.api.sesiones.actualizar(editing.idSesion!, payload)
      : this.api.sesiones.crear(payload);
    request.pipe(finalize(() => (this.busy = false))).subscribe({
      next: () => {
        this.editorOpen = false;
        this.success = 'La sesión se guardó y sus condiciones fueron comprobadas por el servidor.';
        this.load();
      },
      error: (error) => (this.modalError = mensajeError(error)),
    });
  }
  generate(): void {
    if (!this.generationGroup || this.busy || !this.data?.configuracion) return;
    this.busy = true;
    this.modalError = '';
    this.api
      .generar(Number(this.generationGroup))
      .pipe(finalize(() => (this.busy = false)))
      .subscribe({
        next: (sessions) => {
          const id = Number(this.generationGroup);
          if (this.data)
            this.data = {
              ...this.data,
              sesiones: [
                ...this.data.sesiones.filter((s) => s.grupoId !== id || s.estado !== 'PROGRAMADA'),
                ...sessions,
              ],
            };
          this.generationOpen = false;
          this.groupFilter = id;
          this.success = `El grupo tiene ${sessions.length} sesiones programadas. Las sesiones existentes se conservaron.`;
        },
        error: (error) => (this.modalError = mensajeError(error)),
      });
  }
  remove(): void {
    if (!this.deleting || this.busy) return;
    this.busy = true;
    this.modalError = '';
    const id = this.deleting.idSesion!;
    this.api.sesiones
      .eliminar(id)
      .pipe(finalize(() => (this.busy = false)))
      .subscribe({
        next: () => {
          if (this.data)
            this.data = {
              ...this.data,
              sesiones: this.data.sesiones.filter((s) => s.idSesion !== id),
            };
          this.deleting = null;
          this.success = 'La sesión se eliminó correctamente.';
        },
        error: (error) => (this.modalError = mensajeError(error)),
      });
  }
}
