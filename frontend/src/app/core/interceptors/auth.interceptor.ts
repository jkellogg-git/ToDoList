import { HttpInterceptorFn } from '@angular/common/http';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  // For OAuth2, we typically don't need to manually add tokens
  // as credentials are sent automatically when using 'withCredentials'
  const authReq = req.clone({
    withCredentials: true
  });

  return next(authReq);
};
