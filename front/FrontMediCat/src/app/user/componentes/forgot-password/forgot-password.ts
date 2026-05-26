import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { AuthRestService } from '../../services/auth-rest.service';

@Component({
  selector: 'app-forgot-password',
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './forgot-password.html',
  styleUrl: './forgot-password.css',
})
export class ForgotPassword {
  email = '';
  mensaje = '';
  error = '';
  enviando = false;

  constructor(private readonly authService: AuthRestService) {}

  enviarInstrucciones(): void {
    const correoNormalizado = this.email.trim().toLowerCase();
    if (!correoNormalizado) {
      this.error = 'Debes ingresar un correo electrónico.';
      this.mensaje = '';
      return;
    }

    this.enviando = true;
    this.error = '';
    this.mensaje = '';

    this.authService.forgotPassword(correoNormalizado).subscribe({
      next: (resp) => {
        // El backend siempre responde 200 — el mensaje es genérico para no
        // revelar si el correo está registrado.
        this.mensaje = resp.mensaje || 'Si el correo está registrado recibirás un email con instrucciones.';
        this.email = '';
        this.enviando = false;
      },
      error: () => {
        // Aún así mostramos un mensaje genérico — no exponemos detalles del servidor.
        this.mensaje = 'Si el correo está registrado recibirás un email con instrucciones.';
        this.enviando = false;
      },
    });
  }
}
