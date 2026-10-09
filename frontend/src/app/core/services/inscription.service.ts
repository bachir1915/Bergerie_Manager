import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { API_BASE_URL } from '../config/api-config';

export interface InscriptionEleveur {
  nom: string;
  prenom: string;
  email: string;
  telephone: string;
}

export interface EleveurCree {
  id: number;
  nom: string;
  prenom: string;
  email: string;
  telephone: string;
  numEleveur: string;
  codeConnexion: string;
}

export interface InscriptionVeterinaire {
  nom: string;
  prenom: string;
  email: string;
  telephone: string;
  specialite?: string;
  justificatif: File;
}

@Injectable({ providedIn: 'root' })
export class InscriptionService {
  private http = inject(HttpClient);

  inscrireEleveur(data: InscriptionEleveur): Observable<EleveurCree> {
    return this.http.post<EleveurCree>(`${API_BASE_URL}/auth/inscription/eleveur`, data);
  }

  inscrireVeterinaire(data: InscriptionVeterinaire): Observable<unknown> {
    const form = new FormData();
    form.append('nom', data.nom);
    form.append('prenom', data.prenom);
    form.append('email', data.email);
    form.append('telephone', data.telephone);
    if (data.specialite) form.append('specialite', data.specialite);
    form.append('justificatif', data.justificatif);
    return this.http.post(`${API_BASE_URL}/auth/inscription/veterinaire`, form);
  }
}
