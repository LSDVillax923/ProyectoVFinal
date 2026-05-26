import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AdminRequest } from '../../../shared/api/backend-contracts';
import { Navbar } from '../../../shared/components/navbar/navbar';
import { AdminRestService } from '../../services/admin-rest.service';

interface NuevoAdminForm {
  nombre: string;
  correo: string;
  contrasenia: string;
  confirmar: string;
}

@Component({
  selector: 'app-nuevo-admin',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, Navbar],
  templateUrl: './nuevo-admin.html',
  styleUrl: './nuevo-admin.css',
})
export class NuevoAdmin {
  mensaje = '';
  error = '';

  formData: NuevoAdminForm = {
    nombre: '',
    correo: '',
    contrasenia: '',
    confirmar: '',
  };

  constructor(
    private readonly adminRestService: AdminRestService,
    private readonly router: Router,
  ) {}

  guardarAdmin(): void {
    const { nombre, correo, contrasenia, confirmar } = this.formData;

    if (!nombre || !correo || !contrasenia || !confirmar) {
      this.error = 'Todos los campos son obligatorios.';
      this.mensaje = '';
      return;
    }

    if (contrasenia !== confirmar) {
      this.error = 'Las contraseñas no coinciden.';
      this.mensaje = '';
      return;
    }

    const payload: AdminRequest = { nombre, correo, contrasenia };

    this.adminRestService.create(payload).subscribe({
      next: () => {
        this.mensaje = `${nombre} fue registrado como administrador.`;
        this.error = '';
        this.formData = { nombre: '', correo: '', contrasenia: '', confirmar: '' };
        setTimeout(() => this.router.navigate(['/dashboard']), 1200);
      },
      error: (err) => {
        this.error = this.extraerMensajeError(err) || 'No se pudo registrar el administrador.';
        this.mensaje = '';
      },
    });
  }

  // eslint-disable-next-line @typescript-eslint/no-explicit-any
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
