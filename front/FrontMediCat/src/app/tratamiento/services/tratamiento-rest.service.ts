import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { BaseCrudRestService } from '../../shared/api/base-crud-rest.service';
import { MedicamentoCantidad, Tratamiento, TratamientoRequest } from '../../shared/api/backend-contracts';
import { ENDPOINTS } from '../../shared/api/rest-endpoints';

@Injectable({ providedIn: 'root' })
export class TratamientoRestService extends BaseCrudRestService<Tratamiento, TratamientoRequest> {
  
  constructor(http: HttpClient) {
    super(http, ENDPOINTS.TRATAMIENTOS);
  }

  override findAll(programados?: boolean): Observable<Tratamiento[]> {
    let params = new HttpParams();
    if (programados) {
      params = params.set('programados', 'true');
    }
    return this.http.get<Tratamiento[]>(this.baseUrl, { params });
  }

  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  override create(tratamiento: TratamientoRequest, extraParams?: Record<string, any>): Observable<Tratamiento> {
    let params = new HttpParams();
    if (extraParams) {
      Object.entries(extraParams).forEach(([key, value]) => {
        params = params.set(key, String(value));
      });
    }
    return this.http.post<Tratamiento>(this.baseUrl, tratamiento, { params });
  }

  findByMascotaId(mascotaId: number): Observable<Tratamiento[]> {
    return this.http.get<Tratamiento[]>(ENDPOINTS.TRATAMIENTOS_BY_MASCOTA(mascotaId));
  }

  findByVeterinarioId(veterinarioId: number): Observable<Tratamiento[]> {
    return this.http.get<Tratamiento[]>(ENDPOINTS.TRATAMIENTOS_BY_VETERINARIO(veterinarioId));
  }

  findByClienteId(clienteId: number): Observable<Tratamiento[]> {
    return this.http.get<Tratamiento[]>(ENDPOINTS.TRATAMIENTOS_BY_CLIENTE(clienteId));
  }

  findProgramados(): Observable<Tratamiento[]> {
    return this.http.get<Tratamiento[]>(ENDPOINTS.TRATAMIENTOS_PROGRAMADOS);
  }

  getById(id: number): Observable<Tratamiento> {
    return this.findById(id);
  }

  override findById(id: number): Observable<Tratamiento> {
    return super.findById(id);
  }

  override update(id: number, data: TratamientoRequest): Observable<Tratamiento> {
    return super.update(id, data);
  }

  override delete(id: number): Observable<void> {
    return super.delete(id);
  }

  count(inicio: string, fin: string): Observable<number> {
    const params = new HttpParams().set('inicio', inicio).set('fin', fin);
    return this.http.get<number>(ENDPOINTS.TRATAMIENTOS_COUNT, { params });
  }

  medicamentosVendidos(inicio: string, fin: string): Observable<MedicamentoCantidad[]> {
    const params = new HttpParams().set('inicio', inicio).set('fin', fin);
    return this.http.get<MedicamentoCantidad[]>(
      ENDPOINTS.TRATAMIENTO_DROGAS_MEDICAMENTOS_VENDIDOS,
      { params },
    );
  }
}