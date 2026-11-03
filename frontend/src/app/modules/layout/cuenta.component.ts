import { Component } from '@angular/core';
import { AuthService } from '../../services/auth.service';
@Component({
  selector: 'app-cuenta',
  standalone: false,
  template: ` <header class="page-heading">
      <div>
        <h1>Tu acceso a SIS GEN</h1>
        <p class="muted">Información de la sesión actual.</p>
      </div>
    </header>
    <section class="panel account-panel">
      <div class="account-icon"><app-icon name="graduation"></app-icon></div>
      <h2>{{ auth.usuario?.rol === 'DOCENTE' ? 'Acceso docente' : 'Acceso administrativo' }}</h2>
      <p>{{ auth.usuario?.correo }}</p>
      <ng-container *ngIf="auth.usuario?.rol === 'DOCENTE'; else admin">
        <p class="muted">Tu cuenta es de consulta: puedes ver tu horario, pero no modificarlo.</p>
        <a routerLink="/mi-horario" class="button button-primary"
          >Ver mi horario<app-icon name="arrow"></app-icon
        ></a>
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
