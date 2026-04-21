import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { forkJoin, of, switchMap, map } from 'rxjs';
import { AuthService } from '../../../user/services/auth.service';
import { Tratamiento } from '../../tratamiento';
import { Navbar } from '../../../shared/components/navbar/navbar';
import { TratamientoRestService } from '../../services/tratamiento-rest.service';
import { MascotaRestService } from '../../../mascota/services/mascota.service';
import { TratamientoMapper } from '../../../shared/api/model-mappers';
import { Tratamiento as BackendTratamiento } from '../../../shared/api/backend-contracts';

@Component({
  selector: 'app-listar-tratamientos',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, Navbar],
  templateUrl: './listar-tratamientos.html',
  styleUrl: './listar-tratamientos.css',
})
export class ListarTratamientos implements OnInit {
  busqueda = '';
  filtroEstado = '';
  mensaje = '';
  error = '';
  cargando = false;

  private todos: Tratamiento[] = [];

  constructor(
    private readonly tratamientoRestService: TratamientoRestService,
    private readonly mascotaRestService: MascotaRestService,
    private readonly authService: AuthService,
    private readonly route: ActivatedRoute,
  ) {}

  ngOnInit(): void {
    this.cargarTratamientos();
  }

  private cargarTratamientos(): void {
    this.cargando = true;
    this.error = '';

    const sesion = this.authService.getSesion();
    const mascotaId = this.route.snapshot.queryParamMap.get('mascota');

    let request$;
    if (mascotaId) {
      request$ = this.tratamientoRestService.findByMascotaId(Number(mascotaId));
    } else if (sesion?.rol === 'CLIENTE') {
      // El backend no expone /tratamientos/cliente/{id}. Se arma del lado del cliente:
      // obtener mascotas del cliente y juntar tratamientos de cada mascota.
      request$ = this.mascotaRestService.findByClienteId(sesion.id).pipe(
        switchMap((mascotas) => {
          if (mascotas.length === 0) return of<BackendTratamiento[]>([]);
          const peticiones = mascotas.map((m) => this.tratamientoRestService.findByMascotaId(m.id));
          return forkJoin(peticiones).pipe(map((listas) => listas.flat()));
        }),
      );
    } else if (sesion?.rol === 'VETERINARIO') {
      request$ = this.tratamientoRestService.findByVeterinarioId(sesion.id);
    } else {
      request$ = this.tratamientoRestService.findAll();
    }

    request$.subscribe({
      next: (tratamientosDto) => {
        this.todos = tratamientosDto.map(TratamientoMapper.fromDto);
        this.cargando = false;
      },
      error: () => {
        this.error = 'No se pudieron cargar los tratamientos desde el servidor.';
        this.todos = [];
        this.cargando = false;
      },
    });
  }

  get sesion() {
    return this.authService.getSesion();
  }

  get esCliente(): boolean {
    return this.sesion?.rol === 'CLIENTE';
  }

  get puedeEditar(): boolean {
    const rol = this.sesion?.rol;
    return rol === 'VETERINARIO' || rol === 'ADMIN';
  }

  get puedeCrear(): boolean {
    return this.puedeEditar;
  }

  get tratamientosFiltrados(): Tratamiento[] {
    let lista = this.todos;

    const filtro = this.busqueda.trim().toLowerCase();
    if (filtro) {
      lista = lista.filter(
        (t) =>
          t.mascota.toLowerCase().includes(filtro) ||
          t.veterinario.toLowerCase().includes(filtro) ||
          t.diagnostico.toLowerCase().includes(filtro),
      );
    }

    if (this.filtroEstado) {
      lista = lista.filter((t) => t.estado === this.filtroEstado);
    }

    return lista;
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
