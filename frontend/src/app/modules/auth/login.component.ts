import { Component, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { finalize } from 'rxjs';
import { AuthService } from '../../services/auth.service';
import { mensajeError } from '../../services/api-error';
import { correoInstitucional, passwordBytes } from '../../shared/form-validators';
@Component({ selector: 'app-login', standalone: false, templateUrl: './login.component.html' })
export class LoginComponent implements OnInit {
  readonly form;
  busy = false;
  showPassword = false;
  error = '';
  expired = false;
  constructor(
    fb: FormBuilder,
    private readonly auth: AuthService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {
    this.form = fb.nonNullable.group({
      correo: ['', [Validators.required, correoInstitucional]],
      password: ['', [Validators.required, passwordBytes]],
    });
  }
  ngOnInit(): void {
    this.expired = this.route.snapshot.queryParamMap.get('motivo') === 'sesion';
    if (this.auth.usuario)
      void this.router.navigateByUrl(
        this.auth.usuario.rol === 'ADMINISTRADOR' ? '/inicio' : '/cuenta',
      );
  }
  ingresar(): void {
    this.form.markAllAsTouched();
    if (this.form.invalid || this.busy) return;
    this.busy = true;
    this.error = '';
    const data = this.form.getRawValue();
    this.auth
      .ingresar(data.correo, data.password)
      .pipe(finalize(() => (this.busy = false)))
      .subscribe({
        next: (usuario) => {
          this.form.controls.password.reset();
          const continuation = this.route.snapshot.queryParamMap.get('continuar');
          const target =
            usuario.rol === 'DOCENTE'
              ? '/cuenta'
              : continuation?.startsWith('/') &&
                  !continuation.startsWith('//') &&
                  !continuation.startsWith('/ingresar')
                ? continuation
                : '/inicio';
          void this.router.navigateByUrl(target);
        },
        error: (error) => {
          this.error = mensajeError(error);
          this.form.controls.password.reset();
        },
      });
  }
}
