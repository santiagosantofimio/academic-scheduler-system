import {
  AfterViewInit,
  Component,
  ElementRef,
  EventEmitter,
  Input,
  OnDestroy,
  Output,
  ViewChild,
} from '@angular/core';
let nextDialog = 0;
@Component({
  selector: 'app-dialog',
  standalone: false,
  template: ` <dialog
    #dialog
    class="dialog"
    [class.dialog-wide]="wide"
    [attr.aria-labelledby]="headingId"
    (cancel)="cancel($event)"
    (click)="backdrop($event)"
  >
    <header class="dialog-heading">
      <div>
        <p class="eyebrow">SIS GEN</p>
        <h2 [id]="headingId">{{ title }}</h2>
      </div>
      <button
        type="button"
        class="icon-button"
        aria-label="Cerrar diálogo"
        [disabled]="busy"
        (click)="close.emit()"
      >
        <app-icon name="close"></app-icon>
      </button>
    </header>
    <div class="dialog-content"><ng-content></ng-content></div>
  </dialog>`,
})
export class DialogComponent implements AfterViewInit, OnDestroy {
  @Input() title = '';
  @Input() wide = false;
  @Input() busy = false;
  @Output() readonly close = new EventEmitter<void>();
  @ViewChild('dialog', { static: true }) dialog!: ElementRef<HTMLDialogElement>;
  readonly headingId = `dialog-title-${++nextDialog}`;
  ngAfterViewInit(): void {
    this.dialog.nativeElement.showModal();
  }
  ngOnDestroy(): void {
    this.dialog.nativeElement.close();
  }
  cancel(event: Event): void {
    event.preventDefault();
    if (!this.busy) this.close.emit();
  }
  backdrop(event: MouseEvent): void {
    if (event.target === this.dialog.nativeElement && !this.busy) this.close.emit();
  }
}
