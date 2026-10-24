import { Component, Input } from '@angular/core';
@Component({
  selector: 'app-icon',
  standalone: false,
  template: ` <svg
    width="20"
    height="20"
    viewBox="0 0 24 24"
    fill="none"
    stroke="currentColor"
    stroke-width="1.7"
    stroke-linecap="round"
    stroke-linejoin="round"
    aria-hidden="true"
    focusable="false"
  >
    <path *ngFor="let path of paths[name] || paths['calendar']" [attr.d]="path"></path>
  </svg>`,
})
export class IconComponent {
  @Input() name = 'calendar';
  readonly paths: Record<string, string[]> = {
    calendar: ['M4 5h16v16H4z M4 10h16 M8 3v4 M16 3v4 M8 14h2 M14 14h2 M8 17h2'],
    home: ['M3 10 12 3l9 7 M5 9v12h14V9 M9 21v-7h6v7'],
    users: [
      'M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2 M9 11a4 4 0 1 0 0-8 4 4 0 0 0 0 8 M22 21v-2a4 4 0 0 0-3-3.87 M16 3.13a4 4 0 0 1 0 7.75',
    ],
    book: ['M12 5c-3-2-6-2-10-1v16c4-1 7-1 10 1 3-2 6-2 10-1V4c-4-1-7-1-10 1z M12 5v16'],
    layers: ['m12 3 10 5-10 5L2 8z M2 12l10 5 10-5 M2 16l10 5 10-5'],
    settings: ['M4 7h16 M4 17h16 M8 4v6 M16 14v6'],
    shield: ['m12 3 8 3v6c0 5-8 9-8 9s-8-4-8-9V6z M8 12l3 3 5-6'],
    check: ['m5 12 4 4L19 6'],
    alert: ['m12 3 10 18H2z M12 9v5 M12 17v.2'],
    plus: ['M12 5v14 M5 12h14'],
    search: ['M11 18a7 7 0 1 0 0-14 7 7 0 0 0 0 14 M16 16l5 5'],
    edit: ['m16 3 5 5-12 12-6 1 1-6z M14 5l5 5'],
    trash: ['M3 6h18 M9 6V3h6v3 M5 6l1 15h12l1-15 M10 10v7 M14 10v7'],
    arrow: ['M5 12h14 m-6-6 6 6-6 6'],
    back: ['M19 12H5 m6-6-6 6 6 6'],
    close: ['m6 6 12 12 M6 18 18 6'],
    menu: ['M4 6h16 M4 12h16 M4 18h16'],
    logout: ['M9 21H3V3h6 M9 12h12 m-5-5 5 5-5 5'],
    clock: ['M12 22a10 10 0 1 0 0-20 10 10 0 0 0 0 20 M12 6v6l4 2'],
    eye: ['M2 12s4-7 10-7 10 7 10 7-4 7-10 7-10-7-10-7 M12 15a3 3 0 1 0 0-6 3 3 0 0 0 0 6'],
    mail: ['M3 5h18v14H3z m0 0 9 7 9-7'],
    lock: ['M5 10h14v11H5z M8 10V7a4 4 0 0 1 8 0v3 M12 14v3'],
    refresh: ['M20 8a8 8 0 1 0 0 8 M20 3v5h-5'],
    info: ['M12 22a10 10 0 1 0 0-20 10 10 0 0 0 0 20 M12 11v6 M12 7v.2'],
    graduation: ['m12 3 11 6-11 6L1 9z M5 11v6c4 3 10 3 14 0v-6 M23 9v8'],
    briefcase: ['M3 7h18v14H3z M8 7V3h8v4 M3 12c6 3 12 3 18 0 M12 12v4'],
    chevron: ['m9 5 7 7-7 7'],
  };
}
