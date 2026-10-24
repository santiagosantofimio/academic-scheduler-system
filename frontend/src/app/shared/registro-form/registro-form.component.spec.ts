import { FormBuilder } from '@angular/forms';
import { SimpleChange } from '@angular/core';
import { RegistroFormComponent } from './registro-form.component';
describe('Formularios de registro', () => {
  it('conserva los datos ingresados cuando cambia el estado de guardado', () => {
    const component = new RegistroFormComponent(new FormBuilder());
    component.fields = [{ key: 'nombre', label: 'Nombre', required: true }];
    component.ngOnChanges({ fields: new SimpleChange(null, component.fields, true) });
    component.form.patchValue({ nombre: 'Nombre conservado' });
    component.busy = true;
    component.ngOnChanges({ busy: new SimpleChange(false, true, false) });
    expect(component.form.value.nombre).toBe('Nombre conservado');
    component.ngOnDestroy();
  });
  it('exige nivel de formacion solo para requisitos de formacion', () => {
    const component = new RegistroFormComponent(new FormBuilder());
    component.fields = [
      { key: 'tipoRequisito', label: 'Tipo', type: 'select', required: true },
      { key: 'nivelFormacionMinimo', label: 'Nivel', type: 'select' },
    ];
    component.ngOnChanges({ fields: new SimpleChange(null, component.fields, true) });
    component.form.patchValue({ tipoRequisito: 'FORMACION_ACADEMICA' });
    expect(component.form.get('nivelFormacionMinimo')?.invalid).toBeTrue();
    component.form.patchValue({ tipoRequisito: 'EXPERIENCIA_DOCENTE' });
    expect(component.form.get('nivelFormacionMinimo')?.valid).toBeTrue();
    component.ngOnDestroy();
  });
  it('rechaza textos en blanco y contrasenas que superan 72 bytes UTF-8', () => {
    const component = new RegistroFormComponent(new FormBuilder());
    component.fields = [
      { key: 'nombre', label: 'Nombre', required: true },
      { key: 'contrasenia', label: 'Contraseña', type: 'password', required: true },
    ];
    component.ngOnChanges({ fields: new SimpleChange(null, component.fields, true) });
    component.form.patchValue({ nombre: '   ', contrasenia: 'á'.repeat(37) });
    expect(component.form.get('nombre')?.invalid).toBeTrue();
    expect(component.form.get('contrasenia')?.errors?.['passwordBytes']).toBeTrue();
    component.ngOnDestroy();
  });
});
