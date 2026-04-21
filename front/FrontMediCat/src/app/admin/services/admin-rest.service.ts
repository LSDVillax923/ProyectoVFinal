import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { BaseCrudRestService } from '../../shared/api/base-crud-rest.service';
import { Admin, AdminRequest, LoginRequest } from '../../shared/api/backend-contracts';
import { ENDPOINTS } from '../../shared/api/rest-endpoints';

@Injectable({ providedIn: 'root' })
export class AdminRestService extends BaseCrudRestService<Admin, AdminRequest> {
  
  constructor(http: HttpClient) {
    super(http, ENDPOINTS.ADMINS);
  }

  login(credentials: LoginRequest): Observable<Admin> {
    const params = new HttpParams()
      .set('correo', credentials.correo)
      .set('contrasenia', credentials.contrasenia);
    return this.http.post<Admin>(ENDPOINTS.ADMINS_LOGIN, null, { params });
  }

  override findById(id: number): Observable<Admin> {
    return super.findById(id);
  }

  override findAll(): Observable<Admin[]> {
    return super.findAll();
  }

  override create(data: AdminRequest): Observable<Admin> {
    return super.create(data);
  }

  override update(id: number, data: AdminRequest): Observable<Admin> {
    return super.update(id, data);
  }

  override delete(id: number): Observable<void> {
    return super.delete(id);
  }
}