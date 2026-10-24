import { DatosPlaneacion, SesionHorarioDTO } from '../interfaces/api.models';
import { ValidacionHorarioService } from './validacion-horario.service';
import { SemanaComponent } from '../shared/semana/semana.component';
import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { SharedModule } from '../shared/shared.module';
describe('Revision y representacion del horario', () => {
  const revisar = new ValidacionHorarioService();
  function data(sesiones: SesionHorarioDTO[]): DatosPlaneacion {
    return {
      docentes: [
        {
          id: 1,
          nombre: 'Docente',
          apellido: 'Prueba',
          numeroDocumento: '1',
          correoInstitucional: 'docente@example.test',
          estado: true,
          tipoVinculacion: 'MEDIO_TIEMPO',
          escalafon: 'Prueba',
          especialidad: 'Prueba',
          horasMaximasDocenciaSemanal: 10,
        },
      ],
      planes: [],
      asignaturas: [
        {
          idAsginatura: 1,
          codigo: 'P',
          nombre: 'Asignatura de prueba',
          semestre: 1,
          creditos: 1,
          sesionesSemanales: 4,
          areaConocimiento: 'Prueba',
          estado: true,
          planEstudiosId: 1,
        },
      ],
      grupos: [
        { idGrupo: 1, numeroGrupo: '01', estado: 'ACTIVO', asignaturaId: 1, docenteId: 1 },
        { idGrupo: 2, numeroGrupo: '02', estado: 'ACTIVO', asignaturaId: 1, docenteId: 1 },
      ],
      sesiones,
      configuracion: {
        horaInicioSemana: '08:00',
        horaFinSemana: '18:00',
        horaInicioSabado: '08:00',
        horaFinSabado: '12:00',
        horaInicioDescanso: null,
        horaFinDescanso: null,
        duracionSesionMinutos: 60,
        maxSesionesSemanales: 4,
        evitarDiasConsecutivos: false,
      },
    };
  }
  const a: SesionHorarioDTO = {
    idSesion: 1,
    grupoId: 1,
    diaSemana: 'MONDAY',
    horaInicio: '08:00',
    horaFin: '09:00',
    estado: 'PROGRAMADA',
  };
  it('conserva el elemento de una sesion al recalcular la agenda para no cancelar el arrastre', () => {
    TestBed.configureTestingModule({ imports: [SharedModule], providers: [provideRouter([])] });
    const fixture = TestBed.createComponent(SemanaComponent);
    const component = fixture.componentInstance;
    component.data = data([a]);
    component.sessions = [a];
    component.ngOnChanges();
    fixture.detectChanges();
    const original = fixture.nativeElement.querySelector('.session-block');
    component.sessions = [{ ...a }];
    component.ngOnChanges();
    fixture.detectChanges();
    expect(fixture.nativeElement.querySelector('.session-block')).toBe(original);
  });
  it('un destino de arrastre propone el nuevo dia y conserva la duracion antes de guardar', () => {
    const component = new SemanaComponent();
    component.data = data([a]);
    component.sessions = [a];
    component.editable = true;
    component.ngOnChanges();
    const receive = jasmine.createSpy('propuesta');
    component.moveSession.subscribe(receive);
    component.drop(
      {
        clientY: 226,
        currentTarget: { getBoundingClientRect: () => ({ top: 100 }) },
        dataTransfer: { getData: () => '1' },
        preventDefault: () => {},
      } as unknown as DragEvent,
      'FRIDAY',
    );
    expect(receive).toHaveBeenCalledWith({
      sesion: a,
      diaSemana: 'FRIDAY',
      horaInicio: '10:00',
      horaFin: '11:00',
    });
  });
  it('las franjas contiguas no se consideran cruces', () => {
    expect(
      revisar.revisar(data([a, { ...a, idSesion: 2, horaInicio: '09:00', horaFin: '10:00' }])),
    ).toEqual([]);
  });
  it('detecta cruces del mismo docente entre grupos diferentes', () => {
    expect(
      revisar
        .revisar(data([a, { ...a, idSesion: 2, grupoId: 2 }]))
        .some((o) => o.title === 'Cruce de docente'),
    ).toBeTrue();
  });
  it('las sesiones canceladas no generan cruces ni consumen carga', () => {
    expect(
      revisar.revisar(data([a, { ...a, idSesion: 2, grupoId: 2, estado: 'CANCELADA' }])),
    ).toEqual([]);
  });
  it('representa simultaneas en columnas separadas y usa las franjas reales', () => {
    const component = new SemanaComponent();
    component.data = data([a, { ...a, idSesion: 2, grupoId: 2 }]);
    component.sessions = component.data.sesiones;
    component.ngOnChanges();
    expect(component.start).toBe(480);
    expect(component.end).toBe(1080);
    expect(component.days[0].map((s) => s.left)).toEqual([0, 50]);
  });
});
