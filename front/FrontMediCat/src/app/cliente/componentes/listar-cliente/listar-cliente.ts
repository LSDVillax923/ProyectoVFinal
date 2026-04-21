import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { ClienteRestService } from '../../services/cliente.service';
import { MascotaRestService } from '../../../mascota/services/mascota.service';
import { Cliente, Mascota } from '../../../shared/api/backend-contracts';
import { nombreCompletoCliente } from '../../../shared/api/model-mappers';
import { Navbar } from '../../../shared/components/navbar/navbar';

@Component({
  selector: 'app-listar-cliente',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule, Navbar],
  templateUrl: './listar-cliente.html',
  styleUrls: ['./listar-cliente.css']
})
export class ListarClienteComponent implements OnInit {

  clientes: Cliente[] = [];
  clientesFiltrados: Cliente[] = [];
  mascotasPorCliente: Record<number, number> = {};
  loading = false;
  error: string | null = null;
  mensaje = '';
  busqueda = '';

  constructor(
    private clienteService: ClienteRestService,
    private mascotaService: MascotaRestService,
  ) {}

  ngOnInit(): void {
    this.cargarClientes();
    this.cargarContadoresMascotas();
  }

  cargarClientes(): void {
    this.loading = true;
    this.clienteService.findAll().subscribe({
      next: (clientes: Cliente[]) => {
        this.clientes = clientes;
        this.aplicarFiltro();
        this.loading = false;
      },
      error: () => {
        this.error = 'Error al cargar los clientes';
        this.loading = false;
      }
    });
  }

  private cargarContadoresMascotas(): void {
    this.mascotaService.findAll().subscribe({
      next: (mascotas: Mascota[]) => {
        const contador: Record<number, number> = {};
        for (const m of mascotas) {
          const cid = m.cliente?.id;
          if (cid != null) {
            contador[cid] = (contador[cid] ?? 0) + 1;
          }
        }
        this.mascotasPorCliente = contador;
      },
      error: () => {
        this.mascotasPorCliente = {};
      }
    });
  }

  contarMascotas(clienteId: number): number {
    return this.mascotasPorCliente[clienteId] ?? 0;
  }

  aplicarFiltro(): void {
    if (!this.busqueda) {
      this.clientesFiltrados = [...this.clientes];
      return;
    }
    const filtroLower = this.busqueda.toLowerCase();
    this.clientesFiltrados = this.clientes.filter(c =>
      c.nombre.toLowerCase().includes(filtroLower) ||
      c.apellido.toLowerCase().includes(filtroLower) ||
      c.correo.toLowerCase().includes(filtroLower) ||
      c.celular.includes(this.busqueda)
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
    if (confirm('¿Estás seguro de eliminar este cliente?')) {
      this.clienteService.delete(cliente.id).subscribe({
        next: () => {
          this.mensaje = 'Cliente eliminado correctamente.';
          this.cargarClientes();
        },
        error: () => {
          this.error = 'Error al eliminar el cliente';
        }
      });
    }
  }

  nombreCompleto(cliente: Cliente): string {
    return nombreCompletoCliente(cliente);
  }
}
