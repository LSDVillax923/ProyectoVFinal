import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { DrogaRequest } from '../../../shared/api/backend-contracts';
import { Navbar } from '../../../shared/components/navbar/navbar';
import { AuthService } from '../../../user/services/auth.service';
import { DrogaRestService } from '../../services/droga.service';

@Component({
  selector: 'app-editar-droga',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, Navbar],
  templateUrl: './editar-droga.html',
  styleUrl: './editar-droga.css',
})
export class EditarDroga implements OnInit {
  formData: DrogaRequest = { nombre: '', precioCompra: 0, precioVenta: 0, unidadesDisponibles: 0 };
  mensaje = '';
  error = '';
  noEncontrado = false;

  constructor(
    private readonly route: ActivatedRoute,
    private readonly drogaRestService: DrogaRestService,
    private readonly authService: AuthService,
  ) {}

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.drogaRestService.getById(id).subscribe({
      next: (droga) => {
        this.formData = {
          nombre: droga.nombre,
          precioCompra: droga.precioCompra,
          precioVenta: droga.precioVenta,
          unidadesDisponibles: droga.unidadesDisponibles,
        };
      },
      error: () => {
        this.noEncontrado = true;
        this.error = 'No se encontró el medicamento solicitado.';
      },
    });
  }

  get esAdmin(): boolean {
    return this.authService.getSesion()?.rol === 'ADMIN';
  }

  guardarCambios(): void {
    if (!this.formData.nombre) {
      this.error = 'El nombre del medicamento es obligatorio.';
      return;
    }
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.drogaRestService.update(id, this.formData).subscribe({
      next: () => {
        this.mensaje = `${this.formData.nombre} fue actualizado correctamente.`;
        this.error = '';
      },
      error: () => {
        this.error = 'No se pudo actualizar el medicamento.';
      },
    });
  }
}
