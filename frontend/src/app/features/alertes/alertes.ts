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
        <h1 class="page-title">Consulter alertes</h1>
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
              (click)="choisirFiltre('TOUTES')">Toutes</button>
            <button
              class="filtre-btn"
              style="padding:0.35rem 0.75rem; font-size:0.78rem; border-radius:8px; border:1px solid var(--border); cursor:pointer;"
              [style.background]="filtre() === 'NON_LUES' ? '#E11D48' : 'var(--surface)'"
              [style.color]="filtre() === 'NON_LUES' ? '#FFF' : 'var(--ink-soft)'"
              (click)="choisirFiltre('NON_LUES')">Non lues ({{ nonLues() }})</button>
            <button
              class="filtre-btn"
              style="padding:0.35rem 0.75rem; font-size:0.78rem; border-radius:8px; border:1px solid var(--border); cursor:pointer;"
              [style.background]="filtre() === 'URGENTES' ? '#A8502D' : 'var(--surface)'"
              [style.color]="filtre() === 'URGENTES' ? '#FFF' : 'var(--ink-soft)'"
              (click)="choisirFiltre('URGENTES')">Urgences & Critiques</button>
          </div>
        </div>

        <div class="table-responsive">
          <table class="table-dark-custom">
            <thead>
              <tr><th>Date & Heure</th><th>Animal concerné</th><th>Type</th><th>Niveau</th><th>Message & Diagnostic</th><th>Statut</th><th>Action</th></tr>
            </thead>
            <tbody>
              @if (!alertesFiltrees().length) {
                <tr>
                  <td colspan="7" style="text-align:center; padding:2rem; color:var(--text-muted)">
                    <i class="bi bi-shield-check" style="font-size:1.8rem; display:block; margin-bottom:0.5rem; color:#2E6B45;"></i>
                    Aucune alerte pour ce filtre.
                  </td>
                </tr>
              }
              @for (a of alertesPage(); track a.id) {
                <tr [style.background]="a.statutAlerte === 'NOUVELLE' ? 'rgba(225, 29, 72, 0.04)' : 'transparent'">
                  <td>{{ a.dateHeure | date: 'dd/MM/yyyy HH:mm' }}</td>
                  <td><strong>{{ a.animalNom || 'Troupeau / Général' }}</strong></td>
                  <td><span class="tag" style="background:var(--surface-2); padding:0.2rem 0.5rem; border-radius:6px; font-size:0.8rem;">{{ a.type }}</span></td>
                  <td><span class="badge-sante" [class]="classeNiveau(a.niveau)">{{ a.niveau }}</span></td>
                  <td>{{ a.message }}</td>
                  <td>
                    <span [style.color]="a.statutAlerte === 'NOUVELLE' ? '#E11D48' : a.statutAlerte === 'TRAITEE' ? '#2E6B45' : 'var(--ink-faint)'" [style.fontWeight]="a.statutAlerte === 'NOUVELLE' ? '700' : 'normal'">
                      <i class="bi" [class.bi-envelope-fill]="a.statutAlerte === 'NOUVELLE'" [class.bi-check-circle-fill]="a.statutAlerte === 'TRAITEE'" [class.bi-envelope-open]="a.statutAlerte !== 'NOUVELLE' && a.statutAlerte !== 'TRAITEE'"></i>
                      {{ libelleStatut(a.statutAlerte) }}
                    </span>
                  </td>
                  <td>
                    @if (a.statutAlerte !== 'TRAITEE') {
                      <button class="btn-mini btn-ok" style="padding:0.3rem 0.7rem; font-size:0.78rem; border-radius:8px; border:1px solid #2E6B45; background:#2E6B45; color:#fff; cursor:pointer;"
                              [disabled]="enCours() === a.id" (click)="traiter(a)">
                        <i class="bi bi-check2-circle"></i> Marquer comme traitée
                      </button>
                    } @else {
                      <span style="color:var(--ink-faint); font-size:0.8rem;">Traitée</span>
                    }
                  </td>
                </tr>
              }
            </tbody>
          </table>
        </div>
    @if (nbPages() > 1) {
    <div class="pager" style="display:flex; align-items:center; justify-content:flex-end; gap:0.75rem; padding:0.75rem 1.25rem; font-size:0.82rem; color:var(--ink-soft);">
      <span>{{ debutPage() }}–{{ finPage() }} sur {{ alertesFiltrees().length }}</span>
      <button type="button" (click)="pagePrecedente()" [disabled]="pageCourante() === 0" style="padding:0.3rem 0.7rem; border-radius:8px; border:1px solid var(--border); background:var(--surface); cursor:pointer;"><i class="bi bi-chevron-left"></i> Précédent</button>
      <button type="button" (click)="pageSuivante()" [disabled]="pageCourante() >= nbPages() - 1" style="padding:0.3rem 0.7rem; border-radius:8px; border:1px solid var(--border); background:var(--surface); cursor:pointer;">Suivant <i class="bi bi-chevron-right"></i></button>
    </div>
    }
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
  readonly enCours = signal<number | null>(null);
  /** Affichage par pages de 5 éléments. */
  readonly taillePage = 5;
  readonly page = signal(0);
  readonly nbPages = computed(() => Math.max(1, Math.ceil(this.alertesFiltrees().length / this.taillePage)));
  readonly pageCourante = computed(() => Math.min(this.page(), this.nbPages() - 1));
  readonly debutPage = computed(() => (this.alertesFiltrees().length) === 0 ? 0 : this.pageCourante() * this.taillePage + 1);
  readonly finPage = computed(() => Math.min((this.alertesFiltrees().length), (this.pageCourante() + 1) * this.taillePage));
  pagePrecedente(): void { this.page.set(Math.max(0, this.pageCourante() - 1)); }
  pageSuivante(): void { this.page.set(Math.min(this.nbPages() - 1, this.pageCourante() + 1)); }
  readonly alertesPage = computed(() => this.alertesFiltrees().slice(this.pageCourante() * this.taillePage, (this.pageCourante() + 1) * this.taillePage));
  choisirFiltre(f: 'TOUTES' | 'NON_LUES' | 'URGENTES'): void { this.filtre.set(f); this.page.set(0); }


  readonly alertesFiltrees = computed(() => {
    const f = this.filtre();
    if (f === 'NON_LUES') {
      return this.alertes().filter((a) => a.statutAlerte === 'NOUVELLE');
    }
    if (f === 'URGENTES') {
      return this.alertes().filter((a) => a.niveau === 'ELEVE' || a.niveau === 'CRITIQUE');
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

  /** Cas d'utilisation « Marquer l'alerte comme traitée ». */
  traiter(a: AlerteDto): void {
    this.enCours.set(a.id);
    this.service.traiterAlerte(a.id).subscribe({
      next: (maj) => {
        this.alertes.update((l) => l.map((x) => (x.id === maj.id ? maj : x)));
        this.enCours.set(null);
      },
      error: () => {
        this.enCours.set(null);
        this.erreur.set("Impossible de marquer l'alerte comme traitée.");
      }
    });
  }

  libelleStatut(s: string): string {
    return ({ NOUVELLE: 'Non lue', EN_COURS: 'En cours', TRAITEE: 'Traitée', IGNOREE: 'Ignorée' } as Record<string, string>)[s] ?? s;
  }

  nonLues(): number {
    return this.alertes().filter((a) => a.statutAlerte === 'NOUVELLE').length;
  }

  classeNiveau(n: string): string {
    return ({ NORMAL: 'sante-bon', MODERE: 'sante-observation', ELEVE: 'sante-malade', CRITIQUE: 'sante-critique' } as Record<string, string>)[n] ?? 'sante-bon';
  }
}
