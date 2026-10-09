import { Component, inject } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { dashboardPathFor } from '../../core/guards/auth.guard';

@Component({
  selector: 'app-root-redirect',
  template: ''
})
export class RootRedirect {
  constructor() {
    const router = inject(Router);
    const authService = inject(AuthService);
    const role = authService.currentUser()?.role ?? 'ADMIN';
    router.navigateByUrl(dashboardPathFor(role));
  }
}
