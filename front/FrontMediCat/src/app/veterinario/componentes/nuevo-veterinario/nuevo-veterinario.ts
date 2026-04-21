import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { VeterinarioRequest } from '../../../shared/api/backend-contracts';
import { Navbar } from '../../../shared/components/navbar/navbar';
import { VeterinarioRestService } from '../../services/veterinario-rest.service';

interface NuevoVeterinarioForm {
  nombre: string;
  cedula: string;
  correo: string;
  celular: string;
  contrasenia: string;
  especialidad: string;
}

@Component({
  selector: 'app-nuevo-veterinario',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, Navbar],
  templateUrl: './nuevo-veterinario.html',
  styleUrl: './nuevo-veterinario.css',
})
export class NuevoVeterinario {
  mensaje = '';
  error = '';

  especialidades = [
    'Medicina General',
    'Cirugía',
    'Dermatología',
    'Cardiología',
    'Oncología',
    'Oftalmología',
    'Neurología',
    'Ortopedia',
    'Odontología',
    'Nutrición',
  ];

  formData: NuevoVeterinarioForm = {
    nombre: '',
    cedula: '',
    correo: '',
    celular: '',
    contrasenia: '',
    especialidad: '',
  };

  constructor(private readonly veterinarioRestService: VeterinarioRestService) {}

  guardarVeterinario(): void {
    const { nombre, cedula, correo, celular, contrasenia, especialidad } = this.formData;

    if (!nombre || !cedula || !correo || !celular || !contrasenia || !especialidad) {
      this.error = 'Todos los campos son obligatorios.';
      this.mensaje = '';
      return;
    }

    const payload: VeterinarioRequest = {
      nombre,
      cedula,
      correo,
      celular,
      contrasenia,
      especialidad,
      imageUrl: '',
      estado: 'activo',
    };

    this.veterinarioRestService.create(payload).subscribe({
      next: () => {
        this.mensaje = `${nombre} fue registrado correctamente.`;
        this.error = '';
        this.formData = {
          nombre: '',
          cedula: '',
          correo: '',
          celular: '',
          contrasenia: '',
          especialidad: '',
        };
      },
      error: (err) => {
        this.error = this.extraerMensajeError(err) || 'No se pudo registrar el veterinario.';
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
