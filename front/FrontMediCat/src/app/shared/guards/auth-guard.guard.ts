import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthRestService } from '../../user/services/auth-rest.service';

export const authGuard: CanActivateFn = () => {
  const auth = inject(AuthRestService);
  const router = inject(Router);

  if (auth.getSesion()) {
    return true;
  }

  router.navigate(['/inicio/login']);
  return false;
};