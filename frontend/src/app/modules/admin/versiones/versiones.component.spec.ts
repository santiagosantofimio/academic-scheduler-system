import { of } from 'rxjs';
import { VersionesComponent } from './versiones.component';
import { AcademicoService } from '../../../services/academico.service';
import { VersionResumenDTO } from '../../../interfaces/api.models';

function version(id: number, horasMuertas: number, sesiones = 4): VersionResumenDTO {
  return {
    idVersion: id,
    nombre: `V${id}`,
    estado: 'BORRADOR',
    creadaEn: '2026-11-14T09:00:00',
    creadaPor: 'admin@example.test',
    publicadaEn: null,
    publicadaPor: null,
    origenId: null,
    origenNombre: null,
    indicadores: {
      sesiones,
      grupos: 2,
      docentes: 2,
      cargaMaxima: 4,
      cargaMinima: 2,
      diferenciaCarga: 2,
      horasMuertas,
      diasConClasePromedio: 2,
    },
  };
}

describe('Comparacion de versiones', () => {
  let componente: VersionesComponent;
  beforeEach(() => {
    const api = jasmine.createSpyObj<AcademicoService>('AcademicoService', ['versiones', 'conflictos', 'auditoria']);
    api.versiones.and.returnValue(of([]));
    api.conflictos.and.returnValue(of([]));
    api.auditoria.and.returnValue(of([]));
    componente = new VersionesComponent(api);
  });
  it('resalta solo el menor valor cuando las versiones difieren', () => {
    const a = version(1, 1.5);
    const b = version(2, 3);
    componente.versiones = [a, b];
    expect(componente.mejor('horasMuertas', a)).toBeTrue();
    expect(componente.mejor('horasMuertas', b)).toBeFalse();
    expect(componente.mejor('cargaMaxima', a)).toBeFalse();
  });
  it('no compara versiones vacias', () => {
    const vacia = version(3, 0, 0);
    componente.versiones = [version(1, 2), vacia];
    expect(componente.mejor('horasMuertas', vacia)).toBeFalse();
  });
  it('exige un nombre para guardar o publicar', () => {
    componente.nombre = '   ';
    expect(componente.nombreValido).toBeFalse();
    componente.nombre = '2027-1';
    expect(componente.nombreValido).toBeTrue();
  });
});
