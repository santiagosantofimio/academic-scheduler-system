import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

/** Contrato CRUD comun a los controladores Java. Las escrituras responden texto, no JSON. */
export class RecursoApi<T> {
  readonly url: string;
  constructor(
    private readonly http: HttpClient,
    ruta: string,
  ) {
    this.url = `${environment.apiUrl}/${ruta}`;
  }
  listar(): Observable<T[]> {
    return this.http.get<T[]>(this.url);
  }
  obtener(id: number): Observable<T> {
    return this.http.get<T>(`${this.url}/${id}`);
  }
  crear(data: T): Observable<string> {
    return this.http.post(this.url, data, { responseType: 'text' });
  }
  actualizar(id: number, data: T): Observable<string> {
    return this.http.put(`${this.url}/${id}`, data, { responseType: 'text' });
  }
  eliminar(id: number): Observable<string> {
    return this.http.delete(`${this.url}/${id}`, { responseType: 'text' });
  }
}
