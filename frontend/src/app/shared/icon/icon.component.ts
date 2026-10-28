import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-icon',
  standalone: false,
  template: `<i class="fa-solid" [ngClass]="icons[name] || icons['calendar']" aria-hidden="true"></i>`,
})
export class IconComponent {
  @Input() name = 'calendar';
  readonly icons: Record<string, string> = {
    calendar: 'fa-calendar-days', home: 'fa-house', users: 'fa-user-group',
    book: 'fa-book-open', layers: 'fa-layer-group', settings: 'fa-sliders',
    shield: 'fa-shield-halved', check: 'fa-check', alert: 'fa-triangle-exclamation',
    plus: 'fa-plus', search: 'fa-magnifying-glass', edit: 'fa-pen-to-square',
    trash: 'fa-trash-can', arrow: 'fa-arrow-right', back: 'fa-arrow-left',
    close: 'fa-xmark', menu: 'fa-bars', logout: 'fa-right-from-bracket',
    clock: 'fa-clock', eye: 'fa-eye', mail: 'fa-envelope', lock: 'fa-lock',
    refresh: 'fa-arrows-rotate', info: 'fa-circle-info', graduation: 'fa-graduation-cap',
    briefcase: 'fa-briefcase', chevron: 'fa-chevron-right',
  };
}
