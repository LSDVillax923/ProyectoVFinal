import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { CitaRestService } from '../../services/cita-rest.service';
import { ClienteRestService } from '../../../cliente/services/cliente.service';
import { MascotaRestService } from '../../../mascota/services/mascota.service';
import { VeterinarioRestService } from '../../../veterinario/services/veterinario-rest.service';
import { Cliente, Mascota, Veterinario } from '../../../shared/api/backend-contracts';

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
  
  clientes: Cliente[] = [];
  mascotas: Mascota[] = [];
  mascotasFiltradas: Mascota[] = [];
  veterinarios: Veterinario[] = [];
  
  minFecha: string;

  constructor(
    private fb: FormBuilder,
    private citaService: CitaRestService,
    private clienteService: ClienteRestService,
    private mascotaService: MascotaRestService,
    private veterinarioService: VeterinarioRestService,
    private router: Router
  ) {
    // Fecha mínima = hoy
    const hoy = new Date();
    this.minFecha = hoy.toISOString().split('T')[0];
    
    this.citaForm = this.fb.group({
      clienteId: ['', Validators.required],
      mascotaId: ['', Validators.required],
      veterinarioId: ['', Validators.required],
      fecha: [this.minFecha, Validators.required],
      horaInicio: ['09:00', Validators.required],
      horaFin: ['09:30', Validators.required],
      motivo: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    this.cargarClientes();
    this.cargarVeterinarios();
    
    // Cuando cambia el cliente, filtrar mascotas
    this.citaForm.get('clienteId')?.valueChanges.subscribe(clienteId => {
      this.filtrarMascotas(clienteId);
      this.citaForm.get('mascotaId')?.setValue('');
    });
  }

  cargarClientes(): void {
    this.clienteService.findAll().subscribe({
      next: (clientes) => this.clientes = clientes,
      error: (err) => console.error('Error cargando clientes:', err)
    });
  }

  cargarVeterinarios(): void {
    this.veterinarioService.findAll({ estado: 'activo' }).subscribe({
      next: (vets) => this.veterinarios = vets,
      error: (err) => console.error('Error cargando veterinarios:', err)
    });
  }

  filtrarMascotas(clienteId: number): void {
    if (!clienteId) {
      this.mascotasFiltradas = [];
      return;
    }
    
    this.mascotaService.findByClienteId(clienteId).subscribe({
      next: (mascotas) => this.mascotasFiltradas = mascotas,
      error: (err) => console.error('Error cargando mascotas:', err)
    });
  }

  onSubmit(): void {
    if (this.citaForm.invalid) {
      this.citaForm.markAllAsTouched();
      return;
    }

    const formValue = this.citaForm.value;
    
    // Construir fechas ISO
    const fechaInicio = `${formValue.fecha}T${formValue.horaInicio}:00`;
    const fechaFin = `${formValue.fecha}T${formValue.horaFin}:00`;

    const citaData = {
      fechaInicio,
      fechaFin,
      motivo: formValue.motivo,
      estado: 'PENDIENTE' as const
    };

    this.loading = true;
    this.citaService.create(citaData, { clienteId: formValue.clienteId, mascotaId: formValue.mascotaId, veterinarioId: formValue.veterinarioId }).subscribe({
      next: () => {
        this.loading = false;
        this.router.navigate(['/citas']);
      },
      error: (err) => {
        this.loading = false;
        this.error = err.error?.message || 'Error al crear la cita';
        console.error(err);
      }
    });
  }

  // Helpers para validación
  isInvalid(controlName: string): boolean {
    const control = this.citaForm.get(controlName);
    return !!(control && control.invalid && control.touched);
  }
}