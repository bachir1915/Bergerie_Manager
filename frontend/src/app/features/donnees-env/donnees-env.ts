import { Component, inject, signal, ElementRef, ViewChild, OnDestroy, AfterViewChecked, NgZone } from '@angular/core';
import { DatePipe } from '@angular/common';
import { ConsultationService } from '../../core/services/consultation.service';
import { DonneeEnvDto } from '../../core/models/dashboard.model';

@Component({
  selector: 'app-donnees-env',
  imports: [DatePipe],
  template: `
    <div class="page-header">
      <div>
        <h1 class="page-title">Données environnementales</h1>
        <p class="page-subtitle">Mesures des capteurs — {{ mesures().length }} relevé(s)</p>
      </div>
    </div>

    @if (erreur()) {
      <div style="display:flex; align-items:center; gap:0.75rem; padding:0.85rem 1.25rem; margin-bottom:1rem;
                  background:rgba(239,68,68,0.08); border:1px solid rgba(239,68,68,0.3); border-radius:10px; color:#ef4444; font-size:0.9rem;">
        <i class="bi bi-exclamation-triangle-fill"></i>
        <span>{{ erreur() }}</span>
      </div>
    }

    @if (loading()) {
      <div class="card-dark" style="padding:2rem; text-align:center; color:var(--ink-soft);">
        <div style="width:36px;height:36px;border:3px solid var(--border);border-top-color:var(--accent);border-radius:50%;animation:spin 0.8s linear infinite;margin:0 auto 1rem;"></div>
        Chargement des données...
      </div>
    } @else {

      <!-- Graphique courbe temporelle Température + Humidité -->
      @if (mesures().length > 1) {
        <div class="card-dark" style="margin-bottom:1.5rem;">
          <div class="card-dark-header">
            <i class="bi bi-graph-up text-accent"></i>
            Évolution température &amp; humidité ({{ mesures().length }} derniers relevés)
          </div>
          <div style="padding:1rem; background:var(--surface-alt, #1a2412); border-radius:0 0 12px 12px;">
            <canvas id="envChartCanvas" style="width:100%; max-height:280px;"></canvas>
          </div>
        </div>
      }

      <!-- Tableau des relevés -->
      <div class="card-dark">
        <div class="card-dark-header"><i class="bi bi-thermometer-half text-accent"></i> Relevés</div>
        <div class="table-responsive">
          <table class="table-dark-custom">
            <thead>
              <tr><th>Date</th><th>Zone</th><th>Température</th><th>Humidité</th><th>Qualité air</th></tr>
            </thead>
            <tbody>
              @if (!mesures().length) {
                <tr><td colspan="5" style="text-align:center;color:var(--text-muted)">Aucune mesure</td></tr>
              }
              @for (m of mesures(); track m.id) {
                <tr>
                  <td>{{ m.dateHeure | date: 'dd/MM/yyyy HH:mm' }}</td>
                  <td>{{ m.zoneNom || '—' }}</td>
                  <td>
                    <span [style.color]="m.temperature > 35 ? '#ef4444' : m.temperature > 28 ? '#f59e0b' : '#10b981'"
                          style="font-weight:600">
                      {{ m.temperature }} °C
                    </span>
                  </td>
                  <td>{{ m.humidite }} %</td>
                  <td>
                    @if (m.qualiteAir != null) {
                      <span [style.color]="m.qualiteAir! > 150 ? '#ef4444' : m.qualiteAir! > 80 ? '#f59e0b' : '#10b981'"
                            style="font-weight:600">
                        {{ m.qualiteAir }} AQI
                      </span>
                    } @else { — }
                  </td>
                </tr>
              }
            </tbody>
          </table>
        </div>
      </div>
    }
  `
})
export class DonneesEnv implements AfterViewChecked, OnDestroy {
  private service = inject(ConsultationService);
  private zone = inject(NgZone);

  readonly mesures = signal<DonneeEnvDto[]>([]);
  readonly loading = signal(true);
  readonly erreur = signal<string | null>(null);

  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  private chart: any = null;
  private chartDrawn = false;
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  private retryTimer: any = null;

  constructor() {
    this.service.donneesEnv().subscribe({
      next: (l) => {
        this.mesures.set(l);
        this.loading.set(false);
        this.chartDrawn = false;
        // Tenter de dessiner dès réception des données
        this.zone.runOutsideAngular(() => {
          setTimeout(() => this.tryRenderChart(), 200);
        });
      },
      error: () => {
        this.loading.set(false);
        this.erreur.set('Impossible de charger les données environnementales. Vérifiez que le backend est démarré.');
      }
    });
  }

  ngAfterViewChecked(): void {
    if (!this.chartDrawn && !this.loading() && this.mesures().length > 1) {
      this.zone.runOutsideAngular(() => {
        this.tryRenderChart();
      });
    }
  }

  private tryRenderChart(): void {
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    const ChartJS = (window as any)['Chart'];
    const canvas = document.getElementById('envChartCanvas') as HTMLCanvasElement | null;

    if (!ChartJS || !canvas || this.chartDrawn) return;
    if (this.mesures().length < 2) return;

    this.chartDrawn = true;
    this.renderChart(ChartJS, canvas);
  }

  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  private renderChart(ChartJS: any, canvas: HTMLCanvasElement): void {
    // Détruire l'instance précédente si elle existe
    if (this.chart) {
      this.chart.destroy();
      this.chart = null;
    }

    const data = [...this.mesures()].reverse();
    const labels = data.map((_, i) => `Relevé ${i + 1}`);

    this.chart = new ChartJS(canvas, {
      type: 'line',
      data: {
        labels,
        datasets: [
          {
            label: 'Température (°C)',
            data: data.map((d: DonneeEnvDto) => d.temperature),
            borderColor: '#ef4444',
            backgroundColor: 'rgba(239,68,68,0.08)',
            tension: 0.4,
            fill: true,
            pointRadius: 3,
            pointHoverRadius: 6,
            borderWidth: 2
          },
          {
            label: 'Humidité (%)',
            data: data.map((d: DonneeEnvDto) => d.humidite),
            borderColor: '#3b82f6',
            backgroundColor: 'rgba(59,130,246,0.06)',
            tension: 0.4,
            fill: false,
            pointRadius: 3,
            pointHoverRadius: 6,
            borderWidth: 2
          }
        ]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        interaction: { mode: 'index', intersect: false },
        scales: {
          y: {
            beginAtZero: false,
            grid: { color: 'rgba(255,255,255,0.05)' },
            ticks: { color: '#9ca3af' }
          },
          x: {
            grid: { color: 'rgba(255,255,255,0.05)' },
            ticks: { color: '#9ca3af', maxTicksLimit: 12 }
          }
        },
        plugins: {
          legend: { position: 'bottom', labels: { color: '#9ca3af', padding: 15 } }
        }
      }
    });
  }

  ngOnDestroy(): void {
    if (this.retryTimer) clearTimeout(this.retryTimer);
    if (this.chart) {
      this.chart.destroy();
      this.chart = null;
    }
  }
}
