import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { forkJoin, of, Subscription, finalize } from 'rxjs';
import { AcademicoService } from '../../../services/academico.service';
import { RecursoApi } from '../../../services/recurso-api';
import { mensajeError } from '../../../services/api-error';
import { etiqueta } from '../../../interfaces/api.models';
import { Campo } from '../../../shared/registro-form/registro-form.component';
import {
  CATALOGOS,
  CatalogoDefinicion,
  CatalogoKey,
  camposCatalogo,
  Referencias,
} from './catalogo-definitions';
@Component({
  selector: 'app-catalogo',
  standalone: false,
  templateUrl: './catalogo.component.html',
})
export class CatalogoComponent implements OnInit, OnDestroy {
  definition: CatalogoDefinicion = CATALOGOS.planes;
  records: Record<string, unknown>[] = [];
  refs: Referencias = { planes: [], asignaturas: [], docentes: [] };
  loading = true;
  busy = false;
  error = '';
  modalError = '';
  success = '';
  search = '';
  editing: Record<string, unknown> | null = null;
  deleting: Record<string, unknown> | null = null;
  formOpen = false;
  fields: Campo[] = [];
  value: Record<string, unknown> = {};
  private routeSubscription?: Subscription;
  constructor(
    private readonly route: ActivatedRoute,
    private readonly api: AcademicoService,
  ) {}
  ngOnInit(): void {
    this.routeSubscription = this.route.data.subscribe((data) => {
      this.definition = CATALOGOS[data['catalogo'] as CatalogoKey];
      this.search = '';
      this.success = '';
      this.load();
    });
  }
  ngOnDestroy(): void {
    this.routeSubscription?.unsubscribe();
  }
  private recurso(): RecursoApi<Record<string, unknown>> {
    // El formulario compartido usa metadatos; esta es su unica frontera de conversion dinamica.
    return this.api[this.definition.key] as unknown as RecursoApi<Record<string, unknown>>;
  }
  load(): void {
    this.loading = true;
    this.error = '';
    const key = this.definition.key;
    forkJoin({
      records: this.recurso().listar(),
      planes: key === 'asignaturas' ? this.api.planes.listar() : of([]),
      asignaturas: ['grupos', 'requisitos'].includes(key) ? this.api.asignaturas.listar() : of([]),
      docentes: key === 'grupos' ? this.api.docentes.listar() : of([]),
    })
      .pipe(finalize(() => (this.loading = false)))
      .subscribe({
        next: (result) => {
          this.records = result.records;
          this.refs = result;
        },
        error: (error) => (this.error = mensajeError(error)),
      });
  }
  get filtered(): Record<string, unknown>[] {
    const query = this.search.trim().toLocaleLowerCase('es');
    return !query
      ? this.records
      : this.records.filter((record) =>
          this.definition.columns.some((column) =>
            this.display(record, column.key).toLocaleLowerCase('es').includes(query),
          ),
        );
  }
  display(record: Record<string, unknown>, key: string): string {
    const id = record[key];
    if (key === 'planEstudiosId') {
      const p = this.refs.planes.find((x) => x.idPlan === id);
      return p ? `${p.nombrePrograma} · ${p.versionPlan}` : `Plan #${id}`;
    }
    if (key === 'asignaturaId') {
      const a = this.refs.asignaturas.find((x) => x.idAsginatura === id);
      return a ? a.nombre : `Asignatura #${id}`;
    }
    if (key === 'docenteId') {
      const d = this.refs.docentes.find((x) => x.id === id);
      return d ? `${d.nombre} ${d.apellido}` : id ? `Docente #${id}` : 'Sin asignar';
    }
    if (key === 'estado' || key === 'tipoRequisito') return etiqueta(id);
    return id === null || id === undefined ? '—' : String(id);
  }
  inactive(record: Record<string, unknown>): boolean {
    return record['estado'] === false || record['estado'] === 'INACTIVO';
  }
  open(record: Record<string, unknown> | null = null): void {
    this.editing = record;
    this.modalError = '';
    this.fields = camposCatalogo(this.definition, this.refs, !!record);
    this.value = record
      ? { ...record, contrasenia: null }
      : { estado: this.definition.key === 'grupos' ? 'ACTIVO' : true, aniosExperienciaMinima: 0 };
    this.formOpen = true;
  }
  save(value: Record<string, unknown>): void {
    this.busy = true;
    this.modalError = '';
    const editing = this.editing;
    const request = editing
      ? this.recurso().actualizar(Number(editing[this.definition.idKey]), value)
      : this.recurso().crear(value);
    request.pipe(finalize(() => (this.busy = false))).subscribe({
      next: () => {
        this.formOpen = false;
        this.success = 'Los cambios se guardaron correctamente.';
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
      .eliminar(Number(this.deleting[this.definition.idKey]))
      .pipe(finalize(() => (this.busy = false)))
      .subscribe({
        next: () => {
          this.deleting = null;
          this.success = 'El registro se eliminó correctamente.';
          this.load();
        },
        error: (error) => (this.modalError = mensajeError(error)),
      });
  }
}
