import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { forkJoin, of, switchMap } from 'rxjs';
import { Mascota } from '../../../mascota/mascota';
import { Veterinario } from '../../../veterinario/veterinario';
import { Droga } from '../../../droga/droga';
import { TratamientoDroga } from '../../../tratamiento-droga/tratamiento-droga';
import { Navbar } from '../../../shared/components/navbar/navbar';
import { TratamientoRestService } from '../../services/tratamiento-rest.service';
import { MascotaRestService } from '../../../mascota/services/mascota.service';
import { VeterinarioRestService } from '../../../veterinario/services/veterinario-rest.service';
import { DrogaRestService } from '../../../droga/services/droga.service';
import { TratamientoDrogaRestService } from '../../../tratamiento-droga/services/tratamiento-droga-rest.service';
import { DrogaMapper, MascotaMapper, TratamientoMapper, VeterinarioMapper } from '../../../shared/api/model-mappers';
import { AuthService } from '../../../user/services/auth.service';

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
  mascotasDisponibles: Mascota[] = [];
  veterinarios: Veterinario[] = [];
  drogas: Droga[] = [];
  noHayDrogasDisponibles = false;
  veterinarioBloqueado = false;

  formData: NuevoTratamientoForm = this.crearFormularioInicial();

  constructor(
    private readonly route: ActivatedRoute,
    private readonly tratamientoRestService: TratamientoRestService,
    private readonly mascotaRestService: MascotaRestService,
    private readonly veterinarioRestService: VeterinarioRestService,
    private readonly drogaRestService: DrogaRestService,
    private readonly tratamientoDrogaRestService: TratamientoDrogaRestService,
    private readonly authService: AuthService,
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
        this.mascotasDisponibles = this.mascotas.filter((m) => m.estado !== 'INACTIVA');
        this.aplicarMascotaPreseleccionada();
      },
      error: () => { this.error = 'No se pudieron cargar las mascotas.'; },
    });
    this.veterinarioRestService.getAll().subscribe({
      next: (dto) => {
        this.veterinarios = dto.map(VeterinarioMapper.fromDto);
        this.aplicarVeterinarioLogueado();
      },
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

  private aplicarVeterinarioLogueado(): void {
    const vetCookie = this.authService.getVeterinarioLogueado();
    if (!vetCookie) return;
    const vet = this.veterinarios.find((v) => v.id === vetCookie.id);
    if (!vet) return;
    this.formData.veterinarioId = vet.id;
    this.formData.veterinario = vet.nombre;
    this.veterinarioBloqueado = true;
  }

  private aplicarMascotaPreseleccionada(): void {
    const mascotaId = Number(this.route.snapshot.queryParamMap.get('mascotaId'));
    if (!mascotaId) return;
    const mascota = this.mascotas.find((m) => m.id === mascotaId);
    if (!mascota) return;
    if (mascota.estado === 'INACTIVA') {
      this.error = 'La mascota seleccionada está inactiva y no puede recibir tratamientos.';
      return;
    }
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
    this.error = '';
  }

  onDrogaChange(index: number, id: number): void {
    const droga = this.drogas.find((d) => d.id === id);
    if (droga) {
      this.formData.drogas[index].nombreDroga = droga.nombre;
      const maximo = this.unidadesDisponiblesParaLinea(index);
      if (this.formData.drogas[index].cantidad > maximo) {
        this.formData.drogas[index].cantidad = Math.max(1, maximo);
      }
    }
  }

  unidadesDisponiblesParaLinea(index: number): number {
    const linea = this.formData.drogas[index];
    if (!linea?.drogaId) return 0;

    const droga = this.drogas.find((d) => d.id === linea.drogaId);
    if (!droga) return 0;

    const usadasEnOtrasLineas = this.formData.drogas
      .filter((d, i) => i !== index && d.drogaId === linea.drogaId)
      .reduce((acum, d) => acum + (Number(d.cantidad) || 0), 0);

    return Math.max(0, droga.unidadesDisponibles - usadasEnOtrasLineas);
  }

  onCantidadChange(index: number): void {
    const maximo = this.unidadesDisponiblesParaLinea(index);
    const cantidad = Number(this.formData.drogas[index].cantidad) || 0;

    if (cantidad < 1) {
      this.formData.drogas[index].cantidad = 1;
      return;
    }

    if (cantidad > maximo) {
      this.formData.drogas[index].cantidad = Math.max(1, maximo);
    }
  }

  cantidadesDisponiblesParaLinea(index: number): number[] {
    const maximo = this.unidadesDisponiblesParaLinea(index);
    if (maximo < 1) return [];
    return Array.from({ length: maximo }, (_, i) => i + 1);
  }

  quitarDroga(index: number): void {
    this.formData.drogas.splice(index, 1);
  }

  private validarDrogas(): string | null {
    for (let i = 0; i < this.formData.drogas.length; i++) {
      const droga = this.formData.drogas[i];
      if (!droga.drogaId) {
        return `Selecciona un medicamento en la fila ${i + 1}.`;
      }
      if (!droga.cantidad || droga.cantidad < 1) {
        return `La cantidad del medicamento en la fila ${i + 1} debe ser mayor a 0.`;
      }
      if (droga.cantidad > this.unidadesDisponiblesParaLinea(i)) {
        return `No hay suficiente inventario para ${droga.nombreDroga || 'el medicamento seleccionado'}.`;
      }
    }
    return null;
  }


  guardarTratamiento(): void {
    if (this.noHayDrogasDisponibles) {
      this.error = 'No hay drogas disponibles en inventario. No se puede asignar un tratamiento.';
      return;
    }

    const validacionDrogas = this.validarDrogas();
    if (validacionDrogas) {
      this.error = validacionDrogas;
      return;
    }

    const { mascotaId, veterinarioId, diagnostico, fecha } = this.formData;
    if (!mascotaId || !veterinarioId || !diagnostico || !fecha) {
      this.error = 'Mascota, veterinario, diagnóstico y fecha son obligatorios.';
      return;
    }

    const mascotaSeleccionada = this.mascotas.find((m) => m.id === mascotaId);
    if (!mascotaSeleccionada || mascotaSeleccionada.estado === 'INACTIVA') {
      this.error = 'No se puede crear un tratamiento para una mascota inactiva.';
      return;
    }
    
    this.cargando = true;
    const payload = TratamientoMapper.toDto({ ...this.formData, id: 0 });
    this.tratamientoRestService.create(payload, { mascotaId, veterinarioId }).pipe(
      switchMap((tratamientoCreado) => {
        const lineas = this.formData.drogas.filter((d) => d.drogaId > 0 && d.cantidad > 0);
        if (lineas.length === 0) {
          return of(tratamientoCreado);
        }

        return forkJoin(
          lineas.map((d) =>
            this.tratamientoDrogaRestService.agregarDroga(tratamientoCreado.id, d.drogaId, d.cantidad),
          ),
        ).pipe(switchMap(() => of(tratamientoCreado)));
      }),
    ).subscribe({
      next: () => {
        this.mensaje = 'El tratamiento fue registrado correctamente.';
        this.error = '';
        this.formData = this.crearFormularioInicial();
        this.aplicarVeterinarioLogueado();
        this.cargando = false;
      },
      error: () => {
        this.error = 'No se pudo registrar el tratamiento en el servidor.';
        this.cargando = false;
      },
    });
  }
}
