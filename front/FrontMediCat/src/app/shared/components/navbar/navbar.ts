import { CommonModule } from '@angular/common';
import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';
import { Subscription, interval } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { AuthService, SesionActiva } from '../../../user/services/auth.service';
import { NotificacionRestService } from '../../services/notificacion-rest.service';
import { Notificacion } from '../../api/backend-contracts';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterLinkActive],
  templateUrl: './navbar.html',
  styleUrl: './navbar.css',
})
export class Navbar implements OnInit, OnDestroy {
  @Input() botones: any[] = [];

  sesion: SesionActiva | null = null;

  // Campana (solo CLIENTE)
  notificaciones: Notificacion[] = [];
  noLeidas = 0;
  campanaAbierta = false;
  private pollSub?: Subscription;

  constructor(
    private readonly authService: AuthService,
    private readonly router: Router,
    private readonly notificacionService: NotificacionRestService,
  ) {
    this.sesion = this.authService.getSesion();
  }

  ngOnInit(): void {
    if (this.esCliente && this.sesion) {
      this.cargarNotificaciones();
      // Refresca cada 60 s mientras el cliente esté en la app.
      this.pollSub = interval(60000)
        .pipe(switchMap(() => this.notificacionService.findByClienteId(this.sesion!.id)))
        .subscribe({
          next: (lista) => {
            this.notificaciones = lista;
            this.noLeidas = lista.filter((n) => !n.leida).length;
          },
          error: () => {/* silencioso: si falla el polling no rompemos navbar */},
        });
    }
  }

  ngOnDestroy(): void {
    this.pollSub?.unsubscribe();
  }

  private cargarNotificaciones(): void {
    if (!this.sesion) return;
    this.notificacionService.findByClienteId(this.sesion.id).subscribe({
      next: (lista) => {
        this.notificaciones = lista;
        this.noLeidas = lista.filter((n) => !n.leida).length;
      },
      error: () => {/* sin notificaciones */},
    });
  }

  toggleCampana(): void {
    this.campanaAbierta = !this.campanaAbierta;
    if (this.campanaAbierta) this.cargarNotificaciones();
  }

  marcarLeida(n: Notificacion): void {
    if (n.leida) return;
    this.notificacionService.marcarLeida(n.id).subscribe({
      next: (actualizada) => {
        const idx = this.notificaciones.findIndex((x) => x.id === actualizada.id);
        if (idx >= 0) this.notificaciones[idx] = actualizada;
        this.noLeidas = this.notificaciones.filter((x) => !x.leida).length;
      },
      error: () => {/* noop */},
    });
  }

  iconoNotificacion(tipo: string): string {
    switch (tipo) {
      case 'APROBADA':     return '✔';
      case 'RECHAZADA':    return '✖';
      case 'RECORDATORIO': return '⏰';
      default:             return '•';
    }
  }

  get esAdmin(): boolean { return this.sesion?.rol === 'ADMIN'; }
  get esVeterinario(): boolean { return this.sesion?.rol === 'VETERINARIO'; }
  get esCliente(): boolean { return this.sesion?.rol === 'CLIENTE'; }

  get rutaPerfil(): string {
    switch (this.sesion?.rol) {
      case 'ADMIN':       return '/perfil-admin';
      case 'VETERINARIO': return '/perfil-veterinario';
      case 'CLIENTE':     return '/perfil';
      default:            return '/perfil';
    }
  }

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
