import { Component, Input } from '@angular/core';
import { AbstractControl } from '@angular/forms';
@Component({
  selector: 'app-field-error',
  standalone: false,
  template: `<small class="field-error" *ngIf="control?.invalid && control?.touched">{{
    message
  }}</small>`,
})
export class FieldErrorComponent {
  @Input() control: AbstractControl | null = null;
  get message(): string {
    const errors = this.control?.errors || {};
    if (errors['required'] || errors['blank']) return 'Este campo es obligatorio.';
    if (errors['email']) return 'Escribe un correo válido, por ejemplo nombre@universidad.edu.';
    if (errors['passwordBytes']) return 'La contraseña supera el límite de 72 bytes UTF-8.';
    if (errors['futureDate']) return 'La fecha no puede estar en el futuro.';
    if (errors['integer']) return 'Escribe un número entero.';
    if (errors['min']) return `El valor mínimo es ${errors['min'].min}.`;
    if (errors['max']) return `El valor máximo es ${errors['max'].max}.`;
    if (errors['maxlength'])
      return `Usa como máximo ${errors['maxlength'].requiredLength} caracteres.`;
    return 'Revisa el valor de este campo.';
  }
}
