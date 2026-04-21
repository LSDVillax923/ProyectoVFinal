import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { BaseCrudRestService } from '../../shared/api/base-crud-rest.service';
import { Droga, DrogaRequest } from '../../shared/api/backend-contracts';
import { ENDPOINTS } from '../../shared/api/rest-endpoints';

@Injectable({ providedIn: 'root' })
export class DrogaRestService extends BaseCrudRestService<Droga, DrogaRequest> {
  
  constructor(http: HttpClient) {
    super(http, ENDPOINTS.DROGAS);
  }

  override findAll(disponibles?: boolean): Observable<Droga[]> {
    let params = new HttpParams();
    if (disponibles) {
      params = params.set('disponibles', 'true');
    }
    return this.http.get<Droga[]>(this.baseUrl, { params });
  }

  findDisponibles(): Observable<Droga[]> {
    return this.http.get<Droga[]>(ENDPOINTS.DROGAS_DISPONIBLES);
  }

  descontarUnidades(id: number, cantidad: number): Observable<void> {
    const params = new HttpParams().set('cantidad', cantidad.toString());
    return this.http.patch<void>(ENDPOINTS.DROGAS_DESCONTAR(id), null, { params });
  }

  getAll(): Observable<Droga[]> {
    return this.findAll();
  }

  getById(id: number): Observable<Droga> {
    return this.findById(id);
  }

  override findById(id: number): Observable<Droga> {
    return super.findById(id);
  }

  override create(data: DrogaRequest): Observable<Droga> {
    return super.create(data);
  }

  override update(id: number, data: DrogaRequest): Observable<Droga> {
    return super.update(id, data);
  }

  override delete(id: number): Observable<void> {
    return super.delete(id);
  }
}