import { Component, OnInit } from '@angular/core';
import { finalize } from 'rxjs';
import { AcademicoService } from '../../services/academico.service';
import { AuthService } from '../../services/auth.service';
import { mensajeError } from '../../services/api-error';
import {
  DatosPlaneacion,
  DIAS,
  DiaSemana,
  FormatoExportacion,
  SesionHorarioDTO,
} from '../../interfaces/api.models';
import { guardarArchivo } from '../../shared/descarga';
@Component({
  selector: 'app-mi-horario',
  standalone: false,
  templateUrl: './mi-horario.component.html',
})
export class MiHorarioComponent implements OnInit {
  data: DatosPlaneacion | null = null;
  loading = true;
  error = '';
  listView = false;
  constructor(
    private readonly api: AcademicoService,
    private readonly auth: AuthService,
  ) {}
  ngOnInit(): void {
    this.load();
  }
  load(): void {
    const usuario = this.auth.usuario;
    if (!usuario) return;
    this.loading = true;
    this.error = '';
    this.api
      .horarioDocente(usuario)
      .pipe(finalize(() => (this.loading = false)))
      .subscribe({
        next: (data) => (this.data = data),
        error: (error) => (this.error = mensajeError(error)),
      });
  }
  exportar(formato: FormatoExportacion): void {
    this.error = '';
    this.api.exportarMiHorario(formato).subscribe({
      next: (archivo) => guardarArchivo(archivo, `mi-horario.${formato}`),
      error: (error) => (this.error = mensajeError(error)),
    });
  }
  get sesiones(): SesionHorarioDTO[] {
    return this.data?.sesiones.filter((s) => s.estado === 'PROGRAMADA') || [];
  }
  grupo(id: number): string {
    const g = this.data?.grupos.find((x) => x.idGrupo === id);
    const a = this.data?.asignaturas.find((x) => x.idAsginatura === g?.asignaturaId);
    return `${a?.nombre || 'Asignatura'} · Grupo ${g?.numeroGrupo || id}`;
  }
  nombreDia(dia: DiaSemana): string {
    return DIAS.find((d) => d.valor === dia)?.nombre || dia;
  }
}
