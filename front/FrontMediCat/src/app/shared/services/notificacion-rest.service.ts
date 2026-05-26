import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Notificacion } from '../api/backend-contracts';
import { ENDPOINTS } from '../api/rest-endpoints';

@Injectable({ providedIn: 'root' })
export class NotificacionRestService {

  constructor(private readonly http: HttpClient) {}

  /** Lista las notificaciones de un cliente (más recientes primero). */
  findByClienteId(clienteId: number): Observable<Notificacion[]> {
    return this.http.get<Notificacion[]>(ENDPOINTS.NOTIFICACIONES_BY_CLIENTE(clienteId));
  }

  /** Cantidad de notificaciones no leídas (badge de la campana). */
  contarNoLeidas(clienteId: number): Observable<number> {
    return this.http.get<number>(ENDPOINTS.NOTIFICACIONES_NO_LEIDAS(clienteId));
  }

  /** Marca una notificación como leída. */
  marcarLeida(id: number): Observable<Notificacion> {
    return this.http.patch<Notificacion>(ENDPOINTS.NOTIFICACIONES_MARCAR_LEIDA(id), {});
  }
}
