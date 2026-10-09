import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

export const authGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (authService.isAuthenticated()) {
    return true;
  }

  return router.parseUrl('/login');
};

export const roleGuard: CanActivateFn = (route) => {
  const authService = inject(AuthService);
  const router = inject(Router);
  const user = authService.currentUser();
  const requiredRole = route.data['role'] as string | undefined;

  if (!user) {
    return router.parseUrl('/login');
  }
  if (requiredRole && user.role !== requiredRole) {
    return router.parseUrl(dashboardPathFor(user.role));
  }
  return true;
};

export function dashboardPathFor(role: string): string {
  if (role === 'ELEVEUR') return '/dashboard/eleveur';
  if (role === 'VETERINAIRE') return '/dashboard/veterinaire';
  return '/admin';
}

export const localisationGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  const router = inject(Router);
  const user = authService.currentUser();

  if (!user) {
    return router.parseUrl('/login');
  }
  if (user.role === 'VETERINAIRE') {
    return router.parseUrl('/dashboard/veterinaire');
  }
  return true;
};
