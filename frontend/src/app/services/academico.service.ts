import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { catchError, forkJoin, Observable, of, throwError } from 'rxjs';
import { RecursoApi } from './recurso-api';
import {
  AdministradorDTO,
  AsignaturaDTO,
  ConfiguracionHorariosDTO,
  DatosPlaneacion,
  DocenteDTO,
  ExperienciaDocenteDTO,
  FormacionAcademicaDTO,
  GrupoDTO,
  PlanEstudiosDTO,
  RequisitoAsignaturaDTO,
  SesionHorarioDTO,
} from '../interfaces/api.models';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class AcademicoService {
  readonly administradores: RecursoApi<AdministradorDTO>;
  readonly docentes: RecursoApi<DocenteDTO>;
  readonly planes: RecursoApi<PlanEstudiosDTO>;
  readonly asignaturas: RecursoApi<AsignaturaDTO>;
  readonly grupos: RecursoApi<GrupoDTO>;
  readonly formaciones: RecursoApi<FormacionAcademicaDTO>;
  readonly experiencias: RecursoApi<ExperienciaDocenteDTO>;
  readonly requisitos: RecursoApi<RequisitoAsignaturaDTO>;
  readonly configuraciones: RecursoApi<ConfiguracionHorariosDTO>;
  readonly sesiones: RecursoApi<SesionHorarioDTO>;
  constructor(private readonly http: HttpClient) {
    this.administradores = new RecursoApi(http, 'administradores');
    this.docentes = new RecursoApi(http, 'docentes');
    this.planes = new RecursoApi(http, 'planes-estudios');
    this.asignaturas = new RecursoApi(http, 'asignaturas');
    this.grupos = new RecursoApi(http, 'grupos');
    this.formaciones = new RecursoApi(http, 'formaciones');
    this.experiencias = new RecursoApi(http, 'experiencias');
    this.requisitos = new RecursoApi(http, 'requisitos-asignatura');
    this.configuraciones = new RecursoApi(http, 'configuracion-horarios');
    this.sesiones = new RecursoApi(http, 'sesiones');
  }
  configuracion(): Observable<ConfiguracionHorariosDTO | null> {
    return this.http
      .get<ConfiguracionHorariosDTO>(`${this.configuraciones.url}/actual`)
      .pipe(
        catchError((error: unknown) =>
          error instanceof HttpErrorResponse && error.status === 404
            ? of(null)
            : throwError(() => error),
        ),
      );
  }
  planeacion(): Observable<DatosPlaneacion> {
    return forkJoin({
      docentes: this.docentes.listar(),
      planes: this.planes.listar(),
      asignaturas: this.asignaturas.listar(),
      grupos: this.grupos.listar(),
      sesiones: this.sesiones.listar(),
      configuracion: this.configuracion(),
    });
  }
  formacionDocente(id: number): Observable<FormacionAcademicaDTO[]> {
    return this.http.get<FormacionAcademicaDTO[]>(`${environment.apiUrl}/formaciones/por-docente`, {
      params: { valor: id },
    });
  }
  experienciaDocente(id: number): Observable<ExperienciaDocenteDTO[]> {
    return this.http.get<ExperienciaDocenteDTO[]>(
      `${environment.apiUrl}/experiencias/por-docente`,
      { params: { valor: id } },
    );
  }
  generar(grupoId: number): Observable<SesionHorarioDTO[]> {
    return this.http.post<SesionHorarioDTO[]>(
      `${environment.apiUrl}/sesiones/grupo/${grupoId}/generar`,
      null,
    );
  }
}
