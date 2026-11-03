import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { forkJoin, finalize } from 'rxjs';
import { AcademicoService } from '../../../services/academico.service';
import { RecursoApi } from '../../../services/recurso-api';
import { mensajeError } from '../../../services/api-error';
import {
  DIAS,
  DiaSemana,
  DisponibilidadDocenteDTO,
  DocenteDTO,
  FormacionAcademicaDTO,
  ExperienciaDocenteDTO,
  etiqueta,
} from '../../../interfaces/api.models';
import { Campo } from '../../../shared/registro-form/registro-form.component';
import { CAMPOS_DISPONIBILIDAD, CAMPOS_EXPERIENCIA, CAMPOS_FORMACION } from './docente-fields';
type Registro = FormacionAcademicaDTO | ExperienciaDocenteDTO | DisponibilidadDocenteDTO;
type Pestana = 'formacion' | 'experiencia' | 'disponibilidad';
@Component({
  selector: 'app-ficha-docente',
  standalone: false,
  templateUrl: './ficha-docente.component.html',
})
export class FichaDocenteComponent implements OnInit {
  docente: DocenteDTO | null = null;
  formaciones: FormacionAcademicaDTO[] = [];
  experiencias: ExperienciaDocenteDTO[] = [];
  disponibilidades: DisponibilidadDocenteDTO[] = [];
  loading = true;
  busy = false;
  error = '';
  modalError = '';
  success = '';
  tab: Pestana = 'formacion';
  formOpen = false;
  fields: Campo[] = [];
  value: Record<string, unknown> = {};
  editing: Registro | null = null;
  deleting: Registro | null = null;
  readonly etiqueta = etiqueta;
  private id = 0;
  constructor(
    private readonly route: ActivatedRoute,
    private readonly api: AcademicoService,
  ) {}
  ngOnInit(): void {
    this.id = Number(this.route.snapshot.paramMap.get('id'));
    if (!Number.isInteger(this.id) || this.id <= 0) {
      this.loading = false;
      this.error = 'El identificador del docente no es válido.';
      return;
    }
    this.load();
  }
  load(): void {
    this.loading = true;
    this.error = '';
    forkJoin({
      docente: this.api.docentes.obtener(this.id),
      formaciones: this.api.formacionDocente(this.id),
      experiencias: this.api.experienciaDocente(this.id),
      disponibilidades: this.api.disponibilidadDocente(this.id),
    })
      .pipe(finalize(() => (this.loading = false)))
      .subscribe({
        next: (data) => {
          this.docente = data.docente;
          this.formaciones = data.formaciones;
          this.experiencias = data.experiencias;
          this.disponibilidades = data.disponibilidades;
        },
        error: (error) => (this.error = mensajeError(error)),
      });
  }
  open(record: Registro | null = null): void {
    this.editing = record;
    this.fields = {
      formacion: CAMPOS_FORMACION,
      experiencia: CAMPOS_EXPERIENCIA,
      disponibilidad: CAMPOS_DISPONIBILIDAD,
    }[this.tab];
    this.value = record ? { ...record } : {};
    this.modalError = '';
    this.formOpen = true;
  }
  private registroId(record: Registro): number {
    if ('idFormacion' in record) return record.idFormacion!;
    if ('idDisponibilidad' in record) return record.idDisponibilidad!;
    return (record as ExperienciaDocenteDTO).idExperiencia!;
  }
  private recurso(): RecursoApi<Record<string, unknown>> {
    const recursos = {
      formacion: this.api.formaciones,
      experiencia: this.api.experiencias,
      disponibilidad: this.api.disponibilidades,
    };
    return recursos[this.tab] as unknown as RecursoApi<Record<string, unknown>>;
  }
  nombreDia(dia: DiaSemana): string {
    return DIAS.find((d) => d.valor === dia)?.nombre || dia;
  }
  save(value: Record<string, unknown>): void {
    this.busy = true;
    this.modalError = '';
    const data = { ...value, docenteId: this.id };
    const request = this.editing
      ? this.recurso().actualizar(this.registroId(this.editing), data)
      : this.recurso().crear(data);
    request.pipe(finalize(() => (this.busy = false))).subscribe({
      next: () => {
        this.formOpen = false;
        this.success = 'El registro profesional se guardó correctamente.';
        this.load();
      },
      error: (error) => (this.modalError = mensajeError(error)),
    });
  }
  remove(): void {
    if (!this.deleting || this.busy) return;
    this.busy = true;
    this.modalError = '';
    this.recurso()
      .eliminar(this.registroId(this.deleting))
      .pipe(finalize(() => (this.busy = false))).subscribe({
      next: () => {
        this.deleting = null;
        this.success = 'El registro se eliminó correctamente.';
        this.load();
      },
      error: (error) => (this.modalError = mensajeError(error)),
    });
  }
}
