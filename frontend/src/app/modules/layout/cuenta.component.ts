import { Component } from '@angular/core';
import { AuthService } from '../../services/auth.service';
@Component({
  selector: 'app-cuenta',
  standalone: false,
  template: ` <header class="page-heading">
      <div>
        <p class="eyebrow">MI CUENTA</p>
        <h1>Tu acceso a SIS GEN</h1>
        <p class="muted">Información de la sesión actual.</p>
      </div>
    </header>
    <section class="panel account-panel">
      <div class="account-icon"><app-icon name="graduation"></app-icon></div>
      <h2>{{ auth.usuario?.rol === 'DOCENTE' ? 'Acceso docente' : 'Acceso administrativo' }}</h2>
      <p>{{ auth.usuario?.correo }}</p>
      <ng-container *ngIf="auth.usuario?.rol === 'DOCENTE'; else admin">
        <app-feedback
          message="Tu cuenta está autenticada. La consulta y edición de horarios personales todavía no están habilitadas para docentes en el servidor."
        ></app-feedback>
        <p class="muted">
          Cuando el programa habilite estas operaciones, podrás consultar aquí tu información. Por
          ahora, la gestión académica está disponible para administradores.
        </p>
      </ng-container>
      <ng-template #admin
        ><a routerLink="/inicio" class="button button-primary"
          >Ir a planeación<app-icon name="arrow"></app-icon></a
      ></ng-template>
    </section>`,
})
export class CuentaComponent {
  constructor(readonly auth: AuthService) {}
}
