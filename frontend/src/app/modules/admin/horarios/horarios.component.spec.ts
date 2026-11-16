import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';
import { DatosPlaneacion, GrupoDTO, SesionHorarioDTO } from '../../../interfaces/api.models';
import { AcademicoService } from '../../../services/academico.service';
import { ValidacionHorarioService } from '../../../services/validacion-horario.service';
import { HorariosComponent } from './horarios.component';

describe('Selectores de grupos', () => {
  it('ordena por semestre y limita los grupos elegibles solo para la generación', () => {
    const grupo = (idGrupo: number, asignaturaId: number, numeroGrupo = '01'): GrupoDTO => ({
      idGrupo,
      asignaturaId,
      numeroGrupo,
      estado: 'ACTIVO',
      cupoEsperado: 30,
      docenteId: 1,
    });
    const asignaturas = [
      { idAsginatura: 30, semestre: 10, codigo: 'IS1001' },
      { idAsginatura: 20, semestre: 2, codigo: 'IS201' },
      { idAsginatura: 12, semestre: 1, codigo: 'IS102' },
      { idAsginatura: 11, semestre: 1, codigo: 'IS101' },
    ];
    const data: DatosPlaneacion = {
      docentes: [],
      planes: [],
      asignaturas: asignaturas.map((asignatura) => ({
        ...asignatura,
        nombre: asignatura.codigo,
        creditos: 2,
        sesionesSemanales: 1,
        areaConocimiento: 'Ingeniería',
        estado: true,
        planEstudiosId: 1,
      })),
      grupos: [
        grupo(9, 99),
        grupo(5, 30),
        grupo(4, 20),
        grupo(3, 12),
        grupo(2, 11, '10'),
        grupo(1, 11, '2'),
        { ...grupo(6, 11), estado: 'INACTIVO' },
        { ...grupo(7, 20), docenteId: null },
      ],
      sesiones: [],
      configuracion: null,
    };
    const api = { planeacion: () => of(data) } as AcademicoService;
    const component = new HorariosComponent(
      api,
      {} as ActivatedRoute,
      new FormBuilder(),
      new ValidacionHorarioService(),
    );

    component.load();

    expect(component.gruposPorSemestre).toEqual([
      { semestre: 1, grupos: [data.grupos[6], data.grupos[5], data.grupos[4], data.grupos[3]] },
      { semestre: 2, grupos: [data.grupos[2], data.grupos[7]] },
      { semestre: 10, grupos: [data.grupos[1]] },
      { semestre: null, grupos: [data.grupos[0]] },
    ]);
    expect(component.gruposParaGenerar).toEqual([
      { semestre: 1, grupos: [data.grupos[5], data.grupos[4], data.grupos[3]] },
      { semestre: 2, grupos: [data.grupos[2]] },
      { semestre: 10, grupos: [data.grupos[1]] },
      { semestre: null, grupos: [data.grupos[0]] },
    ]);
  });
});

describe('Revisión del horario', () => {
  it('recalcula las observaciones al cargar, generar y eliminar sesiones', () => {
    const first: SesionHorarioDTO = { idSesion: 1, grupoId: 1, diaSemana: 'MONDAY', horaInicio: '08:00', horaFin: '09:00', estado: 'PROGRAMADA' };
    const second: SesionHorarioDTO = { ...first, idSesion: 2, grupoId: 2 };
    const data: DatosPlaneacion = {
      docentes: [{ id: 1, nombre: 'Docente', apellido: 'Prueba', numeroDocumento: '1', correoInstitucional: 'prueba@example.test', estado: true, tipoVinculacion: 'MEDIO_TIEMPO', escalafon: 'Prueba', especialidad: 'Prueba', horasMaximasDocenciaSemanal: 10 }],
      planes: [],
      asignaturas: [{ idAsginatura: 1, codigo: 'P', nombre: 'Prueba', semestre: 1, creditos: 1, sesionesSemanales: 4, areaConocimiento: 'Prueba', estado: true, planEstudiosId: 1 }],
      grupos: [{ idGrupo: 1, numeroGrupo: '01', estado: 'ACTIVO', cupoEsperado: 30, asignaturaId: 1, docenteId: 1 }, { idGrupo: 2, numeroGrupo: '02', estado: 'ACTIVO', cupoEsperado: 30, asignaturaId: 1, docenteId: 1 }],
      sesiones: [first],
      configuracion: { horaInicioSemana: '08:00', horaFinSemana: '18:00', horaInicioSabado: '08:00', horaFinSabado: '12:00', horaInicioDescanso: null, horaFinDescanso: null, duracionSesionMinutos: 60, maxSesionesSemanales: 4, evitarDiasConsecutivos: false, cupoMinimoGrupo: 10, cupoMaximoGrupo: 40, toleranciaCupoPorcentaje: 10 },
    };
    const api = { planeacion: () => of(data), generar: () => of([second]), sesiones: { eliminar: () => of('') } } as unknown as AcademicoService;
    const revision = new ValidacionHorarioService();
    const revisar = spyOn(revision, 'revisar').and.callThrough();
    const component = new HorariosComponent(api, {} as ActivatedRoute, new FormBuilder(), revision);
    component.load();
    expect(component.sesionesProgramadas).toBe(1);
    expect(component.observaciones.length).toBe(0);
    component.generationGroup = 2;
    component.generate();
    expect(component.sesionesProgramadas).toBe(2);
    expect(component.observaciones.some((item) => item.title === 'Cruce de docente')).toBeTrue();
    component.deleting = second;
    component.remove();
    expect(component.sesionesProgramadas).toBe(1);
    expect(component.observaciones.length).toBe(0);
    expect(revisar).toHaveBeenCalledTimes(3);
  });
});

describe('Programación manual de sesiones', () => {
  const primera: SesionHorarioDTO = {
    idSesion: 10,
    grupoId: 3,
    diaSemana: 'MONDAY',
    horaInicio: '07:00',
    horaFin: '09:00',
    estado: 'PROGRAMADA',
  };
  const martes = {
    diaSemana: 'TUESDAY',
    horaInicio: '09:00',
    horaFin: '11:00',
    estado: 'PROGRAMADA',
  } as const;
  const jueves = { ...martes, diaSemana: 'THURSDAY' } as const;

  let component: HorariosComponent;
  let data: DatosPlaneacion;
  let agregarSesiones: jasmine.Spy<AcademicoService['agregarSesiones']>;
  let actualizar: jasmine.Spy<AcademicoService['sesiones']['actualizar']>;

  beforeEach(() => {
    data = {
      docentes: [],
      planes: [],
      asignaturas: [1, 2, 3].map((cantidad) => ({
        idAsginatura: cantidad,
        codigo: String(cantidad),
        nombre: 'Asignatura de prueba',
        semestre: 1,
        creditos: 3,
        sesionesSemanales: cantidad,
        areaConocimiento: 'Ingeniería',
        estado: true,
        planEstudiosId: 1,
      })),
      grupos: [1, 2, 3].map((idGrupo) => ({
        idGrupo,
        numeroGrupo: '01',
        estado: 'ACTIVO',
        cupoEsperado: 30,
        asignaturaId: idGrupo,
        docenteId: 1,
      })),
      sesiones: [],
      configuracion: null,
    };
    agregarSesiones = jasmine.createSpy('agregarSesiones').and.returnValue(of(undefined));
    actualizar = jasmine.createSpy('actualizar').and.returnValue(of('Sesión actualizada'));
    const api = {
      planeacion: () => of(data),
      agregarSesiones,
      sesiones: { actualizar },
    } as unknown as AcademicoService;
    component = new HorariosComponent(
      api,
      {} as ActivatedRoute,
      new FormBuilder(),
      new ValidacionHorarioService(),
    );
    component.load();
  });

  it('muestra un día para cada sesión semanal de la asignatura seleccionada', () => {
    component.open();
    expect(component.sesionesFormulario.length).toBe(0);

    for (const cantidad of [3, 2, 1]) {
      component.form.controls.grupoId.setValue(cantidad);
      expect(component.sesionesFormulario.length).toBe(cantidad);
      expect(component.sesionesFormulario.controls.every((fila) => fila.controls.diaSemana.invalid))
        .toBeTrue();
    }
  });

  it('guarda juntas las sesiones pendientes y descuenta solo las programadas', () => {
    data.sesiones = [primera, { ...primera, idSesion: 11, estado: 'CANCELADA' }];
    component.groupFilter = 3;
    component.open();

    expect(component.sesionesFormulario.length).toBe(2);
    component.sesionesFormulario.at(0).patchValue(martes);
    component.save();
    expect(agregarSesiones).not.toHaveBeenCalled();

    component.sesionesFormulario.at(1).patchValue(jueves);
    component.save();
    expect(agregarSesiones).toHaveBeenCalledOnceWith(3, [
      { ...martes, grupoId: 3 },
      { ...jueves, grupoId: 3 },
    ]);
    expect(component.editorOpen).toBeFalse();
  });

  it('impide agregar sesiones si el grupo ya está completo', () => {
    data.sesiones = [{ ...primera, grupoId: 1 }];
    component.groupFilter = 1;
    component.open();

    expect(component.sesionesFormulario.length).toBe(0);
    component.save();
    expect(agregarSesiones).not.toHaveBeenCalled();
  });

  it('precarga la franja elegida al mover y solo guarda la sesión después de confirmar', () => {
    component.move({ sesion: primera, ...jueves });

    expect(component.sesionesFormulario.length).toBe(1);
    expect(component.sesionesFormulario.at(0).getRawValue()).toEqual(jueves);
    expect(component.form.controls.grupoId.disabled).toBeTrue();
    expect(actualizar).not.toHaveBeenCalled();

    component.save();
    expect(actualizar).toHaveBeenCalledOnceWith(10, { ...jueves, grupoId: 3 });
    expect(agregarSesiones).not.toHaveBeenCalled();
  });
});
