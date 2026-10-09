import { Component, inject, signal, computed } from '@angular/core';
import { ConsultationService } from '../../core/services/consultation.service';
import { VeterinairePublic } from '../../core/models/consultation.model';

@Component({
  selector: 'app-veterinaires',
  template: `
    <div class="page-header">
      <div>
        <h1 class="page-title">
          <i class="bi bi-hospital-fill text-accent me-2"></i>Vétérinaires
        </h1>
        <p class="page-subtitle">
          Praticiens <strong>validés par l'administration</strong> — Contactez-les directement pour les soins de votre cheptel
        </p>
      </div>
    </div>

    @if (loading()) {
      <div class="card-dark" style="padding:2rem; text-align:center; color:var(--ink-soft);">
        <div style="width:36px;height:36px;border:3px solid var(--border);border-top-color:var(--accent);border-radius:50%;animation:spin 0.8s linear infinite;margin:0 auto 1rem;"></div>
        Chargement des praticiens...
      </div>
    } @else {

      <!-- Barre de recherche + compteur -->
      <div class="card-dark" style="margin-bottom:1.5rem; padding:1rem 1.25rem;">
        <div style="display:flex; align-items:center; justify-content:space-between; flex-wrap:wrap; gap:1rem;">
          <div style="display:flex; align-items:center; gap:0.6rem;">
            <span style="background:var(--success-fill); color:var(--forest); padding:0.3rem 0.75rem; border-radius:20px; font-size:0.82rem; font-weight:700; border:1px solid rgba(46,107,69,0.2);">
              ✓ {{ vetsFiltres().length }} vétérinaire(s)
            </span>
            <span style="font-size:0.82rem; color:var(--ink-faint);">validés par l'administrateur</span>
          </div>
          <div style="position:relative;">
            <i class="bi bi-search" style="position:absolute; left:0.65rem; top:50%; transform:translateY(-50%); font-size:0.85rem; color:var(--ink-faint);"></i>
            <input
              type="text"
              placeholder="Rechercher par nom, spécialité..."
              [value]="recherche()"
              (input)="recherche.set($any($event.target).value)"
              style="padding:0.45rem 0.75rem 0.45rem 2rem; font-size:0.85rem; border-radius:8px; border:1px solid var(--border); background:var(--surface); color:var(--ink); width:240px; outline:none;"
            />
          </div>
        </div>
      </div>

      <!-- Grille de cartes vétérinaires -->
      @if (!vetsFiltres().length) {
        <div class="card-dark" style="padding:3rem; text-align:center; color:var(--ink-faint);">
          <i class="bi bi-person-x" style="font-size:2.5rem; display:block; margin-bottom:0.75rem; opacity:0.4;"></i>
          Aucun vétérinaire trouvé pour cette recherche.
        </div>
      } @else {
        <div style="display:grid; grid-template-columns: repeat(auto-fill, minmax(320px, 1fr)); gap:1.25rem;">
          @for (v of vetsFiltres(); track v.id) {
            <div style="
              background:var(--surface);
              border:1px solid var(--border);
              border-radius:14px;
              overflow:hidden;
              transition:transform 0.2s, box-shadow 0.2s;
              box-shadow:0 2px 12px rgba(0,0,0,0.12);
            "
            (mouseenter)="$any($event.target).closest('div[style]').style.transform='translateY(-3px)'; $any($event.target).closest('div[style]').style.boxShadow='0 8px 30px rgba(0,0,0,0.22)'"
            (mouseleave)="$any($event.target).closest('div[style]').style.transform=''; $any($event.target).closest('div[style]').style.boxShadow='0 2px 12px rgba(0,0,0,0.12)'"
            >
              <!-- En-tête de carte -->
              <div style="padding:1.2rem 1.25rem 1rem; display:flex; align-items:center; gap:1rem; border-bottom:1px solid var(--border); background:linear-gradient(135deg, rgba(31,77,52,0.08), transparent);">
                <!-- Avatar initiales -->
                <div style="
                  width:52px; height:52px; border-radius:50%;
                  background:linear-gradient(135deg, var(--forest), var(--forest-strong));
                  color:#FEFCF5; display:flex; align-items:center; justify-content:center;
                  font-weight:700; font-size:1.15rem; flex-shrink:0;
                  box-shadow:0 3px 10px rgba(31,77,52,0.28);
                ">
                  {{ (v.prenom || '?').charAt(0) }}{{ (v.nom || '').charAt(0) }}
                </div>
                <div style="flex:1; min-width:0;">
                  <div style="font-size:1.02rem; font-weight:700; color:var(--ink); font-family:var(--font-display, Georgia, serif);">
                    Dr. {{ v.prenom }} {{ v.nom }}
                  </div>
                  <!-- Badge validé -->
                  <div style="display:flex; align-items:center; gap:0.4rem; margin-top:0.25rem;">
                    <span style="
                      background:var(--success-fill); color:var(--success);
                      padding:0.2rem 0.6rem; border-radius:20px; font-size:0.75rem; font-weight:700;
                      border:1px solid rgba(46,107,69,0.2);
                    ">
                      <i class="bi bi-patch-check-fill me-1"></i>Validé
                    </span>
                  </div>
                </div>
              </div>

              <!-- Corps de la carte -->
              <div style="padding:1.15rem 1.25rem; display:flex; flex-direction:column; gap:0.85rem;">

                <!-- Spécialité -->
                <div style="display:flex; align-items:flex-start; gap:0.75rem;">
                  <i class="bi bi-award-fill" style="color:var(--forest); font-size:1rem; margin-top:0.15rem; flex-shrink:0;"></i>
                  <div>
                    <div style="font-size:0.72rem; color:var(--ink-faint); text-transform:uppercase; letter-spacing:0.5px; font-weight:700; margin-bottom:0.25rem;">Spécialité</div>
                    <span style="
                      background:rgba(31,77,52,0.09); color:var(--forest);
                      padding:0.25rem 0.65rem; border-radius:6px; font-weight:600; font-size:0.88rem;
                      border:1px solid rgba(31,77,52,0.18); display:inline-block;
                    ">
                      {{ v.specialite || 'Généraliste / Ruminants & Camélidés' }}
                    </span>
                  </div>
                </div>

                <!-- Téléphone -->
                <div style="display:flex; align-items:center; gap:0.75rem;">
                  <i class="bi bi-telephone-fill" style="color:var(--forest); font-size:0.95rem; flex-shrink:0;"></i>
                  <div>
                    <div style="font-size:0.72rem; color:var(--ink-faint); text-transform:uppercase; letter-spacing:0.5px; font-weight:700; margin-bottom:0.15rem;">Téléphone</div>
                    @if (v.telephone) {
                      <a [href]="'tel:' + v.telephone" style="color:var(--forest); font-weight:700; font-size:0.92rem; text-decoration:none;">
                        {{ v.telephone }}
                      </a>
                    } @else {
                      <span style="color:var(--ink-faint); font-size:0.88rem;">Non renseigné</span>
                    }
                  </div>
                </div>

                <!-- Email -->
                <div style="display:flex; align-items:center; gap:0.75rem;">
                  <i class="bi bi-envelope-fill" style="color:var(--gold); font-size:0.95rem; flex-shrink:0;"></i>
                  <div>
                    <div style="font-size:0.72rem; color:var(--ink-faint); text-transform:uppercase; letter-spacing:0.5px; font-weight:700; margin-bottom:0.15rem;">Email professionnel</div>
                    @if (v.email) {
                      <a [href]="'mailto:' + v.email" style="color:var(--ink); font-weight:600; font-size:0.88rem; text-decoration:none; word-break:break-all;">
                        {{ v.email }}
                      </a>
                    } @else {
                      <span style="color:var(--ink-faint); font-size:0.88rem;">Non renseigné</span>
                    }
                  </div>
                </div>
              </div>
            </div>
          }
        </div>
      }
    }
  `
})
export class Veterinaires {
  private service = inject(ConsultationService);
  readonly vets = signal<VeterinairePublic[]>([]);
  readonly loading = signal(true);
  readonly recherche = signal('');

  readonly vetsFiltres = computed(() => {
    const q = this.recherche().trim().toLowerCase();
    if (!q) return this.vets();
    return this.vets().filter(
      (v) =>
        v.nom?.toLowerCase().includes(q) ||
        v.prenom?.toLowerCase().includes(q) ||
        v.specialite?.toLowerCase().includes(q) ||
        v.email?.toLowerCase().includes(q)
    );
  });

  constructor() {
    this.service.veterinaires().subscribe({
      next: (l) => { this.vets.set(l); this.loading.set(false); },
      error: () => this.loading.set(false)
    });
  }
}
