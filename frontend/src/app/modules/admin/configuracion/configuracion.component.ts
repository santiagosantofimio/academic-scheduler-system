import { Component, OnInit } from '@angular/core';
import { FormBuilder, ValidatorFn, Validators } from '@angular/forms';
import { finalize } from 'rxjs';
import { AcademicoService } from '../../../services/academico.service';
import { mensajeError } from '../../../services/api-error';
import { ConfiguracionHorariosDTO } from '../../../interfaces/api.models';
import { entero } from '../../../shared/form-validators';
const ordenHoras: ValidatorFn = (form) => {
  const value = form.value;
  if (
    value.horaInicioSemana &&
    value.horaFinSemana &&
    value.horaInicioSemana >= value.horaFinSemana
  )
    return { semana: true };
  if (
    value.horaInicioSabado &&
    value.horaFinSabado &&
    value.horaInicioSabado >= value.horaFinSabado
  )
    return { sabado: true };
  if (
    (value.horaInicioDescanso || value.horaFinDescanso) &&
    (!value.horaInicioDescanso ||
      !value.horaFinDescanso ||
      value.horaInicioDescanso >= value.horaFinDescanso)
  )
    return { descanso: true };
  return null;
};
@Component({
  selector: 'app-configuracion',
  standalone: false,
  templateUrl: './configuracion.component.html',
})
export class ConfiguracionComponent implements OnInit {
  readonly form;
  loading = true;
  loaded = false;
  busy = false;
  error = '';
  success = '';
  configuracion: ConfiguracionHorariosDTO | null = null;
  readonly horas = [
    { key: 'horaInicioSemana', label: 'Hora de inicio' },
    { key: 'horaFinSemana', label: 'Hora de finalización' },
    { key: 'horaInicioSabado', label: 'Hora de inicio' },
    { key: 'horaFinSabado', label: 'Hora de finalización' },
    { key: 'horaInicioDescanso', label: 'Inicio del descanso' },
    { key: 'horaFinDescanso', label: 'Fin del descanso' },
  ] as const;
  constructor(
    fb: FormBuilder,
    private readonly api: AcademicoService,
  ) {
    this.form = fb.nonNullable.group(
      {
        horaInicioSemana: ['', Validators.required],
        horaFinSemana: ['', Validators.required],
        horaInicioSabado: ['', Validators.required],
        horaFinSabado: ['', Validators.required],
        horaInicioDescanso: [''],
        horaFinDescanso: [''],
        duracionSesionMinutos: [
          null as number | null,
          [Validators.required, Validators.min(1), entero],
        ],
        maxSesionesSemanales: [
          null as number | null,
          [Validators.required, Validators.min(1), entero],
        ],
        evitarDiasConsecutivos: [false],
      },
      { validators: ordenHoras },
    );
  }
  ngOnInit(): void {
    this.load();
  }
  load(): void {
    this.loading = true;
    this.loaded = false;
    this.error = '';
    this.api
      .configuracion()
      .pipe(finalize(() => (this.loading = false)))
      .subscribe({
        next: (data) => {
          this.configuracion = data;
          this.loaded = true;
          if (data)
            this.form.patchValue({
              ...data,
              horaInicioDescanso: data.horaInicioDescanso || '',
              horaFinDescanso: data.horaFinDescanso || '',
            });
        },
        error: (error) => (this.error = mensajeError(error)),
      });
  }
  save(): void {
    this.form.markAllAsTouched();
    if (this.form.invalid || this.busy) return;
    this.busy = true;
    this.error = '';
    this.success = '';
    const value = this.form.getRawValue();
    const data: ConfiguracionHorariosDTO = {
      ...value,
      horaInicioDescanso: value.horaInicioDescanso || null,
      horaFinDescanso: value.horaFinDescanso || null,
      duracionSesionMinutos: value.duracionSesionMinutos!,
      maxSesionesSemanales: value.maxSesionesSemanales!,
    };
    const request = this.configuracion
      ? this.api.configuraciones.actualizar(this.configuracion.idConfiguracion!, data)
      : this.api.configuraciones.crear(data);
    request.pipe(finalize(() => (this.busy = false))).subscribe({
      next: () => {
        this.success = 'La configuración institucional se guardó correctamente.';
        this.load();
      },
      error: (error) => (this.error = mensajeError(error)),
    });
  }
}
