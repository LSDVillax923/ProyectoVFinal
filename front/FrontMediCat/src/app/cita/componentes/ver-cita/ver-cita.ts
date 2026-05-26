import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { CitaRestService } from '../../services/cita-rest.service';
import { AuthService } from '../../../user/services/auth.service';
import { Cita } from '../../../shared/api/backend-contracts';
import { formatearFecha, getClaseEstadoCita, getTextoEstadoCita, nombreCompletoCliente } from '../../../shared/api/model-mappers';
import { Navbar } from '../../../shared/components/navbar/navbar';

@Component({
  selector: 'app-ver-cita',
  standalone: true,
  imports: [CommonModule, RouterLink, Navbar],
  templateUrl: './ver-cita.html',
  styleUrls: ['./ver-cita.css']
})
export class VerCitaComponent implements OnInit {

  cita: Cita | null = null;
  loading = true;
  error: string | null = null;

  esCliente = false;
  esAdmin = false;
  esVeterinario = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private citaService: CitaRestService,
    private authService: AuthService,
  ) {}

  ngOnInit(): void {
    const sesion = this.authService.getSesion();
    this.esCliente     = sesion?.rol === 'CLIENTE';
    this.esAdmin       = sesion?.rol === 'ADMIN';
    this.esVeterinario = sesion?.rol === 'VETERINARIO';

    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.cargarCita(+id);
    } else {
      this.error = 'ID de cita no encontrado';
      this.loading = false;
    }
  }

  cargarCita(id: number): void {
    this.citaService.findById(id).subscribe({
      next: (cita) => {
        // Defensa adicional: si soy CLIENTE, la cita debe ser mía (el backend ya
        // la sirve, pero si por algún motivo no es del cliente la oculto).
        const sesion = this.authService.getSesion();
        if (this.esCliente && sesion && cita.clienteId && cita.clienteId !== sesion.id) {
          this.error = 'No tienes acceso a esta cita.';
          this.loading = false;
          return;
        }
        this.cita = cita;
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Error al cargar la cita';
        console.error(err);
        this.loading = false;
      }
    });
  }

  cancelarCita(): void {
    if (this.cita && confirm('¿Estás seguro de cancelar esta cita?')) {
      this.citaService.cancelar(this.cita.id).subscribe({
        next: () => this.router.navigate([this.rutaListado]),
        error: (err) => {
          alert('Error al cancelar la cita');
          console.error(err);
        }
      });
    }
  }

  /** Ruta del listado al que volver según el rol. */
  get rutaListado(): string {
    return this.esCliente ? '/mis-citas' : '/citas';
  }

  /** Formato largo de la fecha (sin hora). */
  formatearFecha(fecha: string): string { return formatearFecha(fecha, 'larga'); }

  /** Solo la hora HH:mm. */
  formatearHora(fecha: string): string {
    return new Date(fecha).toLocaleTimeString('es-CO', { hour: '2-digit', minute: '2-digit' });
  }

  getClaseEstado(estado: string): string { return getClaseEstadoCita(estado); }
  getTextoEstado(estado: string): string { return getTextoEstadoCita(estado); }

  nombreCompleto(cliente: any): string {
    return cliente ? nombreCompletoCliente(cliente) : 'Sin cliente';
  }

  puedeCancelar(): boolean {
    // Solo permitir cancelar si está PENDIENTE/CONFIRMADA y el rol corresponde.
    if (!this.cita) return false;
    const cancelable = this.cita.estado === 'PENDIENTE' || this.cita.estado === 'CONFIRMADA';
    if (!cancelable) return false;
    if (this.esCliente) {
      // El cliente solo puede cancelar sus propias citas (defensa adicional).
      const sesion = this.authService.getSesion();
      return !!sesion && this.cita.clienteId === sesion.id;
    }
    return true;
  }

  puedeEditar(): boolean {
    // Editar solo admin o vet (cliente solo puede cancelar).
    return this.esAdmin || this.esVeterinario;
  }
}
