import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';

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

  enviarInstrucciones(): void {
    const correoNormalizado = this.email.trim().toLowerCase();

    if (!correoNormalizado) {
      this.error = 'Debes ingresar un correo electrónico.';
      this.mensaje = '';
      return;
    }

    this.mensaje = `Si el correo ${correoNormalizado} existe, te enviaremos instrucciones para recuperar tu contraseña.`;
    this.error = '';
    this.email = '';
  }
}