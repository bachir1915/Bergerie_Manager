import { Component, inject, signal, computed } from '@angular/core';
import { DatePipe } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { BergerieChoix, ConsultationService } from '../../core/services/consultation.service';
import { AnimalDto } from '../../core/models/consultation.model';

const STATUTS = ['ACTIF', 'VENDU', 'DECEDE', 'MALADE', 'EN_TRAITEMENT'];
const SANTES = ['BON', 'EN_OBSERVATION', 'MALADE', 'CONVALESCENT', 'CRITIQUE'];

@Component({
  selector: 'app-animaux',
  imports: [DatePipe, ReactiveFormsModule],
  templateUrl: './animaux.html',
  styleUrl: './animaux.scss'
})
export class Animaux {
  private service = inject(ConsultationService);
  private fb = inject(FormBuilder);
  private route = inject(ActivatedRoute);
  readonly auth = inject(AuthService);

  readonly statuts = STATUTS;
  readonly santes = SANTES;

  readonly animaux = signal<AnimalDto[]>([]);
  readonly bergeries = signal<BergerieChoix[]>([]);
  readonly loading = signal(true);
  readonly formOuvert = signal(false);
  readonly editionId = signal<number | null>(null);
  readonly erreur = signal<string | null>(null);

  readonly recherche = signal('');
  readonly filtreSante = signal<string>('TOUS');

  readonly animauxFiltres = computed(() => {
    const q = this.recherche().trim().toLowerCase();
    const f = this.filtreSante();
    return this.animaux().filter((a) => {
      const matchSante = f === 'TOUS' || a.statutSante === f;
      const matchText =
        !q ||
        a.nom?.toLowerCase().includes(q) ||
        a.race?.toLowerCase().includes(q) ||
        a.numeroCapteur?.toLowerCase().includes(q) ||
        a.bergerieNom?.toLowerCase().includes(q);
      return matchSante && matchText;
    });
  });

  readonly estEleveur = this.auth.currentUser()?.role === 'ELEVEUR';

  readonly form = this.fb.group({
    nom: ['', Validators.required],
    race: ['', Validators.required],
    sexe: ['F', Validators.required],
    poids: [null as number | null],
    dateNaissance: [null as string | null],
    statut: ['ACTIF', Validators.required],
    statutSante: ['BON', Validators.required],
    numeroCapteur: [''],
    bergerieId: [null as number | null, Validators.required]
  });

  constructor() {
    this.charger();
    if (this.estEleveur) {
      this.service.mesBergeries().subscribe((b) => this.bergeries.set(b));
    }
    this.route.queryParams.subscribe((p) => {
      if (p['action'] === 'nouveau' && this.estEleveur) {
        this.ouvrirCreation();
      }
    });
  }

  private charger(): void {
    this.loading.set(true);
    this.service.animaux().subscribe({
      next: (l) => { this.animaux.set(l); this.loading.set(false); },
      error: () => this.loading.set(false)
    });
  }

  parSante(s: string): number {
    return this.animaux().filter((a) => a.statutSante === s).length;
  }

  classeSante(s: string | null): string {
    return { BON: 'sante-bon', MALADE: 'sante-malade', CRITIQUE: 'sante-critique', EN_OBSERVATION: 'sante-observation', CONVALESCENT: 'sante-observation' }[s ?? ''] ?? 'sante-bon';
  }

  ouvrirCreation(): void {
    this.erreur.set(null);
    this.editionId.set(null);
    this.form.reset({ sexe: 'F', statut: 'ACTIF', statutSante: 'BON', poids: null, dateNaissance: null, numeroCapteur: '',
      bergerieId: this.bergeries().length === 1 ? this.bergeries()[0].id : null });
    this.formOuvert.set(true);
  }

  ouvrirEdition(a: AnimalDto): void {
    this.erreur.set(null);
    this.editionId.set(a.id);
    const berg = this.bergeries().find((b) => b.nom === a.bergerieNom);
    this.form.reset({
      nom: a.nom, race: a.race, sexe: a.sexe, poids: a.poids, dateNaissance: a.dateNaissance,
      statut: a.statut ?? 'ACTIF', statutSante: a.statutSante ?? 'BON', numeroCapteur: a.numeroCapteur ?? '',
      bergerieId: berg?.id ?? null
    });
    this.formOuvert.set(true);
  }

  annuler(): void {
    this.formOuvert.set(false);
  }

  enregistrer(): void {
    if (this.form.invalid) { this.form.markAllAsTouched(); return; }
    const v = this.form.getRawValue() as any;
    const id = this.editionId();
    const req = id ? this.service.modifierAnimal(id, v) : this.service.creerAnimal(v);
    req.subscribe({
      next: () => { this.formOuvert.set(false); this.charger(); },
      error: (err: HttpErrorResponse) => this.erreur.set(err.error?.detail || 'Enregistrement impossible.')
    });
  }

  supprimer(a: AnimalDto): void {
    if (!confirm(`Supprimer ${a.nom} ?`)) return;
    this.service.supprimerAnimal(a.id).subscribe({
      next: () => this.charger(),
      error: (err: HttpErrorResponse) => this.erreur.set(err.error?.detail || 'Suppression impossible.')
    });
  }
}
