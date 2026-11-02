import { Component, inject, signal } from '@angular/core';
import { NavigationCancel, NavigationEnd, NavigationError, NavigationStart, Router, RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet],
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
export class App {
  private readonly router = inject(Router);

  /** Barre de progression fine en haut de page à chaque changement d'écran (comme NProgress). */
  readonly actif = signal(false);
  readonly largeur = signal(0);
  private minuteur?: ReturnType<typeof setInterval>;

  constructor() {
    if (localStorage.getItem('mouvement-reduit') === '1') {
      document.documentElement.classList.add('mouvement-reduit');
    }
    this.router.events.subscribe((ev) => {
      if (ev instanceof NavigationStart) {
        this.demarrer();
      } else if (ev instanceof NavigationEnd || ev instanceof NavigationCancel || ev instanceof NavigationError) {
        this.terminer();
      }
    });
  }

  private demarrer(): void {
    clearInterval(this.minuteur);
    this.actif.set(true);
    this.largeur.set(12);
    this.minuteur = setInterval(() => {
      const l = this.largeur();
      this.largeur.set(l + (88 - l) * 0.18);
    }, 120);
  }

  private terminer(): void {
    clearInterval(this.minuteur);
    this.largeur.set(100);
    setTimeout(() => {
      this.actif.set(false);
      this.largeur.set(0);
    }, 320);
  }
}
