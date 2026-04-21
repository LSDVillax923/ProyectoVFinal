import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CitaRestService } from '../../services/cita-rest.service';
import { Cita } from '../../../shared/api/backend-contracts';
import { formatearFecha, getClaseEstadoCita, getTextoEstadoCita, nombreCompletoCliente } from '../../../shared/api/model-mappers';

@Component({
  selector: 'app-listar-citas',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule],
  templateUrl: './listar-citas.html',
  styleUrls: ['./listar-citas.css']
})
export class ListarCitasComponent implements OnInit {
  
  citas: Cita[] = [];
  citasFiltradas: Cita[] = [];
  loading = true;
  error: string | null = null;
  
  // Filtros
  filtroFecha: string = '';
  filtroEstado: string = '';
  filtroVeterinario: string = '';
  
  // Opciones para selects
  estados = ['PENDIENTE', 'CONFIRMADA', 'REALIZADA', 'CANCELADA'];

  constructor(private citaService: CitaRestService) {}

  ngOnInit(): void {
    this.cargarCitas();
  }

  cargarCitas(): void {
    this.loading = true;
    this.citaService.findAll().subscribe({
      next: (citas) => {
        this.citas = citas;
        this.citasFiltradas = [...citas];
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Error al cargar las citas';
        console.error(err);
        this.loading = false;
      }
    });
  }

  aplicarFiltros(): void {
    this.citasFiltradas = this.citas.filter(cita => {
      // Filtro por fecha
      if (this.filtroFecha) {
        const fechaCita = cita.fechaInicio.split('T')[0];
        if (fechaCita !== this.filtroFecha) return false;
      }
      
      // Filtro por estado
      if (this.filtroEstado && cita.estado !== this.filtroEstado) {
        return false;
      }
      
      // Filtro por veterinario
      if (this.filtroVeterinario) {
        const nombreVet = cita.veterinario?.nombre?.toLowerCase() || '';
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
        error: (err) => {
          alert('Error al cancelar la cita');
          console.error(err);
        }
      });
    }
  }

  eliminarCita(id: number): void {
    if (confirm('¿Estás seguro de eliminar esta cita permanentemente?')) {
      this.citaService.delete(id).subscribe({
        next: () => this.cargarCitas(),
        error: (err) => {
          alert('Error al eliminar la cita');
          console.error(err);
        }
      });
    }
  }

  // Métodos de utilidad para la vista
  formatearFecha(fecha: string): string {
    return formatearFecha(fecha, 'larga');
  }

  formatearHora(fecha: string): string {
    const date = new Date(fecha);
    return date.toLocaleTimeString('es-CO', { hour: '2-digit', minute: '2-digit' });
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
}