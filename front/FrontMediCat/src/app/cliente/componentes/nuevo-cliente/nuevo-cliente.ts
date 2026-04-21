import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { ClienteRequest } from '../../../shared/api/backend-contracts';
import { Navbar } from '../../../shared/components/navbar/navbar';
import { ClienteRestService } from '../../services/cliente.service';

interface NuevoClienteForm {
  nombre: string;
  apellido: string;
  correo: string;
  celular: string;
  contrasenia: string;
}

@Component({
  selector: 'app-nuevo-cliente',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, Navbar],
  templateUrl: './nuevo-cliente.html',
  styleUrl: './nuevo-cliente.css',
})
export class NuevoCliente {
  mensaje = '';
  error = '';

  formData: NuevoClienteForm = {
    nombre: '',
    apellido: '',
    correo: '',
    celular: '',
    contrasenia: '',
  };

  constructor(private readonly clienteRestService: ClienteRestService) {}

  guardarCliente(): void {
    const { nombre, apellido, correo, celular, contrasenia } = this.formData;

    if (!nombre || !apellido || !correo || !celular || !contrasenia) {
      this.error = 'Todos los campos son obligatorios.';
      this.mensaje = '';
      return;
    }

    const payload: ClienteRequest = { nombre, apellido, correo, celular, contrasenia };
    this.clienteRestService.create(payload).subscribe({
      next: () => {
        this.mensaje = `${nombre} ${apellido} se registró correctamente.`;
        this.error = '';
        this.formData = { nombre: '', apellido: '', correo: '', celular: '', contrasenia: '' };
      },
      error: (err) => {
        this.error = this.extraerMensajeError(err) || 'No se pudo registrar el cliente.';
        this.mensaje = '';
      },
    });
  }

  private extraerMensajeError(err: any): string {
    if (!err?.error) return '';
    if (typeof err.error === 'string') return err.error;
    if (err.error.message) return err.error.message;
    if (err.error.validationErrors) {
      return Object.values(err.error.validationErrors).join(' · ');
    }
    return '';
  }
}
