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
  it('autentica antes de consultar permisos y conserva el rol administrativo real', () => {
    let role = '';
    auth.ingresar(' ADMIN@EXAMPLE.TEST ', 'clave de prueba').subscribe((user) => (role = user.rol));
    const csrf = http.expectOne('/api/csrf');
    expect(csrf.request.headers.get('Authorization')).toBe(
      'Basic ' + btoa('admin@example.test:clave de prueba'),
    );
    expect(csrf.request.withCredentials).toBeTrue();
    expect(auth.usuario).toBeNull();
    csrf.flush(token);
    const probe = http.expectOne(
      '/api/administradores/por-correo-institucional?valor=admin@example.test',
    );
    probe.flush([
      { id: 1, nombre: 'Cuenta', apellido: 'Prueba', correoInstitucional: 'admin@example.test' },
    ]);
    expect(role).toBe('ADMINISTRADOR');
    expect(auth.csrfToken?.token).toBe(token.token);
    auth.cerrar();
    expect(auth.usuario).toBeNull();
    expect(auth.authorizationHeader).toBeNull();
    expect(auth.csrfToken).toBeNull();
  });
  it('solo concede el acceso docente limitado despues de CSRF valido y consulta administrativa 403', () => {
    let role = '';
    auth.ingresar('docente@example.test', 'clave').subscribe((user) => (role = user.rol));
    http.expectOne('/api/csrf').flush(token);
    http
      .expectOne('/api/administradores/por-correo-institucional?valor=docente@example.test')
      .flush({ status: 403 }, { status: 403, statusText: 'Forbidden' });
    expect(role).toBe('DOCENTE');
  });
  it('no autentica con credenciales rechazadas', () => {
    let rejected = false;
    auth.ingresar('admin@example.test', 'incorrecta').subscribe({ error: () => (rejected = true) });
    http.expectOne('/api/csrf').flush({ status: 401 }, { status: 401, statusText: 'Unauthorized' });
    expect(rejected).toBeTrue();
    expect(auth.usuario).toBeNull();
    expect(auth.authorizationHeader).toBeNull();
  });
  it('un fallo del servidor al consultar permisos no se interpreta como rol docente', () => {
    let rejected = false;
    auth.ingresar('admin@example.test', 'clave').subscribe({ error: () => (rejected = true) });
    http.expectOne('/api/csrf').flush(token);
    http
      .expectOne('/api/administradores/por-correo-institucional?valor=admin@example.test')
      .flush({}, { status: 500, statusText: 'Error' });
    expect(rejected).toBeTrue();
    expect(auth.usuario).toBeNull();
  });
});
