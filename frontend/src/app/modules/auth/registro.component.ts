import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { AbstractControl, FormBuilder, ValidationErrors, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { finalize, switchMap } from 'rxjs';
import { AuthService } from '../../services/auth.service';
import { mensajeError } from '../../services/api-error';
import { environment } from '../../../environments/environment';
import {
  correoInstitucional,
  passwordBytes,
  passwordMinimo,
  sinBlancos,
} from '../../shared/form-validators';

const dominioInstitucional = (control: AbstractControl): ValidationErrors | null =>
  control.value && !String(control.value).trim().toLowerCase().endsWith('@unbosque.edu.co')
    ? { dominio: true }
    : null;

@Component({ selector: 'app-registro', standalone: false, templateUrl: './registro.component.html' })
export class RegistroComponent implements OnInit {
  readonly form;
  busy = false;
  error = '';
  pideCodigo = false;
  showPassword = false;
  constructor(
    fb: FormBuilder,
    private readonly http: HttpClient,
    private readonly auth: AuthService,
    private readonly router: Router,
  ) {
    const texto = [Validators.required, sinBlancos, Validators.maxLength(255)];
    this.form = fb.nonNullable.group(
      {
        nombre: ['', texto],
        apellido: ['', texto],
        numeroDocumento: ['', texto],
        correo: ['', [Validators.required, correoInstitucional, dominioInstitucional]],
        cargo: ['', texto],
        password: ['', [Validators.required, passwordMinimo, passwordBytes]],
        confirmacion: ['', Validators.required],
        codigo: [''],
      },
      {
        validators: (group) =>
          group.get('confirmacion')?.value && group.get('password')?.value !== group.get('confirmacion')?.value
            ? { distintas: true }
            : null,
      },
    );
  }
  ngOnInit(): void {
    this.http
      .get<{ pideCodigo: boolean }>(`${environment.apiUrl}/registro/administrador`)
      .subscribe({
        next: (r) => {
          this.pideCodigo = r.pideCodigo;
          if (r.pideCodigo) this.form.controls.codigo.setValidators(Validators.required);
          this.form.controls.codigo.updateValueAndValidity();
        },
      });
  }
  registrar(): void {
    this.form.markAllAsTouched();
    if (this.form.invalid || this.busy) return;
    this.busy = true;
    this.error = '';
    const d = this.form.getRawValue();
    const correo = d.correo.trim().toLowerCase();
    this.http
      .post(`${environment.apiUrl}/registro/administrador`, {
        nombre: d.nombre.trim(),
        apellido: d.apellido.trim(),
        numeroDocumento: d.numeroDocumento.trim(),
        correoInstitucional: correo,
        contrasenia: d.password,
        cargo: d.cargo.trim(),
        codigo: d.codigo.trim() || null,
      })
      .pipe(
        switchMap(() => this.auth.ingresar(correo, d.password)),
        finalize(() => (this.busy = false)),
      )
      .subscribe({
        next: () => void this.router.navigateByUrl('/inicio'),
        error: (error) => (this.error = mensajeError(error)),
      });
  }
}
