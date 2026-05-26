import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CitaRestService } from '../../services/cita-rest.service';
import { AuthService } from '../../../user/services/auth.service';
import { Cita } from '../../../shared/api/backend-contracts';
import { formatearFecha, getClaseEstadoCita, getTextoEstadoCita } from '../../../shared/api/model-mappers';
import { Navbar } from '../../../shared/components/navbar/navbar';

/** Una celda del grid semanal (un slot de 30 min de un día). */
interface CeldaHorario {
  inicio: Date;
  fin: Date;
  citas: Cita[];
}

@Component({
  selector: 'app-listar-citas',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule, Navbar],
  templateUrl: './listar-citas.html',
  styleUrls: ['./listar-citas.css']
})
export class ListarCitasComponent implements OnInit {

  citas: Cita[] = [];
  citasFiltradas: Cita[] = [];
  loading = true;
  error: string | null = null;

  esCliente = false;
  esVeterinario = false;
  esAdmin = false;

  filtroFecha = '';
  filtroEstado = '';
  filtroVeterinario = '';

  estados = ['PENDIENTE', 'CONFIRMADA', 'REALIZADA', 'CANCELADA'];

  // ── Vista (lista | horario) ──────────────────────────────────────────
  vista: 'lista' | 'horario' = 'lista';
  /** Lunes de la semana visible cuando vista === 'horario'. */
  semanaLunes: Date = this.lunesDe(new Date());

  /** Slots verticales del grid: 8:00 hasta 17:30 en pasos de 30 min. */
  readonly horas: string[] = (() => {
    const out: string[] = [];
    for (let h = 8; h < 18; h++) {
      out.push(`${String(h).padStart(2, '0')}:00`);
      out.push(`${String(h).padStart(2, '0')}:30`);
    }
    return out;
  })();

  readonly diasSemana = ['Lunes', 'Martes', 'Miércoles', 'Jueves', 'Viernes'];

  constructor(
    private citaService: CitaRestService,
    private authService: AuthService,
  ) {}

  ngOnInit(): void {
    const sesion = this.authService.getSesion();
    this.esCliente     = sesion?.rol === 'CLIENTE';
    this.esVeterinario = sesion?.rol === 'VETERINARIO';
    this.esAdmin       = sesion?.rol === 'ADMIN';

    this.cargarCitas();
  }

  cargarCitas(): void {
    this.loading = true;
    const sesion = this.authService.getSesion();

    let req$;
    if (this.esCliente && sesion) {
      req$ = this.citaService.findByClienteId(sesion.id);
    } else if (this.esVeterinario && sesion) {
      req$ = this.citaService.findByVeterinarioId(sesion.id);
    } else {
      req$ = this.citaService.findAll();
    }

    req$.subscribe({
      next: (citas) => {
        this.citas = citas;
        this.aplicarFiltros();
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Error al cargar las citas';
        console.error(err);
        this.loading = false;
      },
    });
  }

  aplicarFiltros(): void {
    this.citasFiltradas = this.citas.filter((cita) => {
      if (this.filtroFecha) {
        const fechaCita = cita.fechaInicio.split('T')[0];
        if (fechaCita !== this.filtroFecha) return false;
      }
      if (this.filtroEstado && cita.estado !== this.filtroEstado) return false;
      if (this.filtroVeterinario) {
        const nombreVet = (cita.veterinarioNombre ?? cita.veterinario?.nombre ?? '').toLowerCase();
        if (!nombreVet.includes(this.filtroVeterinario.toLowerCase())) return false;
      }
      return true;
    });
  }

  limpiarFiltros(): void {
    this.filtroFecha = '';
    this.filtroEstado = '';
    this.filtroVeterinario = '';
    this.citasFiltradas = [...this.citas];
  }

  cancelarCita(id: number): void {
    if (confirm('¿Estás seguro de cancelar esta cita?')) {
      this.citaService.cancelar(id).subscribe({
        next: () => this.cargarCitas(),
        error: (err) => { alert('Error al cancelar la cita'); console.error(err); },
      });
    }
  }

  eliminarCita(id: number): void {
    if (confirm('¿Estás seguro de eliminar esta cita permanentemente?')) {
      this.citaService.delete(id).subscribe({
        next: () => this.cargarCitas(),
        error: (err) => { alert('Error al eliminar la cita'); console.error(err); },
      });
    }
  }

  formatearFecha(fecha: string): string { return formatearFecha(fecha, 'larga'); }

  formatearHora(fecha: string): string {
    const date = new Date(fecha);
    return date.toLocaleTimeString('es-CO', { hour: '2-digit', minute: '2-digit' });
  }

  getClaseEstado(estado: string): string { return getClaseEstadoCita(estado); }
  getTextoEstado(estado: string): string { return getTextoEstadoCita(estado); }

  nombreMascota(c: Cita): string { return c.mascotaNombre ?? c.mascota?.nombre ?? 'N/A'; }
  nombreVeterinario(c: Cita): string { return c.veterinarioNombre ?? c.veterinario?.nombre ?? 'No asignado'; }
  nombreCliente(c: Cita): string { return c.clienteNombre ?? (c.cliente ? `${c.cliente.nombre} ${c.cliente.apellido}` : ''); }

  // ─────────────────────────────────────────────────────────────────────
  // Vista horario semanal (tipo horario escolar)
  // ─────────────────────────────────────────────────────────────────────

  cambiarVista(v: 'lista' | 'horario'): void {
    this.vista = v;
  }

  semanaAnterior(): void { this.semanaLunes = this.addDias(this.semanaLunes, -7); }
  semanaSiguiente(): void { this.semanaLunes = this.addDias(this.semanaLunes, 7); }
  semanaHoy(): void { this.semanaLunes = this.lunesDe(new Date()); }

  get rangoSemanaLabel(): string {
    const fin = this.addDias(this.semanaLunes, 4);
    const fmt = (d: Date) => d.toLocaleDateString('es-CO', { day: '2-digit', month: 'short' });
    return `${fmt(this.semanaLunes)} – ${fmt(fin)}`;
  }

  /** Fecha del día N (0=Lunes, 4=Viernes) de la semana visible. */
  diaDeSemana(idx: number): Date { return this.addDias(this.semanaLunes, idx); }

  /** Citas que arrancan en este slot (filtradas) — un slot puede tener varias si hay solapamiento. */
  citasEnSlot(idxDia: number, hora: string): Cita[] {
    const dia = this.diaDeSemana(idxDia);
    const [h, m] = hora.split(':').map(Number);
    const slotInicio = new Date(dia);
    slotInicio.setHours(h, m, 0, 0);
    const slotFin = new Date(slotInicio.getTime() + 30 * 60_000);

    return this.citasFiltradas.filter((c) => {
      const ini = new Date(c.fechaInicio);
      return ini >= slotInicio && ini < slotFin;
    });
  }

  private lunesDe(d: Date): Date {
    const x = new Date(d);
    x.setHours(0, 0, 0, 0);
    const dow = x.getDay();           // 0=domingo, 1=lunes, ..., 6=sábado
    const offset = dow === 0 ? -6 : 1 - dow;
    x.setDate(x.getDate() + offset);
    return x;
  }

  private addDias(d: Date, n: number): Date {
    const x = new Date(d);
    x.setDate(x.getDate() + n);
    return x;
  }
}
