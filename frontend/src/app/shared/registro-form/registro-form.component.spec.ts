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
  it('muestra y guarda solo el mínimo pertinente al tipo de requisito', () => {
    const component = new RegistroFormComponent(new FormBuilder());
    component.fields = [
      { key: 'tipoRequisito', label: 'Tipo', type: 'select', required: true },
      { key: 'nivelFormacionMinimo', label: 'Nivel', type: 'select', required: true },
      { key: 'aniosExperienciaMinima', label: 'Años', type: 'number', required: true, min: 0 },
    ];
    component.value = { tipoRequisito: 'FORMACION_ACADEMICA', aniosExperienciaMinima: 1 };
    component.ngOnChanges({ fields: new SimpleChange(null, component.fields, true) });
    const guardar = spyOn(component.save, 'emit');

    expect(component.camposVisibles.map(({ key }) => key)).toEqual([
      'tipoRequisito',
      'nivelFormacionMinimo',
    ]);
    expect(component.form.get('nivelFormacionMinimo')?.invalid).toBeTrue();
    component.form.patchValue({ nivelFormacionMinimo: 'PREGRADO' });
    component.enviar();
    expect(guardar).toHaveBeenCalledOnceWith({
      tipoRequisito: 'FORMACION_ACADEMICA',
      nivelFormacionMinimo: 'PREGRADO',
      aniosExperienciaMinima: 0,
    });

    guardar.calls.reset();
    component.form.patchValue({ tipoRequisito: 'EXPERIENCIA_DOCENTE', aniosExperienciaMinima: 2 });
    expect(component.camposVisibles.map(({ key }) => key)).toEqual([
      'tipoRequisito',
      'aniosExperienciaMinima',
    ]);
    component.enviar();
    expect(guardar).toHaveBeenCalledOnceWith({
      tipoRequisito: 'EXPERIENCIA_DOCENTE',
      nivelFormacionMinimo: null,
      aniosExperienciaMinima: 2,
    });
    component.ngOnDestroy();
  });
  it('exige que la hora final sea posterior a la inicial', () => {
    const component = new RegistroFormComponent(new FormBuilder());
    component.fields = [
      { key: 'horaInicio', label: 'Inicio', type: 'time', required: true },
      { key: 'horaFin', label: 'Fin', type: 'time', required: true },
    ];
    component.ngOnChanges({ fields: new SimpleChange(null, component.fields, true) });
    component.form.patchValue({ horaInicio: '10:00', horaFin: '08:00' });
    expect(component.form.errors?.['timeOrder']).toBeTrue();
    component.form.patchValue({ horaFin: '12:00' });
    expect(component.form.valid).toBeTrue();
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
