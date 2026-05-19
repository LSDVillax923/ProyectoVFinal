import { Component, OnInit, inject } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { AuthRestService } from '../../user/services/auth-rest.service';

@Component({
  selector: 'app-inicio',
  imports: [RouterLink],
  templateUrl: './inicio.html',
  styleUrl: './inicio.css',
})

export class Inicio implements OnInit {
  private auth = inject(AuthRestService);
  private router = inject(Router);

  ngOnInit(): void {
    const sesion = this.auth.getSesion();
    if (!sesion) return;

    if (sesion.rol === 'ADMIN') {
      this.router.navigate(['/dashboard']);
      return;
    }

    if (sesion.rol === 'VETERINARIO') {
      this.router.navigate(['/clientes']);
      return;
    }

    this.router.navigate(['/mis-mascotas']);
  }
}