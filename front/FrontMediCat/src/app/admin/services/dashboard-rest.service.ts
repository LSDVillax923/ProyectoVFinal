import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { DashboardMetricas } from '../../shared/api/backend-contracts';
import { ENDPOINTS } from '../../shared/api/rest-endpoints';

@Injectable({ providedIn: 'root' })
export class DashboardRestService {
  private readonly http = inject(HttpClient);

  obtenerMetricas(): Observable<DashboardMetricas> {
    return this.http.get<DashboardMetricas>(ENDPOINTS.DASHBOARD_METRICAS);
  }
}
