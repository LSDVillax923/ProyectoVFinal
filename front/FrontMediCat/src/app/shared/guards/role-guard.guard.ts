import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthRestService } from '../../user/services/auth-rest.service';

export const adminGuard: CanActivateFn = () => {
  const auth = inject(AuthRestService);
  const router = inject(Router);

  if (auth.hasRole('ADMIN')) {
    return true;
  }

  router.navigate(['/inicio']);
  return false;
};

export const veterinarioGuard: CanActivateFn = () => {
  const auth = inject(AuthRestService);
  const router = inject(Router);

  if (auth.hasRole('VETERINARIO') || auth.hasRole('ADMIN')) {
    return true;
  }

  router.navigate(['/inicio']);
  return false;
};

export const clienteGuard: CanActivateFn = () => {
  const auth = inject(AuthRestService);
  const router = inject(Router);

  if (auth.hasRole('CLIENTE')) {
    return true;
  }

  router.navigate(['/inicio']);
  return false;
};