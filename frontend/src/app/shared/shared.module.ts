import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { IconComponent } from './icon/icon.component';
import { FeedbackComponent } from './feedback/feedback.component';
import { FieldErrorComponent } from './field-error/field-error.component';
import { DialogComponent } from './dialog/dialog.component';
import { RegistroFormComponent } from './registro-form/registro-form.component';
import { SemanaComponent } from './semana/semana.component';
@NgModule({
  declarations: [
    IconComponent,
    FeedbackComponent,
    FieldErrorComponent,
    DialogComponent,
    RegistroFormComponent,
    SemanaComponent,
  ],
  imports: [CommonModule, FormsModule, ReactiveFormsModule, RouterModule],
  exports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    RouterModule,
    IconComponent,
    FeedbackComponent,
    FieldErrorComponent,
    DialogComponent,
    RegistroFormComponent,
    SemanaComponent,
  ],
})
export class SharedModule {}
