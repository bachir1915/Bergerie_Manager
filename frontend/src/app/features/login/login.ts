import { Component, inject, signal } from '@angular/core';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { AuthService } from '../../core/services/auth.service';
import { dashboardPathFor } from '../../core/guards/auth.guard';

@Component({
  selector: 'app-login',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './login.html',
  styleUrl: './login.scss'
})
export class Login {
  private fb = inject(FormBuilder);
  private authService = inject(AuthService);
  private router = inject(Router);

  readonly loading = signal(false);
  readonly error = signal<string | null>(null);

  readonly form = this.fb.group({
    email: ['', [Validators.required, Validators.email]],
    code: ['', Validators.required]
  });


  submit(): void {
    this.error.set(null);
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.loading.set(true);
    const { email, code } = this.form.getRawValue();
    this.authService.login({ email: email!, code: code!.trim().toUpperCase() }).subscribe({
      next: (response) => {
        this.router.navigateByUrl(dashboardPathFor(response.role));
      },
      error: (err: HttpErrorResponse) => {
        this.loading.set(false);
        if (err.status === 0) {
          this.error.set('Le serveur backend Spring Boot n\'est pas démarré (port 8085).');
        } else if (err.status === 403) {
          this.error.set(err.error?.detail || 'Votre compte n\'est pas encore validé.');
        } else {
          this.error.set('Email ou code incorrect.');
        }
      }
    });
  }
}
