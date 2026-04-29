import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { BaseCrudRestService } from '../../shared/api/base-crud-rest.service';
import { Veterinario, VeterinarioRequest } from '../../shared/api/backend-contracts';
import { ENDPOINTS } from '../../shared/api/rest-endpoints';

@Injectable({ providedIn: 'root' })
export class VeterinarioRestService extends BaseCrudRestService<Veterinario, VeterinarioRequest> {
  
  constructor(http: HttpClient) {
    super(http, ENDPOINTS.VETERINARIOS);
  }

  override findAll(filtros?: { estado?: string }): Observable<Veterinario[]> {
    let params = new HttpParams();
    if (filtros?.estado) {
      params = params.set('estado', filtros.estado);
    }
    return this.http.get<Veterinario[]>(this.baseUrl, { params });
  }

  cambiarEstado(id: number, estado: string): Observable<Veterinario> {
    const params = new HttpParams().set('estado', estado);
    return this.http.patch<Veterinario>(ENDPOINTS.VETERINARIOS_ESTADO(id), null, { params });
  }

  getAll(): Observable<Veterinario[]> {
    return this.findAll();
  }

  override findById(id: number): Observable<Veterinario> {
    return super.findById(id);
  }

  override create(data: VeterinarioRequest): Observable<Veterinario> {
    return super.create(data);
  }

  override update(id: number, data: Partial<VeterinarioRequest>): Observable<Veterinario> {
    return this.http.put<Veterinario>(`${this.baseUrl}/${id}`, data);
  }

  override delete(id: number): Observable<void> {
    return super.delete(id);
  }

  count(estado: 'activo' | 'inactivo'): Observable<number> {
    const params = new HttpParams().set('estado', estado);
    return this.http.get<number>(ENDPOINTS.VETERINARIOS_COUNT, { params });
  }
}