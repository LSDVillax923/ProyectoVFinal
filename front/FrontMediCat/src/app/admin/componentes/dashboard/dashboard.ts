import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { forkJoin } from 'rxjs';
import { AuthRestService, SesionActiva } from '../../../user/services/auth-rest.service';
import { ClienteRestService } from '../../../cliente/services/cliente.service';
import { MascotaRestService } from '../../../mascota/services/mascota.service';
import { CitaRestService } from '../../../cita/services/cita-rest.service';
import { VeterinarioRestService } from '../../../veterinario/services/veterinario-rest.service';
import { TratamientoRestService } from '../../../tratamiento/services/tratamiento-rest.service';
import { DrogaRestService } from '../../../droga/services/droga.service';
import { Cita, Cliente, Droga, Mascota, Tratamiento, Veterinario } from '../../../shared/api/backend-contracts';
import { Navbar } from '../../../shared/components/navbar/navbar';
import {
  formatearFecha,
  formatearMoneda,
  getClaseEstadoCita,
  getTextoEstadoCita,
  nombreCompletoCliente,
  getIniciales
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

interface MedicamentoCantidad {
  nombre: string;
  cantidad: number;
}

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink, Navbar],
  templateUrl: './dashboard.html',
  styleUrls: ['./dashboard.css']
})
export class Dashboard implements OnInit {

  sesion: SesionActiva | null = null;

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

  loadingClientes = false;
  loadingMascotas = false;
  loadingCitas = false;
  loadingMetricas = false;

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

  constructor(
    private readonly authService: AuthRestService,
    private readonly clienteService: ClienteRestService,
    private readonly mascotaService: MascotaRestService,
    private readonly citaService: CitaRestService,
    private readonly veterinarioService: VeterinarioRestService,
    private readonly tratamientoService: TratamientoRestService,
    private readonly drogaService: DrogaRestService,
    private readonly router: Router
  ) {
    this.sesion = this.authService.getSesion();
  }

  ngOnInit(): void {
    this.cargarTotales();
    this.cargarCitasProximas();
    this.cargarMetricasNegocio();
    this.cargarActividadReciente();
  }

  private cargarTotales(): void {
    this.loadingClientes = true;
    this.clienteService.findAll().subscribe({
      next: (clientes: Cliente[]) => {
        this.totalClientes = clientes.length;
        this.loadingClientes = false;
      },
      error: (err: Error) => {
        console.error('Error cargando clientes:', err);
        this.totalClientes = 0;
        this.loadingClientes = false;
      }
    });

    this.loadingMascotas = true;
    this.mascotaService.findAll().subscribe({
      next: (mascotas: Mascota[]) => {
        this.totalMascotas = mascotas.length;
        this.mascotasEnTratamiento = mascotas.filter((m: Mascota) => m.estado === 'TRATAMIENTO').length;
        this.loadingMascotas = false;
      },
      error: (err: Error) => {
        console.error('Error cargando mascotas:', err);
        this.totalMascotas = 0;
        this.mascotasEnTratamiento = 0;
        this.loadingMascotas = false;
      }
    });
  }

  private cargarCitasProximas(): void {
    this.loadingCitas = true;
    const hoy = new Date();
    const inicio = new Date(hoy);
    inicio.setHours(0, 0, 0, 0);
    const fin = new Date(hoy);
    fin.setHours(23, 59, 59, 999);

    this.citaService.findAll({
      inicio: inicio.toISOString(),
      fin: fin.toISOString()
    }).subscribe({
      next: (citas: Cita[]) => {
        this.citasHoy = citas.length;
        this.citasProximas = citas
          .filter((c: Cita) => c.estado === 'PENDIENTE' || c.estado === 'CONFIRMADA')
          .slice(0, 5)
          .map((cita: Cita) => ({
            hora: formatearFecha(cita.fechaInicio, 'hora'),
            mascota: cita.mascota?.nombre || 'Sin mascota',
            duenio: nombreCompletoCliente(cita.cliente),
            tipo: cita.motivo,
            estado: getTextoEstadoCita(cita.estado),
            estadoClase: getClaseEstadoCita(cita.estado)
          }));
        this.loadingCitas = false;
      },
      error: (err: Error) => {
        console.error('Error cargando citas:', err);
        this.citasHoy = 0;
        this.citasProximas = [];
        this.loadingCitas = false;
      }
    });
  }

  private cargarMetricasNegocio(): void {
    this.loadingMetricas = true;
    forkJoin({
      veterinarios: this.veterinarioService.getAll(),
      tratamientos: this.tratamientoService.findAll(),
      drogas: this.drogaService.getAll(),
    }).subscribe({
      next: ({ veterinarios, tratamientos, drogas }) => {
        this.calcularVeterinarios(veterinarios);
        this.calcularTratamientosUltimoMes(tratamientos);
        this.calcularVentasYGanancias(drogas);
        this.calcularTopMedicamentos(drogas);
        this.loadingMetricas = false;
      },
      error: (err: Error) => {
        console.error('Error cargando métricas del negocio:', err);
        this.loadingMetricas = false;
      }
    });
  }

  private calcularVeterinarios(veterinarios: Veterinario[]): void {
    this.veterinariosActivos = veterinarios.filter((v) => v.estado === 'activo').length;
    this.veterinariosInactivos = veterinarios.filter((v) => v.estado === 'inactivo').length;
  }

  private calcularTratamientosUltimoMes(tratamientos: Tratamiento[]): void {
    const hoy = new Date();
    const haceUnMes = new Date(hoy);
    haceUnMes.setMonth(hoy.getMonth() - 1);

    const recientes = tratamientos.filter((t) => {
      if (!t.fecha) return false;
      const fecha = new Date(t.fecha);
      return !isNaN(fecha.getTime()) && fecha >= haceUnMes && fecha <= hoy;
    });

    this.tratamientosUltimoMes = recientes.length;

    const conteoMedicamentos = new Map<string, number>();
    recientes.forEach((t) => {
      (t.drogas ?? []).forEach((td) => {
        const nombre = td.droga?.nombre ?? 'Sin nombre';
        const cantidad = td.cantidad ?? 0;
        conteoMedicamentos.set(nombre, (conteoMedicamentos.get(nombre) ?? 0) + cantidad);
      });
    });

    this.medicamentosUltimoMes = Array.from(conteoMedicamentos.entries())
      .map(([nombre, cantidad]) => ({ nombre, cantidad }))
      .sort((a, b) => b.cantidad - a.cantidad);
  }

  private calcularVentasYGanancias(drogas: Droga[]): void {
    this.ventasTotales = drogas.reduce(
      (acc, d) => acc + (d.precioVenta ?? 0) * (d.unidadesVendidas ?? 0),
      0,
    );
    this.gananciasTotales = drogas.reduce(
      (acc, d) => acc + ((d.precioVenta ?? 0) - (d.precioCompra ?? 0)) * (d.unidadesVendidas ?? 0),
      0,
    );
  }

  private calcularTopMedicamentos(drogas: Droga[]): void {
    this.topMedicamentos = [...drogas]
      .filter((d) => (d.unidadesVendidas ?? 0) > 0)
      .sort((a, b) => (b.unidadesVendidas ?? 0) - (a.unidadesVendidas ?? 0))
      .slice(0, 3)
      .map((d) => ({ nombre: d.nombre, cantidad: d.unidadesVendidas ?? 0 }));
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
      }
    ];
  }

  formatearMoneda(valor: number): string {
    return formatearMoneda(valor);
  }

  get inicialesAdmin(): string {
    if (!this.sesion?.nombre) return 'A';
    return getIniciales(this.sesion.nombre);
  }

  estadoClase(estado: string): string {
    const map: Record<string, string> = {
      'Confirmada': 'estado-confirmada',
      'Pendiente': 'estado-pendiente',
      'Emergencia': 'estado-emergencia',
      'Realizada': 'estado-realizada',
      'Cancelada': 'estado-cancelada',
    };
    return map[estado] ?? '';
  }

  cerrarSesion(): void {
    this.authService.logout();
    this.router.navigate(['/inicio/login']);
  }

  irSitio(): void {
    this.router.navigate(['/inicio']);
  }

  get isLoading(): boolean {
    return this.loadingClientes || this.loadingMascotas || this.loadingCitas || this.loadingMetricas;
  }

  get tieneCitasProximas(): boolean {
    return this.citasProximas.length > 0;
  }

  get tieneActividad(): boolean {
    return this.actividad.length > 0;
  }
}
