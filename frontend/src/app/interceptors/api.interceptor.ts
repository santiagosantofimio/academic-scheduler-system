import { Injectable, Injector } from '@angular/core';
import {
  HttpEvent,
  HttpHandler,
  HttpInterceptor,
  HttpRequest,
  HttpErrorResponse,
} from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, catchError, throwError } from 'rxjs';
import { AuthService } from '../services/auth.service';
import { environment } from '../../environments/environment';

@Injectable()
export class ApiInterceptor implements HttpInterceptor {
  constructor(
    private readonly injector: Injector,
    private readonly router: Router,
  ) {}
  intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    const base = environment.apiUrl.replace(/\/$/, '');
    // No transmitir credenciales a imagenes, enlaces externos ni otros origenes.
    if (!request.url.startsWith(`${base}/`) && request.url !== base) return next.handle(request);
    const auth = this.injector.get(AuthService);
    let headers = request.headers;
    if (auth.authorizationHeader && !headers.has('Authorization'))
      headers = headers.set('Authorization', auth.authorizationHeader);
    const escritura = !['GET', 'HEAD', 'OPTIONS'].includes(request.method);
    if (escritura && auth.csrfToken)
      headers = headers.set(auth.csrfToken.headerName, auth.csrfToken.token);
    return next.handle(request.clone({ headers, withCredentials: true })).pipe(
      catchError((error: unknown) => {
        if (error instanceof HttpErrorResponse && error.status === 401 && auth.usuario) {
          auth.cerrar();
          void this.router.navigate(['/ingresar'], { queryParams: { motivo: 'sesion' } });
        }
        return throwError(() => error);
      }),
    );
  }
}
