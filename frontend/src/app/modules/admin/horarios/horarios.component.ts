import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { finalize, Observable, Subscription } from 'rxjs';
import { AcademicoService } from '../../../services/academico.service';
import { mensajeError } from '../../../services/api-error';
import { ObservacionHorario, ValidacionHorarioService } from '../../../services/validacion-horario.service';
import {
  DatosPlaneacion,
  DIAS,
  DiaSemana,
  FormatoExportacion,
  SesionHorarioDTO,
} from '../../../interfaces/api.models';
import { guardarArchivo } from '../../../shared/descarga';
import { ordenHoras } from '../../../shared/form-validators';
import { agruparGruposPorSemestre, detalleGrupo, GruposPorSemestre } from '../../../shared/grupo';
import { MovimientoSesion } from '../../../shared/semana/semana.component';
@Component({
  selector: 'app-horarios',
  standalone: false,
  templateUrl: './horarios.component.html',
  styleUrl: './horarios.component.css',
})
export class HorariosComponent implements OnInit, OnDestroy {
  data: DatosPlaneacion | null = null;
  observaciones: ObservacionHorario[] = [];
  observacionesVisibles: ObservacionHorario[] = [];
  sesionesProgramadas = 0;
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
  gruposPorSemestre: GruposPorSemestre[] = [];
  gruposParaGenerar: GruposPorSemestre[] = [];
  editorOpen = false;
  editing: SesionHorarioDTO | null = null;
  deleting: SesionHorarioDTO | null = null;
  readonly dias = DIAS;
  readonly form;
  private readonly cambiosGrupo: Subscription;
  constructor(
    private readonly api: AcademicoService,
    private readonly route: ActivatedRoute,
    private readonly fb: FormBuilder,
    private readonly validacion: ValidacionHorarioService,
  ) {
    this.form = fb.group({
      grupoId: fb.control<number | null>(null, Validators.required),
      sesiones: fb.array<ReturnType<HorariosComponent['formularioSesion']>>([]),
    });
    this.cambiosGrupo = this.form.controls.grupoId.valueChanges.subscribe((grupoId) => {
      this.modalError = '';
      this.cargarSesionesPendientes(grupoId);
    });
  }
  ngOnInit(): void {
    this.load(true);
  }
  ngOnDestroy(): void {
    this.cambiosGrupo.unsubscribe();
  }
  get sesionesFormulario() {
    return this.form.controls.sesiones;
  }
  private formularioSesion(sesion?: SesionHorarioDTO) {
    return this.fb.group(
      {
        diaSemana: this.fb.control<DiaSemana | null>(sesion?.diaSemana ?? null, Validators.required),
        horaInicio: this.fb.nonNullable.control(sesion?.horaInicio ?? '', Validators.required),
        horaFin: this.fb.nonNullable.control(sesion?.horaFin ?? '', Validators.required),
        estado: this.fb.nonNullable.control<SesionHorarioDTO['estado']>(
          sesion?.estado ?? 'PROGRAMADA',
          Validators.required,
        ),
      },
      { validators: ordenHoras('horaInicio', 'horaFin') },
    );
  }
  private cargarSesionesPendientes(grupoId: number | null): void {
    this.sesionesFormulario.clear();
    if (!this.data || grupoId === null) return;
    const grupo = this.data.grupos.find((g) => g.idGrupo === grupoId);
    const asignatura = this.data.asignaturas.find((a) => a.idAsginatura === grupo?.asignaturaId);
    if (!asignatura) return;
    const programadas = this.data.sesiones.filter(
      (s) => s.grupoId === grupoId && s.estado === 'PROGRAMADA',
    ).length;
    for (let i = programadas; i < asignatura.sesionesSemanales; i++) {
      this.sesionesFormulario.push(this.formularioSesion());
    }
  }
  load(initial = false): void {
    this.loading = true;
    this.error = '';
    this.api
      .planeacion()
      .pipe(finalize(() => (this.loading = false)))
      .subscribe({
        next: (data) => {
          this.actualizarDatos(data);
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
  private actualizarDatos(data: DatosPlaneacion): void {
    this.data = data;
    this.gruposPorSemestre = agruparGruposPorSemestre(data);
    this.gruposParaGenerar = this.gruposPorSemestre
      .map(({ semestre, grupos }) => ({
        semestre,
        grupos: grupos.filter((grupo) => grupo.estado === 'ACTIVO' && grupo.docenteId),
      }))
      .filter(({ grupos }) => grupos.length > 0);
    this.sesionesProgramadas = data.sesiones.filter((s) => s.estado === 'PROGRAMADA').length;
    this.observaciones = this.validacion.revisar(data);
    this.observacionesVisibles = this.observaciones.slice(0, 3);
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
  grupo(id: number) {
    return detalleGrupo(this.data, id);
  }
  nombreGrupo(id: number): string {
    const { asignatura, numero } = this.grupo(id);
    return `${asignatura} · Grupo ${numero}`;
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
    const grupoId = session?.grupoId ?? (Number(this.groupFilter) || null);
    this.form.controls.grupoId.reset({ value: grupoId, disabled: !!session }, { emitEvent: false });
    if (session) {
      this.sesionesFormulario.clear();
      this.sesionesFormulario.push(this.formularioSesion(session));
    } else {
      this.cargarSesionesPendientes(grupoId);
    }
    this.editorOpen = true;
  }
  move(m: MovimientoSesion): void {
    this.open(m.sesion);
    this.sesionesFormulario.at(0).patchValue({
      diaSemana: m.diaSemana,
      horaInicio: m.horaInicio,
      horaFin: m.horaFin,
    });
  }
  save(): void {
    this.form.markAllAsTouched();
    if (this.form.invalid || this.busy || !this.sesionesFormulario.length) return;
    this.busy = true;
    this.modalError = '';
    const { grupoId, sesiones } = this.form.getRawValue();
    const payload: SesionHorarioDTO[] = sesiones.map((sesion) => ({
      ...sesion,
      grupoId: grupoId!,
      diaSemana: sesion.diaSemana!,
    }));
    const editing = this.editing;
    const request: Observable<unknown> = editing
      ? this.api.sesiones.actualizar(editing.idSesion!, payload[0])
      : this.api.agregarSesiones(grupoId!, payload);
    request.pipe(finalize(() => (this.busy = false))).subscribe({
      next: () => {
        this.editorOpen = false;
        this.success =
          payload.length === 1 ? 'La sesión se guardó.' : 'Las sesiones se guardaron.';
        this.load();
      },
      error: (error) => (this.modalError = mensajeError(error)),
    });
  }
  exportar(formato: FormatoExportacion): void {
    this.error = '';
    this.api
      .exportar(formato, {
        grupo: Number(this.groupFilter),
        docente: Number(this.teacherFilter),
      })
      .subscribe({
        next: (archivo) => guardarArchivo(archivo, `horario.${formato}`),
        error: (error) => (this.error = mensajeError(error)),
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
            this.actualizarDatos({
              ...this.data,
              sesiones: [
                ...this.data.sesiones.filter((s) => s.grupoId !== id || s.estado !== 'PROGRAMADA'),
                ...sessions,
              ],
            });
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
            this.actualizarDatos({
              ...this.data,
              sesiones: this.data.sesiones.filter((s) => s.idSesion !== id),
            });
          this.deleting = null;
          this.success = 'La sesión se eliminó correctamente.';
        },
        error: (error) => (this.modalError = mensajeError(error)),
      });
  }
}
