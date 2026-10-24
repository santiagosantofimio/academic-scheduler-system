import { NgModule } from '@angular/core';
import { SharedModule } from '../../shared/shared.module';
import { LayoutComponent } from './layout.component';
import { CuentaComponent } from './cuenta.component';
@NgModule({
  declarations: [LayoutComponent, CuentaComponent],
  imports: [SharedModule],
  exports: [LayoutComponent],
})
export class LayoutModule {}
