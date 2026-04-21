import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { MascotaRestService } from '../../services/mascota.service';
import { TratamientoService } from '../../../tratamiento/services/tratamiento-service';
import { AuthService } from '../../../user/services/auth.service';
import { Mascota } from '../../mascota';
import { MascotaMapper } from '../../../shared/api/model-mappers';
import { Tratamiento } from '../../../tratamiento/tratamiento';
import { Navbar } from '../../../shared/components/navbar/navbar';

const MESES: Record<string, string> = {
  '01': 'Ene', '02': 'Feb', '03': 'Mar', '04': 'Abr',
  '05': 'May', '06': 'Jun', '07': 'Jul', '08': 'Ago',
  '09': 'Sep', '10': 'Oct', '11': 'Nov', '12': 'Dic',
};

@Component({
  selector: 'app-ver-mascota',
  standalone: true,
  imports: [CommonModule, RouterLink, Navbar],
  templateUrl: './ver-mascota.html',
  styleUrl: './ver-mascota.css',
})
export class VerMascota implements OnInit {
  mascota: Mascota | null = null;
  tratamientos: Tratamiento[] = [];
  errorMascota = '';
  esCliente = false;
  private mascotaId = 0;

  constructor(
    private readonly route: ActivatedRoute,
    private readonly mascotaService: MascotaRestService,
    private readonly tratamientoService: TratamientoService,
    private readonly authService: AuthService,
  ) {}

  ngOnInit(): void {
    this.mascotaId = Number(this.route.snapshot.paramMap.get('id'));
    this.esCliente = this.authService.getSesion()?.rol === 'CLIENTE';

    this.mascotaService.getById(this.mascotaId).subscribe({
      next: (mascotaDto) => {
        this.mascota = MascotaMapper.fromDto(mascotaDto);
        this.tratamientos = this.tratamientoService.getByMascotaId(this.mascotaId);
      },
      error: () => {
        this.errorMascota = 'No se encontró la mascota solicitada.';
      },
    });
  }

  get volverUrl(): string[] {
    return this.esCliente ? ['/mis-mascotas'] : ['/mascotas'];
  }

  get estadoClase(): string {
    const map: Record<string, string> = {
      ACTIVA: 'badge-activo',
      TRATAMIENTO: 'badge-warning',
      INACTIVA: 'badge-danger',
    };
    return map[this.mascota?.estado ?? ''] ?? '';
  }

  tratamientoEstadoClase(estado: string): string {
    const map: Record<string, string> = {
      Activo: 'badge-activo',
      Completado: 'badge-activo',
      Pendiente: 'badge-warning',
      Cancelado: 'badge-danger',
    };
    return map[estado] ?? '';
  }

  mesAbreviado(fecha: string): string {
    const mes = fecha?.slice(5, 7) ?? '';
    return MESES[mes] ?? mes;
  }
}
