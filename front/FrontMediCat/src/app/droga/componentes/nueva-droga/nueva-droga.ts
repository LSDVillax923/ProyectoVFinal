import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { DrogaRequest } from '../../../shared/api/backend-contracts';
import { Navbar } from '../../../shared/components/navbar/navbar';
import { DrogaRestService } from '../../services/droga.service';

interface NuevaDrogaForm {
  nombre: string;
  precioCompra: number;
  precioVenta: number;
  unidadesDisponibles: number;
}

@Component({
  selector: 'app-nueva-droga',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, Navbar],
  templateUrl: './nueva-droga.html',
  styleUrl: './nueva-droga.css',
})
export class NuevaDroga {
  mensaje = '';
  error = '';

  formData: NuevaDrogaForm = {
    nombre: '',
    precioCompra: 0,
    precioVenta: 0,
    unidadesDisponibles: 0,
  };

  constructor(private readonly drogaRestService: DrogaRestService) {}

  guardarDroga(): void {
    const { nombre, precioCompra, precioVenta, unidadesDisponibles } = this.formData;

    if (!nombre) {
      this.error = 'El nombre del medicamento es obligatorio.';
      return;
    }

    const payload: DrogaRequest = { nombre, precioCompra, precioVenta, unidadesDisponibles };
    this.drogaRestService.create(payload).subscribe({
      next: () => {
        this.mensaje = `${nombre} fue agregado al inventario.`;
        this.error = '';
        this.formData = { nombre: '', precioCompra: 0, precioVenta: 0, unidadesDisponibles: 0 };
      },
      error: () => {
        this.error = 'No se pudo agregar el medicamento.';
        this.mensaje = '';
      },
    });
  }
}
