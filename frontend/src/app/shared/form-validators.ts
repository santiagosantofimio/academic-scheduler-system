import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';
export const sinBlancos: ValidatorFn = (control) =>
  typeof control.value === 'string' && !control.value.trim() ? { blank: true } : null;
export const entero: ValidatorFn = (control) =>
  control.value !== null && control.value !== '' && !Number.isInteger(Number(control.value))
    ? { integer: true }
    : null;
export const passwordBytes: ValidatorFn = (control) =>
  control.value && new TextEncoder().encode(control.value).length > 72
    ? { passwordBytes: true }
    : null;
export const correoInstitucional: ValidatorFn = (control) =>
  /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(String(control.value).trim()) ? null : { email: true };
export function fechaActual(): string {
  return new Intl.DateTimeFormat('en-CA', {
    timeZone: 'America/Bogota',
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
  }).format(new Date());
}
export const noFutura: ValidatorFn = (control) =>
  control.value && String(control.value) > fechaActual() ? { futureDate: true } : null;
export function ordenFechas(inicio: string, fin: string): ValidatorFn {
  return (control: AbstractControl): ValidationErrors | null => {
    const desde = control.get(inicio)?.value,
      hasta = control.get(fin)?.value;
    return desde && hasta && desde > hasta ? { dateOrder: true } : null;
  };
}
