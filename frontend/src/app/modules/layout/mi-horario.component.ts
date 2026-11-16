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
import { detalleGrupo } from '../../shared/grupo';
@Component({
  selector: 'app-mi-horario',
  standalone: false,
  templateUrl: './mi-horario.component.html',
})
export class MiHorarioComponent implements OnInit {
  data: DatosPlaneacion | null = null;
  version: string | null = null;
  publicadaEn: string | null = null;
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
        next: (data) => {
          this.data = data;
          this.version = data.versionPublicada || null;
          this.publicadaEn = data.publicadaEn || null;
        },
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
  grupo(id: number) {
    return detalleGrupo(this.data, id);
  }
  get fechaPublicacion(): string {
    return this.publicadaEn
      ? new Date(this.publicadaEn).toLocaleString('es-CO', { dateStyle: 'long', timeStyle: 'short' })
      : '';
  }
  nombreDia(dia: DiaSemana): string {
    return DIAS.find((d) => d.valor === dia)?.nombre || dia;
  }
}
