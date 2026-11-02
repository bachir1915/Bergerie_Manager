import { AfterViewInit, Component, HostListener, OnDestroy, inject, signal } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../core/services/auth.service';
import { dashboardPathFor } from '../../core/guards/auth.guard';
import { RevealDirective } from '../../core/directives/reveal.directive';

type FeatureTab = 'cheptel' | 'iot' | 'vet' | 'sante';

const MOTS = ['la santé de chaque bête', 'la position GPS en direct', 'le climat de vos enclos', 'les alertes précoces', 'les soins de votre vétérinaire'];

@Component({
  selector: 'app-landing',
  standalone: true,
  imports: [CommonModule, RouterLink, RevealDirective],
  templateUrl: './landing.html',
  styleUrl: './landing.scss'
})
export class Landing implements AfterViewInit, OnDestroy {
  readonly authService = inject(AuthService);
  private router = inject(Router);

  readonly activeTab = signal<FeatureTab>('cheptel');
  readonly progression = signal(0);    // barre de lecture en haut de la page
  readonly section = signal('');       // section visible (soulignement du menu)
  readonly motRotatif = signal('');    // mot qui s'écrit sous le titre
  private arret = false;
  private observateur?: IntersectionObserver;
  readonly scrolled = signal(false);   // en-tête plus compact et ombré après un peu de défilement
  readonly showTop = signal(false);    // bouton « revenir en haut »

  @HostListener('window:scroll')
  onScroll(): void {
    const y = window.scrollY || document.documentElement.scrollTop;
    this.scrolled.set(y > 40);
    this.showTop.set(y > 350);
    document.documentElement.style.setProperty('--sy', String(Math.min(y, 900)));
    const haut = document.documentElement.scrollHeight - window.innerHeight;
    this.progression.set(haut > 0 ? Math.min(100, (y / haut) * 100) : 0);
    let actif = '';
    for (const id of ['features', 'faq']) {
      const r = document.getElementById(id)?.getBoundingClientRect();
      if (r && r.top <= 140 && r.bottom > 140) actif = id;
    }
    this.section.set(actif);   // parallaxe des halos du hero
  }

  ngAfterViewInit(): void {
    // Les chiffres de l'aperçu du tableau de bord « montent » jusqu'à leur valeur pendant l'animation d'entrée.
    const reduit = typeof matchMedia === 'function' && matchMedia('(prefers-reduced-motion: reduce)').matches;
    if (!reduit) {
      setTimeout(() => this.compterChiffres(), 4200);
      setTimeout(() => this.ecrire(0, 0, true), 4000);
    } else {
      this.motRotatif.set(MOTS[0]);
    }
    // Les chiffres clés montent jusqu'à leur valeur quand la bande entre dans l'écran.
    const bande = document.querySelector('.stats-band');
    if (bande && typeof IntersectionObserver !== 'undefined' && !reduit) {
      this.observateur = new IntersectionObserver((entrees) => {
        if (entrees.some((e) => e.isIntersecting)) {
          this.compterBande();
          this.observateur?.disconnect();
        }
      }, { threshold: 0.35 });
      this.observateur.observe(bande);
    }
  }

  /** Machine à écrire : écrit un mot, le garde un instant, l'efface, passe au suivant. */
  private ecrire(indice: number, n: number, avance: boolean): void {
    if (this.arret) return;
    const mot = MOTS[indice % MOTS.length];
    if (avance) {
      this.motRotatif.set(mot.slice(0, n + 1));
      if (n + 1 < mot.length) {
        setTimeout(() => this.ecrire(indice, n + 1, true), 105);
      } else {
        setTimeout(() => this.ecrire(indice, n + 1, false), 2800);
      }
    } else {
      const restant = mot.slice(0, Math.max(0, n - 1));
      this.motRotatif.set(restant);
      if (restant.length > 0) {
        setTimeout(() => this.ecrire(indice, n - 1, false), 55);
      } else {
        setTimeout(() => this.ecrire(indice + 1, 0, true), 350);
      }
    }
  }

  private compterBande(): void {
    document.querySelectorAll<HTMLElement>('.stats-band [data-count]').forEach((el) => {
      const cible = parseInt(el.dataset['count'] ?? '0', 10);
      const t0 = performance.now();
      const pas = (t: number) => {
        const k = Math.min(1, (t - t0) / 3000);
        el.textContent = String(Math.round(cible * (1 - Math.pow(1 - k, 3))));
        if (k < 1) requestAnimationFrame(pas);
      };
      requestAnimationFrame(pas);
    });
  }

  ngOnDestroy(): void {
    this.arret = true;
    this.observateur?.disconnect();
    document.documentElement.style.removeProperty('--sy');
  }

  private compterChiffres(): void {
    document.querySelectorAll<HTMLElement>('.mockup-kpi-card .kpi-number').forEach((el) => {
      const texte = (el.textContent ?? '').trim();
      const m = texte.match(/^(\d+(?:[.,]\d+)?)(.*)$/);
      if (!m) return;
      const cible = parseFloat(m[1].replace(',', '.'));
      const decimales = (m[1].split(/[.,]/)[1] ?? '').length;
      const t0 = performance.now();
      const duree = 2800;
      const pas = (t: number) => {
        const k = Math.min(1, (t - t0) / duree);
        const ease = 1 - Math.pow(1 - k, 3);
        el.textContent = (cible * ease).toFixed(decimales) + m[2];
        if (k < 1) requestAnimationFrame(pas); else el.textContent = texte;
      };
      requestAnimationFrame(pas);
    });
  }

  remonter(): void {
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  get dashboardUrl(): string {
    const role = this.authService.currentUser()?.role;
    return role ? dashboardPathFor(role) : '/dashboard/eleveur';
  }

  setTab(tab: FeatureTab): void {
    this.activeTab.set(tab);
  }

  scrollToSection(target: string, event: Event): void {
    event.preventDefault();
    if (target === 'iot-ia' || target === 'iot') {
      this.activeTab.set('iot');
      const elem = document.getElementById('features');
      if (elem) {
        elem.scrollIntoView({ behavior: 'smooth', block: 'start' });
      }
      return;
    }

    if (target === 'sante' || target === 'ia') {
      this.activeTab.set('sante');
      const elem = document.getElementById('features');
      if (elem) {
        elem.scrollIntoView({ behavior: 'smooth', block: 'start' });
      }
      return;
    }

    if (target === 'features') {
      this.activeTab.set('cheptel');
      const elem = document.getElementById('features');
      if (elem) {
        elem.scrollIntoView({ behavior: 'smooth', block: 'start' });
      }
      return;
    }

    const elem = document.getElementById(target);
    if (elem) {
      elem.scrollIntoView({ behavior: 'smooth', block: 'start' });
    }
  }
}
