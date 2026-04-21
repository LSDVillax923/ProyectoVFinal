import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { VeterinarioMapper } from '../../../shared/api/model-mappers';
import { Navbar } from '../../../shared/components/navbar/navbar';
import { Veterinario } from '../../veterinario';
import { VeterinarioRestService } from '../../services/veterinario-rest.service';

@Component({
  selector: 'app-listar-veterinarios',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, Navbar],
  templateUrl: './listar-veterinarios.html',
  styleUrl: './listar-veterinarios.css',
})
export class ListarVeterinarios implements OnInit {
  busqueda = '';
  mensaje = '';
  error = '';
  private veterinarios: Veterinario[] = [];

  constructor(private readonly veterinarioRestService: VeterinarioRestService) {}

  ngOnInit(): void {
    this.cargarVeterinarios();
  }

  private cargarVeterinarios(): void {
    this.veterinarioRestService.getAll().subscribe({
      next: (veterinariosDto) => {
        this.veterinarios = veterinariosDto.map(VeterinarioMapper.fromDto);
      },
      error: () => {
        this.error = 'No se pudieron cargar los veterinarios desde el servidor.';
        this.veterinarios = [];
      },
    });
  }

  get veterinariosFiltrados(): Veterinario[] {
    const filtro = this.busqueda.trim().toLowerCase();
    if (!filtro) return this.veterinarios;

    return this.veterinarios.filter(
      (v) =>
        v.nombre.toLowerCase().includes(filtro) ||
        v.correo.toLowerCase().includes(filtro) ||
        v.especialidad.toLowerCase().includes(filtro) ||
        (v.cedula?.toLowerCase().includes(filtro) ?? false),
    );
  }

  eliminarVeterinario(vet: Veterinario): void {
    if (!confirm(`¿Eliminar a ${vet.nombre}?`)) return;

    this.veterinarioRestService.delete(vet.id).subscribe({
      next: () => {
        this.mensaje = `${vet.nombre} fue eliminado correctamente.`;
        this.error = '';
        this.cargarVeterinarios();
      },
      error: () => {
        this.error = 'No se pudo eliminar el veterinario.';
      },
    });
  }
}