import { Component, inject, signal, computed, OnInit, OnDestroy } from '@angular/core';
import { DatePipe } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { AdminService } from '../../core/services/admin.service';
import { BergerieAdmin, EleveurAdmin, StatutValidation, VeterinaireAdmin } from '../../core/models/admin.model';

type FiltreStatut = 'TOUS' | StatutValidation;

@Component({
  selector: 'app-admin',
  imports: [DatePipe],
  templateUrl: './admin.html',
  styleUrl: './admin.scss'
})
export class Admin implements OnInit, OnDestroy {
  private adminService = inject(AdminService);
  private route = inject(ActivatedRoute);
  private router = inject(Router);

  readonly tab = signal<'veterinaires' | 'eleveurs'>('veterinaires');
  private subRoute?: Subscription;

  readonly veterinaires = signal<VeterinaireAdmin[]>([]);
  readonly bergeries = signal<BergerieAdmin[]>([]);
  readonly eleveurs = signal<EleveurAdmin[]>([]);

  readonly filtre = signal<FiltreStatut>('EN_ATTENTE');
  readonly erreur = signal<string | null>(null);

  readonly filtres: { valeur: FiltreStatut; libelle: string }[] = [
    { valeur: 'EN_ATTENTE', libelle: 'En attente' },
    { valeur: 'VALIDE', libelle: 'Validés' },
    { valeur: 'SUSPENDU', libelle: 'Suspendus' },
    { valeur: 'REJETE', libelle: 'Rejetés' },
    { valeur: 'TOUS', libelle: 'Tous' }
  ];

  readonly nbEnAttente = computed(
    () => this.veterinaires().filter((v) => v.statutValidation === 'EN_ATTENTE').length
  );

  ngOnInit(): void {
    this.subRoute = this.route.queryParams.subscribe((params) => {
      const t = params['tab'];
      if (t === 'eleveurs' || t === 'veterinaires') {
        this.tab.set(t);
      } else {
        this.tab.set('veterinaires');
      }
    });

    this.reloadVeterinaires();
    this.reloadBergeries();
    this.reloadEleveurs();
  }

  ngOnDestroy(): void {
    this.subRoute?.unsubscribe();
  }

  setTab(t: 'veterinaires' | 'eleveurs'): void {
    this.tab.set(t);
    this.router.navigate([], {
      relativeTo: this.route,
      queryParams: { tab: t },
      queryParamsHandling: 'merge'
    });
  }

  changerFiltre(f: FiltreStatut): void {
    this.filtre.set(f);
    this.reloadVeterinaires();
  }

  private reloadVeterinaires(): void {
    const f = this.filtre();
    this.adminService.listVeterinaires(f === 'TOUS' ? undefined : f).subscribe({
      next: (list) => this.veterinaires.set(list),
      error: () => this.erreur.set('Impossible de charger les vétérinaires.')
    });
  }

  private reloadBergeries(): void {
    this.adminService.listBergeries().subscribe((list) => this.bergeries.set(list));
  }

  private reloadEleveurs(): void {
    this.adminService.listEleveurs().subscribe((list) => this.eleveurs.set(list));
  }

  // ---- Actions de validation ----

  valider(v: VeterinaireAdmin): void {
    this.adminService.validerVeterinaire(v.id).subscribe(() => this.reloadVeterinaires());
  }

  reactiver(v: VeterinaireAdmin): void {
    this.adminService.reactiverVeterinaire(v.id).subscribe(() => this.reloadVeterinaires());
  }

  rejeter(v: VeterinaireAdmin): void {
    const motif = prompt(`Motif du rejet de l'inscription de ${v.prenom} ${v.nom} :`);
    if (!motif || !motif.trim()) return;
    this.adminService.rejeterVeterinaire(v.id, motif.trim()).subscribe(() => this.reloadVeterinaires());
  }

  suspendre(v: VeterinaireAdmin): void {
    const motif = prompt(`Motif de la suspension du compte de ${v.prenom} ${v.nom} :`);
    if (!motif || !motif.trim()) return;
    this.adminService.suspendreVeterinaire(v.id, motif.trim()).subscribe(() => this.reloadVeterinaires());
  }

  ouvrirJustificatif(v: VeterinaireAdmin): void {
    const url = this.adminService.justificatifUrl(v);
    if (url) {
      window.open(url, '_blank');
    }
  }

  badgeClass(statut: StatutValidation): string {
    return {
      EN_ATTENTE: 'badge badge-warning',
      VALIDE: 'badge badge-success',
      REJETE: 'badge badge-danger',
      SUSPENDU: 'badge badge-muted'
    }[statut];
  }
}
