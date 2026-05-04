import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { MascotaRestService } from '../../services/mascota.service';
import { Mascota } from '../../mascota';
import { MascotaRequest } from '../../../shared/api/backend-contracts';
import { Navbar } from '../../../shared/components/navbar/navbar';
import { urlFotoMascota } from '../../../shared/utils/helpers';

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
    clienteId: 0,
  };

  mensaje = '';
  error = '';
  noEncontrada = false;
  private mascotaId = 0;

  fotoArchivo: File | null = null;
  fotoPreview: string | null = null;
  errorFoto = '';

  private static readonly TIPOS_PERMITIDOS = ['image/jpeg', 'image/png', 'image/gif', 'image/webp'];
  private static readonly TAMANO_MAX_MB = 10;

  constructor(
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly mascotaService: MascotaRestService,
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
          fechaNacimiento: this.normalizarFecha(encontrada.fechaNacimiento),
          edad: encontrada.edad,
          peso: encontrada.peso,
          estado: encontrada.estado,
          enfermedad: encontrada.enfermedad,
          observaciones: encontrada.observaciones,
          foto: encontrada.foto,
          clienteId: encontrada.cliente?.id ?? 0,
        };
      },
      error: () => {
        this.noEncontrada = true;
        this.error = 'No se encontró la mascota solicitada.';
      },
    });
  }

  get fotoActualUrl(): string {
    return this.fotoPreview ?? urlFotoMascota(this.mascota.foto);
  }

  onFotoSeleccionada(event: Event): void {
    const input = event.target as HTMLInputElement;
    const archivo = input.files?.[0];
    this.errorFoto = '';

    if (!archivo) {
      this.fotoArchivo = null;
      this.fotoPreview = null;
      return;
    }

    if (!EditarMascota.TIPOS_PERMITIDOS.includes(archivo.type)) {
      this.errorFoto = 'Formato no válido. Usa JPG, PNG, GIF o WEBP.';
      this.fotoArchivo = null;
      this.fotoPreview = null;
      input.value = '';
      return;
    }

    if (archivo.size > EditarMascota.TAMANO_MAX_MB * 1024 * 1024) {
      this.errorFoto = `La imagen supera el tamaño máximo de ${EditarMascota.TAMANO_MAX_MB} MB.`;
      this.fotoArchivo = null;
      this.fotoPreview = null;
      input.value = '';
      return;
    }

    this.fotoArchivo = archivo;
    const lector = new FileReader();
    lector.onload = () => (this.fotoPreview = lector.result as string);
    lector.readAsDataURL(archivo);
  }

  guardarCambios(): void {
    if (!this.mascota.nombre || !this.mascota.especie || !this.mascota.raza || !this.mascota.fechaNacimiento) {
      this.error = 'Los campos nombre, especie, raza y fecha de nacimiento son obligatorios.';
      return;
    }

    if (this.mascota.peso <= 0) {
      this.error = 'El peso debe ser mayor a 0.';
      return;
    }

    const request: MascotaRequest = {
      nombre: this.mascota.nombre,
      especie: this.mascota.especie,
      raza: this.mascota.raza,
      sexo: this.mascota.sexo as 'Macho' | 'Hembra',
      fechaNacimiento: this.mascota.fechaNacimiento,
      edad: this.mascota.edad,
      peso: this.mascota.peso,
      estado: this.mascota.estado,
      enfermedad: this.mascota.enfermedad,
      observaciones: this.mascota.observaciones,
      foto: this.mascota.foto,
    };

    this.mascotaService.update(this.mascotaId, request).subscribe({
      next: () => {
        if (this.fotoArchivo) {
          this.mascotaService.subirFoto(this.mascotaId, this.fotoArchivo).subscribe({
            next: (actualizada) => {
              this.mascota.foto = actualizada.foto;
              this.fotoArchivo = null;
              this.fotoPreview = null;
              this.mensaje = `Se actualizaron los datos y la foto de ${this.mascota.nombre}.`;
              this.error = '';
            },
            error: () => {
              this.mensaje = `Se actualizaron los datos de ${this.mascota.nombre}, pero no se pudo subir la nueva foto.`;
              this.error = '';
            },
          });
        } else {
          this.mensaje = `Se actualizaron los datos de ${this.mascota.nombre}.`;
          this.error = '';
        }
      },
      error: () => {
        this.error = 'Error al actualizar la mascota.';
      },
    });
  }

  cancelar(): void {
    this.router.navigate(['/mascotas']);
  }

   private normalizarFecha(fecha?: string): string {
    return fecha ? fecha.slice(0, 10) : '';
  }


}
