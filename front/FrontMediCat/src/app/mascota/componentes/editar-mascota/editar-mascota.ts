import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { MascotaRestService } from '../../services/mascota.service';
import { VeterinarioService } from '../../../veterinario/services/veterinario.service';
import { Veterinario } from '../../../veterinario/veterinario';
import { Mascota } from '../../mascota';
import { MascotaRequest } from '../../../shared/api/backend-contracts';
import { Navbar } from '../../../shared/components/navbar/navbar';

@Component({
  selector: 'app-editar-mascota',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, Navbar],
  templateUrl: './editar-mascota.html',
  styleUrl: './editar-mascota.css',
})
export class EditarMascota implements OnInit {
  mascota: Mascota = {
    id: 0,
    nombre: '',
    especie: '',
    raza: '',
    sexo: '',
    fechaNacimiento: '',
    edad: 0,
    peso: 0,
    estado: 'ACTIVA',
    enfermedad: '',
    observaciones: '',
    veterinarioAsignado: '',
    clienteId: 0,
  };

  veterinariosDisponibles: Veterinario[] = [];
  mensaje = '';
  error = '';
  noEncontrada = false;
  private mascotaId = 0;

  constructor(
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly mascotaService: MascotaRestService,
    private readonly veterinarioService: VeterinarioService,
  ) {}

  ngOnInit(): void {
    this.mascotaId = Number(this.route.snapshot.paramMap.get('id'));
    this.mascotaService.getById(this.mascotaId).subscribe({
      next: (encontrada) => {
        this.mascota = {
          id: encontrada.id,
          nombre: encontrada.nombre,
          especie: encontrada.especie,
          raza: encontrada.raza,
          sexo: encontrada.sexo,
          fechaNacimiento: encontrada.fechaNacimiento,
          edad: encontrada.edad,
          peso: encontrada.peso,
          estado: encontrada.estado,
          enfermedad: encontrada.enfermedad,
          observaciones: encontrada.observaciones,
          veterinarioAsignado: encontrada.veterinarioAsignado ?? '',
          clienteId: encontrada.cliente?.id ?? 0,
        };
      },
      error: () => {
        this.noEncontrada = true;
        this.error = 'No se encontró la mascota solicitada.';
      },
    });
    this.veterinariosDisponibles = this.veterinarioService.getActivos();
  }

  guardarCambios(): void {
    if (!this.mascota.nombre || !this.mascota.especie || !this.mascota.raza) {
      this.error = 'Los campos nombre, especie y raza son obligatorios.';
      return;
    }

    const request: MascotaRequest = {
      nombre: this.mascota.nombre,
      especie: this.mascota.especie,
      raza: this.mascota.raza,
      sexo: this.mascota.sexo as 'Macho' | 'Hembra',
      fechaNacimiento: this.mascota.fechaNacimiento,
      peso: this.mascota.peso,
      estado: this.mascota.estado,
      enfermedad: this.mascota.enfermedad,
      observaciones: this.mascota.observaciones,
      veterinarioAsignado: this.mascota.veterinarioAsignado,
    };

    this.mascotaService.update(this.mascotaId, request).subscribe({
      next: () => {
        this.mensaje = `Se actualizaron los datos de ${this.mascota.nombre}.`;
        this.error = '';
      },
      error: () => {
        this.error = 'Error al actualizar la mascota.';
      },
    });
  }

  cancelar(): void {
    this.router.navigate(['/mascotas']);
  }

}
