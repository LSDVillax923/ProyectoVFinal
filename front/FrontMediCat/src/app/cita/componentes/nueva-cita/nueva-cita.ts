import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { CitaRestService } from '../../services/cita-rest.service';
import { ClienteRestService } from '../../../cliente/services/cliente.service';
import { MascotaRestService } from '../../../mascota/services/mascota.service';
import { VeterinarioRestService } from '../../../veterinario/services/veterinario-rest.service';
import { AuthService } from '../../../user/services/auth.service';
import { Cliente, Mascota, Veterinario, SlotDisponible } from '../../../shared/api/backend-contracts';

@Component({
  selector: 'app-nueva-cita',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './nueva-cita.html',
  styleUrls: ['./nueva-cita.css']
})
export class NuevaCitaComponent implements OnInit {

  citaForm: FormGroup;
  loading = false;
  error: string | null = null;
  errorFecha: string | null = null;
  mensaje: string | null = null;

  esCliente = false;
  esAdmin = false;
  clienteIdFijo: number | null = null;

  clientes: Cliente[] = [];
  mascotasFiltradas: Mascota[] = [];
  veterinarios: Veterinario[] = [];
  slots: SlotDisponible[] = [];
  cargandoSlots = false;

  minFecha: string;

  constructor(
    private fb: FormBuilder,
    private citaService: CitaRestService,
    private clienteService: ClienteRestService,
    private mascotaService: MascotaRestService,
    private veterinarioService: VeterinarioRestService,
    private authService: AuthService,
    private router: Router,
    private route: ActivatedRoute,
  ) {
    const hoy = new Date();
    this.minFecha = hoy.toISOString().split('T')[0];

    this.citaForm = this.fb.group({
      clienteId: ['', Validators.required],
      mascotaId: ['', Validators.required],
      veterinarioId: ['', Validators.required],
      fecha: [this.minFecha, Validators.required],
      slot: ['', Validators.required],
      motivo: ['', Validators.required],
    });
  }

  ngOnInit(): void {
    const sesion = this.authService.getSesion();
    this.esCliente = sesion?.rol === 'CLIENTE';
    this.esAdmin   = sesion?.rol === 'ADMIN';

    this.cargarVeterinarios();

    if (this.esCliente && sesion) {
      // Cliente: cliente fijo + sus propias mascotas, sin selector de cliente.
      this.clienteIdFijo = sesion.id;
      this.citaForm.patchValue({ clienteId: sesion.id });
      this.citaForm.get('clienteId')?.disable();
      this.cargarMascotasDeCliente(sesion.id);
    } else {
      // Admin: lista todos los clientes y carga mascotas cuando elija.
      this.cargarClientes();
      this.citaForm.get('clienteId')?.valueChanges.subscribe((clienteId) => {
        this.citaForm.get('mascotaId')?.setValue('');
        if (clienteId) this.cargarMascotasDeCliente(Number(clienteId));
        else this.mascotasFiltradas = [];
      });
    }

    // Cuando cambia veterinario o fecha, recargar slots disponibles
    this.citaForm.get('veterinarioId')?.valueChanges.subscribe(() => this.recargarSlots());
    this.citaForm.get('fecha')?.valueChanges.subscribe(() => this.recargarSlots());
  }

  private cargarClientes(): void {
    this.clienteService.findAll().subscribe({
      next: (clientes) => (this.clientes = clientes),
      error: (err) => console.error('Error cargando clientes:', err),
    });
  }

  private cargarVeterinarios(): void {
    // El backend filtra solo activos cuando llega ?estado=activo.
    this.veterinarioService.findAll({ estado: 'activo' }).subscribe({
      next: (vets) => (this.veterinarios = vets),
      error: (err) => console.error('Error cargando veterinarios:', err),
    });
  }

  private cargarMascotasDeCliente(clienteId: number): void {
    this.mascotaService.findByClienteId(clienteId).subscribe({
      next: (mascotas) => {
        this.mascotasFiltradas = mascotas;
        // Si se llegó con ?mascota=<id> (p.ej. desde "Mis Mascotas → Agendar") y la
        // mascota pertenece a este cliente, pre-seleccionarla.
        const mascotaQp = this.route.snapshot.queryParamMap.get('mascota');
        if (mascotaQp && mascotas.some((m) => String(m.id) === mascotaQp)) {
          this.citaForm.patchValue({ mascotaId: Number(mascotaQp) });
        }
      },
      error: (err) => console.error('Error cargando mascotas:', err),
    });
  }

  private recargarSlots(): void {
    const vetId = this.citaForm.get('veterinarioId')?.value;
    const fecha = this.citaForm.get('fecha')?.value;
    this.citaForm.get('slot')?.setValue('');
    this.errorFecha = null;

    if (!vetId || !fecha) {
      this.slots = [];
      return;
    }

    // Validaciones tempranas en cliente — el backend también las aplica,
    // pero al detectarlas aquí evitamos el round-trip y damos feedback inmediato.
    if (this.esFechaPasada(fecha)) {
      this.errorFecha = 'No se pueden agendar citas en fechas anteriores a hoy.';
      this.slots = [];
      return;
    }
    if (this.esFinDeSemana(fecha)) {
      this.errorFecha = 'Este día es inválido: la veterinaria no atiende los fines de semana.';
      this.slots = [];
      return;
    }

    this.cargandoSlots = true;
    this.citaService.disponibilidad(Number(vetId), fecha).subscribe({
      next: (slots) => {
        this.slots = slots;
        this.cargandoSlots = false;
      },
      error: () => {
        this.slots = [];
        this.cargandoSlots = false;
      },
    });
  }

  private esFechaPasada(fechaIso: string): boolean {
    return fechaIso < this.minFecha;
  }

  private esFinDeSemana(fechaIso: string): boolean {
    const [y, m, d] = fechaIso.split('-').map(Number);
    const date = new Date(y, (m ?? 1) - 1, d ?? 1);
    const day = date.getDay();
    return day === 0 || day === 6;
  }

  formatearSlot(slot: SlotDisponible): string {
    const inicio = new Date(slot.inicio);
    const fin = new Date(slot.fin);
    const fmt = (d: Date) => d.toLocaleTimeString('es-CO', { hour: '2-digit', minute: '2-digit' });
    return `${fmt(inicio)} - ${fmt(fin)}`;
  }

  onSubmit(): void {
    if (this.citaForm.invalid) {
      this.citaForm.markAllAsTouched();
      return;
    }
    const v = this.citaForm.getRawValue();
    const slot: SlotDisponible | undefined = this.slots.find((s) => s.inicio === v.slot);
    if (!slot) {
      this.error = 'Selecciona un horario válido.';
      return;
    }

    const cita = {
      fechaInicio: slot.inicio,
      fechaFin: slot.fin,
      motivo: v.motivo,
    };

    this.loading = true;
    this.error = null;
    this.mensaje = null;

    if (this.esCliente) {
      // Cliente: solicitud (queda en PENDIENTE).
      this.citaService.solicitar(cita, Number(v.clienteId), Number(v.mascotaId), Number(v.veterinarioId)).subscribe({
        next: () => {
          this.loading = false;
          this.mensaje = 'Tu solicitud fue enviada. Un administrador la aprobará pronto.';
          setTimeout(() => this.router.navigate(['/mis-citas']), 1200);
        },
        error: (err) => {
          this.loading = false;
          this.error = err.error?.message || 'No se pudo enviar la solicitud.';
        },
      });
    } else {
      // Admin: crea cita directamente (queda CONFIRMADA).
      this.citaService.create(cita, {
        clienteId: v.clienteId,
        mascotaId: v.mascotaId,
        veterinarioId: v.veterinarioId,
      }).subscribe({
        next: () => {
          this.loading = false;
          this.router.navigate(['/citas']);
        },
        error: (err) => {
          this.loading = false;
          this.error = err.error?.message || 'No se pudo crear la cita.';
        },
      });
    }
  }

  isInvalid(controlName: string): boolean {
    const control = this.citaForm.get(controlName);
    return !!(control && control.invalid && control.touched);
  }
}
