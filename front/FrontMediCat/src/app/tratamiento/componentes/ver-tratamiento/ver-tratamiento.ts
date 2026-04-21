import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { AuthService } from '../../../user/services/auth.service';
import { Tratamiento } from '../../tratamiento';
import { Navbar } from '../../../shared/components/navbar/navbar';
import { TratamientoRestService } from '../../services/tratamiento-rest.service';
import { TratamientoMapper } from '../../../shared/api/model-mappers';

@Component({
  selector: 'app-ver-tratamiento',
  standalone: true,
  imports: [CommonModule, RouterLink, Navbar],
  templateUrl: './ver-tratamiento.html',
  styleUrl: './ver-tratamiento.css',
})
export class VerTratamiento implements OnInit {
  tratamiento: Tratamiento | null = null;
  cargando = false;

  constructor(
    private readonly route: ActivatedRoute,
    private readonly tratamientoRestService: TratamientoRestService,
    private readonly authService: AuthService,
  ) {}

  ngOnInit(): void {
    
    const id = Number(this.route.snapshot.paramMap.get('id'));
    if (!id) return;

    this.cargando = true;
    this.tratamientoRestService.getById(id).subscribe({
      next: (tratamientoDto) => {
        this.tratamiento = TratamientoMapper.fromDto(tratamientoDto);
        this.cargando = false;
      },
      error: () => {
        this.tratamiento = null;
        this.cargando = false;
      },
    });

  }

  get puedeEditar(): boolean {
    const rol = this.authService.getSesion()?.rol;
    return rol === 'VETERINARIO' || rol === 'ADMIN';
  }

  estadoClase(estado: string): string {
    const map: Record<string, string> = {
      COMPLETADO: 'badge-activo',
      PENDIENTE: 'badge-warning',
      CANCELADO: 'badge-danger',
    };
    return map[estado] ?? '';
  }
}