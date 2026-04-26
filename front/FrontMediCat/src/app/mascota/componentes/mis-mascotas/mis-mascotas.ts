import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../../user/services/auth.service';
import { MascotaRestService } from '../../services/mascota.service';
import { Mascota } from '../../../shared/api/backend-contracts';
import { Navbar } from '../../../shared/components/navbar/navbar';

@Component({
  selector: 'app-mis-mascotas',
  standalone: true,
  imports: [CommonModule, RouterLink, Navbar],
  templateUrl: './mis-mascotas.html',
  styleUrl: './mis-mascotas.css',
})
export class MisMascotas implements OnInit {
  mascotas: Mascota[] = [];
  navBotones: { label: string; ruta: string; tipo: 'primary' | 'secondary' }[] = [];

  constructor(
    private readonly authService: AuthService,
    private readonly mascotaService: MascotaRestService,
  ) {}

  ngOnInit(): void {
    const sesion = this.authService.getSesion();

    if (sesion && sesion.rol !== 'CLIENTE') {
      this.navBotones = [
        { label: '+ Nueva Mascota', ruta: '/mascotas/nueva', tipo: 'primary' },
      ];
    }

    if (sesion) {
      this.mascotaService.findByClienteId(sesion.id).subscribe({
        next: (mascotas) => { this.mascotas = mascotas; },
        error: () => { this.mascotas = []; },
      });
    }
  }

  private esPerro(especie: string): boolean {
    const e = (especie ?? '').toLowerCase();
    return e === 'perro' || e === 'canino';
  }

  private esGato(especie: string): boolean {
    const e = (especie ?? '').toLowerCase();
    return e === 'gato' || e === 'felino';
  }

  get totalPerros(): number {
    return this.mascotas.filter((m) => this.esPerro(m.especie)).length;
  }

  get totalGatos(): number {
    return this.mascotas.filter((m) => this.esGato(m.especie)).length;
  }

  estadoBadge(estado: string): string {
    const map: Record<string, string> = {
      ACTIVA: 'badge-verde',
      TRATAMIENTO: 'badge-amarillo',
      INACTIVA: 'badge-rojo',
    };
    return map[estado] ?? '';
  }

  especieBadge(especie: string): string {
    if (this.esPerro(especie)) return 'badge-especie--perro';
    if (this.esGato(especie)) return 'badge-especie--gato';
    return 'badge-especie--otro';
  }

  especieLabel(especie: string): string {
    if (this.esPerro(especie)) return 'Perro';
    if (this.esGato(especie)) return 'Gato';
    return especie;
  }
}
