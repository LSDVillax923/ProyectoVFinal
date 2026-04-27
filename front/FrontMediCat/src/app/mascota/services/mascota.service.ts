import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { BaseCrudRestService } from '../../shared/api/base-crud-rest.service';
import { Mascota, MascotaRequest, MascotaFiltros } from '../../shared/api/backend-contracts';
import { ENDPOINTS } from '../../shared/api/rest-endpoints';

@Injectable({ providedIn: 'root' })
export class MascotaRestService extends BaseCrudRestService<Mascota, MascotaRequest> {
  
  constructor(http: HttpClient) {
    super(http, ENDPOINTS.MASCOTAS);
  }

  getById(id: number): Observable<Mascota> {
    return this.findById(id);
  }

  getAll(): Observable<Mascota[]> {
    return this.findAll();
  }

  override findAll(filtros?: MascotaFiltros): Observable<Mascota[]> {
    let params = new HttpParams();
    if (filtros?.query) {
      params = params.set('query', filtros.query);
    }
    if (filtros?.estado) {
      params = params.set('estado', filtros.estado);
    }
    return this.http.get<Mascota[]>(this.baseUrl, { params });
  }

  // Método específico para crear mascota (requiere clienteId)
  crearMascota(mascota: MascotaRequest, clienteId: number): Observable<Mascota> {
    const params = new HttpParams().set('clienteId', clienteId.toString());
    return this.http.post<Mascota>(this.baseUrl, mascota, { params });
  }

  subirFoto(id: number, archivo: File): Observable<Mascota> {
    const formData = new FormData();
    formData.append('archivo', archivo);
    return this.http.post<Mascota>(ENDPOINTS.MASCOTAS_FOTO(id), formData);
  }

  deactivate(id: number): Observable<void> {
    return this.http.patch<void>(ENDPOINTS.MASCOTAS_DEACTIVATE(id), {});
  }

  findByClienteId(clienteId: number): Observable<Mascota[]> {
    return this.http.get<Mascota[]>(ENDPOINTS.MASCOTAS_BY_CLIENTE(clienteId));
  }

  override findById(id: number): Observable<Mascota> {
    return super.findById(id);
  }

  override update(id: number, data: MascotaRequest): Observable<Mascota> {
    return super.update(id, data);
  }

  override delete(id: number): Observable<void> {
    return super.delete(id);
  }
}