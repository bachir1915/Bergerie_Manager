import { Component, inject, signal } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../core/services/auth.service';
import { dashboardPathFor } from '../../core/guards/auth.guard';

type FeatureTab = 'cheptel' | 'iot' | 'vet' | 'sante';

@Component({
  selector: 'app-landing',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './landing.html',
  styleUrl: './landing.scss'
})
export class Landing {
  readonly authService = inject(AuthService);
  private router = inject(Router);

  readonly activeTab = signal<FeatureTab>('cheptel');

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
