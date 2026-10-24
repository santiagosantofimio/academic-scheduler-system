import { Component, OnInit } from '@angular/core';
import { finalize } from 'rxjs';
import { AcademicoService } from '../../../services/academico.service';
import { mensajeError } from '../../../services/api-error';
import {
  ObservacionHorario,
  ValidacionHorarioService,
} from '../../../services/validacion-horario.service';
@Component({
  selector: 'app-validaciones',
  standalone: false,
  template: ` <header class="page-heading">
      <div>
        <p class="eyebrow">PLANEACIÓN ACADÉMICA</p>
        <h1>Validaciones del horario</h1>
        <p class="muted">Revisión de las sesiones consultadas y las condiciones institucionales.</p>
      </div>
      <button type="button" class="button button-secondary" [disabled]="loading" (click)="load()">
        <app-icon name="refresh"></app-icon>Actualizar revisión
      </button>
    </header>
    <app-feedback
      [message]="error"
      kind="error"
      [retry]="true"
      (retryClick)="load()"
    ></app-feedback>
    <div *ngIf="loading" class="loading-state" role="status">
      <span class="spinner"></span>Revisando sesiones…
    </div>
    <ng-container *ngIf="!loading && !error"
      ><div class="validation-summary" [class.validation-warning]="observaciones.length">
        <app-icon [name]="observaciones.length ? 'alert' : 'shield'"></app-icon>
        <div>
          <h2>
            {{
              observaciones.length
                ? observaciones.length + ' observaciones encontradas'
                : 'Sin observaciones en el horario consultado'
            }}
          </h2>
          <p>
            {{ sesiones }} sesiones programadas revisadas.
            {{
              configurada
                ? 'Configuración institucional disponible.'
                : 'La configuración institucional aún no está registrada.'
            }}
          </p>
        </div>
      </div>
      <section class="panel observation-list" *ngIf="observaciones.length">
        <article *ngFor="let o of observaciones">
          <span class="observation-icon"><app-icon name="alert"></app-icon></span>
          <div>
            <h3>{{ o.title }}</h3>
            <p>{{ o.detail }}</p>
          </div>
          <a routerLink="/horarios" [queryParams]="{ sesion: o.sesiones[0] }" class="text-button"
            >Revisar sesión<app-icon name="arrow"></app-icon
          ></a>
        </article>
      </section>
      <section class="panel validation-explanation">
        <h2>Qué se comprueba</h2>
        <div class="validation-points">
          <span><app-icon name="check"></app-icon>Cruces de docente y grupo</span
          ><span><app-icon name="check"></app-icon>Franjas y descanso</span
          ><span><app-icon name="check"></app-icon>Duración y carga semanal</span
          ><span><app-icon name="check"></app-icon>Cantidad y días consecutivos</span>
        </div>
        <p class="muted">
          Esta revisión utiliza la información consultada. Al guardar o generar sesiones, el
          servidor aplica sus validaciones, incluidos los requisitos académicos del docente.
        </p>
        <a routerLink="/horarios" class="button button-secondary"
          >Ir a horarios<app-icon name="arrow"></app-icon
        ></a>
      </section>
    </ng-container>`,
})
export class ValidacionesComponent implements OnInit {
  loading = true;
  error = '';
  observaciones: ObservacionHorario[] = [];
  sesiones = 0;
  configurada = false;
  constructor(
    private readonly api: AcademicoService,
    private readonly validation: ValidacionHorarioService,
  ) {}
  ngOnInit(): void {
    this.load();
  }
  load(): void {
    this.loading = true;
    this.error = '';
    this.api
      .planeacion()
      .pipe(finalize(() => (this.loading = false)))
      .subscribe({
        next: (data) => {
          this.observaciones = this.validation.revisar(data);
          this.sesiones = data.sesiones.filter((s) => s.estado === 'PROGRAMADA').length;
          this.configurada = !!data.configuracion;
        },
        error: (error) => (this.error = mensajeError(error)),
      });
  }
}
