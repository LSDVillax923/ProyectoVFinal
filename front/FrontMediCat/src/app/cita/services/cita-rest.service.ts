import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { BaseCrudRestService } from '../../shared/api/base-crud-rest.service';
import { Cita, CitaRequest, CitaFiltros, CitaResumen, SlotDisponible } from '../../shared/api/backend-contracts';
import { ENDPOINTS } from '../../shared/api/rest-endpoints';

@Injectable({ providedIn: 'root' })
export class CitaRestService extends BaseCrudRestService<Cita, CitaRequest> {

  constructor(http: HttpClient) {
    super(http, ENDPOINTS.CITAS);
  }

  override findAll(filtros?: CitaFiltros): Observable<Cita[]> {
    let params = new HttpParams();
    if (filtros?.inicio) params = params.set('inicio', filtros.inicio);
    if (filtros?.fin) params = params.set('fin', filtros.fin);
    return this.http.get<Cita[]>(this.baseUrl, { params });
  }

  /** Solicitudes pendientes — solo ADMIN puede llamarlo (bandeja de aprobación). */
  findPendientes(): Observable<Cita[]> {
    return this.http.get<Cita[]>(ENDPOINTS.CITAS_PENDIENTES);
  }

  /** Crea cita directa (admin) — backend la deja CONFIRMADA. */
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

  /** Cliente solicita una cita — queda en PENDIENTE. */
  solicitar(cita: CitaRequest, clienteId: number, mascotaId: number, veterinarioId: number): Observable<Cita> {
    const params = new HttpParams()
      .set('clienteId', String(clienteId))
      .set('mascotaId', String(mascotaId))
      .set('veterinarioId', String(veterinarioId));
    return this.http.post<Cita>(ENDPOINTS.CITAS_SOLICITAR, cita, { params });
  }

  /** Admin aprueba una solicitud. */
  aprobar(id: number): Observable<Cita> {
    return this.http.patch<Cita>(ENDPOINTS.CITAS_APROBAR(id), {});
  }

  /** Admin rechaza una solicitud con motivo opcional. */
  rechazar(id: number, motivo?: string): Observable<Cita> {
    let params = new HttpParams();
    if (motivo) params = params.set('motivo', motivo);
    return this.http.patch<Cita>(ENDPOINTS.CITAS_RECHAZAR(id), {}, { params });
  }

  /** Slots libres de 30 min para un veterinario en una fecha. */
  disponibilidad(veterinarioId: number, fecha: string): Observable<SlotDisponible[]> {
    const params = new HttpParams()
      .set('veterinarioId', String(veterinarioId))
      .set('fecha', fecha);
    return this.http.get<SlotDisponible[]>(ENDPOINTS.CITAS_DISPONIBILIDAD, { params });
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

  override delete(id: number): Observable<void> { return super.delete(id); }
  override findById(id: number): Observable<Cita> { return super.findById(id); }

  count(inicio: string, fin: string): Observable<number> {
    const params = new HttpParams().set('inicio', inicio).set('fin', fin);
    return this.http.get<number>(ENDPOINTS.CITAS_COUNT, { params });
  }

  proximas(inicio: string, fin: string, limite = 5): Observable<CitaResumen[]> {
    const params = new HttpParams()
      .set('inicio', inicio)
      .set('fin', fin)
      .set('limite', limite.toString());
    return this.http.get<CitaResumen[]>(ENDPOINTS.CITAS_PROXIMAS, { params });
  }
}
