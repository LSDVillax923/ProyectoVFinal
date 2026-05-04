import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { Cliente, MascotaRequest } from '../../../shared/api/backend-contracts';
import { Navbar } from '../../../shared/components/navbar/navbar';
import { ClienteRestService } from '../../../cliente/services/cliente.service';
import { MascotaRestService } from '../../services/mascota.service';
import { AuthRestService } from '../../../user/services/auth-rest.service';

interface NuevaMascotaForm {
  nombre: string;
  especie: string;
  raza: string;
  sexo: string;
  fechaNacimiento: string;
  edad: number | null;
  peso: number | null;
  estado: string;
  clienteId: number | null;
  enfermedad: string;
  observaciones: string;
}

@Component({
  selector: 'app-nueva-mascota',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, Navbar],
  templateUrl: './nueva-mascota.html',
  styleUrl: './nueva-mascota.css',
})
export class NuevaMascota implements OnInit {
  clientes: Cliente[] = [];
  mascotaForm: NuevaMascotaForm = this.formInicial();
  mascotaRegistrada = '';
  error = '';
  loading = false;

  fotoArchivo: File | null = null;
  fotoPreview: string | null = null;
  errorFoto = '';

  private static readonly TIPOS_PERMITIDOS = ['image/jpeg', 'image/png', 'image/gif', 'image/webp'];
  private static readonly TAMANO_MAX_MB = 10;

  constructor(
    private readonly mascotaService: MascotaRestService,
    private readonly clienteService: ClienteRestService,
    private readonly authService: AuthRestService,
  ) {}

  ngOnInit(): void {
    const sesion = this.authService.getSesion();
    if (sesion?.rol === 'CLIENTE') {
      this.mascotaForm.clienteId = sesion.id;
      this.clienteService.findById(sesion.id).subscribe({
        next: (c) => this.clientes = [c],
        error: () => {},
      });
    } else {
      this.clienteService.findAll().subscribe({
        next: (c) => this.clientes = c,
        error: () => {},
      });
    }
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

    if (!NuevaMascota.TIPOS_PERMITIDOS.includes(archivo.type)) {
      this.errorFoto = 'Formato no válido. Usa JPG, PNG, GIF o WEBP.';
      this.fotoArchivo = null;
      this.fotoPreview = null;
      input.value = '';
      return;
    }

    if (archivo.size > NuevaMascota.TAMANO_MAX_MB * 1024 * 1024) {
      this.errorFoto = `La imagen supera el tamaño máximo de ${NuevaMascota.TAMANO_MAX_MB} MB.`;
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

  registrarMascota(): void {
    const { clienteId, nombre, especie, raza, sexo, fechaNacimiento, edad, peso, estado, enfermedad, observaciones } = this.mascotaForm;

    if (!clienteId || !nombre || !especie || !raza || !estado) {
      this.error = 'Completa todos los campos obligatorios.';
      return;
    }

    const request: MascotaRequest = {
      nombre,
      especie,
      raza,
      sexo: sexo as 'Macho' | 'Hembra',
      fechaNacimiento,
      edad: edad ?? 0,
      peso: peso ?? 0,
      estado: estado as 'ACTIVA' | 'TRATAMIENTO' | 'INACTIVA',
      enfermedad,
      observaciones,
    };

    this.loading = true;
    this.mascotaService.crearMascota(request, clienteId).subscribe({
      next: (creada) => {
        if (this.fotoArchivo && creada?.id) {
          this.mascotaService.subirFoto(creada.id, this.fotoArchivo).subscribe({
            next: () => this.finalizarRegistro(nombre),
            error: () => {
              this.error = 'La mascota se creó, pero no se pudo subir la foto.';
              this.loading = false;
            },
          });
        } else {
          this.finalizarRegistro(nombre);
        }
      },
      error: () => {
        this.error = 'No se pudo registrar la mascota.';
        this.loading = false;
      },
    });
  }

  private finalizarRegistro(nombre: string): void {
    this.mascotaRegistrada = `La mascota "${nombre}" fue registrada correctamente.`;
    this.error = '';
    this.errorFoto = '';
    this.mascotaForm = this.formInicial();
    this.fotoArchivo = null;
    this.fotoPreview = null;
    this.loading = false;
    const sesion = this.authService.getSesion();
    if (sesion?.rol === 'CLIENTE') {
      this.mascotaForm.clienteId = sesion.id;
    }
  }

  private formInicial(): NuevaMascotaForm {
    return {
      nombre: '', especie: '', raza: '', sexo: '', fechaNacimiento: '',
      edad: null, peso: null, estado: '', clienteId: null, enfermedad: '',
      observaciones: '',
    };
  }
}
