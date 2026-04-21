import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { MascotaMapper } from '../../../shared/api/model-mappers';
import { Navbar } from '../../../shared/components/navbar/navbar';
import { AuthService } from '../../../user/services/auth.service';
import { Mascota } from '../../mascota';
import { MascotaRestService } from '../../services/mascota.service';

@Component({
  selector: 'app-listar-mascotas',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, Navbar],
  templateUrl: './listar-mascotas.html',
  styleUrl: './listar-mascotas.css',
})
export class ListarMascotas implements OnInit {
  busqueda = '';
  estadoSeleccionado = '';
  mensaje = '';
  error = '';
  clienteId: number | null = null;
  esCliente = false;
  private todasMascotas: Mascota[] = [];

  constructor(
    private readonly mascotaRestService: MascotaRestService,
    private readonly authService: AuthService,
    private readonly route: ActivatedRoute,
  ) {}

  ngOnInit(): void {
    const sesion = this.authService.getSesion();

    if (sesion?.rol === 'CLIENTE') {
      this.clienteId = sesion.id;
      this.esCliente = true;
      this.cargarMascotas();
    } else {
      this.route.paramMap.subscribe((params) => {
        const id = params.get('id');
        this.clienteId = id ? Number(id) : null;
        this.cargarMascotas();
      });
    }
  }

  private cargarMascotas(): void {
    this.mascotaRestService.getAll().subscribe({
      next: (mascotasDto) => {
        this.todasMascotas = mascotasDto.map(MascotaMapper.fromDto);
      },
      error: () => {
        this.error = 'No se pudieron cargar las mascotas desde el servidor.';
        this.todasMascotas = [];
      },
    });
  }

  get mascotasFiltradas(): Mascota[] {
    const filtroTexto = this.busqueda.trim().toLowerCase();
    return this.todasMascotas.filter((mascota) => {
      const coincideCliente = !this.clienteId || mascota.clienteId === this.clienteId;
      const coincideTexto =
        !filtroTexto ||
        mascota.nombre.toLowerCase().includes(filtroTexto) ||
        mascota.raza.toLowerCase().includes(filtroTexto) ||
        mascota.especie.toLowerCase().includes(filtroTexto) ||
        (mascota.propietario?.toLowerCase().includes(filtroTexto) ?? false);
      const coincideEstado = !this.estadoSeleccionado || mascota.estado === this.estadoSeleccionado;
      return coincideCliente && coincideTexto && coincideEstado;
    });
  }

  get totalMascotas(): number { return this.mascotasFiltradas.length; }
  get saludables(): number { return this.mascotasFiltradas.filter((m) => m.estado === 'ACTIVA').length; }
  get enTratamiento(): number { return this.mascotasFiltradas.filter((m) => m.estado === 'TRATAMIENTO').length; }
  get inactivas(): number { return this.mascotasFiltradas.filter((m) => m.estado === 'INACTIVA').length; }

  get filtroNombre(): string { return this.busqueda; }
  set filtroNombre(value: string) { this.busqueda = value; }

  aplicarFiltros(): void {}

  limpiarFiltros(): void {
    this.busqueda = '';
    this.estadoSeleccionado = '';
  }

  desactivarMascota(mascota: Mascota): void {
    if (!confirm(`¿Desactivar a ${mascota.nombre}? Esto la marcará como Inactiva.`)) return;
    this.mascotaRestService.patch(mascota.id, { estado: 'INACTIVA' }).subscribe({
      next: () => {
        this.mensaje = `${mascota.nombre} fue desactivada correctamente.`;
        this.error = '';
        this.cargarMascotas();
      },
      error: () => { this.error = 'No se pudo desactivar la mascota.'; },
    });
  }

  eliminarMascotaPermanente(mascota: Mascota): void {
    if (!confirm(`¿Eliminar permanentemente a ${mascota.nombre}?`)) return;
    this.mascotaRestService.delete(mascota.id).subscribe({
      next: () => {
        this.mensaje = `${mascota.nombre} fue eliminada permanentemente.`;
        this.error = '';
        this.cargarMascotas();
      },
      error: () => { this.error = 'No se pudo eliminar la mascota.'; },
    });
  }

  borrarMascota(mascota: Mascota): void {
    this.desactivarMascota(mascota);
  }
}
