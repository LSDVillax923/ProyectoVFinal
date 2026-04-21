import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, Router, UrlTree } from '@angular/router';
import { Observable } from 'rxjs';
import { AuthRestService } from '../../user/services/auth-rest.service';

@Injectable({
  providedIn: 'root'
})
export class RoleGuard implements CanActivate {
  
  constructor(
    private authService: AuthRestService,
    private router: Router
  ) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {
    const expectedRoles: string[] = route.data['roles'] || [];
    const sesion = this.authService.getSesion();
    
    if (!sesion) {
      return this.router.createUrlTree(['/inicio/login']);
    }
    
    if (expectedRoles.length === 0 || expectedRoles.includes(sesion.rol)) {
      return true;
    }
    
    // Redirigir según el rol
    switch (sesion.rol) {
      case 'ADMIN':
        return this.router.createUrlTree(['/dashboard']);
      case 'VETERINARIO':
        return this.router.createUrlTree(['/veterinario/dashboard']);
      case 'CLIENTE':
        return this.router.createUrlTree(['/cliente/mis-mascotas']);
      default:
        return this.router.createUrlTree(['/inicio']);
    }
  }
}