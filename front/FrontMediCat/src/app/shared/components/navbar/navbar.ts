import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService, SesionActiva } from '../../../user/services/auth.service';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterLinkActive],
  templateUrl: './navbar.html',
  styleUrl: './navbar.css',
})
export class Navbar {
  @Input() botones: any[] = [];
  sesion: SesionActiva | null = null;

  constructor(
    private readonly authService: AuthService,
    private readonly router: Router,
  ) {
    this.sesion = this.authService.getSesion();
  }

  get esAdmin(): boolean {
    return this.sesion?.rol === 'ADMIN';
  }

  get esVeterinario(): boolean {
    return this.sesion?.rol === 'VETERINARIO';
  }

  get esCliente(): boolean {
    return this.sesion?.rol === 'CLIENTE';
  }

  /** Ruta de perfil según el rol */
  get rutaPerfil(): string {
    switch (this.sesion?.rol) {
      case 'ADMIN':       return '/perfil-admin';
      case 'VETERINARIO': return '/perfil-veterinario';
      case 'CLIENTE':     return '/perfil';
      default:            return '/perfil';
    }
  }

  /** Etiqueta visible del rol */
  get etiquetaRol(): string {
    switch (this.sesion?.rol) {
      case 'ADMIN':       return 'Administrador';
      case 'VETERINARIO': return 'Veterinario';
      case 'CLIENTE':     return 'Cliente';
      default:            return '';
    }
  }

  get iniciales(): string {
    if (!this.sesion) return '';
    return this.sesion.nombre
      .split(' ')
      .slice(0, 2)
      .map((p) => p[0].toUpperCase())
      .join('');
  }

  cerrarSesion(): void {
    this.authService.logout();
    this.router.navigate(['/inicio/login']);
  }
}