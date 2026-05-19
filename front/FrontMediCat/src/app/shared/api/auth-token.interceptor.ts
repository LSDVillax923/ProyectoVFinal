import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthRestService } from '../../user/services/auth-rest.service';

export const authTokenInterceptor: HttpInterceptorFn = (req, next) => {
  const auth = inject(AuthRestService);
  const token = auth.getToken();

  if (!token) {
    return next(req);
  }

  const authReq = req.clone({
    setHeaders: {
      Authorization: `Bearer ${token}`
    }
  });

  return next(authReq);
};