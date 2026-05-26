import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map, tap } from 'rxjs/operators';
import { LoginRequest, Cliente, ClienteRequest, LoginResponse } from '../../shared/api/backend-contracts';
import { ENDPOINTS } from '../../shared/api/rest-endpoints';

export interface SesionActiva {
  id: number;
  nombre: string;
  correo: string;
  rol: 'ADMIN' | 'VETERINARIO' | 'CLIENTE';
}

const STORAGE_KEY = 'vet_session';
const TOKEN_KEY = 'vet_token';
const COOKIE_VET_KEY = 'vet_logueado';
const COOKIE_MAX_AGE_SECONDS = 60 * 60 * 8;

@Injectable({ providedIn: 'root' })
export class AuthRestService {

  private sesion: SesionActiva | null = null;
  private token: string | null = null;

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
    this.token = sessionStorage.getItem(TOKEN_KEY);
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

  getToken(): string | null {
    return this.token;
  }

// Agregar este método a AuthRestService

/**
 * Login unificado - detecta automáticamente el tipo de usuario
 */
login(credentials: LoginRequest, tipoUsuario: 'CLIENTE' | 'VETERINARIO' | 'ADMIN'): Observable<SesionActiva> {
   return this.http.post<LoginResponse>(ENDPOINTS.AUTH_LOGIN, credentials).pipe(
    tap((response) => {
      this.token = response.token ?? null;
      if (this.token) {
        sessionStorage.setItem(TOKEN_KEY, this.token);
      } else {
        sessionStorage.removeItem(TOKEN_KEY);
      }
    }),
    map((response) => ({
      id: response.id,
      nombre: response.nombre,
      correo: response.correo,
      rol: response.rol
    })),
    tap((sesion) => {
      if (sesion.rol !== tipoUsuario) {
        throw new Error('El tipo de usuario no coincide con la cuenta.');
      }
      this.guardarSesion(sesion);
    })
  );
}

  register(data: ClienteRequest): Observable<Cliente> {
    return this.http.post<Cliente>(ENDPOINTS.CLIENTES, data);
  }

  /** Inicia el flujo "olvidé mi contraseña". El backend siempre devuelve 200. */
  forgotPassword(correo: string): Observable<{ mensaje: string }> {
    return this.http.post<{ mensaje: string }>(ENDPOINTS.AUTH_FORGOT_PASSWORD, { correo });
  }

  /** Completa el reset con el token recibido por email. */
  resetPassword(token: string, contrasenia: string): Observable<{ mensaje: string }> {
    return this.http.post<{ mensaje: string }>(ENDPOINTS.AUTH_RESET_PASSWORD, { token, contrasenia });
  }

   logout(): void {
    this.sesion = null;
    this.token = null;
    sessionStorage.removeItem(STORAGE_KEY);
    sessionStorage.removeItem(TOKEN_KEY);
    this.borrarVeterinarioCookie();
  }

  
}