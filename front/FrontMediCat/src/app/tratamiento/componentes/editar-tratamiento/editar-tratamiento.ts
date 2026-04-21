import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { AuthService } from '../../../user/services/auth.service';
import { Tratamiento } from '../../tratamiento';
import { Veterinario } from '../../../veterinario/veterinario';
import { Droga } from '../../../droga/droga';
import { TratamientoDroga } from '../../../tratamiento-droga/tratamiento-droga';
import { Navbar } from '../../../shared/components/navbar/navbar';
import { TratamientoRestService } from '../../services/tratamiento-rest.service';
import { VeterinarioRestService } from '../../../veterinario/services/veterinario-rest.service';
import { DrogaRestService } from '../../../droga/services/droga.service';
import { DrogaMapper, TratamientoMapper, VeterinarioMapper } from '../../../shared/api/model-mappers';

@Component({
  selector: 'app-editar-tratamiento',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, Navbar],
  templateUrl: './editar-tratamiento.html',
  styleUrl: './editar-tratamiento.css',
})
export class EditarTratamiento implements OnInit {
  formData: Tratamiento = {
    id: 0, mascotaId: 0, mascota: '', clienteId: 0,
    veterinarioId: 0, veterinario: '',
    diagnostico: '', observaciones: '', fecha: '',
    estado: 'PENDIENTE', drogas: [],
  };

  mensaje = '';
  error = '';
  noEncontrado = false;
  cargando = false;

  veterinarios: Veterinario[] = [];
  drogas: Droga[] = [];
  private tratamientoOriginal: Tratamiento | null = null;

  constructor(
    private readonly route: ActivatedRoute,
    private readonly tratamientoRestService: TratamientoRestService,
    private readonly veterinarioRestService: VeterinarioRestService,
    private readonly drogaRestService: DrogaRestService,
    private readonly authService: AuthService,
  ) {}

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    if (!id) {
      this.noEncontrado = true;
      this.error = 'No se encontró el tratamiento solicitado.';
      return;
    }
    this.cargarCatalogos();
    this.cargarTratamiento(id);
  }

  private cargarCatalogos(): void {
    this.veterinarioRestService.getAll().subscribe({
      next: (dto) => { this.veterinarios = dto.map(VeterinarioMapper.fromDto); },
      error: () => { this.error = 'No se pudieron cargar los veterinarios.'; },
    });
    this.drogaRestService.getAll().subscribe({
      next: (dto) => { this.drogas = dto.map(DrogaMapper.fromDto); },
      error: () => { this.error = 'No se pudieron cargar las drogas.'; },
    });
  }

  private cargarTratamiento(id: number): void {
    this.cargando = true;
    this.tratamientoRestService.getById(id).subscribe({
      next: (dto) => {
        const tratamiento = TratamientoMapper.fromDto(dto);
        this.tratamientoOriginal = tratamiento;
        this.formData = { ...tratamiento, drogas: tratamiento.drogas.map((d: TratamientoDroga) => ({ ...d })) };
        this.cargando = false;
      },
      error: () => {
        this.noEncontrado = true;
        this.error = 'No se encontró el tratamiento solicitado.';
        this.cargando = false;
      },
    });
  }

  get esAdmin(): boolean {
    return this.authService.getSesion()?.rol === 'ADMIN';
  }

  onVetChange(id: number): void {
    const vet = this.veterinarios.find((v) => v.id === id);
    this.formData.veterinario = vet ? vet.nombre : '';
  }

  agregarDroga(): void {
    const nuevoId = Math.max(0, ...this.formData.drogas.map((d) => d.id ?? 0)) + 1;
    this.formData.drogas.push({ id: nuevoId, drogaId: 0, nombreDroga: '', cantidad: 1 });
  }

  onDrogaChange(index: number, id: number): void {
    const droga = this.drogas.find((d) => d.id === id);
    if (droga) {
      this.formData.drogas[index].nombreDroga = droga.nombre;
    }
  }

  quitarDroga(index: number): void {
    this.formData.drogas.splice(index, 1);
  }

  guardarCambios(): void {
    const { id, veterinarioId, diagnostico, fecha } = this.formData;
    if (!veterinarioId || !diagnostico || !fecha) {
      this.error = 'Veterinario, diagnóstico y fecha son obligatorios.';
      return;
    }
    this.cargando = true;
    this.tratamientoRestService.update(id, TratamientoMapper.toDto(this.formData)).subscribe({
      next: () => {
        this.mensaje = 'El tratamiento fue actualizado correctamente.';
        this.error = '';
        this.cargando = false;
      },
      error: () => {
        this.error = 'No se pudo actualizar el tratamiento en el servidor.';
        this.cargando = false;
      },
    });
  }
}
