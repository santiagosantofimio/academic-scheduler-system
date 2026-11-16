import { Injectable, Injector } from '@angular/core';
import {
  HttpEvent,
  HttpHandler,
  HttpInterceptor,
  HttpRequest,
  HttpErrorResponse,
  HttpResponse,
} from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, catchError, filter, map, switchMap, throwError } from 'rxjs';
import { AuthService } from '../services/auth.service';
import { requiereRenovarCsrf } from '../services/api-error';
import { environment } from '../../environments/environment';
import { TokenCsrf } from '../interfaces/api.models';

@Injectable()
export class ApiInterceptor implements HttpInterceptor {
  constructor(
    private readonly injector: Injector,
    private readonly router: Router,
  ) {}
  intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    const base = environment.apiUrl.replace(/\/$/, '');
    if (!request.url.startsWith(`${base}/`) && request.url !== base) return next.handle(request);
    const auth = this.injector.get(AuthService);
    const escritura = !['GET', 'HEAD', 'OPTIONS'].includes(request.method);
    const preparar = (req: HttpRequest<unknown>): HttpRequest<unknown> => {
      let headers = req.headers.set('X-Requested-With', 'XMLHttpRequest');
      if (auth.authorizationHeader && !headers.has('Authorization'))
        headers = headers.set('Authorization', auth.authorizationHeader);
      if (escritura && auth.csrfToken)
        headers = headers.set(auth.csrfToken.headerName, auth.csrfToken.token);
      return req.clone({ headers, withCredentials: true });
    };
    const enviar = (): Observable<HttpEvent<unknown>> => next.handle(preparar(request));
    return enviar().pipe(
      catchError((error: unknown) => {
        if (
          escritura &&
          auth.authorizationHeader &&
          error instanceof HttpErrorResponse &&
          requiereRenovarCsrf(error)
        )
          return this.nuevoToken(base, next).pipe(
            switchMap((csrf) => {
              auth.renovarCsrf(csrf);
              return enviar();
            }),
          );
        return throwError(() => error);
      }),
      catchError((error: unknown) => {
        if (error instanceof HttpErrorResponse && error.status === 401 && auth.usuario) {
          auth.cerrar();
          void this.router.navigate(['/ingresar'], { queryParams: { motivo: 'sesion' } });
        }
        return throwError(() => error);
      }),
    );
  }
  private nuevoToken(base: string, next: HttpHandler): Observable<TokenCsrf> {
    const auth = this.injector.get(AuthService);
    const peticion = new HttpRequest<unknown>('GET', `${base}/csrf`, null, {
      withCredentials: true,
    });
    return next
      .handle(
        peticion.clone({
          setHeaders: {
            'X-Requested-With': 'XMLHttpRequest',
            Authorization: auth.authorizationHeader ?? '',
          },
        }),
      )
      .pipe(
        filter((evento): evento is HttpResponse<TokenCsrf> => evento instanceof HttpResponse),
        map((respuesta) => respuesta.body as TokenCsrf),
      );
  }
}
