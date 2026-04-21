import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { Cliente, MascotaRequest, Veterinario } from '../../../shared/api/backend-contracts';
import { Navbar } from '../../../shared/components/navbar/navbar';
import { ClienteRestService } from '../../../cliente/services/cliente.service';
import { MascotaRestService } from '../../services/mascota.service';
import { VeterinarioRestService } from '../../../veterinario/services/veterinario-rest.service';
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
  veterinarioAsignado: string;
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
  veterinariosDisponibles: Veterinario[] = [];
  mascotaForm: NuevaMascotaForm = this.formInicial();
  mascotaRegistrada = '';
  error = '';
  loading = false;

  constructor(
    private readonly mascotaService: MascotaRestService,
    private readonly clienteService: ClienteRestService,
    private readonly veterinarioService: VeterinarioRestService,
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
    this.veterinarioService.findAll({ estado: 'activo' }).subscribe({
      next: (v) => this.veterinariosDisponibles = v,
      error: () => {},
    });
  }

  registrarMascota(): void {
    const { clienteId, nombre, especie, raza, sexo, fechaNacimiento, edad, peso, estado, enfermedad, veterinarioAsignado, observaciones } = this.mascotaForm;

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
      veterinarioAsignado,
    };

    this.loading = true;
    this.mascotaService.crearMascota(request, clienteId).subscribe({
      next: () => {
        this.mascotaRegistrada = `La mascota "${nombre}" fue registrada correctamente.`;
        this.error = '';
        this.mascotaForm = this.formInicial();
        this.loading = false;
        const sesion = this.authService.getSesion();
        if (sesion?.rol === 'CLIENTE') {
          this.mascotaForm.clienteId = sesion.id;
        }
      },
      error: () => {
        this.error = 'No se pudo registrar la mascota.';
        this.loading = false;
      },
    });
  }

  private formInicial(): NuevaMascotaForm {
    return {
      nombre: '', especie: '', raza: '', sexo: '', fechaNacimiento: '',
      edad: null, peso: null, estado: '', clienteId: null, enfermedad: '',
      veterinarioAsignado: '', observaciones: '',
    };
  }
}
