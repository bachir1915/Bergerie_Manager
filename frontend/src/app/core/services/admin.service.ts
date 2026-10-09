import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { API_BASE_URL } from '../config/api-config';
import { BergerieAdmin, EleveurAdmin, StatutValidation, VeterinaireAdmin } from '../models/admin.model';

@Injectable({ providedIn: 'root' })
export class AdminService {
  private http = inject(HttpClient);

  // ---- Vétérinaires ----

  listVeterinaires(statut?: StatutValidation): Observable<VeterinaireAdmin[]> {
    let params = new HttpParams();
    if (statut) {
      params = params.set('statut', statut);
    }
    return this.http.get<VeterinaireAdmin[]>(`${API_BASE_URL}/admin/veterinaires`, { params });
  }

  validerVeterinaire(id: number): Observable<VeterinaireAdmin> {
    return this.http.post<VeterinaireAdmin>(`${API_BASE_URL}/admin/veterinaires/${id}/valider`, {});
  }

  rejeterVeterinaire(id: number, motif: string): Observable<VeterinaireAdmin> {
    return this.http.post<VeterinaireAdmin>(`${API_BASE_URL}/admin/veterinaires/${id}/rejeter`, { motif });
  }

  suspendreVeterinaire(id: number, motif: string): Observable<VeterinaireAdmin> {
    return this.http.post<VeterinaireAdmin>(`${API_BASE_URL}/admin/veterinaires/${id}/suspendre`, { motif });
  }

  reactiverVeterinaire(id: number): Observable<VeterinaireAdmin> {
    return this.http.post<VeterinaireAdmin>(`${API_BASE_URL}/admin/veterinaires/${id}/reactiver`, {});
  }

  justificatifUrl(v: VeterinaireAdmin): string | null {
    return v.justificatifUrl ? `http://localhost:8085${v.justificatifUrl}` : null;
  }

  // ---- Bergeries ----

  listBergeries(): Observable<BergerieAdmin[]> {
    return this.http.get<BergerieAdmin[]>(`${API_BASE_URL}/admin/bergeries`);
  }

  // ---- Éleveurs (lecture seule) ----

  listEleveurs(): Observable<EleveurAdmin[]> {
    return this.http.get<EleveurAdmin[]>(`${API_BASE_URL}/admin/eleveurs`);
  }
}
