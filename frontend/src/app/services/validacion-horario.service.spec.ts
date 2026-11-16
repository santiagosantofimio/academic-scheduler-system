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
        { idGrupo: 1, numeroGrupo: '01', estado: 'ACTIVO', cupoEsperado: 30, asignaturaId: 1, docenteId: 1 },
        { idGrupo: 2, numeroGrupo: '02', estado: 'ACTIVO', cupoEsperado: 30, asignaturaId: 2, docenteId: 2 },
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
        cupoMinimoGrupo: 10,
        cupoMaximoGrupo: 40,
        toleranciaCupoPorcentaje: 10,
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
  it('muestra el docente y el rango de horas en las tarjetas editables de una hora', () => {
    TestBed.configureTestingModule({ imports: [SharedModule], providers: [provideRouter([])] });
    const fixture = TestBed.createComponent(SemanaComponent);
    const sinDocente: SesionHorarioDTO = { ...a, idSesion: 2, grupoId: 2, diaSemana: 'TUESDAY' };
    const planeacion = data([a, sinDocente]);
    planeacion.grupos[1].docenteId = null;
    fixture.componentRef.setInput('data', planeacion);
    fixture.componentRef.setInput('sessions', planeacion.sesiones);
    fixture.componentRef.setInput('editable', true);
    fixture.detectChanges();

    const calendario: HTMLElement = fixture.nativeElement;
    const [asignada, pendiente] = calendario.querySelectorAll<HTMLButtonElement>('.session-block');
    expect(asignada.textContent).toContain('Docente Prueba');
    expect(asignada.getAttribute('aria-label')).toContain('Docente Prueba');
    expect(pendiente.textContent).toContain('Sin docente asignado');
    const horario = asignada.querySelector('small')!;
    expect(horario.textContent).toContain('08:00 - 09:00');
    expect(horario.getBoundingClientRect().bottom).toBeLessThanOrEqual(
      asignada.getBoundingClientRect().bottom,
    );

    fixture.componentRef.setInput('editable', false);
    fixture.detectChanges();
    expect(asignada.textContent).not.toContain('Docente Prueba');
  });
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
  it('usa la fila exacta al soltar una sesión y conserva su duración', () => {
    TestBed.configureTestingModule({ imports: [SharedModule], providers: [provideRouter([])] });
    const fixture = TestBed.createComponent(SemanaComponent);
    const component = fixture.componentInstance;
    const session = { ...a, horaFin: '10:00' };
    component.data = data([session]);
    component.sessions = [session];
    component.editable = true;
    component.ngOnChanges();
    fixture.detectChanges();

    const receive = jasmine.createSpy('propuesta');
    component.moveSession.subscribe(receive);
    const source = fixture.nativeElement.querySelector('.session-block') as HTMLButtonElement;
    const target = fixture.nativeElement.querySelectorAll('.week-day')[3] as HTMLElement;
    const columnTop = -40;
    const bounds = new DOMRect(0, columnTop + 0.001, 100, component.height);
    spyOn(target, 'getBoundingClientRect').and.returnValue(bounds);
    const sourceBounds = new DOMRect(0, 100, 100, 123);
    spyOn(source, 'getBoundingClientRect').and.returnValue(sourceBounds);
    const dataTransfer = new DataTransfer();
    const grabY = sourceBounds.top + 81 * component.scale;
    const clientY = columnTop + 141 * component.scale;
    source.dispatchEvent(new DragEvent('dragstart', { dataTransfer, clientY: grabY, bubbles: true }));
    target.dispatchEvent(new DragEvent('dragover', { dataTransfer, clientY, cancelable: true }));
    fixture.detectChanges();

    const preview = target.querySelector('.week-drop-preview') as HTMLElement;
    expect(preview.textContent?.trim()).toBe('09:00 – 11:00');
    expect(preview.style.top).toBe('63px');
    expect(preview.style.height).toBe('126px');
    expect(receive).not.toHaveBeenCalled();

    target.dispatchEvent(new DragEvent('drop', { dataTransfer, clientY, cancelable: true }));
    fixture.detectChanges();
    expect(receive).toHaveBeenCalledOnceWith({
      sesion: session,
      diaSemana: 'THURSDAY',
      horaInicio: '09:00',
      horaFin: '11:00',
    });
    expect(target.querySelector('.week-drop-preview')).toBeNull();
  });
  it('descarta un destino que cruza el descanso o rebasa el horario del sábado', () => {
    const component = new SemanaComponent();
    const session = { ...a, horaFin: '10:00' };
    component.data = data([session]);
    component.data.configuracion!.horaInicioDescanso = '12:00';
    component.data.configuracion!.horaFinDescanso = '13:00';
    component.sessions = [session];
    component.editable = true;
    component.ngOnChanges();
    const receive = jasmine.createSpy('propuesta');
    component.moveSession.subscribe(receive);
    const dataTransfer = new DataTransfer();
    const event = {
      clientY: 100 + 181 * component.scale,
      currentTarget: { getBoundingClientRect: () => ({ top: 100 }) },
      dataTransfer,
      preventDefault: jasmine.createSpy('preventDefault'),
    } as unknown as DragEvent;

    const startEvent = {
      clientY: 100,
      currentTarget: event.currentTarget,
      dataTransfer,
    } as unknown as DragEvent;
    for (const day of ['FRIDAY', 'SATURDAY'] as const) {
      component.drag(startEvent, session);
      component.allowDrop(event, day);
      expect(component.dropPreview).toBeNull();
      component.drop(event, day);
    }
    expect(event.preventDefault).not.toHaveBeenCalled();
    expect(receive).not.toHaveBeenCalled();
  });
  it('usa el final del descanso cuando la franja disponible inicia en media hora', () => {
    const component = new SemanaComponent();
    component.data = data([a]);
    component.data.configuracion!.horaInicioDescanso = '12:30';
    component.data.configuracion!.horaFinDescanso = '13:30';
    component.sessions = [a];
    component.editable = true;
    component.ngOnChanges();
    const receive = jasmine.createSpy('propuesta');
    component.moveSession.subscribe(receive);
    const dataTransfer = new DataTransfer();
    const startEvent = {
      clientY: 100,
      currentTarget: { getBoundingClientRect: () => ({ top: 100 }) },
      dataTransfer,
      preventDefault: () => {},
    } as unknown as DragEvent;
    const dropEvent = { ...startEvent, clientY: 100 + 340 * component.scale } as DragEvent;

    component.drag(startEvent, a);
    component.drop(dropEvent, 'FRIDAY');

    expect(receive).toHaveBeenCalledOnceWith({
      sesion: a,
      diaSemana: 'FRIDAY',
      horaInicio: '13:30',
      horaFin: '14:30',
    });
  });
  it('ignora un arrastre externo que contiene el ID de una sesión', () => {
    const component = new SemanaComponent();
    component.data = data([a]);
    component.sessions = [a];
    component.editable = true;
    component.ngOnChanges();
    const receive = jasmine.createSpy('propuesta');
    component.moveSession.subscribe(receive);
    const dataTransfer = new DataTransfer();
    dataTransfer.setData('text/plain', '1');
    component.drop(new DragEvent('drop', { dataTransfer }), 'THURSDAY');
    expect(receive).not.toHaveBeenCalled();
  });
  it('advierte una sesion fuera de la disponibilidad registrada del docente', () => {
    const base = data([a]);
    base.disponibilidades = [
      { docenteId: 1, diaSemana: 'MONDAY', horaInicio: '10:00', horaFin: '12:00' },
    ];
    expect(revisar.revisar(base).map((o) => o.title)).toEqual([
      'Fuera de la disponibilidad del docente',
    ]);
  });
  it('une franjas contiguas de disponibilidad y no advierte sin franjas registradas', () => {
    const base = data([{ ...a, horaInicio: '08:30', horaFin: '09:30' }]);
    base.disponibilidades = [
      { docenteId: 1, diaSemana: 'MONDAY', horaInicio: '08:00', horaFin: '09:00' },
      { docenteId: 1, diaSemana: 'MONDAY', horaInicio: '09:00', horaFin: '10:00' },
    ];
    expect(revisar.revisar(base)).toEqual([]);
    expect(revisar.revisar(data([a]))).toEqual([]);
  });
  it('las franjas contiguas no se consideran cruces', () => {
    expect(
      revisar.revisar(data([a, { ...a, idSesion: 2, horaInicio: '09:00', horaFin: '10:00' }])),
    ).toEqual([]);
  });
  it('detecta cruces del mismo docente entre grupos diferentes', () => {
    const base = data([a, { ...a, idSesion: 2, grupoId: 2 }]);
    base.grupos[1].docenteId = 1;
    expect(revisar.revisar(base).map((o) => o.title)).toEqual(['Cruce de docente']);
  });
  it('grupos con docentes distintos pueden compartir franja', () => {
    expect(revisar.revisar(data([a, { ...a, idSesion: 2, grupoId: 2 }]))).toEqual([]);
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
