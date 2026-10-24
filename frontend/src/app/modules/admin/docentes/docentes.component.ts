import { Component, OnInit } from '@angular/core';
import { finalize } from 'rxjs';
import { AcademicoService } from '../../../services/academico.service';
import { mensajeError } from '../../../services/api-error';
import { DocenteDTO, etiqueta } from '../../../interfaces/api.models';
import { Campo } from '../../../shared/registro-form/registro-form.component';
import { camposDocente } from './docente-fields';
@Component({
  selector: 'app-docentes',
  standalone: false,
  templateUrl: './docentes.component.html',
})
export class DocentesComponent implements OnInit {
  docentes: DocenteDTO[] = [];
  loading = true;
  busy = false;
  error = '';
  modalError = '';
  success = '';
  search = '';
  estado = 'todos';
  formOpen = false;
  editing: DocenteDTO | null = null;
  deleting: DocenteDTO | null = null;
  fields: Campo[] = [];
  value: Record<string, unknown> = {};
  readonly etiqueta = etiqueta;
  constructor(private readonly api: AcademicoService) {}
  ngOnInit(): void {
    this.load();
  }
  load(): void {
    this.loading = true;
    this.error = '';
    this.api.docentes
      .listar()
      .pipe(finalize(() => (this.loading = false)))
      .subscribe({
        next: (data) => (this.docentes = data),
        error: (error) => (this.error = mensajeError(error)),
      });
  }
  get filtered(): DocenteDTO[] {
    const q = this.search.trim().toLocaleLowerCase('es');
    return this.docentes.filter(
      (d) =>
        (this.estado === 'todos' || d.estado === (this.estado === 'activos')) &&
        `${d.nombre} ${d.apellido} ${d.numeroDocumento} ${d.correoInstitucional}`
          .toLocaleLowerCase('es')
          .includes(q),
    );
  }
  open(docente: DocenteDTO | null = null): void {
    this.editing = docente;
    this.fields = camposDocente(!!docente);
    this.value = docente ? { ...docente, contrasenia: null } : { estado: true };
    this.modalError = '';
    this.formOpen = true;
  }
  save(value: Record<string, unknown>): void {
    this.busy = true;
    this.modalError = '';
    const data = value as unknown as DocenteDTO;
    const request = this.editing
      ? this.api.docentes.actualizar(this.editing.id!, data)
      : this.api.docentes.crear(data);
    request.pipe(finalize(() => (this.busy = false))).subscribe({
      next: () => {
        this.formOpen = false;
        this.success = 'La información del docente se guardó correctamente.';
        this.load();
      },
      error: (error) => (this.modalError = mensajeError(error)),
    });
  }
  remove(): void {
    if (!this.deleting || this.busy) return;
    this.busy = true;
    this.modalError = '';
    this.api.docentes
      .eliminar(this.deleting.id!)
      .pipe(finalize(() => (this.busy = false)))
      .subscribe({
        next: () => {
          this.deleting = null;
          this.success = 'El docente se eliminó correctamente.';
          this.load();
        },
        error: (error) => (this.modalError = mensajeError(error)),
      });
  }
}
