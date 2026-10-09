import {
  Component,
  OnInit,
  OnDestroy,
  AfterViewChecked,
  signal,
  inject,
  ChangeDetectorRef
} from '@angular/core';
import { DatePipe, DecimalPipe } from '@angular/common';
import { ConsultationService } from '../../core/services/consultation.service';
import { LocalisationDto } from '../../core/models/localisation.model';

/** Couleurs des marqueurs selon l'état de santé */
const SANTE_COLORS: Record<string, string> = {
  BON: '#10b981',
  EN_OBSERVATION: '#f59e0b',
  MALADE: '#ef4444',
  CRITIQUE: '#7f1d1d',
  CONVALESCENT: '#6366f1'
};

// Coordonnées de référence : Parcelles Assainies, Dakar
const DAKAR_PARCELLES_LAT = 14.7585;
const DAKAR_PARCELLES_LON = -17.4420;

@Component({
  selector: 'app-localisation',
  imports: [DatePipe, DecimalPipe],
  templateUrl: './localisation.html',
  styleUrl: './localisation.scss'
})
export class Localisation implements OnInit, AfterViewChecked, OnDestroy {
  private service = inject(ConsultationService);
  private cdr = inject(ChangeDetectorRef);

  readonly positions = signal<LocalisationDto[]>([]);
  readonly loading = signal(true);
  readonly erreur = signal<string | null>(null);

  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  private map: any = null;
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  private markersMap = new Map<number, any>();
  private mapInitialized = false;
  private mapReady = false;
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  private refreshInterval: any = null;
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  private leafletRetryInterval: any = null;

  ngOnInit(): void {
    this.chargerPositions();

    // Rafraîchissement automatique toutes les 5 secondes
    this.refreshInterval = setInterval(() => {
      this.service.localisations().subscribe({
        next: (data) => {
          this.positions.set(data);
          if (this.mapReady) {
            this.mettreAJourMarqueurs();
          } else if (!this.mapInitialized) {
            // Réessayer l'init si la carte n'est pas encore prête
            this.tentativeInitCarte();
          }
          this.cdr.detectChanges();
        }
      });
    }, 5000);

    // Tentative d'init carte dès le démarrage (avec retry si Leaflet pas encore chargé)
    this.leafletRetryInterval = setInterval(() => {
      if (!this.mapInitialized) {
        this.tentativeInitCarte();
      } else {
        clearInterval(this.leafletRetryInterval);
        this.leafletRetryInterval = null;
      }
    }, 800);
  }

  chargerPositions(): void {
    this.loading.set(true);
    this.erreur.set(null);

    this.service.localisations().subscribe({
      next: (data) => {
        this.positions.set(data);
        this.loading.set(false);

        if (this.mapReady) {
          this.mettreAJourMarqueurs();
        } else {
          this.mapInitialized = false;
        }
        this.cdr.detectChanges();
      },
      error: () => {
        this.loading.set(false);
        this.erreur.set('Impossible de charger les positions GPS. Vérifiez que le serveur est démarré.');
      }
    });
  }

  actualiser(): void {
    this.service.localisations().subscribe({
      next: (data) => {
        this.positions.set(data);
        if (this.mapReady) {
          this.mettreAJourMarqueurs();
        }
      },
      error: () => {
        this.erreur.set('Échec de l\'actualisation.');
      }
    });
  }

  ngAfterViewChecked(): void {
    if (!this.mapInitialized && !this.loading()) {
      this.tentativeInitCarte();
    }
  }

  private tentativeInitCarte(): void {
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    const L = (window as any)['L'];
    const container = document.getElementById('leaflet-map');
    if (L && container && !this.mapInitialized) {
      this.mapInitialized = true;
      setTimeout(() => this.initCarte(), 100);
    }
  }

  private initCarte(): void {
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    const L = (window as any)['L'];
    if (!L) {
      console.error('[Localisation] Leaflet (L) non disponible.');
      return;
    }

    const container = document.getElementById('leaflet-map');
    if (!container || this.mapReady) return;

    // Centrage direct sur Parcelles Assainies, Dakar
    let centerLat = DAKAR_PARCELLES_LAT;
    let centerLon = DAKAR_PARCELLES_LON;

    if (this.positions().length > 0) {
      const lats = this.positions().map(p => p.latitude);
      const lons = this.positions().map(p => p.longitude);
      centerLat = lats.reduce((a, b) => a + b, 0) / lats.length;
      centerLon = lons.reduce((a, b) => a + b, 0) / lons.length;
    }

    this.map = L.map('leaflet-map', {
      center: [centerLat, centerLon],
      zoom: 16,
      zoomControl: true
    });

    // Fond de carte OpenStreetMap
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '© <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> | Parcelles Assainies, Dakar',
      maxZoom: 19
    }).addTo(this.map);

    // Dessin d'un cercle symbolisant la zone de la bergerie aux Parcelles Assainies
    L.circle([centerLat, centerLon], {
      radius: 180,
      color: '#10b981',
      fillColor: '#10b981',
      fillOpacity: 0.1,
      weight: 2,
      dashArray: '5, 5'
    }).addTo(this.map).bindPopup('<strong>DIOURGUI</strong><br/>Parcelles Assainies, Dakar');

    // Marqueurs des animaux
    this.mettreAJourMarqueurs();

    this.mapReady = true;
  }

  /** Met à jour la position des marqueurs sur la carte de manière fluide */
  private mettreAJourMarqueurs(): void {
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    const L = (window as any)['L'];
    if (!L || !this.map) return;

    this.positions().forEach(pos => {
      const couleur = SANTE_COLORS[pos.animalStatutSante] ?? '#10b981';

      const icon = L.divIcon({
        className: 'custom-sheep-marker',
        html: `
          <div style="
            width: 36px; height: 36px;
            background: ${couleur};
            border: 3px solid rgba(255,255,255,0.9);
            border-radius: 50% 50% 50% 0;
            transform: rotate(-45deg);
            box-shadow: 0 4px 12px rgba(0,0,0,0.35);
            display:flex; align-items:center; justify-content:center;
            transition: all 0.5s ease;
          ">
            <span style="
              transform: rotate(45deg);
              font-size: 15px; color:#fff; font-weight:700;
              line-height:1;
            ">🐑</span>
          </div>`,
        iconSize: [36, 36],
        iconAnchor: [18, 36],
        popupAnchor: [0, -38]
      });

      const popupHtml = `
        <div style="font-family:'Inter',sans-serif; min-width:180px; padding:4px 0">
          <div style="font-size:1.05rem; font-weight:700; margin-bottom:5px; color:#1a2e1a">
            🐑 ${pos.animalNom}
          </div>
          <div style="font-size:0.85rem; color:#444; margin-bottom:3px">
            <strong>Race :</strong> ${pos.animalRace}
          </div>
          <div style="font-size:0.85rem; color:#444; margin-bottom:3px">
            <strong>Santé :</strong>
            <span style="color:${couleur}; font-weight:600">${pos.animalStatutSante}</span>
          </div>
          <div style="font-size:0.85rem; color:#444; margin-bottom:3px">
            <strong>Vitesse :</strong> ${pos.vitesse != null ? pos.vitesse.toFixed(1) + ' km/h' : '0.0 km/h'}
          </div>
          <hr style="margin:6px 0; border-color:#eee"/>
          <div style="font-size:0.75rem; color:#888">
            Parcelles Assainies : ${pos.latitude.toFixed(5)}, ${pos.longitude.toFixed(5)}
          </div>
        </div>
      `;

      if (this.markersMap.has(pos.animalId)) {
        const marker = this.markersMap.get(pos.animalId);
        marker.setLatLng([pos.latitude, pos.longitude]);
        marker.setIcon(icon);
        marker.setPopupContent(popupHtml);
      } else {
        const marker = L.marker([pos.latitude, pos.longitude], { icon })
          .addTo(this.map)
          .bindPopup(popupHtml, { maxWidth: 240 });

        this.markersMap.set(pos.animalId, marker);
      }
    });
  }

  centrerSurAnimal(pos: LocalisationDto): void {
    if (!this.map) return;
    this.map.setView([pos.latitude, pos.longitude], 17, { animate: true });
    const marker = this.markersMap.get(pos.animalId);
    if (marker) {
      marker.openPopup();
    }
  }

  classeSante(s: string): string {
    const map: Record<string, string> = {
      BON: 'sante-bon',
      MALADE: 'sante-malade',
      CRITIQUE: 'sante-critique',
      EN_OBSERVATION: 'sante-observation',
      CONVALESCENT: 'sante-observation'
    };
    return map[s] ?? 'sante-bon';
  }

  private detruireCarte(): void {
    if (this.refreshInterval) {
      clearInterval(this.refreshInterval);
      this.refreshInterval = null;
    }
    if (this.leafletRetryInterval) {
      clearInterval(this.leafletRetryInterval);
      this.leafletRetryInterval = null;
    }
    if (this.map) {
      this.markersMap.clear();
      this.map.remove();
      this.map = null;
    }
    this.mapReady = false;
    this.mapInitialized = false;
  }

  ngOnDestroy(): void {
    this.detruireCarte();
  }
}
