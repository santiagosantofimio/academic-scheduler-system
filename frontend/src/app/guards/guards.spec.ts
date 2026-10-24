import { TestBed } from '@angular/core/testing';
import {
  ActivatedRouteSnapshot,
  provideRouter,
  Router,
  RouterStateSnapshot,
  UrlTree,
} from '@angular/router';
import { AuthGuard } from './auth.guard';
import { AdminGuard } from './admin.guard';
import { AuthService } from '../services/auth.service';
describe('Permisos de rutas', () => {
  let router: Router;
  beforeEach(() => {
    TestBed.configureTestingModule({ providers: [provideRouter([])] });
    router = TestBed.inject(Router);
  });
  it('una sesion ausente vuelve al login conservando la ruta solicitada', () => {
    const guard = new AuthGuard({ usuario: null } as AuthService, router);
    const result = guard.canActivate(
      {} as ActivatedRouteSnapshot,
      { url: '/docentes' } as RouterStateSnapshot,
    ) as UrlTree;
    expect(result.queryParams['continuar']).toBe('/docentes');
    expect(router.serializeUrl(result)).toContain('/ingresar');
  });
  it('el docente no puede entrar a las pantallas administrativas', () => {
    const guard = new AdminGuard({ usuario: { rol: 'DOCENTE' } } as AuthService, router);
    expect(router.serializeUrl(guard.canActivate() as UrlTree)).toBe('/cuenta');
  });
});
