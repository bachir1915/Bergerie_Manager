import { Component, inject, signal } from '@angular/core';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { RouterLink, Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { EleveurCree, InscriptionService } from '../../core/services/inscription.service';

type Profil = 'ELEVEUR' | 'VETERINAIRE';

@Component({
  selector: 'app-inscription',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './inscription.html',
  styleUrl: './inscription.scss'
})
export class Inscription {
  private fb = inject(FormBuilder);
  private service = inject(InscriptionService);
  private router = inject(Router);

  readonly profil = signal<Profil>('ELEVEUR');
  readonly loading = signal(false);
  readonly error = signal<string | null>(null);
  readonly succes = signal(false);
  readonly eleveurCree = signal<EleveurCree | null>(null);
  fichier: File | null = null;

  readonly form = this.fb.group({
    nom: ['', Validators.required],
    prenom: ['', Validators.required],
    email: ['', [Validators.required, Validators.email]],
    telephone: ['', [Validators.required, Validators.pattern(/^\d{2}\s?\d{3}\s?\d{2}\s?\d{2}$/)]],
    specialite: ['']
  });

  choisir(p: Profil): void {
    this.profil.set(p);
    this.error.set(null);
  }

  onFichier(event: Event): void {
    const input = event.target as HTMLInputElement;
    this.fichier = input.files?.[0] ?? null;
  }

  submit(): void {
    this.error.set(null);
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const v = this.form.getRawValue();

    if (this.profil() === 'VETERINAIRE' && !this.fichier) {
      this.error.set('Le justificatif (carte professionnelle, attestation à l\'ordre, diplôme...) est obligatoire.');
      return;
    }

    this.loading.set(true);

    if (this.profil() === 'ELEVEUR') {
      this.service.inscrireEleveur({
        nom: v.nom!, prenom: v.prenom!, email: v.email!, telephone: v.telephone!
      }).subscribe({
        next: (e) => {
          this.loading.set(false);
          this.eleveurCree.set(e);
          this.succes.set(true);
        },
        error: (err: HttpErrorResponse) => this.echec(err)
      });
    } else {
      this.service.inscrireVeterinaire({
        nom: v.nom!, prenom: v.prenom!, email: v.email!,
        telephone: v.telephone!,
        specialite: v.specialite || undefined,
        justificatif: this.fichier!
      }).subscribe({
        next: () => {
          this.loading.set(false);
          this.succes.set(true);
        },
        error: (err: HttpErrorResponse) => this.echec(err)
      });
    }
  }

  private echec(err: HttpErrorResponse): void {
    this.loading.set(false);
    this.error.set(err.error?.detail || err.error?.message || 'Inscription impossible. Vérifiez vos informations.');
  }

  allerConnexion(): void {
    this.router.navigateByUrl('/login');
  }
}
