import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AuthGuard } from '../../guards/auth.guard';
import { AdminGuard } from '../../guards/admin.guard';
import { SharedModule } from '../../shared/shared.module';
import { LayoutComponent } from './layout.component';
import { CuentaComponent } from './cuenta.component';

const routes: Routes = [
  {
    path: '',
    component: LayoutComponent,
    canActivate: [AuthGuard],
    children: [
      { path: 'cuenta', component: CuentaComponent },
      {
        path: '',
        canActivate: [AdminGuard],
        loadChildren: () => import('../admin/admin.module').then((m) => m.AdminModule),
      },
    ],
  },
];
@NgModule({
  declarations: [LayoutComponent, CuentaComponent],
  imports: [SharedModule, RouterModule.forChild(routes)],
})
export class LayoutModule {}
