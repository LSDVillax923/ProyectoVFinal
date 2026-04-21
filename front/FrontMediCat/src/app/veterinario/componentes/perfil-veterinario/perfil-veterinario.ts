import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { VeterinarioService } from '../../services/veterinario.service';
import { AuthService } from '../../../user/services/auth.service';
import { Navbar } from '../../../shared/components/navbar/navbar';

interface VeterinarioEditable {
  id: number;
  nombre: string;
  cedula: string;
  correo: string;
  celular: string;
  contrasenia: string;
  especialidad: string;
}

@Component({
  selector: 'app-perfil-veterinario',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, Navbar],
  templateUrl: './perfil-veterinario.html',
  styleUrl: './perfil-veterinario.css',
})
export class PerfilVeterinario {
  formData: VeterinarioEditable = {
    id: 0,
    nombre: '',
    cedula: '',
    correo: '',
    celular: '',
    contrasenia: '',
    especialidad: '',
  };

  mensaje = '';
  error = '';
  noEncontrado = false;

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

  constructor(
    private readonly router: Router,
    private readonly veterinarioService: VeterinarioService,
    private readonly authService: AuthService,
  ) {
    const sesion = this.authService.getSesion();
    if (!sesion) {
      this.noEncontrado = true;
      return;
    }

    const vet = this.veterinarioService.getById(sesion.id);
    if (vet) {
      this.formData = {
        id: vet.id,
        nombre: vet.nombre,
        cedula: vet.cedula,
        correo: vet.correo,
        celular: vet.celular,
        contrasenia: '',
        especialidad: vet.especialidad,
      };
    } else {
      this.noEncontrado = true;
      this.error = 'No se encontró el perfil del veterinario.';
    }
  }

  guardarCambios(): void {
    const { id, nombre, cedula, correo, celular, contrasenia, especialidad } = this.formData;

    if (!nombre || !cedula || !correo || !celular || !especialidad) {
      this.error = 'Todos los campos son obligatorios excepto la contraseña.';
      return;
    }

    const cambios: Partial<VeterinarioEditable> = { nombre, cedula, correo, celular, especialidad };
    if (contrasenia) cambios['contrasenia'] = contrasenia;

    this.veterinarioService.update(id, cambios);

    this.authService.setSesion({
      ...this.authService.getSesion()!,
      nombre,
      correo,
    });

    this.mensaje = 'Tu perfil fue actualizado correctamente.';
    this.error = '';
  }

  volver(): void {
    this.router.navigate(['/mascotas']);
  }
}
