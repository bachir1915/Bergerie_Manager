import { Component, inject, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { ActivatedRoute, Router } from '@angular/router';
import { ActeForm, DashboardService } from '../../core/services/dashboard.service';
import { AuthService } from '../../core/services/auth.service';
import { EspaceVeterinaireResponse } from '../../core/models/espace-veterinaire.model';

const TYPES_SOIN = ['VACCINATION', 'TRAITEMENT', 'ANALYSE', 'CONSULTATION'];

@Component({
  selector: 'app-dashboard-veterinaire',
  imports: [DatePipe, ReactiveFormsModule],
  templateUrl: './dashboard-veterinaire.html',
  styleUrl: './dashboard-veterinaire.scss'
})
export class DashboardVeterinaire {
  private dashboardService = inject(DashboardService);
  private fb = inject(FormBuilder);
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  readonly authService = inject(AuthService);

  readonly typesSoin = TYPES_SOIN;
  readonly data = signal<EspaceVeterinaireResponse | null>(null);
  readonly loading = signal(true);
  readonly erreur = signal<string | null>(null);
  readonly acteFormOuvert = signal(false);
  readonly acteErreur = signal<string | null>(null);

  readonly acteForm = this.fb.group({
    animalId: [null as number | null, Validators.required],
    typeSoin: ['CONSULTATION', Validators.required],
    description: [''],
    medicament: [''],
    dose: [''],
    dateSoin: [new Date().toISOString().slice(0, 10), Validators.required],
    prochainRappel: [null as string | null]
  });

  constructor() {
    this.charger();
    this.route.queryParams.subscribe((p) => {
      if (p['action'] === 'nouveau-acte') {
        this.ouvrirActeForm();
      }
    });
  }

  private charger(): void {
    this.loading.set(true);
    this.dashboardService.getEspaceVeterinaire().subscribe({
      next: (r) => { this.data.set(r); this.loading.set(false); this.erreur.set(null); },
      error: (err: HttpErrorResponse) => {
        this.loading.set(false);
        if (err.status === 401 || err.status === 403) {
          // Session expiree (ex. redemarrage backend H2) -- deconnexion auto
          this.authService.logout();
        } else if (err.status === 400) {
          this.erreur.set("Aucune bergerie selectionnee. Reconnectez-vous avec le code d'une bergerie.");
        } else {
          this.erreur.set("Impossible de charger l'espace veterinaire.");
        }
      }
    });
  }

  changerBergerie(code: string): void {
    const c = code.trim().toUpperCase();
    if (!c) return;
    this.loading.set(true);
    this.dashboardService.choisirBergerie(c).subscribe({
      next: (r) => { this.data.set(r); this.loading.set(false); this.erreur.set(null); },
      error: (err: HttpErrorResponse) => {
        this.loading.set(false);
        this.erreur.set(err.error?.detail || 'Code de bergerie invalide ou inconnu.');
      }
    });
  }

  ouvrirActeForm(): void {
    this.acteErreur.set(null);
    this.acteForm.reset({
      animalId: null, typeSoin: 'CONSULTATION', description: '', medicament: '', dose: '',
      dateSoin: new Date().toISOString().slice(0, 10), prochainRappel: null
    });
    this.acteFormOuvert.set(true);
  }

  enregistrerActe(): void {
    if (this.acteForm.invalid) { this.acteForm.markAllAsTouched(); return; }
    const v = this.acteForm.getRawValue();
    const payload: ActeForm = {
      animalId: v.animalId!, typeSoin: v.typeSoin!,
      description: v.description || null, medicament: v.medicament || null, dose: v.dose || null,
      dateSoin: v.dateSoin!, prochainRappel: v.prochainRappel || null
    };
    this.dashboardService.ajouterActe(payload).subscribe({
      next: (r) => { this.data.set(r); this.acteFormOuvert.set(false); },
      error: (err: HttpErrorResponse) => this.acteErreur.set(err.error?.detail || 'Enregistrement impossible.')
    });
  }

  badgeSante(s: string): string {
    return { BON: 'badge badge-ok', MALADE: 'badge badge-warn', CRITIQUE: 'badge badge-crit' }[s] ?? 'badge';
  }
}