import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { NEVER } from 'rxjs';
import { AppRoutingModule } from './app-routing.module';
import { LoginComponent } from './modules/auth/login.component';
import { AuthService } from './services/auth.service';
import { AcademicoService } from './services/academico.service';
import { SesionUsuario } from './interfaces/api.models';

describe('Navegación con módulos diferidos', () => {
  let auth: { usuario: SesionUsuario | null; cerrar: () => void };
  beforeEach(async () => {
    auth = { usuario: null, cerrar: () => {} };
    await TestBed.configureTestingModule({
      imports: [AppRoutingModule],
      providers: [
        { provide: AuthService, useValue: auth },
        { provide: AcademicoService, useValue: { planeacion: () => NEVER } },
      ],
    }).compileComponents();
  });

  it('redirige a ingreso y conserva la ruta solicitada cuando no hay sesión', async () => {
    const harness = await RouterTestingHarness.create();
    await harness.navigateByUrl('/horarios', LoginComponent);
    expect(TestBed.inject(Router).url).toBe('/ingresar?continuar=%2Fhorarios');
  });

  it('permite la cuenta docente y redirige las rutas administrativas', async () => {
    auth.usuario = { nombre: 'Docente', correo: 'docente@example.test', rol: 'DOCENTE' };
    const harness = await RouterTestingHarness.create();
    await harness.navigateByUrl('/cuenta');
    expect(harness.routeNativeElement?.querySelector('app-cuenta')).toBeTruthy();
    await harness.navigateByUrl('/horarios');
    expect(TestBed.inject(Router).url).toBe('/cuenta');
    expect(harness.routeNativeElement?.querySelector('app-cuenta')).toBeTruthy();
  });

  it('permite al administrador entrar a inicio y redirige rutas desconocidas', async () => {
    auth.usuario = { nombre: 'Administrador', correo: 'admin@example.test', rol: 'ADMINISTRADOR' };
    const harness = await RouterTestingHarness.create();
    await harness.navigateByUrl('/inicio');
    expect(harness.routeNativeElement?.querySelector('app-inicio')).toBeTruthy();
    const logo = harness.routeNativeElement?.querySelector('.sidebar-brand img');
    expect(logo?.getAttribute('src')).toBe('images/logos/sis-gen.png');
    expect(logo?.getAttribute('alt')).toBe('SIS GEN');
    await harness.navigateByUrl('/ruta-desconocida');
    expect(TestBed.inject(Router).url).toBe('/inicio');
  });
});
