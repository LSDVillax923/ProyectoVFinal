import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { ClienteRestService } from '../../services/cliente.service';
import { Cliente } from '../../../shared/api/backend-contracts';
import { nombreCompletoCliente } from '../../../shared/api/model-mappers';
import { Navbar } from '../../../shared/components/navbar/navbar';
import { AuthService } from '../../../user/services/auth.service';

@Component({
  selector: 'app-listar-cliente',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule, Navbar],
  templateUrl: './listar-cliente.html',
  styleUrls: ['./listar-cliente.css'],
})
export class ListarClienteComponent implements OnInit {

  clientes: Cliente[] = [];
  clientesFiltrados: Cliente[] = [];
  loading = false;
  error: string | null = null;
  mensaje = '';
  busqueda = '';

  esAdmin = false;
  esVeterinario = false;

  constructor(
    private readonly clienteService: ClienteRestService,
    private readonly authService: AuthService,
  ) {}

  ngOnInit(): void {
    const sesion = this.authService.getSesion();
    this.esAdmin       = sesion?.rol === 'ADMIN';
    this.esVeterinario = sesion?.rol === 'VETERINARIO';
    this.cargarClientes();
  }

  cargarClientes(): void {
    this.loading = true;
    this.clienteService.findAll().subscribe({
      next: (clientes) => {
        this.clientes = clientes;
        this.aplicarFiltro();
        this.loading = false;
      },
      error: () => {
        this.error = 'Error al cargar los clientes';
        this.loading = false;
      },
    });
  }

  /** El backend ahora expone mascotasCount dentro de ClienteDto → no hace falta segunda llamada. */
  contarMascotas(cliente: Cliente): number {
    return cliente.mascotasCount ?? 0;
  }

  aplicarFiltro(): void {
    if (!this.busqueda) {
      this.clientesFiltrados = [...this.clientes];
      return;
    }
    const filtroLower = this.busqueda.toLowerCase();
    this.clientesFiltrados = this.clientes.filter((c) =>
      c.nombre.toLowerCase().includes(filtroLower) ||
      c.apellido.toLowerCase().includes(filtroLower) ||
      c.correo.toLowerCase().includes(filtroLower) ||
      c.celular.includes(this.busqueda),
    );
  }

  limpiarBusqueda(): void {
    this.busqueda = '';
    this.aplicarFiltro();
  }

  get totalClientes(): number {
    return this.clientesFiltrados.length;
  }

  eliminarCliente(cliente: Cliente): void {
    if (!this.esAdmin) return; // defensa: solo admin puede borrar
    if (confirm(`¿Eliminar a ${cliente.nombre} ${cliente.apellido}?`)) {
      this.clienteService.delete(cliente.id).subscribe({
        next: () => {
          this.mensaje = 'Cliente eliminado correctamente.';
          this.cargarClientes();
        },
        error: () => (this.error = 'Error al eliminar el cliente'),
      });
    }
  }

  nombreCompleto(cliente: Cliente): string {
    return nombreCompletoCliente(cliente);
  }
}
