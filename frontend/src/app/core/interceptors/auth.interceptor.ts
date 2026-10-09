import { HttpInterceptorFn } from '@angular/common/http';

// withCredentials: true = "envoie/accepte le cookie de session" même si Angular (4200)
// et Spring Boot (8085) sont sur des ports différents.
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  return next(req.clone({ withCredentials: true }));
};
