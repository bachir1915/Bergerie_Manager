import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { AuthService } from '../services/auth.service';

// withCredentials: true = "envoie/accepte le cookie de session" même si Angular (4200)
// et Spring Boot (8085) sont sur des ports différents.
// Une réponse 401 hors connexion signifie que la session a expiré : retour à l'écran de connexion.
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const auth = inject(AuthService);
  const router = inject(Router);
  return next(req.clone({ withCredentials: true })).pipe(
    catchError((err: HttpErrorResponse) => {
      if (err.status === 401 && !req.url.includes('/auth/') && auth.isAuthenticated()) {
        auth.expireSession();
        router.navigate(['/login']);
      }
      return throwError(() => err);
    })
  );
};
