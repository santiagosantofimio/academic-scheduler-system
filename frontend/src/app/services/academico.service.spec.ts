import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { AcademicoService } from './academico.service';
import { mensajeError } from './api-error';
import { HttpErrorResponse } from '@angular/common/http';
describe('Servicios academicos y serializacion', () => {
  let api: AcademicoService;
  let http: HttpTestingController;
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    api = TestBed.inject(AcademicoService);
    http = TestBed.inject(HttpTestingController);
  });
  afterEach(() => http.verify());
  it('respeta el nombre idAsginatura y las respuestas de texto del CRUD', () => {
    const payload = {
      codigo: 'PRUEBA',
      nombre: 'Asignatura de prueba',
      semestre: 1,
      creditos: 3,
      sesionesSemanales: 2,
      areaConocimiento: 'Prueba',
      estado: true,
      planEstudiosId: 1,
    };
    let result = '';
    api.asignaturas.actualizar(5, payload).subscribe((value) => (result = value));
    const req = http.expectOne('/api/asignaturas/5');
    expect(req.request.method).toBe('PUT');
    expect(req.request.responseType).toBe('text');
    expect(req.request.body).toEqual(payload);
    req.flush('Asignatura actualizado');
    expect(result).toBe('Asignatura actualizado');
  });
  it('consulta formacion por el parametro valor del controlador', () => {
    api.formacionDocente(8).subscribe();
    const req = http.expectOne('/api/formaciones/por-docente?valor=8');
    expect(req.request.method).toBe('GET');
    req.flush([]);
  });
  it('genera por grupo mediante POST y recibe sesiones JSON', () => {
    let count = 0;
    api.generar(9).subscribe((data) => (count = data.length));
    const req = http.expectOne('/api/sesiones/grupo/9/generar');
    expect(req.request.method).toBe('POST');
    req.flush([{ idSesion: 1 }]);
    expect(count).toBe(1);
  });
  it('solo un 404 de configuracion se interpreta como ausencia de configuracion', () => {
    let result: unknown = 'sin respuesta';
    api.configuracion().subscribe((data) => (result = data));
    http
      .expectOne('/api/configuracion-horarios/actual')
      .flush({}, { status: 404, statusText: 'Not Found' });
    expect(result).toBeNull();
  });
  it('un fallo al cargar planeacion no se convierte en listas vacias de exito', () => {
    let rejected = false;
    api.planeacion().subscribe({ error: () => (rejected = true) });
    const requests = http.match(() => true);
    expect(requests.length).toBe(6);
    requests[0].flush({}, { status: 500, statusText: 'Error' });
    expect(rejected).toBeTrue();
  });
  it('conserva ProblemDetail incluso en errores recibidos como texto', () => {
    expect(
      mensajeError(
        new HttpErrorResponse({ status: 409, error: '{"detail":"La franja se superpone"}' }),
      ),
    ).toBe('La franja se superpone');
    expect(
      mensajeError(new HttpErrorResponse({ status: 500, error: { detail: 'SQL privado' } })),
    ).not.toContain('SQL');
  });
});
