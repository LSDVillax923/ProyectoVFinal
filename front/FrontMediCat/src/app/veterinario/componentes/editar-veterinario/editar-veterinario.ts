import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { VeterinarioUpdateDto } from '../../../shared/api/backend-contracts';
import { VeterinarioMapper } from '../../../shared/api/model-mappers';
import { Navbar } from '../../../shared/components/navbar/navbar';
import { VeterinarioRestService } from '../../services/veterinario-rest.service';

interface VeterinarioEditable {
  id: number;
  nombre: string;
  cedula: string;
  correo: string;
  celular: string;
  contrasenia: string;
  especialidad: string;
}

@Component({
  selector: 'app-editar-veterinario',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, Navbar],
  templateUrl: './editar-veterinario.html',
  styleUrl: './editar-veterinario.css',
})
export class EditarVeterinario implements OnInit {
  formData: VeterinarioEditable = {
    id: 0,
    nombre: '',
    cedula: '',
    correo: '',
    celular: '',
    contrasenia: '',
    especialidad: '',
  };
  mensaje = '';
  error = '';
  noEncontrado = false;

  especialidades = [
    'Medicina General',
    'Cirugía',
    'Dermatología',
    'Cardiología',
    'Oncología',
    'Oftalmología',
    'Neurología',
    'Ortopedia',
    'Odontología',
    'Nutrición',
  ];

  constructor(
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly veterinarioRestService: VeterinarioRestService,
  ) {}

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.veterinarioRestService.findById(id).subscribe({
      next: (vetDto) => {
        const vet = VeterinarioMapper.fromDto(vetDto);
        this.formData = {
          id: vet.id,
          nombre: vet.nombre,
          cedula: vet.cedula,
          correo: vet.correo,
          celular: vet.celular,
          contrasenia: '',
          especialidad: vet.especialidad,
        };
      },
      error: () => {
        this.noEncontrado = true;
        this.error = 'No se encontró el veterinario solicitado.';
      },
    });
  }

  guardarCambios(): void {
    const { id, nombre, cedula, correo, celular, contrasenia, especialidad } = this.formData;

    if (!nombre || !cedula || !correo || !celular || !especialidad) {
      this.error = 'Todos los campos son obligatorios excepto la contraseña.';
      return;
    }

    const cambios: VeterinarioUpdateDto = { nombre, cedula, correo, celular, especialidad };
    if (contrasenia) cambios.contrasenia = contrasenia;

    this.veterinarioRestService.update(id, cambios).subscribe({
      next: () => {
        this.mensaje = `${nombre} fue actualizado correctamente.`;
        this.error = '';
      },
      error: () => {
        this.error = 'No se pudo actualizar el veterinario.';
      },
    });
  }
}
