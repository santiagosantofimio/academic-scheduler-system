import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { BehaviorSubject, catchError, map, Observable, of, switchMap, throwError } from 'rxjs';
import { environment } from '../../environments/environment';
import { AdministradorDTO, SesionUsuario, TokenCsrf } from '../interfaces/api.models';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly usuarioSubject = new BehaviorSubject<SesionUsuario | null>(null);
  readonly usuario$ = this.usuarioSubject.asObservable();
  // HTTP Basic exige conservar el encabezado durante la sesion. Nunca se escribe en almacenamiento web.
  private autorizacion: string | null = null;
  private csrf: TokenCsrf | null = null;
  constructor(private readonly http: HttpClient) {}
  get usuario(): SesionUsuario | null {
    return this.usuarioSubject.value;
  }
  get authorizationHeader(): string | null {
    return this.autorizacion;
  }
  get csrfToken(): TokenCsrf | null {
    return this.csrf;
  }

  ingresar(correo: string, password: string): Observable<SesionUsuario> {
    const normalizado = correo.trim().toLocaleLowerCase('en');
    const bytes = new TextEncoder().encode(`${normalizado}:${password}`);
    const basic = `Basic ${btoa(Array.from(bytes, (b) => String.fromCharCode(b)).join(''))}`;
    const headers = new HttpHeaders({ Authorization: basic });
    return this.http
      .get<TokenCsrf>(`${environment.apiUrl}/csrf`, { headers, withCredentials: true })
      .pipe(
        switchMap((csrf) => {
          if (!csrf.token || !csrf.headerName)
            return throwError(() => new Error('Respuesta CSRF invalida'));
          // No hay /me ni roles en /csrf. Esta consulta comprueba la capacidad administrativa real.
          // El servidor actual admite exclusivamente Administrador y Docente.
          return this.http
            .get<AdministradorDTO[]>(
              `${environment.apiUrl}/administradores/por-correo-institucional`,
              {
                headers,
                withCredentials: true,
                params: { valor: normalizado },
              },
            )
            .pipe(
              map((registros) => {
                const administrador = registros.find(
                  (a) => a.correoInstitucional.toLowerCase() === normalizado,
                );
                if (!administrador)
                  throw new Error('No fue posible identificar la cuenta administrativa');
                return this.abrir(basic, csrf, {
                  correo: normalizado,
                  nombre: `${administrador.nombre} ${administrador.apellido}`,
                  rol: 'ADMINISTRADOR',
                });
              }),
              catchError((error: unknown) =>
                error instanceof HttpErrorResponse && error.status === 403
                  ? of(
                      this.abrir(basic, csrf, {
                        correo: normalizado,
                        nombre: normalizado,
                        rol: 'DOCENTE',
                      }),
                    )
                  : throwError(() => error),
              ),
            );
        }),
      );
  }
  private abrir(header: string, csrf: TokenCsrf, usuario: SesionUsuario): SesionUsuario {
    this.autorizacion = header;
    this.csrf = csrf;
    this.usuarioSubject.next(usuario);
    return usuario;
  }
  cerrar(): void {
    this.autorizacion = null;
    this.csrf = null;
    this.usuarioSubject.next(null);
  }
}
