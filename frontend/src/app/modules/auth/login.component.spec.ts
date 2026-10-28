import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { AuthModule } from './auth.module';
import { LoginComponent } from './login.component';

describe('Accesibilidad del acceso', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AuthModule],
      providers: [provideRouter([]), { provide: AuthService, useValue: { usuario: null } }],
    }).compileComponents();
  });
  it('asocia los mensajes de validación a los campos inválidos', () => {
    const fixture = TestBed.createComponent(LoginComponent);
    fixture.detectChanges();
    fixture.componentInstance.form.markAllAsTouched();
    fixture.detectChanges();
    for (const id of ['correo', 'password']) {
      const input = fixture.nativeElement.querySelector(`#${id}`) as HTMLInputElement;
      expect(input.getAttribute('aria-invalid')).toBe('true');
      expect(input.getAttribute('aria-describedby')).toBe(`${id}-error`);
      expect(fixture.nativeElement.querySelector(`#${id}-error`).textContent).toContain('obligatorio');
    }
  });
  it('muestra el logo oficial con texto alternativo', () => {
    const fixture = TestBed.createComponent(LoginComponent);
    fixture.detectChanges();
    const logo = fixture.nativeElement.querySelector('.brand-logo--login') as HTMLImageElement;
    expect(logo.getAttribute('src')).toBe('images/logos/sis-gen.png');
    expect(logo.getAttribute('alt')).toBe('SIS GEN');
  });
});
