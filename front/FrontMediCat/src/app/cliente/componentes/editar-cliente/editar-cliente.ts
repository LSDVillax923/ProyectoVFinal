import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { ClienteRequest } from '../../../shared/api/backend-contracts';
import { ClienteRestService } from '../../services/cliente.service';
import { AuthRestService } from '../../../user/services/auth-rest.service';
import { Navbar } from '../../../shared/components/navbar/navbar';

interface ClienteForm {
  nombre: string;
  apellido: string;
  correo: string;
  celular: string;
  contrasenia: string;
}

@Component({
  selector: 'app-editar-cliente',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, Navbar],
  templateUrl: './editar-cliente.html',
  styleUrls: ['./editar-cliente.css']
})
export class EditarClienteComponent implements OnInit {

  formData: ClienteForm = { nombre: '', apellido: '', correo: '', celular: '', contrasenia: '' };
  contraseniaOriginal = '';
  loading = false;
  error: string | null = null;
  mensaje = '';
  noEncontrado = false;
  esPerfil = false;
  clienteId: number | null = null;

  constructor(
    private clienteService: ClienteRestService,
    private route: ActivatedRoute,
    private router: Router,
    private authService: AuthRestService,
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.clienteId = +id;
    } else {
      const sesion = this.authService.getSesion();
      if (sesion) {
        this.clienteId = sesion.id;
        this.esPerfil = true;
      }
    }
    if (this.clienteId) {
      this.cargarCliente();
    }
  }

  cargarCliente(): void {
    if (!this.clienteId) return;
    this.loading = true;
    this.clienteService.findById(this.clienteId).subscribe({
      next: (cliente) => {
        this.formData = {
          nombre: cliente.nombre,
          apellido: cliente.apellido,
          correo: cliente.correo,
          celular: cliente.celular,
          contrasenia: '',
        };
        this.contraseniaOriginal = cliente.contrasenia ?? '';
        this.loading = false;
      },
      error: () => {
        this.noEncontrado = true;
        this.error = 'No se encontró el cliente';
        this.loading = false;
      }
    });
  }

  guardarCambios(): void {
    if (!this.clienteId) return;
    this.loading = true;
    this.error = null;
    this.mensaje = '';

    const payload: ClienteRequest = {
      nombre: this.formData.nombre,
      apellido: this.formData.apellido,
      correo: this.formData.correo,
      celular: this.formData.celular,
      contrasenia: this.formData.contrasenia || this.contraseniaOriginal,
    };

    this.clienteService.update(this.clienteId, payload).subscribe({
      next: () => {
        this.mensaje = 'Cambios guardados correctamente.';
        this.loading = false;
      },
      error: (err) => {
        this.error = err?.error?.message || 'Error al actualizar el cliente';
        this.loading = false;
      }
    });
  }

  volver(): void {
    this.router.navigate(this.esPerfil ? ['/mis-mascotas'] : ['/clientes']);
  }
}
