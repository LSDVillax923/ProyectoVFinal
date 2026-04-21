import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { CitaRestService } from '../../services/cita-rest.service';
import { Cita } from '../../../shared/api/backend-contracts';
import { formatearFecha, getClaseEstadoCita, getTextoEstadoCita, nombreCompletoCliente } from '../../../shared/api/model-mappers';

@Component({
  selector: 'app-ver-cita',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './ver-cita.html',
  styleUrls: ['./ver-cita.css']
})
export class VerCitaComponent implements OnInit {
  
  cita: Cita | null = null;
  loading = true;
  error: string | null = null;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private citaService: CitaRestService
  ) {}

  ngOnInit(): void {
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
        next: () => this.router.navigate(['/citas']),
        error: (err) => {
          alert('Error al cancelar la cita');
          console.error(err);
        }
      });
    }
  }

  formatearFecha(fecha: string): string {
    return formatearFecha(fecha, 'larga');
  }

  getClaseEstado(estado: string): string {
    return getClaseEstadoCita(estado);
  }

  getTextoEstado(estado: string): string {
    return getTextoEstadoCita(estado);
  }

  nombreCompleto(cliente: any): string {
    return cliente ? nombreCompletoCliente(cliente) : 'Sin cliente';
  }

  puedeCancelar(): boolean {
    return this.cita?.estado === 'PENDIENTE' || this.cita?.estado === 'CONFIRMADA';
  }
}