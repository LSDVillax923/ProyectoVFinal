import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ENDPOINTS } from '../../shared/api/rest-endpoints';

@Injectable({ providedIn: 'root' })
export class TratamientoDrogaRestService {
  constructor(private readonly http: HttpClient) {}

  agregarDroga(tratamientoId: number, drogaId: number, cantidad: number): Observable<unknown> {
    const params = new HttpParams()
      .set('tratamientoId', String(tratamientoId))
      .set('drogaId', String(drogaId))
      .set('cantidad', String(cantidad));

    return this.http.post(ENDPOINTS.TRATAMIENTO_DROGAS, null, { params });
  }
}