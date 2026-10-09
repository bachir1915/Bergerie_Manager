import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { API_BASE_URL } from '../config/api-config';
import { DashboardEleveurResponse, DashboardVeterinaireResponse } from '../models/dashboard.model';
import { EspaceVeterinaireResponse } from '../models/espace-veterinaire.model';

@Injectable({ providedIn: 'root' })
export class DashboardService {
  private http = inject(HttpClient);

  getEleveurDashboard(): Observable<DashboardEleveurResponse> {
    return this.http.get<DashboardEleveurResponse>(`${API_BASE_URL}/dashboard/eleveur`);
  }

  getVeterinaireDashboard(): Observable<DashboardVeterinaireResponse> {
    return this.http.get<DashboardVeterinaireResponse>(`${API_BASE_URL}/dashboard/veterinaire`);
  }

  /** Espace vétérinaire focalisé sur la bergerie du code utilisé à la connexion. */
  getEspaceVeterinaire(): Observable<EspaceVeterinaireResponse> {
    return this.http.get<EspaceVeterinaireResponse>(`${API_BASE_URL}/veterinaire/espace`);
  }

  /** Change la bergerie consultée à partir de son code. */
  choisirBergerie(code: string): Observable<EspaceVeterinaireResponse> {
    return this.http.post<EspaceVeterinaireResponse>(
      `${API_BASE_URL}/veterinaire/bergerie?code=${encodeURIComponent(code)}`, {});
  }

  /** Enregistre un acte médical pour un animal de la bergerie consultée. */
  ajouterActe(form: ActeForm): Observable<EspaceVeterinaireResponse> {
    return this.http.post<EspaceVeterinaireResponse>(`${API_BASE_URL}/veterinaire/actes`, form);
  }
}

export interface ActeForm {
  animalId: number;
  typeSoin: string;
  description: string | null;
  medicament: string | null;
  dose: string | null;
  dateSoin: string;
  prochainRappel: string | null;
}
