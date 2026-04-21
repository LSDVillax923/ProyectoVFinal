import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { Cliente, Mascota } from '../../../shared/api/backend-contracts';
import { ClienteRestService } from '../../services/cliente.service';
import { MascotaRestService } from '../../../mascota/services/mascota.service';
import { Navbar } from '../../../shared/components/navbar/navbar';

@Component({
  selector: 'app-ver-cliente',
  standalone: true,
  imports: [CommonModule, RouterLink, Navbar],
  templateUrl: './ver-cliente.html',
  styleUrl: './ver-cliente.css',
})
export class VerCliente implements OnInit {
  cliente: Cliente | null = null;
  mascotas: Mascota[] = [];

  constructor(
    private readonly route: ActivatedRoute,
    private readonly clienteRestService: ClienteRestService,
    private readonly mascotaRestService: MascotaRestService,
  ) {}

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.clienteRestService.findById(id).subscribe({
      next: (cliente) => {
        this.cliente = cliente;
      },
      error: () => {
        this.cliente = null;
      },
    });
    this.mascotaRestService.findByClienteId(id).subscribe({
      next: (mascotas) => {
        this.mascotas = mascotas;
      },
      error: () => {
        this.mascotas = [];
      },
    });
  }

  get totalMascotas(): number {
    return this.mascotas.length;
  }
}
