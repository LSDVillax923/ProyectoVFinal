import { Routes } from '@angular/router';

import { authGuard }                              from './shared/guards/auth-guard.guard';
import { adminGuard, veterinarioGuard, clienteGuard } from './shared/guards/role-guard.guard';

import { Inicio }             from './inicio/inicio/inicio';
import { LoginComponent as Login } from './user/componentes/login/login';
import { ForgotPassword }     from './user/componentes/forgot-password/forgot-password';
import { SignUp }             from './user/componentes/sign-up/sign-up';

import { Dashboard }          from './admin/componentes/dashboard/dashboard';

import { ListarClienteComponent as ListarCliente } from './cliente/componentes/listar-cliente/listar-cliente';
import { NuevoCliente }       from './cliente/componentes/nuevo-cliente/nuevo-cliente';
import { EditarClienteComponent as EditarCliente } from './cliente/componentes/editar-cliente/editar-cliente';
import { VerCliente }         from './cliente/componentes/ver-cliente/ver-cliente';

import { MisMascotas }        from './mascota/componentes/mis-mascotas/mis-mascotas';
import { ListarMascotas }     from './mascota/componentes/listar-mascotas/listar-mascotas';
import { NuevaMascota }       from './mascota/componentes/nueva-mascota/nueva-mascota';
import { EditarMascota }      from './mascota/componentes/editar-mascota/editar-mascota';
import { VerMascota }         from './mascota/componentes/ver-mascota/ver-mascota';

import { ListarVeterinarios } from './veterinario/componentes/listar-veterinarios/listar-veterinarios';
import { NuevoVeterinario }   from './veterinario/componentes/nuevo-veterinario/nuevo-veterinario';
import { EditarVeterinario }  from './veterinario/componentes/editar-veterinario/editar-veterinario';
import { VerVeterinario } from './veterinario/componentes/ver-veterinario/ver-veterinario';
import { PerfilVeterinario }  from './veterinario/componentes/perfil-veterinario/perfil-veterinario';

import { ListarDrogas }       from './droga/componentes/listar-drogas/listar-drogas';
import { NuevaDroga }         from './droga/componentes/nueva-droga/nueva-droga';
import { EditarDroga }        from './droga/componentes/editar-droga/editar-droga';

import { ListarTratamientos } from './tratamiento/componentes/listar-tratamientos/listar-tratamientos';
import { NuevoTratamiento }   from './tratamiento/componentes/nuevo-tratamiento/nuevo-tratamiento';
import { EditarTratamiento }  from './tratamiento/componentes/editar-tratamiento/editar-tratamiento';
import { VerTratamiento }     from './tratamiento/componentes/ver-tratamiento/ver-tratamiento';

import { ListarCitasComponent } from './cita/componentes/listar-citas/listar-citas';
import { NuevaCitaComponent } from './cita/componentes/nueva-cita/nueva-cita';
import { VerCitaComponent } from './cita/componentes/ver-cita/ver-cita';
import { EditarCitaComponent } from './cita/componentes/editar-cita/editar-cita';



export const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'inicio' },

  // ── Rutas públicas ───────────────────────────────────
  { path: 'inicio',                  component: Inicio },
  { path: 'inicio/login',            component: Login },
  { path: 'inicio/registro',         component: SignUp },
  { path: 'inicio/forgot-password',  component: ForgotPassword },

  // ── Perfiles propios (cada rol edita el suyo) ────────
  { path: 'perfil',                  component: EditarCliente,      canActivate: [authGuard, clienteGuard] },
  { path: 'perfil-admin',            component: EditarCliente,      canActivate: [authGuard, adminGuard] },
  { path: 'perfil-veterinario',      component: PerfilVeterinario,  canActivate: [authGuard, veterinarioGuard] },

  // ── Admin ────────────────────────────────────────────
  { path: 'dashboard',               component: Dashboard,          canActivate: [authGuard, adminGuard] },

  // Clientes — admin y veterinario (crear), admin (gestión completa)
  { path: 'clientes',                component: ListarCliente,      canActivate: [authGuard, veterinarioGuard] },
  { path: 'clientes/nuevo',          component: NuevoCliente,       canActivate: [authGuard, veterinarioGuard] },
  { path: 'clientes/:id',            component: VerCliente,         canActivate: [authGuard, adminGuard] },
  { path: 'clientes/:id/editar',     component: EditarCliente,      canActivate: [authGuard, adminGuard] },
  { path: 'clientes/:id/mismascotas',component: ListarMascotas,     canActivate: [authGuard, adminGuard] },

  // Veterinarios — solo admin
  { path: 'veterinarios',            component: ListarVeterinarios, canActivate: [authGuard, adminGuard] },
  { path: 'veterinarios/nuevo',      component: NuevoVeterinario,   canActivate: [authGuard, adminGuard] },
  { path: 'veterinarios/:id',        component: VerVeterinario,     canActivate: [authGuard, adminGuard] },
  { path: 'veterinarios/:id/editar', component: EditarVeterinario,  canActivate: [authGuard, adminGuard] },

  // ── Mascotas del cliente ─────────────────────────────
  { path: 'mis-mascotas',            component: MisMascotas,        canActivate: [authGuard, clienteGuard] },
  { path: 'mis-mascotas/nueva',      component: NuevaMascota,       canActivate: [authGuard, clienteGuard] },
  { path: 'mis-mascotas/:id',        component: VerMascota,         canActivate: [authGuard] },

  // ── Mascotas — admin y veterinario ───────────────────
  { path: 'mascotas',                component: ListarMascotas,     canActivate: [authGuard, veterinarioGuard] },
  { path: 'mascotas/nueva',          component: NuevaMascota,       canActivate: [authGuard, veterinarioGuard] },
  { path: 'mascotas/:id',            component: VerMascota,         canActivate: [authGuard] },
  { path: 'mascotas/:id/editar',     component: EditarMascota,      canActivate: [authGuard, veterinarioGuard] },

  // ── Drogas ───────────────────────────────────────────
  { path: 'drogas',                  component: ListarDrogas,       canActivate: [authGuard] },
  { path: 'drogas/nueva',            component: NuevaDroga,         canActivate: [authGuard, adminGuard] },
  { path: 'drogas/:id/editar',       component: EditarDroga,        canActivate: [authGuard, veterinarioGuard] },

  // ── Tratamientos ─────────────────────────────────────
  { path: 'tratamientos',            component: ListarTratamientos, canActivate: [authGuard] },
  { path: 'tratamientos/nuevo',      component: NuevoTratamiento,   canActivate: [authGuard, veterinarioGuard] },
  { path: 'tratamientos/:id',        component: VerTratamiento,     canActivate: [authGuard] },
  { path: 'tratamientos/:id/editar', component: EditarTratamiento,  canActivate: [authGuard, veterinarioGuard] },


    // Citas
  { path: 'citas', component: ListarCitasComponent },
  { path: 'citas/nueva', component: NuevaCitaComponent },
  { path: 'citas/:id', component: VerCitaComponent },
  { path: 'citas/:id/editar', component: EditarCitaComponent },

  

  { path: '**', redirectTo: 'inicio' },
];
