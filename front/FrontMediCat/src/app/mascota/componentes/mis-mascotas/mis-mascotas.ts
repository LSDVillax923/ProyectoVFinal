import { CommonModule } from '@angular/common';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { Subject, Subscription, interval, takeUntil } from 'rxjs';
import { AuthService } from '../../../user/services/auth.service';
import { MascotaRestService } from '../../services/mascota.service';
import { Cita, Mascota } from '../../../shared/api/backend-contracts';
import { Navbar } from '../../../shared/components/navbar/navbar';
import { urlFotoMascota } from '../../../shared/utils/helpers';
import { ClienteRestService } from '../../../cliente/services/cliente.service';
import { CitaRestService } from '../../../cita/services/cita-rest.service';

@Component({
  selector: 'app-mis-mascotas',
  standalone: true,
  imports: [CommonModule, RouterLink, Navbar],
  templateUrl: './mis-mascotas.html',
  styleUrl: './mis-mascotas.css',
})
export class MisMascotas implements OnInit, OnDestroy {
  mascotas: Mascota[] = [];
  citasProximas = 0;
  navBotones: { label: string; ruta: string; tipo: 'primary' | 'secondary' }[] = [];

  private clienteIdActivo: number | null = null;
  private readonly destroy$ = new Subject<void>();
  private refreshSub?: Subscription;

  constructor(
    private readonly authService: AuthService,
    private readonly mascotaService: MascotaRestService,
    private readonly clienteService: ClienteRestService,
    private readonly citaService: CitaRestService,
  ) {}

  ngOnInit(): void {
    const sesion = this.authService.getSesion();

    if (sesion && sesion.rol !== 'CLIENTE') {
      this.navBotones = [
        { label: '+ Nueva Mascota', ruta: '/mascotas/nueva', tipo: 'primary' },
      ];
    }

    if (sesion) {
      this.cargarMascotasDeSesion(sesion.id, sesion.correo);
    }

    // Refresca la tarjeta de citas próximas cada 30s para que el cliente la
    // vea actualizada cuando admin/vet le asignen o muevan una cita.
    this.refreshSub = interval(30_000)
      .pipe(takeUntil(this.destroy$))
      .subscribe(() => this.recargarCitasProximas());
  }

  ngOnDestroy(): void {
    this.refreshSub?.unsubscribe();
    this.destroy$.next();
    this.destroy$.complete();
  }

  private cargarMascotasDeSesion(clienteId: number, identificador: string): void {
    this.mascotaService.findByClienteId(clienteId).subscribe({
      next: (mascotas) => {
        if (mascotas.length > 0) {
          this.mascotas = mascotas;
          this.fijarClienteActivo(clienteId);
          return;
        }
        this.buscarMascotasPorIdentificador(identificador);
      },
      error: () => this.buscarMascotasPorIdentificador(identificador),
    });
  }

  private buscarMascotasPorIdentificador(identificador: string): void {
    if (!identificador) {
      this.mascotas = [];
      return;
    }

    this.clienteService.findAll({ query: identificador }).subscribe({
      next: (clientes) => {
        const cliente = clientes.find((c) => c.cedula === identificador || c.correo === identificador);
        if (!cliente?.id) {
          this.mascotas = [];
          return;
        }
        this.mascotaService.findByClienteId(cliente.id).subscribe({
          next: (mascotas) => {
            this.mascotas = mascotas;
            this.fijarClienteActivo(cliente.id!);
          },
          error: () => { this.mascotas = []; },
        });
      },
      error: () => { this.mascotas = []; },
    });
  }

  private fijarClienteActivo(clienteId: number): void {
    this.clienteIdActivo = clienteId;
    this.recargarCitasProximas();
  }

  private recargarCitasProximas(): void {
    if (this.clienteIdActivo == null) return;
    this.citaService.findByClienteId(this.clienteIdActivo).subscribe({
      next: (citas: Cita[]) => {
        const ahora = new Date();
        this.citasProximas = citas.filter((c) =>
          (c.estado === 'PENDIENTE' || c.estado === 'CONFIRMADA') &&
          new Date(c.fechaInicio) >= ahora,
        ).length;
      },
      error: () => { /* mantenemos el último valor conocido */ },
    });
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

  fotoUrl(foto?: string): string {
    return urlFotoMascota(foto);
  }
}
