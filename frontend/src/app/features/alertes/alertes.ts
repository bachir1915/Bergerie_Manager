import { Component, inject, signal, computed } from '@angular/core';
import { DatePipe } from '@angular/common';
import { ConsultationService } from '../../core/services/consultation.service';
import { AlerteDto } from '../../core/models/dashboard.model';

@Component({
  selector: 'app-alertes',
  imports: [DatePipe],
  template: `
    <div class="page-header">
      <div>
        <h1 class="page-title">Alertes & Notifications</h1>
        <p class="page-subtitle">{{ alertes().length }} alerte(s) au total · {{ nonLues() }} non lue(s)</p>
      </div>
    </div>

    @if (loading()) {
      <div class="card-dark">Chargement des alertes...</div>
    } @else {
      <div class="card-dark">
        <div class="card-dark-header" style="display:flex; justify-content:space-between; align-items:center; flex-wrap:wrap; gap:1rem;">
          <div style="display:flex; align-items:center; gap:0.55rem;">
            <i class="bi bi-bell-fill text-accent"></i>
            <span>Historique des alertes</span>
            <span class="badge" style="background:rgba(46,107,69,0.15); color:#2E6B45; font-size:0.75rem; border-radius:999px; padding:0.2rem 0.6rem;">
              {{ alertesFiltrees().length }} affichée(s)
            </span>
          </div>

          <!-- Filtres rapides -->
          <div style="display:flex; gap:0.4rem;">
            <button
              class="filtre-btn"
              style="padding:0.35rem 0.75rem; font-size:0.78rem; border-radius:8px; border:1px solid var(--border); cursor:pointer;"
              [style.background]="filtre() === 'TOUTES' ? '#2E6B45' : 'var(--surface)'"
              [style.color]="filtre() === 'TOUTES' ? '#FFF' : 'var(--ink-soft)'"
              (click)="filtre.set('TOUTES')">Toutes</button>
            <button
              class="filtre-btn"
              style="padding:0.35rem 0.75rem; font-size:0.78rem; border-radius:8px; border:1px solid var(--border); cursor:pointer;"
              [style.background]="filtre() === 'NON_LUES' ? '#E11D48' : 'var(--surface)'"
              [style.color]="filtre() === 'NON_LUES' ? '#FFF' : 'var(--ink-soft)'"
              (click)="filtre.set('NON_LUES')">Non lues ({{ nonLues() }})</button>
            <button
              class="filtre-btn"
              style="padding:0.35rem 0.75rem; font-size:0.78rem; border-radius:8px; border:1px solid var(--border); cursor:pointer;"
              [style.background]="filtre() === 'URGENTES' ? '#A8502D' : 'var(--surface)'"
              [style.color]="filtre() === 'URGENTES' ? '#FFF' : 'var(--ink-soft)'"
              (click)="filtre.set('URGENTES')">Urgences & Critiques</button>
          </div>
        </div>

        <div class="table-responsive">
          <table class="table-dark-custom">
            <thead>
              <tr><th>Date & Heure</th><th>Animal concerné</th><th>Type</th><th>Niveau</th><th>Message & Diagnostic</th><th>Statut</th></tr>
            </thead>
            <tbody>
              @if (!alertesFiltrees().length) {
                <tr>
                  <td colspan="6" style="text-align:center; padding:2rem; color:var(--text-muted)">
                    <i class="bi bi-shield-check" style="font-size:1.8rem; display:block; margin-bottom:0.5rem; color:#2E6B45;"></i>
                    Aucune alerte pour ce filtre.
                  </td>
                </tr>
              }
              @for (a of alertesFiltrees(); track a.id) {
                <tr [style.background]="a.statutAlerte === 'NON_LUE' ? 'rgba(225, 29, 72, 0.04)' : 'transparent'">
                  <td>{{ a.dateHeure | date: 'dd/MM/yyyy HH:mm' }}</td>
                  <td><strong>{{ a.animalNom || 'Troupeau / Général' }}</strong></td>
                  <td><span class="tag" style="background:var(--surface-2); padding:0.2rem 0.5rem; border-radius:6px; font-size:0.8rem;">{{ a.type }}</span></td>
                  <td><span class="badge-sante" [class]="classeNiveau(a.niveau)">{{ a.niveau }}</span></td>
                  <td>{{ a.message }}</td>
                  <td>
                    <span [style.color]="a.statutAlerte === 'NON_LUE' ? '#E11D48' : 'var(--ink-faint)'" [style.fontWeight]="a.statutAlerte === 'NON_LUE' ? '700' : 'normal'">
                      <i class="bi" [class.bi-envelope-fill]="a.statutAlerte === 'NON_LUE'" [class.bi-envelope-open]="a.statutAlerte !== 'NON_LUE'"></i>
                      {{ a.statutAlerte === 'NON_LUE' ? 'Non lue' : 'Lue' }}
                    </span>
                  </td>
                </tr>
              }
            </tbody>
          </table>
        </div>
      </div>
    }
  `
})
export class Alertes {
  private service = inject(ConsultationService);
  readonly alertes = signal<AlerteDto[]>([]);
  readonly loading = signal(true);
  readonly erreur = signal<string | null>(null);
  readonly filtre = signal<'TOUTES' | 'NON_LUES' | 'URGENTES'>('TOUTES');

  readonly alertesFiltrees = computed(() => {
    const f = this.filtre();
    if (f === 'NON_LUES') {
      return this.alertes().filter((a) => a.statutAlerte === 'NON_LUE');
    }
    if (f === 'URGENTES') {
      return this.alertes().filter((a) => a.niveau === 'URGENCE' || a.niveau === 'CRITIQUE');
    }
    return this.alertes();
  });

  constructor() {
    this.service.alertes().subscribe({
      next: (l) => { this.alertes.set(l); this.loading.set(false); },
      error: () => {
        this.loading.set(false);
        this.erreur.set('Impossible de charger les alertes. Vérifiez que le backend est démarré.');
      }
    });
  }

  nonLues(): number {
    return this.alertes().filter((a) => a.statutAlerte === 'NON_LUE').length;
  }

  classeNiveau(n: string): string {
    return { INFO: 'sante-bon', WARNING: 'sante-observation', CRITIQUE: 'sante-malade', URGENCE: 'sante-critique' }[n] ?? 'sante-bon';
  }
}
