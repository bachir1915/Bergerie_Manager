import { AfterViewInit, Component, HostListener, OnDestroy, inject, signal } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../core/services/auth.service';
import { dashboardPathFor } from '../../core/guards/auth.guard';
import { RevealDirective } from '../../core/directives/reveal.directive';

type FeatureTab = 'cheptel' | 'iot' | 'vet' | 'sante';

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
  readonly scrolled = signal(false);   // en-tête plus compact et ombré après un peu de défilement
  readonly showTop = signal(false);    // bouton « revenir en haut »

  @HostListener('window:scroll')
  onScroll(): void {
    const y = window.scrollY || document.documentElement.scrollTop;
    this.scrolled.set(y > 40);
    this.showTop.set(y > 350);
    document.documentElement.style.setProperty('--sy', String(Math.min(y, 900)));   // parallaxe des halos du hero
  }

  ngAfterViewInit(): void {
    // Les chiffres de l'aperçu du tableau de bord « montent » jusqu'à leur valeur pendant l'animation d'entrée.
    const reduit = typeof matchMedia === 'function' && matchMedia('(prefers-reduced-motion: reduce)').matches;
    if (!reduit) {
      setTimeout(() => this.compterChiffres(), 1700);
    }
  }

  ngOnDestroy(): void {
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
      const duree = 1700;
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
