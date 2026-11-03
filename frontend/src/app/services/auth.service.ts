import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { BehaviorSubject, map, Observable, switchMap, throwError } from 'rxjs';
import { environment } from '../../environments/environment';
import { SesionUsuario, TokenCsrf, UsuarioActualDTO } from '../interfaces/api.models';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly usuarioSubject = new BehaviorSubject<SesionUsuario | null>(null);
  readonly usuario$ = this.usuarioSubject.asObservable();
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
          return this.http
            .get<UsuarioActualDTO>(`${environment.apiUrl}/usuarios/actual`, {
              headers,
              withCredentials: true,
            })
            .pipe(
              map((usuario) =>
                this.abrir(basic, csrf, {
                  id: usuario.id,
                  correo: usuario.correoInstitucional,
                  nombre: `${usuario.nombre} ${usuario.apellido}`,
                  rol: usuario.rol,
                }),
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
