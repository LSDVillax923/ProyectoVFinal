import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { Mascota } from '../../../mascota/mascota';
import { Veterinario } from '../../../veterinario/veterinario';
import { Droga } from '../../../droga/droga';
import { TratamientoDroga } from '../../../tratamiento-droga/tratamiento-droga';
import { Navbar } from '../../../shared/components/navbar/navbar';
import { TratamientoRestService } from '../../services/tratamiento-rest.service';
import { MascotaRestService } from '../../../mascota/services/mascota.service';
import { VeterinarioRestService } from '../../../veterinario/services/veterinario-rest.service';
import { DrogaRestService } from '../../../droga/services/droga.service';
import { DrogaMapper, MascotaMapper, TratamientoMapper, VeterinarioMapper } from '../../../shared/api/model-mappers';

interface NuevoTratamientoForm {
  mascotaId: number;
  mascota: string;
  clienteId: number;
  veterinarioId: number;
  veterinario: string;
  diagnostico: string;
  observaciones: string;
  fecha: string;
  estado: 'PENDIENTE' | 'COMPLETADO' | 'CANCELADO';
  drogas: TratamientoDroga[];
}

@Component({
  selector: 'app-nuevo-tratamiento',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, Navbar],
  templateUrl: './nuevo-tratamiento.html',
  styleUrl: './nuevo-tratamiento.css',
})
export class NuevoTratamiento implements OnInit {
  mensaje = '';
  error = '';
  cargando = false;

  mascotas: Mascota[] = [];
  veterinarios: Veterinario[] = [];
  drogas: Droga[] = [];
  noHayDrogasDisponibles = false;

  formData: NuevoTratamientoForm = this.crearFormularioInicial();

  constructor(
    private readonly route: ActivatedRoute,
    private readonly tratamientoRestService: TratamientoRestService,
    private readonly mascotaRestService: MascotaRestService,
    private readonly veterinarioRestService: VeterinarioRestService,
    private readonly drogaRestService: DrogaRestService,
  ) {}

  ngOnInit(): void {
    this.cargarCatalogos();
  }

  private crearFormularioInicial(): NuevoTratamientoForm {
    return {
      mascotaId: 0, mascota: '', clienteId: 0,
      veterinarioId: 0, veterinario: '',
      diagnostico: '', observaciones: '',
      fecha: new Date().toISOString().split('T')[0],
      estado: 'PENDIENTE', drogas: [],
    };
  }

  private cargarCatalogos(): void {
    this.cargando = true;
    this.mascotaRestService.getAll().subscribe({
      next: (dto) => {
        this.mascotas = dto.map(MascotaMapper.fromDto);
        this.aplicarMascotaPreseleccionada();
      },
      error: () => { this.error = 'No se pudieron cargar las mascotas.'; },
    });
    this.veterinarioRestService.getAll().subscribe({
      next: (dto) => { this.veterinarios = dto.map(VeterinarioMapper.fromDto); },
      error: () => { this.error = 'No se pudieron cargar los veterinarios.'; },
    });
    this.drogaRestService.findDisponibles().subscribe({
      next: (dto) => {
        this.drogas = dto.map(DrogaMapper.fromDto);
        this.noHayDrogasDisponibles = this.drogas.length === 0;
        if (this.noHayDrogasDisponibles) {
          this.error = 'No hay drogas disponibles en inventario. No se puede asignar un tratamiento.';
        }
        this.cargando = false;
      },
      error: () => { this.error = 'No se pudieron cargar las drogas.'; this.cargando = false; },
    });
  }

  private aplicarMascotaPreseleccionada(): void {
    const mascotaId = Number(this.route.snapshot.queryParamMap.get('mascotaId'));
    if (!mascotaId) return;
    const mascota = this.mascotas.find((m) => m.id === mascotaId);
    if (!mascota) return;
    this.formData.mascotaId = mascotaId;
    this.onMascotaChange(mascotaId);
  }

  onMascotaChange(id: number): void {
    const mascota = this.mascotas.find((m) => m.id === id);
    this.formData.mascota = mascota?.nombre ?? '';
    this.formData.clienteId = mascota?.clienteId ?? 0;
  }

  onVetChange(id: number): void {
    const vet = this.veterinarios.find((v) => v.id === id);
    this.formData.veterinario = vet ? vet.nombre : '';
  }

  agregarDroga(): void {
    if (this.noHayDrogasDisponibles) {
      this.error = 'No hay drogas disponibles en inventario. No se puede asignar un tratamiento.';
      return;
    }
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

  guardarTratamiento(): void {
    if (this.noHayDrogasDisponibles) {
      this.error = 'No hay drogas disponibles en inventario. No se puede asignar un tratamiento.';
      return;
    }
    const { mascotaId, veterinarioId, diagnostico, fecha } = this.formData;
    if (!mascotaId || !veterinarioId || !diagnostico || !fecha) {
      this.error = 'Mascota, veterinario, diagnóstico y fecha son obligatorios.';
      return;
    }
    this.cargando = true;
    const payload = TratamientoMapper.toDto({ ...this.formData, id: 0 });
    this.tratamientoRestService.create(payload, { mascotaId, veterinarioId }).subscribe({
      next: () => {
        this.mensaje = 'El tratamiento fue registrado correctamente.';
        this.error = '';
        this.formData = this.crearFormularioInicial();
        this.cargando = false;
      },
      error: () => {
        this.error = 'No se pudo registrar el tratamiento en el servidor.';
        this.cargando = false;
      },
    });
  }
}
