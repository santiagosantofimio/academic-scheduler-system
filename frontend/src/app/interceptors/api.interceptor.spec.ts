import { TestBed } from '@angular/core/testing';
import {
  HTTP_INTERCEPTORS,
  HttpClient,
  provideHttpClient,
  withInterceptorsFromDi,
  withNoXsrfProtection,
} from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { Router } from '@angular/router';
import { ApiInterceptor } from './api.interceptor';
import { AuthService } from '../services/auth.service';
describe('Cabeceras y limites del interceptor', () => {
  let client: HttpClient;
  let http: HttpTestingController;
  const auth = {
    usuario: { rol: 'ADMINISTRADOR' },
    authorizationHeader: 'Basic prueba',
    csrfToken: { headerName: 'X-CSRF-TOKEN', token: 'csrf-prueba' },
    cerrar: jasmine.createSpy('cerrar'),
  };
  const router = { navigate: jasmine.createSpy('navigate').and.resolveTo(true) };
  beforeEach(() => {
    auth.cerrar.calls.reset();
    router.navigate.calls.reset();
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(withInterceptorsFromDi(), withNoXsrfProtection()),
        provideHttpClientTesting(),
        { provide: AuthService, useValue: auth },
        { provide: Router, useValue: router },
        { provide: HTTP_INTERCEPTORS, useClass: ApiInterceptor, multi: true },
      ],
    });
    client = TestBed.inject(HttpClient);
    http = TestBed.inject(HttpTestingController);
  });
  afterEach(() => http.verify());
  it('envia Basic, cookie y el token CSRF real en escrituras', () => {
    client.post('/api/grupos', {}).subscribe();
    const req = http.expectOne('/api/grupos');
    expect(req.request.headers.get('Authorization')).toBe('Basic prueba');
    expect(req.request.headers.get('X-CSRF-TOKEN')).toBe('csrf-prueba');
    expect(req.request.withCredentials).toBeTrue();
    req.flush({});
  });
  it('no adjunta credenciales a otros origenes ni rutas similares', () => {
    for (const url of ['https://example.test/api/grupos', '/api-externa/grupos']) {
      client.get(url).subscribe();
      const req = http.expectOne(url);
      expect(req.request.headers.has('Authorization')).toBeFalse();
      expect(req.request.headers.has('X-CSRF-TOKEN')).toBeFalse();
      expect(req.request.withCredentials).toBeFalse();
      req.flush({});
    }
  });
  it('un 401 de una sesion activa limpia sus credenciales y vuelve al login', () => {
    client.get('/api/docentes').subscribe({ error: () => {} });
    http.expectOne('/api/docentes').flush({}, { status: 401, statusText: 'Unauthorized' });
    expect(auth.cerrar).toHaveBeenCalled();
    expect(router.navigate).toHaveBeenCalledWith(['/ingresar'], {
      queryParams: { motivo: 'sesion' },
    });
  });
});
