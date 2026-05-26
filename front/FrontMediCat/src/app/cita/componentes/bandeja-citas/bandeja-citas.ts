import { CommonModule, DatePipe } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { CitaRestService } from '../../services/cita-rest.service';
import { Cita } from '../../../shared/api/backend-contracts';
import { Navbar } from '../../../shared/components/navbar/navbar';

@Component({
  selector: 'app-bandeja-citas',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, Navbar, DatePipe],
  templateUrl: './bandeja-citas.html',
  styleUrls: ['./bandeja-citas.css'],
})
export class BandejaCitasComponent implements OnInit {

  pendientes: Cita[] = [];
  cargando = false;
  error: string | null = null;
  mensaje: string | null = null;

  // Modal de rechazo
  rechazoAbierto = false;
  rechazoCitaId: number | null = null;
  rechazoMotivo = '';

  constructor(private readonly citaService: CitaRestService) {}

  ngOnInit(): void {
    this.cargar();
  }

  cargar(): void {
    this.cargando = true;
    this.error = null;
    this.citaService.findPendientes().subscribe({
      next: (citas) => {
        this.pendientes = citas;
        this.cargando = false;
      },
      error: (err) => {
        this.error = err.error?.message || 'No se pudieron cargar las solicitudes.';
        this.cargando = false;
      },
    });
  }

  aprobar(cita: Cita): void {
    if (!confirm(`¿Aprobar la solicitud de ${cita.clienteNombre} para ${cita.mascotaNombre}?`)) return;
    this.error = null;
    this.citaService.aprobar(cita.id).subscribe({
      next: () => {
        // Optimistic update: la cita aprobada ya no está PENDIENTE, la quitamos
        // de la lista visible al instante para que la UI refleje el cambio sin esperar
        // al refetch. Luego forzamos un recargado para que quede consistente con backend.
        this.pendientes = this.pendientes.filter((c) => c.id !== cita.id);
        this.mensaje = 'Solicitud aprobada y notificación enviada al cliente.';
        this.cargar();
      },
      error: (err) => {
        this.mensaje = null;
        this.error = err.error?.message || 'Error al aprobar la solicitud.';
      },
    });
  }

  abrirRechazo(cita: Cita): void {
    this.rechazoCitaId = cita.id;
    this.rechazoMotivo = '';
    this.rechazoAbierto = true;
  }

  cerrarRechazo(): void {
    this.rechazoAbierto = false;
    this.rechazoCitaId = null;
    this.rechazoMotivo = '';
  }

  confirmarRechazo(): void {
    if (this.rechazoCitaId == null) return;
    const idRechazado = this.rechazoCitaId;
    this.error = null;
    this.citaService.rechazar(idRechazado, this.rechazoMotivo.trim() || undefined).subscribe({
      next: () => {
        this.pendientes = this.pendientes.filter((c) => c.id !== idRechazado);
        this.mensaje = 'Solicitud rechazada y notificación enviada al cliente.';
        this.cerrarRechazo();
        this.cargar();
      },
      error: (err) => {
        this.mensaje = null;
        this.error = err.error?.message || 'Error al rechazar la solicitud.';
        this.cerrarRechazo();
      },
    });
  }
}
