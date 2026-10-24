import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { forkJoin, finalize } from 'rxjs';
import { AcademicoService } from '../../../services/academico.service';
import { mensajeError } from '../../../services/api-error';
import {
  DocenteDTO,
  FormacionAcademicaDTO,
  ExperienciaDocenteDTO,
  etiqueta,
} from '../../../interfaces/api.models';
import { Campo } from '../../../shared/registro-form/registro-form.component';
import { CAMPOS_EXPERIENCIA, CAMPOS_FORMACION } from './docente-fields';
type Registro = FormacionAcademicaDTO | ExperienciaDocenteDTO;
@Component({
  selector: 'app-ficha-docente',
  standalone: false,
  templateUrl: './ficha-docente.component.html',
})
export class FichaDocenteComponent implements OnInit {
  docente: DocenteDTO | null = null;
  formaciones: FormacionAcademicaDTO[] = [];
  experiencias: ExperienciaDocenteDTO[] = [];
  loading = true;
  busy = false;
  error = '';
  modalError = '';
  success = '';
  tab: 'formacion' | 'experiencia' = 'formacion';
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
    })
      .pipe(finalize(() => (this.loading = false)))
      .subscribe({
        next: (data) => {
          this.docente = data.docente;
          this.formaciones = data.formaciones;
          this.experiencias = data.experiencias;
        },
        error: (error) => (this.error = mensajeError(error)),
      });
  }
  open(record: Registro | null = null): void {
    this.editing = record;
    this.fields = this.tab === 'formacion' ? CAMPOS_FORMACION : CAMPOS_EXPERIENCIA;
    this.value = record ? { ...record } : {};
    this.modalError = '';
    this.formOpen = true;
  }
  private registroId(record: Registro): number {
    return 'idFormacion' in record
      ? record.idFormacion!
      : (record as ExperienciaDocenteDTO).idExperiencia!;
  }
  save(value: Record<string, unknown>): void {
    this.busy = true;
    this.modalError = '';
    const data = { ...value, docenteId: this.id };
    const request =
      this.tab === 'formacion'
        ? this.editing
          ? this.api.formaciones.actualizar(
              this.registroId(this.editing),
              data as unknown as FormacionAcademicaDTO,
            )
          : this.api.formaciones.crear(data as unknown as FormacionAcademicaDTO)
        : this.editing
          ? this.api.experiencias.actualizar(
              this.registroId(this.editing),
              data as unknown as ExperienciaDocenteDTO,
            )
          : this.api.experiencias.crear(data as unknown as ExperienciaDocenteDTO);
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
    const request =
      this.tab === 'formacion'
        ? this.api.formaciones.eliminar(this.registroId(this.deleting))
        : this.api.experiencias.eliminar(this.registroId(this.deleting));
    request.pipe(finalize(() => (this.busy = false))).subscribe({
      next: () => {
        this.deleting = null;
        this.success = 'El registro se eliminó correctamente.';
        this.load();
      },
      error: (error) => (this.modalError = mensajeError(error)),
    });
  }
}
