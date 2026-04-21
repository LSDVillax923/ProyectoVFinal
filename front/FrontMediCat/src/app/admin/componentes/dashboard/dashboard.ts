import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { AuthRestService, SesionActiva } from '../../../user/services/auth-rest.service';
import { ClienteRestService } from '../../../cliente/services/cliente.service';
import { MascotaRestService } from '../../../mascota/services/mascota.service';
import { CitaRestService } from '../../../cita/services/cita-rest.service';
import { Cita, Cliente, Mascota } from '../../../shared/api/backend-contracts';
import { Navbar } from '../../../shared/components/navbar/navbar';
import {
  formatearFecha,
  getClaseEstadoCita,
  getTextoEstadoCita,
  nombreCompletoCliente,
  getIniciales
} from '../../../shared/api/model-mappers';

// ============================================
// INTERFACES LOCALES
// ============================================

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

// ============================================
// COMPONENTE
// ============================================

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink, Navbar],
  templateUrl: './dashboard.html',
  styleUrls: ['./dashboard.css']
})
export class Dashboard implements OnInit {
  
  // Sesión
  sesion: SesionActiva | null = null;
  
  // Estadísticas
  totalClientes = 0;
  totalMascotas = 0;
  citasHoy = 0;
  emergencias = 0;
  
  // Datos para la vista
  citasProximas: CitaDisplay[] = [];
  actividad: ActividadReciente[] = [];
  
  // Estado de carga
  loadingClientes = false;
  loadingMascotas = false;
  loadingCitas = false;
  
  // Accesos rápidos
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
      svgPath: '<circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="12"/><line x1="12" y1="16" x2="12.01" y2="16"/>',
      label: 'Emergencias',
      ruta: '/citas',
    },
    {
      svgPath: '<circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="16"/><line x1="8" y1="12" x2="16" y2="12"/>',
      label: 'Nueva Mascota',
      ruta: '/mascotas/nueva',
    },
    {
      svgPath: '<path d="M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><line x1="19" y1="8" x2="19" y2="14"/><line x1="22" y1="11" x2="16" y2="11"/>',
      label: 'Nuevo Cliente',
      ruta: '/clientes/nuevo',
    },
  ];

  // ============================================
  // CONSTRUCTOR
  // ============================================

  constructor(
    private readonly authService: AuthRestService,
    private readonly clienteService: ClienteRestService,
private readonly mascotaService: MascotaRestService,    private readonly citaService: CitaRestService,
    private readonly router: Router
  ) {
    this.sesion = this.authService.getSesion();
  }

  // ============================================
  // LIFECYCLE
  // ============================================

  ngOnInit(): void {
    this.cargarTotales();
    this.cargarCitasProximas();
    this.cargarActividadReciente();
  }

  // ============================================
  // CARGA DE DATOS
  // ============================================

  private cargarTotales(): void {
    // Cargar total de clientes
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

    // Cargar total de mascotas y emergencias
    this.loadingMascotas = true;
    this.mascotaService.findAll().subscribe({
      next: (mascotas: Mascota[]) => {
        this.totalMascotas = mascotas.length;
        this.emergencias = mascotas.filter((m: Mascota) => m.estado === 'TRATAMIENTO').length;
        this.loadingMascotas = false;
      },
      error: (err: Error) => {
        console.error('Error cargando mascotas:', err);
        this.totalMascotas = 0;
        this.emergencias = 0;
        this.loadingMascotas = false;
      }
    });
  }

  private cargarCitasProximas(): void {
    this.loadingCitas = true;
    
    // Obtener citas de hoy
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

  private cargarActividadReciente(): void {
    // Por ahora datos estáticos - Se puede conectar a un endpoint de auditoría/logs
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

  // ============================================
  // MÉTODOS PÚBLICOS (usados en el template)
  // ============================================

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

  // ============================================
  // GETTERS PARA EL TEMPLATE
  // ============================================

  get isLoading(): boolean {
    return this.loadingClientes || this.loadingMascotas || this.loadingCitas;
  }

  get tieneCitasProximas(): boolean {
    return this.citasProximas.length > 0;
  }

  get tieneActividad(): boolean {
    return this.actividad.length > 0;
  }
}