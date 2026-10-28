import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';
import { DatosPlaneacion, SesionHorarioDTO } from '../../../interfaces/api.models';
import { AcademicoService } from '../../../services/academico.service';
import { ValidacionHorarioService } from '../../../services/validacion-horario.service';
import { HorariosComponent } from './horarios.component';

describe('Revisión del horario', () => {
  it('recalcula las observaciones al cargar, generar y eliminar sesiones', () => {
    const first: SesionHorarioDTO = { idSesion: 1, grupoId: 1, diaSemana: 'MONDAY', horaInicio: '08:00', horaFin: '09:00', estado: 'PROGRAMADA' };
    const second: SesionHorarioDTO = { ...first, idSesion: 2, grupoId: 2 };
    const data: DatosPlaneacion = {
      docentes: [{ id: 1, nombre: 'Docente', apellido: 'Prueba', numeroDocumento: '1', correoInstitucional: 'prueba@example.test', estado: true, tipoVinculacion: 'MEDIO_TIEMPO', escalafon: 'Prueba', especialidad: 'Prueba', horasMaximasDocenciaSemanal: 10 }],
      planes: [],
      asignaturas: [{ idAsginatura: 1, codigo: 'P', nombre: 'Prueba', semestre: 1, creditos: 1, sesionesSemanales: 4, areaConocimiento: 'Prueba', estado: true, planEstudiosId: 1 }],
      grupos: [{ idGrupo: 1, numeroGrupo: '01', estado: 'ACTIVO', asignaturaId: 1, docenteId: 1 }, { idGrupo: 2, numeroGrupo: '02', estado: 'ACTIVO', asignaturaId: 1, docenteId: 1 }],
      sesiones: [first],
      configuracion: { horaInicioSemana: '08:00', horaFinSemana: '18:00', horaInicioSabado: '08:00', horaFinSabado: '12:00', horaInicioDescanso: null, horaFinDescanso: null, duracionSesionMinutos: 60, maxSesionesSemanales: 4, evitarDiasConsecutivos: false },
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
