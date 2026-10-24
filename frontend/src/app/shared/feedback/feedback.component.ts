import { Component, Input, Output, EventEmitter } from '@angular/core';
@Component({
  selector: 'app-feedback',
  standalone: false,
  template: ` <div
    *ngIf="message"
    class="feedback"
    [class]="'feedback feedback-' + kind"
    [attr.role]="kind === 'error' ? 'alert' : 'status'"
  >
    <app-icon
      [name]="kind === 'success' ? 'check' : kind === 'error' ? 'alert' : 'info'"
    ></app-icon>
    <span>{{ message }}</span>
    <button *ngIf="retry" type="button" class="text-button" (click)="retryClick.emit()">
      Reintentar
    </button>
  </div>`,
})
export class FeedbackComponent {
  @Input() message = '';
  @Input() kind: 'error' | 'success' | 'info' = 'info';
  @Input() retry = false;
  @Output() readonly retryClick = new EventEmitter<void>();
}
