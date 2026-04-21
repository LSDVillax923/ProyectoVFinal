import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthRestService, SesionActiva } from '../../services/auth-rest.service';
import { ClienteRestService } from '../../../cliente/services/cliente.service';
import { VeterinarioRestService } from '../../../veterinario/services/veterinario-rest.service';
import { AdminRestService } from '../../../admin/services/admin-rest.service';
import { Cliente, Veterinario, Admin } from '../../../shared/api/backend-contracts';

@Component({
  selector: 'app-perfil-usuario',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './perfil-usuario.html',
  styleUrls: ['./perfil-usuario.css']
})
export class PerfilUsuarioComponent implements OnInit {
  
  sesion: SesionActiva | null = null;
  perfilForm: FormGroup;
  loading = false;
  error: string | null = null;
  success: string | null = null;
  
  usuarioActual: Cliente | Veterinario | Admin | null = null;

  constructor(
    private fb: FormBuilder,
    private authService: AuthRestService,
    private clienteService: ClienteRestService,
    private veterinarioService: VeterinarioRestService,
    private adminService: AdminRestService,
    private router: Router
  ) {
    this.sesion = this.authService.getSesion();
    
    this.perfilForm = this.fb.group({
      nombre: ['', Validators.required],
      correo: ['', [Validators.required, Validators.email]],
      contrasenia: ['', Validators.minLength(6)],
      confirmarContrasenia: ['']
    }, { validator: this.passwordsMatch });
  }

  ngOnInit(): void {
    if (!this.sesion) {
      this.router.navigate(['/inicio/login']);
      return;
    }
    this.cargarPerfil();
  }

  cargarPerfil(): void {
    if (!this.sesion) return;
    
    this.loading = true;
    
    switch (this.sesion.rol) {
      case 'CLIENTE':
        this.clienteService.findById(this.sesion.id).subscribe({
          next: (cliente) => {
            this.usuarioActual = cliente;
            this.perfilForm.patchValue({
              nombre: cliente.nombre,
              correo: cliente.correo
            });
            this.loading = false;
          },
          error: (err) => {
            this.error = 'Error al cargar perfil';
            this.loading = false;
          }
        });
        break;
        
      case 'VETERINARIO':
        this.veterinarioService.findById(this.sesion.id).subscribe({
          next: (vet) => {
            this.usuarioActual = vet;
            this.perfilForm.patchValue({
              nombre: vet.nombre,
              correo: vet.correo
            });
            this.loading = false;
          },
          error: (err) => {
            this.error = 'Error al cargar perfil';
            this.loading = false;
          }
        });
        break;
        
      case 'ADMIN':
        this.adminService.findById(this.sesion.id).subscribe({
          next: (admin) => {
            this.usuarioActual = admin;
            this.perfilForm.patchValue({
              nombre: admin.nombre,
              correo: admin.correo
            });
            this.loading = false;
          },
          error: (err) => {
            this.error = 'Error al cargar perfil';
            this.loading = false;
          }
        });
        break;
    }
  }

  passwordsMatch(group: FormGroup): { [key: string]: boolean } | null {
    const pass = group.get('contrasenia')?.value;
    const confirm = group.get('confirmarContrasenia')?.value;
    return pass === confirm ? null : { mismatch: true };
  }

  onSubmit(): void {
    if (this.perfilForm.invalid || !this.sesion || !this.usuarioActual) {
      this.perfilForm.markAllAsTouched();
      return;
    }

    this.loading = true;
    this.error = null;
    this.success = null;

    const formValue = this.perfilForm.value;
    const datosActualizados: any = { ...this.usuarioActual };
    
    datosActualizados.nombre = formValue.nombre;
    datosActualizados.correo = formValue.correo;
    
    if (formValue.contrasenia) {
      datosActualizados.contrasenia = formValue.contrasenia;
    }

    switch (this.sesion.rol) {
      case 'CLIENTE':
        this.clienteService.update(this.sesion.id, datosActualizados).subscribe({
          next: () => {
            this.success = 'Perfil actualizado correctamente';
            this.loading = false;
          },
          error: (err) => {
            this.error = err.error?.message || 'Error al actualizar';
            this.loading = false;
          }
        });
        break;
        
      case 'VETERINARIO':
        this.veterinarioService.update(this.sesion.id, datosActualizados).subscribe({
          next: () => {
            this.success = 'Perfil actualizado correctamente';
            this.loading = false;
          },
          error: (err) => {
            this.error = err.error?.message || 'Error al actualizar';
            this.loading = false;
          }
        });
        break;
        
      case 'ADMIN':
        this.adminService.update(this.sesion.id, datosActualizados).subscribe({
          next: () => {
            this.success = 'Perfil actualizado correctamente';
            this.loading = false;
          },
          error: (err) => {
            this.error = err.error?.message || 'Error al actualizar';
            this.loading = false;
          }
        });
        break;
    }
  }

  isInvalid(controlName: string): boolean {
    const control = this.perfilForm.get(controlName);
    return !!(control && control.invalid && control.touched);
  }
}