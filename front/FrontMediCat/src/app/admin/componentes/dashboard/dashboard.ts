import { CommonModule } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { forkJoin } from 'rxjs';
import { AuthRestService, SesionActiva } from '../../../user/services/auth-rest.service';
import { ClienteRestService } from '../../../cliente/services/cliente.service';
import { MascotaRestService } from '../../../mascota/services/mascota.service';
import { VeterinarioRestService } from '../../../veterinario/services/veterinario-rest.service';
import { TratamientoRestService } from '../../../tratamiento/services/tratamiento-rest.service';
import { DrogaRestService } from '../../../droga/services/droga.service';
import { CitaRestService } from '../../../cita/services/cita-rest.service';
import { CitaResumen, MedicamentoCantidad } from '../../../shared/api/backend-contracts';
import { Navbar } from '../../../shared/components/navbar/navbar';
import {
  formatearMoneda,
  getClaseEstadoCita,
  getTextoEstadoCita,
  getIniciales,
} from '../../../shared/api/model-mappers';

interface CitaDisplay {
  hora: string;
  mascota: string;
  duenio: string;
  tipo: string;
  estado: string;
  estadoClase: string;
}

interface ActividadReciente {
  svgPath: string;
  texto: string;
  hace: string;
}

interface AccesoRapido {
  svgPath: string;
  label: string;
  ruta: string;
}

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink, Navbar],
  templateUrl: './dashboard.html',
  styleUrls: ['./dashboard.css'],
})
export class Dashboard implements OnInit {
  private readonly authService = inject(AuthRestService);
  private readonly clienteService = inject(ClienteRestService);
  private readonly mascotaService = inject(MascotaRestService);
  private readonly veterinarioService = inject(VeterinarioRestService);
  private readonly tratamientoService = inject(TratamientoRestService);
  private readonly drogaService = inject(DrogaRestService);
  private readonly citaService = inject(CitaRestService);
  private readonly router = inject(Router);

  sesion: SesionActiva | null = this.authService.getSesion();

  totalClientes = 0;
  totalMascotas = 0;
  mascotasEnTratamiento = 0;
  citasHoy = 0;

  tratamientosUltimoMes = 0;
  medicamentosUltimoMes: MedicamentoCantidad[] = [];

  veterinariosActivos = 0;
  veterinariosInactivos = 0;

  ventasTotales = 0;
  gananciasTotales = 0;

  topMedicamentos: MedicamentoCantidad[] = [];
  citasProximas: CitaDisplay[] = [];
  actividad: ActividadReciente[] = [];

  loadingMetricas = false;
  errorMetricas = '';

  accesos: AccesoRapido[] = [
    {
      svgPath: '<path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/>',
      label: 'Clientes',
      ruta: '/clientes',
    },
    {
      svgPath: '<path d="M10 5.172C10 3.782 8.423 2.679 6.5 3c-2.823.47-4.113 6.006-4 7 .08.703 1.725 1.722 3.656 1 1.261-.472 1.96-1.45 2.344-2.5"/><path d="M8 14v.5"/><path d="M16 14v.5"/>',
      label: 'Mascotas',
      ruta: '/mascotas',
    },
    {
      svgPath: '<rect x="3" y="4" width="18" height="18" rx="2" ry="2"/><line x1="16" y1="2" x2="16" y2="6"/><line x1="8" y1="2" x2="8" y2="6"/><line x1="3" y1="10" x2="21" y2="10"/>',
      label: 'Citas',
      ruta: '/citas',
    },
    {
      svgPath: '<path d="M19 14c1.49-1.46 3-3.21 3-5.5A5.5 5.5 0 0 0 16.5 3c-1.76 0-3 .5-4.5 2-1.5-1.5-2.74-2-4.5-2A5.5 5.5 0 0 0 2 8.5c0 2.29 1.51 4.04 3 5.5l7 7Z"/>',
      label: 'Veterinarios',
      ruta: '/veterinarios',
    },
    {
      svgPath: '<path d="m10.5 20.5 10-10a4.95 4.95 0 1 0-7-7l-10 10a4.95 4.95 0 1 0 7 7Z"/><path d="m8.5 8.5 7 7"/>',
      label: 'Tratamientos',
      ruta: '/tratamientos',
    },
    {
      svgPath: '<circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="16"/><line x1="8" y1="12" x2="16" y2="12"/>',
      label: 'Nueva Mascota',
      ruta: '/mascotas/nueva',
    },
  ];

  ngOnInit(): void {
    this.cargarMetricas();
    this.cargarActividadReciente();
  }

  private cargarMetricas(): void {
    this.loadingMetricas = true;
    this.errorMetricas = '';

    const hoy = new Date();
    const inicioDia = this.inicioDelDia(hoy);
    const finDia = this.finDelDia(hoy);
    const haceUnMes = this.fechaIso(this.restarMeses(hoy, 1));
    const hoyIso = this.fechaIso(hoy);

    forkJoin({
      totalClientes: this.clienteService.count(),
      totalMascotas: this.mascotaService.count(),
      mascotasEnTratamiento: this.mascotaService.count('TRATAMIENTO'),
      veterinariosActivos: this.veterinarioService.count('activo'),
      veterinariosInactivos: this.veterinarioService.count('inactivo'),
      tratamientosUltimoMes: this.tratamientoService.count(haceUnMes, hoyIso),
      medicamentosUltimoMes: this.tratamientoService.medicamentosVendidos(haceUnMes, hoyIso),
      ventasTotales: this.drogaService.ventasTotales(),
      gananciasTotales: this.drogaService.gananciasTotales(),
      topMedicamentos: this.drogaService.topMasVendidos(3),
      citasHoy: this.citaService.count(inicioDia, finDia),
      citasProximas: this.citaService.proximas(inicioDia, finDia, 5),
    }).subscribe({
      next: (res) => {
        this.totalClientes = res.totalClientes;
        this.totalMascotas = res.totalMascotas;
        this.mascotasEnTratamiento = res.mascotasEnTratamiento;
        this.veterinariosActivos = res.veterinariosActivos;
        this.veterinariosInactivos = res.veterinariosInactivos;
        this.tratamientosUltimoMes = res.tratamientosUltimoMes;
        this.medicamentosUltimoMes = res.medicamentosUltimoMes;
        this.ventasTotales = res.ventasTotales;
        this.gananciasTotales = res.gananciasTotales;
        this.topMedicamentos = res.topMedicamentos;
        this.citasHoy = res.citasHoy;
        this.citasProximas = res.citasProximas.map((c: CitaResumen) => ({
          hora: c.hora,
          mascota: c.mascota,
          duenio: c.duenio,
          tipo: c.tipo,
          estado: getTextoEstadoCita(c.estado),
          estadoClase: getClaseEstadoCita(c.estado),
        }));
        this.loadingMetricas = false;
      },
      error: (err: Error) => {
        console.error('Error cargando métricas del dashboard:', err);
        this.errorMetricas = 'No se pudieron cargar las métricas del dashboard.';
        this.loadingMetricas = false;
      },
    });
  }

  private cargarActividadReciente(): void {
    this.actividad = [
      {
        svgPath: '<path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/>',
        texto: 'Sistema conectado a API REST',
        hace: 'Ahora',
      },
      {
        svgPath: '<path d="M10 5.172C10 3.782 8.423 2.679 6.5 3c-2.823.47-4.113 6.006-4 7 .08.703 1.725 1.722 3.656 1 1.261-.472 1.96-1.45 2.344-2.5"/>',
        texto: 'Dashboard cargado exitosamente',
        hace: 'Ahora',
      },
    ];
  }

  formatearMoneda(valor: number): string {
    return formatearMoneda(valor);
  }

  get inicialesAdmin(): string {
    if (!this.sesion?.nombre) return 'A';
    return getIniciales(this.sesion.nombre);
  }

  cerrarSesion(): void {
    this.authService.logout();
    this.router.navigate(['/inicio/login']);
  }

  irSitio(): void {
    this.router.navigate(['/inicio']);
  }

  get isLoading(): boolean {
    return this.loadingMetricas;
  }

  get tieneCitasProximas(): boolean {
    return this.citasProximas.length > 0;
  }

  get tieneActividad(): boolean {
    return this.actividad.length > 0;
  }

  // ─── Helpers de fechas ────────────────────────────────────────────────
  private fechaIso(d: Date): string {
    return d.toISOString().split('T')[0];
  }

  private inicioDelDia(d: Date): string {
    const fecha = new Date(d);
    fecha.setHours(0, 0, 0, 0);
    return this.toLocalDateTimeIso(fecha);
  }

  private finDelDia(d: Date): string {
    const fecha = new Date(d);
    fecha.setHours(23, 59, 59, 999);
    return this.toLocalDateTimeIso(fecha);
  }

  private toLocalDateTimeIso(fecha: Date): string {
    const pad = (n: number) => n.toString().padStart(2, '0');
    return `${fecha.getFullYear()}-${pad(fecha.getMonth() + 1)}-${pad(fecha.getDate())}T${pad(fecha.getHours())}:${pad(fecha.getMinutes())}:${pad(fecha.getSeconds())}`;
  }

  private restarMeses(d: Date, meses: number): Date {
    const fecha = new Date(d);
    fecha.setMonth(fecha.getMonth() - meses);
    return fecha;
  }
}
