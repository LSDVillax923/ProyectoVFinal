import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { DrogaMapper } from '../../../shared/api/model-mappers';
import { Navbar } from '../../../shared/components/navbar/navbar';
import { AuthService } from '../../../user/services/auth.service';
import { Droga } from '../../droga';
import { DrogaRestService } from '../../services/droga.service';

@Component({
  selector: 'app-listar-drogas',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, Navbar],
  templateUrl: './listar-drogas.html',
  styleUrl: './listar-drogas.css',
})
export class ListarDrogas implements OnInit {
  busqueda = '';
  mensaje = '';
  error = '';
  private drogas: Droga[] = [];

  constructor(
    private readonly drogaRestService: DrogaRestService,
    private readonly authService: AuthService,
  ) {}

  ngOnInit(): void {
    this.cargarDrogas();
  }

  private cargarDrogas(): void {
    this.drogaRestService.getAll().subscribe({
      next: (drogasDto) => {
        this.drogas = drogasDto.map(DrogaMapper.fromDto);
      },
      error: () => {
        this.error = 'No se pudieron cargar los medicamentos desde el servidor.';
        this.drogas = [];
      },
    });
  }

  get esAdmin(): boolean {
    return this.authService.getSesion()?.rol === 'ADMIN';
  }

  get puedeEditar(): boolean {
    const rol = this.authService.getSesion()?.rol;
    return rol === 'ADMIN' || rol === 'VETERINARIO';
  }

  get drogasFiltradas(): Droga[] {
    const filtro = this.busqueda.trim().toLowerCase();
    if (!filtro) return this.drogas;
    return this.drogas.filter((d) => d.nombre.toLowerCase().includes(filtro));
  }

  stockClase(stock: number): string {
    if (stock === 0) return 'badge-danger';
    if (stock <= 5) return 'badge-warning';
    return 'badge-activo';
  }

  eliminarDroga(droga: Droga): void {
    if (!confirm(`¿Eliminar ${droga.nombre} del inventario?`)) return;
    this.drogaRestService.delete(droga.id).subscribe({
      next: () => {
        this.mensaje = `${droga.nombre} fue eliminado del inventario.`;
        this.error = '';
        this.cargarDrogas();
      },
      error: () => {
        this.error = 'No se pudo eliminar el medicamento.';
      },
    });
  }
}
