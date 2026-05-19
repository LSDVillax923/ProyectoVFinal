import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthRestService } from '../../services/auth-rest.service';
import { API_BASE_URL } from '../../../shared/api/rest-endpoints';


@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './login.html',
  styleUrls: ['./login.css']
})
export class LoginComponent {
  
  loginForm: FormGroup;
  loading = false;
  error: string | null = null;
  tipoUsuario: 'CLIENTE' | 'VETERINARIO' | 'ADMIN' = 'CLIENTE';
  mostrarContrasenia = false;

  constructor(
    private fb: FormBuilder,
    private authService: AuthRestService,
    private router: Router
  ) {
    this.loginForm = this.fb.group({
      correo: ['', [Validators.required]],
      contrasenia: ['', Validators.required]
    });
  }

  /**
   * Para clientes el campo "correo" acepta también la cédula. Para
   * veterinarios/admins se exige correo electrónico válido.
   */
  private validarIdentificador(valor: string): boolean {
    if (!valor) return false;
    if (this.tipoUsuario === 'CLIENTE') return true;
    return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(valor);
  }

  onSubmit(): void {
    const identificador = (this.loginForm.value.correo ?? '').toString().trim();
    if (this.loginForm.invalid || !this.validarIdentificador(identificador)) {
      this.loginForm.markAllAsTouched();
      this.error = this.tipoUsuario === 'CLIENTE'
        ? 'Ingresa tu correo o cédula y tu contraseña.'
        : 'Ingresa un correo válido y tu contraseña.';
      return;
    }

    this.loading = true;
    this.error = null;

    const credentials = { ...this.loginForm.value, correo: identificador };

    this.authService.login(credentials, this.tipoUsuario).subscribe({
      next: (sesion) => {
        this.loading = false;
        switch (sesion.rol) {
          case 'ADMIN':
            this.router.navigate(['/dashboard']);
            break;
          case 'VETERINARIO':
            this.router.navigate(['/mascotas']);
            break;
          case 'CLIENTE':
            this.router.navigate(['/mis-mascotas']);
            break;
          default:
            this.router.navigate(['/inicio']);
        }
      },
       error: (err: HttpErrorResponse) => {
        const backendMessage = err?.error?.message;
         const apiHost = API_BASE_URL.replace('/api', '');

        if (err.status === 0) {
          this.error = `No se pudo conectar con el servidor en ${apiHost}. Verifica que el backend esté ejecutándose y que el puerto sea correcto.`;
        } else if (backendMessage && backendMessage.toLowerCase().includes('desactivad')) {
          this.error = backendMessage;
        } else {
          this.error = 'Credenciales inválidas. Intenta de nuevo.';
        }
        console.error(err);
        this.loading = false;
      }
    });
  }

  isInvalid(controlName: string): boolean {
    const control = this.loginForm.get(controlName);
    return !!(control && control.invalid && control.touched);
  }

  setTipoUsuario(tipo: 'CLIENTE' | 'VETERINARIO' | 'ADMIN'): void {
    this.tipoUsuario = tipo;
  }
}
