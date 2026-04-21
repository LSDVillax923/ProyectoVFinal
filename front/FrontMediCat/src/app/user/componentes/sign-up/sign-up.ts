import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthRestService } from '../../services/auth-rest.service';

interface RegistroForm {
  nombre: string;
  apellido: string;
  correo: string;
  celular: string;
  contrasenia: string;
  confirmar: string;
  terminos: boolean;
}

@Component({
  selector: 'app-sign-up',
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './sign-up.html',
  styleUrl: './sign-up.css',
})

export class SignUp {
  form: RegistroForm = {
    nombre: '',
    apellido: '',
    correo: '',
    celular: '',
    contrasenia: '',
    confirmar: '',
    terminos: false,
  };

  mensaje = '';
  error = '';
  cargando = false;

  constructor(
    private readonly router: Router,
    private readonly authRestService: AuthRestService,
  ) {}

  registrar(): void {
    this.mensaje = '';
    this.error = '';

    if (this.form.contrasenia !== this.form.confirmar) {
      this.error = 'Las contraseñas no coinciden.';
      return;
    }

    this.cargando = true;

    this.authRestService
      .register({
        nombre: this.form.nombre.trim(),
        apellido: this.form.apellido.trim(),
        correo: this.form.correo.trim().toLowerCase(),
        celular: this.form.celular.trim(),
        contrasenia: this.form.contrasenia,
      })
      .subscribe({
        next: () => {
          this.mensaje = 'Cuenta creada correctamente. Ahora puedes iniciar sesión.';
          this.cargando = false;
          setTimeout(() => {
            this.router.navigate(['/inicio/login']);
          }, 1000);
        },
        error: () => {
          this.error = 'No fue posible crear la cuenta. Verifica tus datos e intenta de nuevo.';
          this.cargando = false;
        },
      });
  }
}