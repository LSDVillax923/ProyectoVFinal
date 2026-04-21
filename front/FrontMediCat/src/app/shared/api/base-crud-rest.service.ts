import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export abstract class BaseCrudRestService<T, TRequest = Partial<T>> {
  
  constructor(
    protected http: HttpClient,
    protected baseUrl: string
  ) {}

  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  findAll(params?: any): Observable<T[]> {
    return this.http.get<T[]>(this.baseUrl, typeof params === 'object' && !(params instanceof HttpParams) ? { params: new HttpParams({ fromObject: params }) } : { params });
  }

  findById(id: number): Observable<T> {
    return this.http.get<T>(`${this.baseUrl}/${id}`);
  }

  create(data: TRequest, extraParams?: Record<string, any>): Observable<T> {
    let params: HttpParams | undefined;
    if (extraParams) {
      params = new HttpParams({ fromObject: extraParams as any });
    }
    return this.http.post<T>(this.baseUrl, data, { params });
  }

  update(id: number, data: TRequest): Observable<T> {
    return this.http.put<T>(`${this.baseUrl}/${id}`, data);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }

  patch(id: number, data: Partial<T>): Observable<T> {
    return this.http.patch<T>(`${this.baseUrl}/${id}`, data);
  }
}