import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { BaseCrudRestService } from '../../shared/api/base-crud-rest.service';
import { Cita, CitaRequest, CitaFiltros } from '../../shared/api/backend-contracts';
import { ENDPOINTS } from '../../shared/api/rest-endpoints';

@Injectable({ providedIn: 'root' })
export class CitaRestService extends BaseCrudRestService<Cita, CitaRequest> {
  
  constructor(http: HttpClient) {
    super(http, ENDPOINTS.CITAS);
  }

  override findAll(filtros?: CitaFiltros): Observable<Cita[]> {
    let params = new HttpParams();
    if (filtros?.inicio) {
      params = params.set('inicio', filtros.inicio);
    }
    if (filtros?.fin) {
      params = params.set('fin', filtros.fin);
    }
    return this.http.get<Cita[]>(this.baseUrl, { params });
  }

  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  override create(cita: CitaRequest, extraParams?: Record<string, any>): Observable<Cita> {
    let params = new HttpParams();
    if (extraParams) {
      Object.entries(extraParams).forEach(([key, value]) => {
        params = params.set(key, String(value));
      });
    }
    return this.http.post<Cita>(this.baseUrl, cita, { params });
  }

  findByVeterinarioId(veterinarioId: number): Observable<Cita[]> {
    return this.http.get<Cita[]>(ENDPOINTS.CITAS_BY_VETERINARIO(veterinarioId));
  }

  findByMascotaId(mascotaId: number): Observable<Cita[]> {
    return this.http.get<Cita[]>(ENDPOINTS.CITAS_BY_MASCOTA(mascotaId));
  }

  findByClienteId(clienteId: number): Observable<Cita[]> {
    return this.http.get<Cita[]>(ENDPOINTS.CITAS_BY_CLIENTE(clienteId));
  }

  cancelar(id: number): Observable<void> {
    return this.http.patch<void>(ENDPOINTS.CITAS_CANCELAR(id), {});
  }

  // Alias para compatibilidad
  override delete(id: number): Observable<void> {
    return super.delete(id);
  }

  override findById(id: number): Observable<Cita> {
    return super.findById(id);
  }
}