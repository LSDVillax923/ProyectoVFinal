import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { map } from 'rxjs/operators';
import { LoginRequest, Cliente, ClienteRequest, Veterinario, Admin } from '../../shared/api/backend-contracts';
import { ENDPOINTS } from '../../shared/api/rest-endpoints';

export interface SesionActiva {
  id: number;
  nombre: string;
  correo: string;
  rol: 'ADMIN' | 'VETERINARIO' | 'CLIENTE';
}

const STORAGE_KEY = 'vet_session';
const COOKIE_VET_KEY = 'vet_logueado';
const COOKIE_MAX_AGE_SECONDS = 60 * 60 * 8;

@Injectable({ providedIn: 'root' })
export class AuthRestService {

  private sesion: SesionActiva | null = null;

  constructor(private http: HttpClient) {
    this.cargarSesionDesdeStorage();
  }

  private cargarSesionDesdeStorage(): void {
    const stored = sessionStorage.getItem(STORAGE_KEY);
    if (stored) {
      try {
        this.sesion = JSON.parse(stored);
      } catch {
        this.sesion = null;
      }
    }
  }

  private guardarSesion(sesion: SesionActiva): void {
    this.sesion = sesion;
    sessionStorage.setItem(STORAGE_KEY, JSON.stringify(sesion));
    if (sesion.rol === 'VETERINARIO') {
      this.guardarVeterinarioEnCookie(sesion);
    } else {
      this.borrarVeterinarioCookie();
    }
  }

  private guardarVeterinarioEnCookie(sesion: SesionActiva): void {
    const valor = encodeURIComponent(JSON.stringify(sesion));
    document.cookie = `${COOKIE_VET_KEY}=${valor}; path=/; max-age=${COOKIE_MAX_AGE_SECONDS}; SameSite=Lax`;
  }

  private borrarVeterinarioCookie(): void {
    document.cookie = `${COOKIE_VET_KEY}=; path=/; max-age=0; SameSite=Lax`;
  }

  getVeterinarioLogueado(): SesionActiva | null {
    const cookies = document.cookie ? document.cookie.split('; ') : [];
    const entrada = cookies.find((c) => c.startsWith(`${COOKIE_VET_KEY}=`));
    if (!entrada) return null;
    try {
      const valor = decodeURIComponent(entrada.substring(COOKIE_VET_KEY.length + 1));
      const datos = JSON.parse(valor) as SesionActiva;
      return datos.rol === 'VETERINARIO' ? datos : null;
    } catch {
      return null;
    }
  }

  getSesion(): SesionActiva | null {
    return this.sesion;
  }

  isAuthenticated(): boolean {
    return this.sesion !== null;
  }

  hasRole(rol: string): boolean {
    return this.sesion?.rol === rol;
  }

  setSesion(sesion: SesionActiva): void {
    this.guardarSesion(sesion);
  }

// Agregar este método a AuthRestService

/**
 * Login unificado - detecta automáticamente el tipo de usuario
 */
login(credentials: LoginRequest, tipoUsuario: 'CLIENTE' | 'VETERINARIO' | 'ADMIN'): Observable<SesionActiva> {
  switch (tipoUsuario) {
    case 'CLIENTE':
      return this.loginCliente(credentials);
    case 'VETERINARIO':
      return this.loginVeterinario(credentials);
    case 'ADMIN':
      return this.loginAdmin(credentials);
    default:
      throw new Error('Tipo de usuario no válido');
  }
}

  register(data: ClienteRequest): Observable<Cliente> {
    return this.http.post<Cliente>(ENDPOINTS.CLIENTES, data);
  }

  loginCliente(credentials: LoginRequest): Observable<SesionActiva> {
    const params = new HttpParams()
      .set('correo', credentials.correo)
      .set('contrasenia', credentials.contrasenia);
    
    return this.http.post<Cliente>(ENDPOINTS.CLIENTES_LOGIN, null, { params }).pipe(
      map(cliente => ({
        id: cliente.id,
        nombre: `${cliente.nombre} ${cliente.apellido}`,
        correo: cliente.correo,
        rol: 'CLIENTE' as const
      })),
      tap(sesion => this.guardarSesion(sesion))
    );
  }

  loginVeterinario(credentials: LoginRequest): Observable<SesionActiva> {
    const params = new HttpParams()
      .set('correo', credentials.correo)
      .set('contrasenia', credentials.contrasenia);
    
    return this.http.post<Veterinario>(ENDPOINTS.VETERINARIOS_LOGIN, null, { params }).pipe(
      map(vet => ({
        id: vet.id,
        nombre: vet.nombre,
        correo: vet.correo,
        rol: 'VETERINARIO' as const
      })),
      tap(sesion => this.guardarSesion(sesion))
    );
  }

  loginAdmin(credentials: LoginRequest): Observable<SesionActiva> {
    const params = new HttpParams()
      .set('correo', credentials.correo)
      .set('contrasenia', credentials.contrasenia);
    
    return this.http.post<Admin>(ENDPOINTS.ADMINS_LOGIN, null, { params }).pipe(
      map(admin => ({
        id: admin.id,
        nombre: admin.nombre,
        correo: admin.correo,
        rol: 'ADMIN' as const
      })),
      tap(sesion => this.guardarSesion(sesion))
    );
  }

  logout(): void {
    this.sesion = null;
    sessionStorage.removeItem(STORAGE_KEY);
    this.borrarVeterinarioCookie();
  }

  
}