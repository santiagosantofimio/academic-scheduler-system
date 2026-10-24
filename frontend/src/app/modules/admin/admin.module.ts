import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { SharedModule } from '../../shared/shared.module';
import { InicioComponent } from './inicio/inicio.component';
import { DocentesComponent } from './docentes/docentes.component';
import { FichaDocenteComponent } from './docentes/ficha-docente.component';
import { CatalogoComponent } from './catalogos/catalogo.component';
import { ConfiguracionComponent } from './configuracion/configuracion.component';
import { HorariosComponent } from './horarios/horarios.component';
import { ValidacionesComponent } from './validaciones/validaciones.component';
const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'inicio' },
  { path: 'inicio', component: InicioComponent },
  { path: 'docentes', component: DocentesComponent },
  { path: 'docentes/:id', component: FichaDocenteComponent },
  { path: 'planes', component: CatalogoComponent, data: { catalogo: 'planes' } },
  { path: 'asignaturas', component: CatalogoComponent, data: { catalogo: 'asignaturas' } },
  { path: 'grupos', component: CatalogoComponent, data: { catalogo: 'grupos' } },
  { path: 'requisitos', component: CatalogoComponent, data: { catalogo: 'requisitos' } },
  { path: 'administradores', component: CatalogoComponent, data: { catalogo: 'administradores' } },
  { path: 'configuracion', component: ConfiguracionComponent },
  { path: 'horarios', component: HorariosComponent },
  { path: 'validaciones', component: ValidacionesComponent },
];
@NgModule({
  declarations: [
    InicioComponent,
    DocentesComponent,
    FichaDocenteComponent,
    CatalogoComponent,
    ConfiguracionComponent,
    HorariosComponent,
    ValidacionesComponent,
  ],
  imports: [SharedModule, RouterModule.forChild(routes)],
})
export class AdminModule {}
