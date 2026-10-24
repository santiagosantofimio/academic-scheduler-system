import { HttpErrorResponse } from '@angular/common/http';

export function mensajeError(error: unknown): string {
  if (!(error instanceof HttpErrorResponse))
    return 'No fue posible completar la operación. Inténtalo de nuevo.';
  if (error.status === 0 || error.status === 502 || error.status === 503 || error.status === 504)
    return 'El servicio no está disponible. Verifica la conexión e inténtalo de nuevo.';
  if (error.status === 401)
    return 'El correo o la contraseña no son válidos, o tu sesión debe renovarse.';
  if (error.status >= 500)
    return 'El servidor no pudo completar la operación. Inténtalo de nuevo más tarde.';
  let body = error.error;
  if (typeof body === 'string') {
    try {
      body = JSON.parse(body);
    } catch {
      return 'No se pudo completar la operación. Revisa los datos e inténtalo nuevamente.';
    }
  }
  if (typeof body?.detail === 'string') return body.detail;
  if (error.status === 403)
    return 'Tu cuenta no tiene permiso para esta operación o debes renovar la sesión.';
  if (error.status === 404) return 'El registro solicitado no está disponible.';
  return 'No se pudo completar la operación. Revisa los datos e inténtalo nuevamente.';
}
