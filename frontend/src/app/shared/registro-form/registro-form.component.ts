import {
  Component,
  Input,
  Output,
  EventEmitter,
  OnChanges,
  SimpleChanges,
  OnDestroy,
} from '@angular/core';
import { Subscription } from 'rxjs';
import { FormBuilder, FormGroup, ValidatorFn, Validators } from '@angular/forms';
import {
  correoInstitucional,
  entero,
  noFutura,
  ordenFechas,
  passwordBytes,
  sinBlancos,
} from '../form-validators';
export interface Opcion {
  value: string | number | null;
  label: string;
}
export interface Campo {
  key: string;
  label: string;
  type?:
    | 'text'
    | 'email'
    | 'number'
    | 'select'
    | 'textarea'
    | 'date'
    | 'password'
    | 'checkbox'
    | 'time';
  required?: boolean;
  min?: number;
  max?: number;
  options?: Opcion[];
  hint?: string;
  full?: boolean;
}
@Component({
  selector: 'app-registro-form',
  standalone: false,
  templateUrl: './registro-form.component.html',
})
export class RegistroFormComponent implements OnChanges, OnDestroy {
  @Input() fields: Campo[] = [];
  @Input() value: Record<string, unknown> = {};
  @Input() busy = false;
  @Input() submitLabel = 'Guardar';
  @Output() readonly save = new EventEmitter<Record<string, unknown>>();
  @Output() readonly cancel = new EventEmitter<void>();
  form: FormGroup;
  private conditionalSubscription?: Subscription;
  constructor(private readonly fb: FormBuilder) {
    this.form = this.fb.group({});
  }
  ngOnChanges(changes: SimpleChanges): void {
    if (!changes['fields'] && !changes['value']) return;
    this.conditionalSubscription?.unsubscribe();
    const controls: Record<string, unknown> = {};
    for (const field of this.fields) {
      const rules: ValidatorFn[] = [];
      if (field.required) rules.push(Validators.required);
      if (['text', 'textarea'].includes(field.type || 'text')) {
        rules.push(Validators.maxLength(255));
        if (field.required) rules.push(sinBlancos);
      }
      if (field.type === 'email') rules.push(correoInstitucional, Validators.maxLength(255));
      if (field.type === 'number') rules.push(entero);
      if (field.type === 'date') rules.push(noFutura);
      if (field.type === 'password')
        rules.push(passwordBytes, (control) =>
          control.value && !String(control.value).trim() ? { blank: true } : null,
        );
      if (field.min !== undefined) rules.push(Validators.min(field.min));
      if (field.max !== undefined) rules.push(Validators.max(field.max));
      const fallback = field.type === 'checkbox' ? false : field.type === 'select' ? null : '';
      controls[field.key] = [this.value[field.key] ?? fallback, rules];
    }
    this.form = this.fb.group(controls);
    if (this.form.get('fechaInicio') && this.form.get('fechaFin'))
      this.form.addValidators(ordenFechas('fechaInicio', 'fechaFin'));
    if (this.form.get('tipoRequisito')) {
      const update = (): void => {
        const control = this.form.get('nivelFormacionMinimo');
        control?.setValidators(
          this.form.get('tipoRequisito')?.value === 'FORMACION_ACADEMICA'
            ? Validators.required
            : null,
        );
        control?.updateValueAndValidity({ emitEvent: false });
      };
      this.conditionalSubscription = this.form.get('tipoRequisito')?.valueChanges.subscribe(update);
      update();
    }
  }
  ngOnDestroy(): void {
    this.conditionalSubscription?.unsubscribe();
  }
  enviar(): void {
    if (this.busy) return;
    this.form.markAllAsTouched();
    if (this.form.invalid) return;
    const value: Record<string, unknown> = { ...this.form.getRawValue() };
    for (const field of this.fields) {
      if (field.type === 'number') value[field.key] = Number(value[field.key]);
      else if (field.type === 'password' && !value[field.key]) value[field.key] = null;
      else if (value[field.key] === '') value[field.key] = null;
    }
    if (value['tipoRequisito'] && value['tipoRequisito'] !== 'FORMACION_ACADEMICA')
      value['nivelFormacionMinimo'] = null;
    this.save.emit(value);
  }
}
