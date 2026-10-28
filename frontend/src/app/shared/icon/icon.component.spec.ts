import { ComponentFixture, TestBed } from '@angular/core/testing';
import { SharedModule } from '../shared.module';
import { IconComponent } from './icon.component';

describe('IconComponent', () => {
  let fixture: ComponentFixture<IconComponent>;
  beforeEach(async () => {
    await TestBed.configureTestingModule({ imports: [SharedModule] }).compileComponents();
    fixture = TestBed.createComponent(IconComponent);
  });
  it('muestra el icono solicitado sin añadir texto decorativo accesible', () => {
    fixture.componentInstance.name = 'users';
    fixture.detectChanges();
    const icon = fixture.nativeElement.querySelector('i') as HTMLElement;
    expect(icon.classList.contains('fa-user-group')).toBeTrue();
    expect(icon.getAttribute('aria-hidden')).toBe('true');
  });
  it('usa el calendario cuando el nombre no existe', () => {
    fixture.componentInstance.name = 'desconocido';
    fixture.detectChanges();
    expect(fixture.nativeElement.querySelector('i').classList.contains('fa-calendar-days')).toBeTrue();
  });
  it('carga realmente la fuente local de Font Awesome', async () => {
    fixture.detectChanges();
    const icon = fixture.nativeElement.querySelector('i') as HTMLElement;
    expect(getComputedStyle(icon, '::before').fontFamily).toContain('Font Awesome 6 Free');
    const faces = await document.fonts.load('900 16px "Font Awesome 6 Free"', '\uf073');
    expect(faces.length).toBeGreaterThan(0);
    expect(faces.every((face) => face.status === 'loaded')).toBeTrue();
  });
});
