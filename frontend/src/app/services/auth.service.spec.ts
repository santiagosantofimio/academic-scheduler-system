import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { AuthService } from './auth.service';
describe('Autenticacion con el contrato HTTP Basic existente', () => {
  let auth: AuthService;
  let http: HttpTestingController;
  const token = { headerName: 'X-CSRF-TOKEN', parameterName: '_csrf', token: 'token-de-prueba' };
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    auth = TestBed.inject(AuthService);
    http = TestBed.inject(HttpTestingController);
  });
  afterEach(() => http.verify());
  it('autentica antes de consultar la identidad y conserva el rol real', () => {
    let role = '';
    auth.ingresar(' ADMIN@EXAMPLE.TEST ', 'clave de prueba').subscribe((user) => (role = user.rol));
    const csrf = http.expectOne('/api/csrf');
    expect(csrf.request.headers.get('Authorization')).toBe(
      'Basic ' + btoa('admin@example.test:clave de prueba'),
    );
    expect(csrf.request.withCredentials).toBeTrue();
    expect(auth.usuario).toBeNull();
    csrf.flush(token);
    http.expectOne('/api/usuarios/actual').flush({
      id: 1,
      nombre: 'Cuenta',
      apellido: 'Prueba',
      correoInstitucional: 'admin@example.test',
      rol: 'ADMINISTRADOR',
    });
    expect(role).toBe('ADMINISTRADOR');
    expect(auth.usuario?.nombre).toBe('Cuenta Prueba');
    expect(auth.csrfToken?.token).toBe(token.token);
    auth.cerrar();
    expect(auth.usuario).toBeNull();
    expect(auth.authorizationHeader).toBeNull();
    expect(auth.csrfToken).toBeNull();
  });
  it('toma el rol docente de la identidad informada por el servidor', () => {
    auth.ingresar('docente@example.test', 'clave').subscribe();
    http.expectOne('/api/csrf').flush(token);
    http.expectOne('/api/usuarios/actual').flush({
      id: 2,
      nombre: 'Luis',
      apellido: 'Prueba',
      correoInstitucional: 'docente@example.test',
      rol: 'DOCENTE',
    });
    expect(auth.usuario?.rol).toBe('DOCENTE');
    expect(auth.usuario?.id).toBe(2);
  });
  it('no autentica con credenciales rechazadas', () => {
    let rejected = false;
    auth.ingresar('admin@example.test', 'incorrecta').subscribe({ error: () => (rejected = true) });
    http.expectOne('/api/csrf').flush({ status: 401 }, { status: 401, statusText: 'Unauthorized' });
    expect(rejected).toBeTrue();
    expect(auth.usuario).toBeNull();
    expect(auth.authorizationHeader).toBeNull();
  });
  it('un fallo al consultar la identidad no abre la sesion', () => {
    let rejected = false;
    auth.ingresar('admin@example.test', 'clave').subscribe({ error: () => (rejected = true) });
    http.expectOne('/api/csrf').flush(token);
    http.expectOne('/api/usuarios/actual').flush({}, { status: 500, statusText: 'Error' });
    expect(rejected).toBeTrue();
    expect(auth.usuario).toBeNull();
  });
});
