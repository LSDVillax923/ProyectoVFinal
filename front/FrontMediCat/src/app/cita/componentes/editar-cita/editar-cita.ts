import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { CitaRestService } from '../../services/cita-rest.service';
import { Cita } from '../../../shared/api/backend-contracts';

@Component({
  selector: 'app-editar-cita',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './editar-cita.html',
  styleUrls: ['./editar-cita.css']
})
export class EditarCitaComponent implements OnInit {
  
  citaForm: FormGroup;
  loading = false;
  error: string | null = null;
  citaId: number | null = null;
  cita: Cita | null = null;

  estados = ['PENDIENTE', 'CONFIRMADA', 'REALIZADA', 'CANCELADA'];
  motivos = ['Consulta general', 'Vacunación', 'Control', 'Cirugía', 'Emergencia', 'Peluquería', 'Desparasitación'];

  constructor(
    private fb: FormBuilder,
    private citaService: CitaRestService,
    private route: ActivatedRoute,
    private router: Router
  ) {
    this.citaForm = this.fb.group({
      motivo: ['', Validators.required],
      estado: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.citaId = +id;
      this.cargarCita();
    } else {
      this.error = 'ID de cita no encontrado';
    }
  }

  cargarCita(): void {
    if (!this.citaId) return;
    
    this.loading = true;
    this.citaService.findById(this.citaId).subscribe({
      next: (cita: Cita) => {
        this.cita = cita;
        this.citaForm.patchValue({
          motivo: cita.motivo,
          estado: cita.estado
        });
        this.loading = false;
      },
      error: (err: any) => {
        this.error = 'Error al cargar la cita';
        console.error(err);
        this.loading = false;
      }
    });
  }

  onSubmit(): void {
    if (this.citaForm.invalid || !this.citaId) {
      this.citaForm.markAllAsTouched();
      return;
    }

    this.loading = true;
    const formValue = this.citaForm.value;

    this.citaService.update(this.citaId, formValue).subscribe({
      next: () => {
        this.loading = false;
        this.router.navigate(['/citas']);
      },
      error: (err: any) => {
        this.loading = false;
        this.error = err.error?.message || 'Error al actualizar la cita';
        console.error(err);
      }
    });
  }

  isInvalid(controlName: string): boolean {
    const control = this.citaForm.get(controlName);
    return !!(control && control.invalid && control.touched);
  }

  getTextoEstado(estado: string): string {
    const textos: Record<string, string> = {
      'PENDIENTE': 'Pendiente',
      'CONFIRMADA': 'Confirmada',
      'REALIZADA': 'Realizada',
      'CANCELADA': 'Cancelada'
    };
    return textos[estado] || estado;
  }
}