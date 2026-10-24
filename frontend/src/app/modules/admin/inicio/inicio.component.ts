import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { finalize } from 'rxjs';
import { AcademicoService } from '../../../services/academico.service';
import { mensajeError } from '../../../services/api-error';
import { DatosPlaneacion, SesionHorarioDTO } from '../../../interfaces/api.models';
import {
  ObservacionHorario,
  ValidacionHorarioService,
} from '../../../services/validacion-horario.service';
@Component({ selector: 'app-inicio', standalone: false, templateUrl: './inicio.component.html' })
export class InicioComponent implements OnInit {
  data: DatosPlaneacion | null = null;
  loading = true;
  error = '';
  groupFilter = 0;
  observaciones: ObservacionHorario[] = [];
  constructor(
    private readonly api: AcademicoService,
    private readonly validation: ValidacionHorarioService,
    private readonly router: Router,
  ) {}
  ngOnInit(): void {
    this.load();
  }
  load(): void {
    this.loading = true;
    this.error = '';
    this.api
      .planeacion()
      .pipe(finalize(() => (this.loading = false)))
      .subscribe({
        next: (data) => {
          this.data = data;
          this.observaciones = this.validation.revisar(data);
        },
        error: (error) => (this.error = mensajeError(error)),
      });
  }
  get sessions(): SesionHorarioDTO[] {
    return (
      this.data?.sesiones.filter(
        (s) =>
          s.estado === 'PROGRAMADA' &&
          (!this.groupFilter || s.grupoId === Number(this.groupFilter)),
      ) || []
    );
  }
  get docentesActivos(): number {
    return this.data?.docentes.filter((d) => d.estado).length || 0;
  }
  get gruposActivos(): number {
    return this.data?.grupos.filter((g) => g.estado === 'ACTIVO').length || 0;
  }
  get sesionesProgramadas(): number {
    return this.data?.sesiones.filter((s) => s.estado === 'PROGRAMADA').length || 0;
  }
  get sinDocente(): number {
    return this.data?.grupos.filter((g) => g.estado === 'ACTIVO' && !g.docenteId).length || 0;
  }
  get completos(): number {
    return (
      this.data?.grupos.filter(
        (g) =>
          g.estado === 'ACTIVO' &&
          this.data!.sesiones.filter((s) => s.grupoId === g.idGrupo && s.estado === 'PROGRAMADA')
            .length >=
            (this.data!.asignaturas.find((a) => a.idAsginatura === g.asignaturaId)
              ?.sesionesSemanales ?? Infinity),
      ).length || 0
    );
  }
  get progress(): number {
    return this.gruposActivos ? Math.round((this.completos / this.gruposActivos) * 100) : 0;
  }
  abrirSesion(session: SesionHorarioDTO): void {
    void this.router.navigate(['/horarios'], { queryParams: { sesion: session.idSesion } });
  }
}
