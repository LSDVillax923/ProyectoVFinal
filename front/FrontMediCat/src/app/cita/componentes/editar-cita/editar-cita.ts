import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { CitaRestService } from '../../services/cita-rest.service';
import { Cita, CitaRequest, SlotDisponible } from '../../../shared/api/backend-contracts';

@Component({
  selector: 'app-editar-cita',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './editar-cita.html',
  styleUrls: ['./editar-cita.css'],
})
export class EditarCitaComponent implements OnInit {

  citaForm: FormGroup;
  loading = false;
  guardando = false;
  cargandoSlots = false;
  error: string | null = null;
  errorFecha: string | null = null;
  mensaje: string | null = null;

  citaId: number | null = null;
  cita: Cita | null = null;
  slots: SlotDisponible[] = [];
  minFecha: string;

  estados: Array<Cita['estado']> = ['PENDIENTE', 'CONFIRMADA', 'REALIZADA', 'CANCELADA'];
  motivos = ['Consulta general', 'Vacunación', 'Control', 'Cirugía', 'Emergencia', 'Peluquería', 'Desparasitación'];

  constructor(
    private readonly fb: FormBuilder,
    private readonly citaService: CitaRestService,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
  ) {
    const hoy = new Date();
    this.minFecha = hoy.toISOString().split('T')[0];

    this.citaForm = this.fb.group({
      motivo: ['', Validators.required],
      estado: ['', Validators.required],
      fecha: [this.minFecha, Validators.required],
      slot: ['', Validators.required],
    });
  }

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (!id) {
      this.error = 'ID de cita no encontrado';
      return;
    }
    this.citaId = +id;
    this.cargarCita();

    this.citaForm.get('fecha')?.valueChanges.subscribe(() => this.recargarSlots());
  }

  private cargarCita(): void {
    if (!this.citaId) return;

    this.loading = true;
    this.citaService.findById(this.citaId).subscribe({
      next: (cita) => {
        this.cita = cita;
        const fechaInicio = cita.fechaInicio ? new Date(cita.fechaInicio) : null;
        const fecha = fechaInicio ? this.toIsoDate(fechaInicio) : this.minFecha;

        this.citaForm.patchValue({
          motivo: cita.motivo,
          estado: cita.estado,
          fecha,
          slot: cita.fechaInicio,
        });

        this.recargarSlots(cita.fechaInicio);
        this.loading = false;
      },
      error: () => {
        this.error = 'Error al cargar la cita';
        this.loading = false;
      },
    });
  }

  private recargarSlots(slotPreseleccionado?: string): void {
    this.errorFecha = null;
    if (!this.cita?.veterinarioId) {
      this.slots = [];
      return;
    }
    const fecha = this.citaForm.get('fecha')?.value as string;
    if (!fecha) {
      this.slots = [];
      return;
    }

    if (this.esFinDeSemana(fecha)) {
      this.errorFecha = 'Este día es inválido: la veterinaria no atiende los fines de semana.';
      this.slots = [];
      this.citaForm.get('slot')?.setValue('');
      return;
    }
    if (this.esFechaPasada(fecha)) {
      this.errorFecha = 'No se pueden agendar citas en fechas anteriores a hoy.';
      this.slots = [];
      this.citaForm.get('slot')?.setValue('');
      return;
    }

    this.cargandoSlots = true;
    this.citaService.disponibilidad(this.cita.veterinarioId, fecha).subscribe({
      next: (slots) => {
        // El backend filtra los slots libres; agregamos el actual de la cita para que
        // editar sin mover el horario siga siendo válido.
        const slotsConActual = slotPreseleccionado && !slots.some((s) => s.inicio === slotPreseleccionado)
          ? [{ inicio: slotPreseleccionado, fin: this.cita?.fechaFin ?? slotPreseleccionado }, ...slots]
          : slots;
        this.slots = slotsConActual.sort((a, b) => a.inicio.localeCompare(b.inicio));
        this.cargandoSlots = false;
      },
      error: () => {
        this.slots = [];
        this.cargandoSlots = false;
      },
    });
  }

  onSubmit(): void {
    if (!this.citaId || !this.cita) return;
    if (this.citaForm.invalid) {
      this.citaForm.markAllAsTouched();
      return;
    }

    const v = this.citaForm.value;
    const slot = this.slots.find((s) => s.inicio === v.slot);
    if (!slot) {
      this.error = 'Selecciona un horario válido.';
      return;
    }

    const payload: CitaRequest = {
      motivo: v.motivo,
      estado: v.estado,
      fechaInicio: slot.inicio,
      fechaFin: slot.fin,
    };

    this.guardando = true;
    this.error = null;
    this.mensaje = null;

    this.citaService.update(this.citaId, payload).subscribe({
      next: () => {
        this.guardando = false;
        this.mensaje = 'Cita actualizada correctamente.';
        setTimeout(() => this.router.navigate(['/citas']), 900);
      },
      error: (err) => {
        this.guardando = false;
        this.error = err?.error?.message || 'No se pudo actualizar la cita.';
      },
    });
  }

  formatearSlot(slot: SlotDisponible): string {
    const inicio = new Date(slot.inicio);
    const fin = new Date(slot.fin);
    const fmt = (d: Date) => d.toLocaleTimeString('es-CO', { hour: '2-digit', minute: '2-digit' });
    return `${fmt(inicio)} - ${fmt(fin)}`;
  }

  isInvalid(controlName: string): boolean {
    const control = this.citaForm.get(controlName);
    return !!(control && control.invalid && control.touched);
  }

  getTextoEstado(estado: string): string {
    const textos: Record<string, string> = {
      PENDIENTE: 'Pendiente',
      CONFIRMADA: 'Confirmada',
      REALIZADA: 'Realizada',
      CANCELADA: 'Cancelada',
    };
    return textos[estado] || estado;
  }

  private esFinDeSemana(fechaIso: string): boolean {
    // Construir como local para evitar el offset que da `new Date('YYYY-MM-DD')`
    const [y, m, d] = fechaIso.split('-').map(Number);
    const date = new Date(y, (m ?? 1) - 1, d ?? 1);
    const day = date.getDay();
    return day === 0 || day === 6;
  }

  private esFechaPasada(fechaIso: string): boolean {
    return fechaIso < this.minFecha;
  }

  private toIsoDate(d: Date): string {
    const pad = (n: number) => n.toString().padStart(2, '0');
    return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}`;
  }
}
