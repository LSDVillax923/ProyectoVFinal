import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { BaseCrudRestService } from '../../shared/api/base-crud-rest.service';
import { Cliente, ClienteRequest, LoginRequest, ClienteFiltros } from '../../shared/api/backend-contracts';
import { ENDPOINTS } from '../../shared/api/rest-endpoints';

@Injectable({ providedIn: 'root' })
export class ClienteRestService extends BaseCrudRestService<Cliente, ClienteRequest> {
  
  constructor(http: HttpClient) {
    super(http, ENDPOINTS.CLIENTES);
  }

getById(id: number): Observable<Cliente> {
  return this.findById(id);
}

getAll(): Observable<Cliente[]> {
  return this.findAll();
}

  override findAll(filtros?: ClienteFiltros): Observable<Cliente[]> {
    let params = new HttpParams();
    if (filtros?.query) {
      params = params.set('query', filtros.query);
    }
    return this.http.get<Cliente[]>(this.baseUrl, { params });
  }

  login(credentials: LoginRequest): Observable<Cliente> {
    const params = new HttpParams()
      .set('correo', credentials.correo)
      .set('contrasenia', credentials.contrasenia);
    return this.http.post<Cliente>(ENDPOINTS.CLIENTES_LOGIN, null, { params });
  }

  getMascotasByCliente(clienteId: number): Observable<any[]> {
    return this.http.get<any[]>(ENDPOINTS.CLIENTES_MASCOTAS(clienteId));
  }

  override findById(id: number): Observable<Cliente> {
    return super.findById(id);
  }

  override create(data: ClienteRequest): Observable<Cliente> {
    return super.create(data);
  }

  override update(id: number, data: ClienteRequest): Observable<Cliente> {
    return super.update(id, data);
  }

  override delete(id: number): Observable<void> {
    return super.delete(id);
  }
}