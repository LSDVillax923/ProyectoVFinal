import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { AuthRestService } from '../../services/auth-rest.service';

@Component({
  selector: 'app-reset-password',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './reset-password.html',
  styleUrl: './reset-password.css',
})
export class ResetPassword implements OnInit {
  token = '';
  nuevaContrasenia = '';
  confirmacion = '';
  mensaje = '';
  error = '';
  enviando = false;
  tokenAusente = false;

  constructor(
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly authService: AuthRestService,
  ) {}

  ngOnInit(): void {
    this.token = this.route.snapshot.queryParamMap.get('token') ?? '';
    if (!this.token) {
      this.tokenAusente = true;
      this.error = 'Link inválido: falta el token. Abre el link desde el correo que recibiste.';
    }
  }

  cambiarContrasenia(): void {
    if (!this.token) {
      this.error = 'Falta el token.';
      return;
    }
    if (!this.nuevaContrasenia || this.nuevaContrasenia.length < 4) {
      this.error = 'La contraseña debe tener al menos 4 caracteres.';
      return;
    }
    if (this.nuevaContrasenia !== this.confirmacion) {
      this.error = 'Las contraseñas no coinciden.';
      return;
    }

    this.enviando = true;
    this.error = '';
    this.mensaje = '';

    this.authService.resetPassword(this.token, this.nuevaContrasenia).subscribe({
      next: (resp) => {
        this.mensaje = resp.mensaje || 'Contraseña actualizada correctamente.';
        this.enviando = false;
        this.nuevaContrasenia = '';
        this.confirmacion = '';
        // Redirige al login después de un par de segundos para que el usuario lea el mensaje.
        setTimeout(() => this.router.navigate(['/inicio/login']), 1800);
      },
      error: (err) => {
        this.error = err.error?.message || 'No se pudo actualizar la contraseña. El link puede haber expirado.';
        this.enviando = false;
      },
    });
  }
}
