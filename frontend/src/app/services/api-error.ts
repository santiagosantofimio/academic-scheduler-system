import { HttpErrorResponse } from '@angular/common/http';

const detalle403Anterior = 'Permiso insuficiente o token CSRF ausente o invalido';

function problemaApi(cuerpo: unknown): { code?: unknown; detail?: unknown } | null {
  if (typeof cuerpo === 'string') {
    try {
      cuerpo = JSON.parse(cuerpo);
    } catch {
      return null;
    }
  }
  return cuerpo && typeof cuerpo === 'object' ? cuerpo : null;
}

export function requiereRenovarCsrf(error: HttpErrorResponse): boolean {
  const problema = problemaApi(error.error);
  return (
    error.status === 403 &&
    (problema?.code === 'CSRF_INVALIDO' || problema?.detail === detalle403Anterior)
  );
}

export function mensajeError(error: unknown): string {
  if (!(error instanceof HttpErrorResponse))
    return 'No fue posible completar la operación. Inténtalo de nuevo.';
  if (error.status === 0 || error.status === 502 || error.status === 503 || error.status === 504)
    return 'El servicio no está disponible. Verifica la conexión e inténtalo de nuevo.';
  if (error.status === 401)
    return 'El correo o la contraseña no son válidos, o tu sesión debe renovarse.';
  if (error.status >= 500)
    return 'El servidor no pudo completar la operación. Inténtalo de nuevo más tarde.';
  const detalle = problemaApi(error.error)?.detail;
  if (error.status === 403 && detalle === detalle403Anterior)
    return 'La API respondió con un aviso de seguridad de una versión anterior. Reinicia el backend del proyecto y vuelve a iniciar sesión.';
  if (typeof detalle === 'string') return detalle;
  if (error.status === 403)
    return 'Tu cuenta no tiene permiso para esta operación o debes renovar la sesión.';
  if (error.status === 404) return 'El registro solicitado no está disponible.';
  return 'No se pudo completar la operación. Revisa los datos e inténtalo nuevamente.';
}
