import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthRestService } from '../../services/auth-rest.service';


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
      correo: ['', [Validators.required, Validators.email]],
      contrasenia: ['', Validators.required]
    });
  }

  onSubmit(): void {
    if (this.loginForm.invalid) {
      this.loginForm.markAllAsTouched();
      return;
    }

    this.loading = true;
    this.error = null;

    const credentials = this.loginForm.value;

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
      error: (err: Error) => {
        this.error = 'Credenciales inválidas. Intenta de nuevo.';
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
