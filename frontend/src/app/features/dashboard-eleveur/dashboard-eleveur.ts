import { Component, ElementRef, OnDestroy, ViewChild, signal } from '@angular/core';
import { DatePipe, DecimalPipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { Chart } from 'chart.js/auto';
import { DashboardService } from '../../core/services/dashboard.service';
import { AuthService } from '../../core/services/auth.service';
import { DashboardEleveurResponse } from '../../core/models/dashboard.model';

const SANTE_COLORS: Record<string, string> = {
  BON: '#10b981',
  MALADE: '#ef4444',
  CRITIQUE: '#7f1d1d',
  EN_OBSERVATION: '#f59e0b',
  CONVALESCENT: '#6366f1'
};

@Component({
  selector: 'app-dashboard-eleveur',
  imports: [RouterLink, DatePipe, DecimalPipe],
  templateUrl: './dashboard-eleveur.html',
  styleUrl: './dashboard-eleveur.scss'
})
export class DashboardEleveur implements OnDestroy {
  @ViewChild('santeCanvas') santeCanvas?: ElementRef<HTMLCanvasElement>;

  readonly data = signal<DashboardEleveurResponse | null>(null);
  readonly loading = signal(true);
  private chart?: Chart;

  constructor(
    private dashboardService: DashboardService,
    public authService: AuthService
  ) {
    this.dashboardService.getEleveurDashboard().subscribe((response) => {
      this.data.set(response);
      this.loading.set(false);
      setTimeout(() => this.renderChart(response));
    });
  }

  private renderChart(data: DashboardEleveurResponse): void {
    if (!this.santeCanvas) return;
    this.chart?.destroy();
    this.chart = new Chart(this.santeCanvas.nativeElement, {
      type: 'doughnut',
      data: {
        labels: data.statsSante.map((s) => s.label),
        datasets: [
          {
            data: data.statsSante.map((s) => s.count),
            backgroundColor: data.statsSante.map((s) => SANTE_COLORS[s.label] ?? '#3b82f6'),
            borderWidth: 0
          }
        ]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        cutout: '65%',
        plugins: { legend: { position: 'bottom', labels: { color: '#9ca3af', padding: 15 } } }
      }
    });
  }

  ngOnDestroy(): void {
    this.chart?.destroy();
  }
}
