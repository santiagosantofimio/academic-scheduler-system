import { Component, OnInit } from '@angular/core';
import { finalize, forkJoin } from 'rxjs';
import { AcademicoService } from '../../../services/academico.service';
import { mensajeError } from '../../../services/api-error';
import {
  AuditoriaCambioDTO,
  ConflictoDTO,
  EstadoVersion,
  IndicadoresDTO,
  VersionResumenDTO,
} from '../../../interfaces/api.models';

type Indicador = Exclude<keyof IndicadoresDTO, 'sesiones' | 'grupos' | 'docentes' | 'cargaMinima'>;

@Component({
  selector: 'app-versiones',
  standalone: false,
  templateUrl: './versiones.component.html',
})
export class VersionesComponent implements OnInit {
  loading = true;
  busy = false;
  error = '';
  success = '';
  modalError = '';
  nombre = '';
  versiones: VersionResumenDTO[] = [];
  conflictos: ConflictoDTO[] = [];
  cambios: AuditoriaCambioDTO[] = [];
  cargando: VersionResumenDTO | null = null;
  eliminando: VersionResumenDTO | null = null;
  publicando = false;
  readonly estados: Record<EstadoVersion, string> = {
    BORRADOR: 'Borrador',
    PUBLICADA: 'Publicada',
    HISTORICA: 'Histórica',
  };
  readonly acciones: Record<string, string> = {
    CREAR_SESION: 'Agregó una sesión',
    AJUSTAR_SESION: 'Ajustó una sesión',
    ELIMINAR_SESION: 'Eliminó una sesión',
    GENERAR_GRUPO: 'Generó el horario de un grupo',
    GUARDAR_BORRADOR: 'Guardó un borrador',
    ELIMINAR_BORRADOR: 'Eliminó un borrador',
    CARGAR_BORRADOR: 'Cargó un borrador',
    RECUPERAR_VERSION: 'Recuperó una versión',
    PUBLICAR: 'Publicó el horario',
    REGISTRAR_NOVEDAD: 'Registró una novedad',
    CERRAR_NOVEDAD: 'Cerró una novedad',
  };
  constructor(private readonly api: AcademicoService) {}
  ngOnInit(): void {
    this.load();
  }
  load(): void {
    this.loading = true;
    this.error = '';
    forkJoin({
      versiones: this.api.versiones(),
      conflictos: this.api.conflictos(),
      cambios: this.api.auditoria(),
    })
      .pipe(finalize(() => (this.loading = false)))
      .subscribe({
        next: (data) => {
          this.versiones = data.versiones;
          this.conflictos = data.conflictos;
          this.cambios = data.cambios;
        },
        error: (error) => (this.error = mensajeError(error)),
      });
  }
  get vigente(): VersionResumenDTO | undefined {
    return this.versiones.find((v) => v.estado === 'PUBLICADA');
  }
  get nombreValido(): boolean {
    return this.nombre.trim().length > 0 && this.nombre.trim().length <= 120;
  }
  mejor(campo: Indicador, version: VersionResumenDTO): boolean {
    const comparables = this.versiones.filter((v) => v.indicadores.sesiones > 0);
    if (comparables.length < 2 || version.indicadores.sesiones === 0) return false;
    const valores = comparables.map((v) => v.indicadores[campo]);
    const minimo = Math.min(...valores);
    return version.indicadores[campo] === minimo && valores.some((x) => x !== minimo);
  }
  guardarBorrador(): void {
    if (!this.nombreValido) return;
    this.ejecutar(this.api.guardarBorrador(this.nombre.trim()), (v) => {
      this.success = `Borrador "${v.nombre}" guardado con ${v.indicadores.sesiones} sesiones.`;
      this.nombre = '';
    });
  }
  publicar(): void {
    this.modalError = '';
    this.busy = true;
    this.api
      .publicar(this.nombre.trim())
      .pipe(finalize(() => (this.busy = false)))
      .subscribe({
        next: (v) => {
          this.publicando = false;
          this.success = `Se publicó "${v.nombre}". Los docentes ya la ven en Mi horario.`;
          this.nombre = '';
          this.load();
        },
        error: (error) => (this.modalError = mensajeError(error)),
      });
  }
  cargar(): void {
    const version = this.cargando;
    if (!version) return;
    this.modalError = '';
    this.busy = true;
    this.api
      .cargarVersion(version.idVersion)
      .pipe(finalize(() => (this.busy = false)))
      .subscribe({
        next: (r) => {
          this.cargando = null;
          const omitidas = r.omitidas ? ` ${r.omitidas} sesiones de grupos eliminados no se cargaron.` : '';
          const copia = r.versionCreadaId ? ' Se creó un borrador nuevo a partir de ella.' : '';
          const conflictos = r.conflictos.length
            ? ` Hay ${r.conflictos.length} ${r.conflictos.length === 1 ? 'conflicto' : 'conflictos'} con las reglas actuales; revísalos antes de publicar.`
            : '';
          this.success = `Se cargó "${version.nombre}" con ${r.sesiones} sesiones.${copia}${omitidas}${conflictos}`;
          this.load();
        },
        error: (error) => (this.modalError = mensajeError(error)),
      });
  }
  eliminar(): void {
    const version = this.eliminando;
    if (!version) return;
    this.modalError = '';
    this.busy = true;
    this.api
      .eliminarBorrador(version.idVersion)
      .pipe(finalize(() => (this.busy = false)))
      .subscribe({
        next: () => {
          this.eliminando = null;
          this.success = `Se eliminó el borrador "${version.nombre}".`;
          this.load();
        },
        error: (error) => (this.modalError = mensajeError(error)),
      });
  }
  fecha(valor: string | null): string {
    if (!valor) return '';
    const d = new Date(valor);
    return d.toLocaleString('es-CO', { dateStyle: 'medium', timeStyle: 'short' });
  }
  accion(clave: string): string {
    return this.acciones[clave] || clave;
  }
  private ejecutar<T>(peticion: import('rxjs').Observable<T>, ok: (valor: T) => void): void {
    this.error = '';
    this.success = '';
    this.busy = true;
    peticion.pipe(finalize(() => (this.busy = false))).subscribe({
      next: (valor) => {
        ok(valor);
        this.load();
      },
      error: (error) => (this.error = mensajeError(error)),
    });
  }
}
