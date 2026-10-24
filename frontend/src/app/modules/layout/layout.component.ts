import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
@Component({ selector: 'app-layout', standalone: false, templateUrl: './layout.component.html' })
export class LayoutComponent {
  mobileMenu = false;
  readonly navigation = [
    { path: '/inicio', label: 'Inicio', icon: 'home' },
    { path: '/docentes', label: 'Docentes', icon: 'users' },
    { path: '/planes', label: 'Planes de estudio', icon: 'book' },
    { path: '/asignaturas', label: 'Asignaturas', icon: 'layers' },
    { path: '/grupos', label: 'Grupos', icon: 'users' },
    { path: '/horarios', label: 'Horarios', icon: 'calendar' },
    { path: '/validaciones', label: 'Validaciones', icon: 'shield' },
  ];
  readonly administration = [
    { path: '/requisitos', label: 'Requisitos académicos', icon: 'graduation' },
    { path: '/administradores', label: 'Administradores', icon: 'shield' },
    { path: '/configuracion', label: 'Configuración', icon: 'settings' },
  ];
  constructor(
    readonly auth: AuthService,
    private readonly router: Router,
  ) {}
  get initials(): string {
    return (
      this.auth.usuario?.nombre
        .split(' ')
        .slice(0, 2)
        .map((x) => x.charAt(0))
        .join('')
        .toUpperCase() || 'SG'
    );
  }
  salir(): void {
    this.auth.cerrar();
    void this.router.navigate(['/ingresar']);
  }
}
