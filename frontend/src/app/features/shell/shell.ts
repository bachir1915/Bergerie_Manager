import { Component, OnInit, OnDestroy, signal, inject, computed } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet, Router, NavigationEnd } from '@angular/router';
import { Subscription, filter } from 'rxjs';
import { AuthService } from '../../core/services/auth.service';
import { ConsultationService } from '../../core/services/consultation.service';
import { AdminService } from '../../core/services/admin.service';

export interface NavItem {
  path: string;
  icon: string;
  label: string;
  description?: string;
  badge?: () => number | string | null;
  badgeClass?: string;
  queryParams?: Record<string, string>;
}

export interface NavSection {
  title: string;
  items: NavItem[];
}

export interface QuickAction {
  label: string;
  icon: string;
  path: string;
  queryParams?: Record<string, string>;
}

@Component({
  selector: 'app-shell',
  imports: [RouterLink, RouterLinkActive, RouterOutlet],
  templateUrl: './shell.html',
  styleUrl: './shell.scss'
})
export class Shell implements OnInit, OnDestroy {
  readonly authService = inject(AuthService);
  private router = inject(Router);
  private consultationService = inject(ConsultationService);
  private adminService = inject(AdminService);

  readonly collapsed = signal(false);
  readonly mobileOpen = signal(false);
  readonly alertesNonLues = signal(0);
  readonly vetsEnAttente = signal(0);

  private subRouter?: Subscription;

  // Menus alignés sur les diagrammes de cas d'utilisation : un cas d'utilisation principal = un écran, même ordre.
  readonly eleveurSections: NavSection[] = [
    {
      title: 'Fonctionnalités',
      items: [
        {
          path: '/donnees-env',
          icon: 'bi-thermometer-sun',
          label: 'Consulter données environnementales',
          description: 'Graphique et historique des mesures'
        },
        {
          path: '/alertes',
          icon: 'bi-bell-fill',
          label: 'Consulter alertes',
          description: 'Filtrer, marquer comme traitée',
          badge: () => this.alertesNonLues() > 0 ? this.alertesNonLues() : null,
          badgeClass: 'badge-pill-danger'
        },
        {
          path: '/animaux',
          icon: 'bi-collection-play-fill',
          label: 'Gérer les animaux',
          description: 'Consulter, ajouter, modifier, supprimer'
        },
        {
          path: '/dashboard/eleveur',
          icon: 'bi-speedometer2',
          label: 'Consulter statistiques du troupeau',
          description: 'Tableau de bord'
        },
        {
          path: '/localisation',
          icon: 'bi-geo-alt-fill',
          label: 'Consulter localisation des animaux',
          description: 'Carte, positions et historique'
        },
        {
          path: '/veterinaires',
          icon: 'bi-hospital-fill',
          label: 'Vérifier connexion vétérinaire',
          description: 'Accepter, refuser, annuler'
        }
      ]
    }
  ];

  readonly veterinaireSections: NavSection[] = [
    {
      title: 'Fonctionnalités',
      items: [
        {
          path: '/dashboard/veterinaire',
          icon: 'bi-house-heart-fill',
          label: 'Gérer acte médical',
          description: 'Consulter, ajouter, modifier, supprimer'
        },
        {
          path: '/animaux',
          icon: 'bi-collection-play-fill',
          label: 'Consulter un animal',
          description: 'Dossier de santé, filtres'
        },
        {
          path: '/donnees-env',
          icon: 'bi-thermometer-snow',
          label: 'Consulter données environnementales',
          description: 'Graphique et historique des mesures'
        },
        {
          path: '/alertes',
          icon: 'bi-bell-fill',
          label: 'Consulter alertes',
          description: 'Filtrer, marquer comme traitée',
          badge: () => this.alertesNonLues() > 0 ? this.alertesNonLues() : null,
          badgeClass: 'badge-pill-danger'
        }
      ]
    }
  ];

  readonly adminSections: NavSection[] = [
    {
      title: 'Validations',
      items: [
        {
          path: '/admin',
          queryParams: { tab: 'veterinaires' },
          icon: 'bi-shield-check',
          label: 'Gérer les vétérinaires',
          description: 'Valider, rejeter, suspendre, réactiver',
          badge: () => this.vetsEnAttente() > 0 ? this.vetsEnAttente() : null,
          badgeClass: 'badge-pill-warning'
        },
        {
          path: '/admin',
          queryParams: { tab: 'eleveurs' },
          icon: 'bi-person-badge-fill',
          label: 'Consulter les éleveurs',
          description: 'Éleveurs et bergeries'
        }
      ]
    },
    {
      title: 'Supervision',
      items: [
        {
          path: '/animaux',
          icon: 'bi-collection-play-fill',
          label: 'Consulter les animaux',
          description: 'Suivi de tout le cheptel'
        },
        {
          path: '/donnees-env',
          icon: 'bi-cpu-fill',
          label: 'Consulter les données environnementales',
          description: 'Capteurs et télémétrie'
        }
      ]
    }
  ];

  ngOnInit(): void {
    // Fermeture automatique du tiroir mobile lors du changement de route
    this.subRouter = this.router.events
      .pipe(filter((event) => event instanceof NavigationEnd))
      .subscribe(() => {
        this.mobileOpen.set(false);
      });

    this.rafraichirBadges();
  }

  ngOnDestroy(): void {
    this.subRouter?.unsubscribe();
  }

  rafraichirBadges(): void {
    // 1. Charger le nombre d'alertes non lues
    this.consultationService.alertes().subscribe({
      next: (list) => {
        const nonLues = list.filter((a) => a.statutAlerte === 'NOUVELLE').length;
        this.alertesNonLues.set(nonLues);
      },
      error: () => {}
    });

    // 2. Si administrateur, charger le nombre de validations vétérinaires en attente
    if (this.authService.currentUser()?.role === 'ADMIN') {
      this.adminService.listVeterinaires('EN_ATTENTE').subscribe({
        next: (list) => {
          this.vetsEnAttente.set(list.length);
        },
        error: () => {}
      });
    }
  }

  get navSections(): NavSection[] {
    const role = this.authService.currentUser()?.role;
    if (role === 'ELEVEUR') return this.eleveurSections;
    if (role === 'VETERINAIRE') return this.veterinaireSections;
    return this.adminSections;
  }

  get quickAction(): QuickAction | null {
    const role = this.authService.currentUser()?.role;
    if (role === 'ELEVEUR') {
      return {
        label: 'Ajouter un animal',
        icon: 'bi-plus-circle-fill',
        path: '/animaux',
        queryParams: { action: 'nouveau' }
      };
    }
    if (role === 'VETERINAIRE') {
      return {
        label: 'Nouvel acte médical',
        icon: 'bi-capsule-pill',
        path: '/dashboard/veterinaire',
        queryParams: { action: 'nouveau-acte' }
      };
    }
    return null;
  }

  get brandIcon(): string {
    const role = this.authService.currentUser()?.role;
    if (role === 'ELEVEUR') return 'bi-house-heart-fill';
    if (role === 'VETERINAIRE') return 'bi-hospital-fill';
    return 'bi-shield-shaded';
  }

  get roleLabel(): string {
    const role = this.authService.currentUser()?.role;
    if (role === 'ELEVEUR') return 'Éleveur Exploitant';
    if (role === 'VETERINAIRE') return 'Docteur Vétérinaire';
    return 'Super Administrateur';
  }

  get roleColorClass(): string {
    const role = this.authService.currentUser()?.role;
    if (role === 'ELEVEUR') return 'role-eleveur';
    if (role === 'VETERINAIRE') return 'role-veterinaire';
    return 'role-admin';
  }

  toggleSidebar(): void {
    if (window.innerWidth <= 900) {
      this.mobileOpen.update((v) => !v);
    } else {
      this.collapsed.update((v) => !v);
    }
  }

  closeMobileSidebar(): void {
    this.mobileOpen.set(false);
  }

  logout(): void {
    this.authService.logout();
    this.router.navigateByUrl('/login');
  }
}
