import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { API_BASE_URL } from '../config/api-config';
import { AnimalDto, VeterinairePublic } from '../models/consultation.model';
import { AlerteDto, DonneeEnvDto } from '../models/dashboard.model';
import { LocalisationDto, ZoneDto, SimulationStatusDto } from '../models/localisation.model';

export interface BergerieChoix {
  id: number;
  nom: string;
  code: string;
}

export interface AnimalForm {
  nom: string;
  race: string;
  sexe: string;
  poids: number | null;
  dateNaissance: string | null;
  statut: string;
  statutSante: string;
  numeroCapteur: string | null;
  bergerieId: number | null;
}

@Injectable({ providedIn: 'root' })
export class ConsultationService {
  private http = inject(HttpClient);

  animaux(): Observable<AnimalDto[]> {
    return this.http.get<AnimalDto[]>(`${API_BASE_URL}/animaux`);
  }

  alertes(): Observable<AlerteDto[]> {
    return this.http.get<AlerteDto[]>(`${API_BASE_URL}/alertes`);
  }

  donneesEnv(): Observable<DonneeEnvDto[]> {
    return this.http.get<DonneeEnvDto[]>(`${API_BASE_URL}/donnees-env`);
  }

  localisations(): Observable<LocalisationDto[]> {
    return this.http.get<LocalisationDto[]>(`${API_BASE_URL}/localisation`);
  }

  zones(): Observable<ZoneDto[]> {
    return this.http.get<ZoneDto[]>(`${API_BASE_URL}/localisation/zones`);
  }

  historiqueAnimal(animalId: number): Observable<LocalisationDto[]> {
    return this.http.get<LocalisationDto[]>(`${API_BASE_URL}/localisation/animal/${animalId}`);
  }

  // ---- Contrôle de Simulation IoT ----

  simulationStatus(): Observable<SimulationStatusDto> {
    return this.http.get<SimulationStatusDto>(`${API_BASE_URL}/iot/simulation/status`);
  }

  simulationTick(): Observable<any> {
    return this.http.post<any>(`${API_BASE_URL}/iot/simulation/tick`, {});
  }

  simulationToggle(): Observable<any> {
    return this.http.post<any>(`${API_BASE_URL}/iot/simulation/toggle`, {});
  }

  simulationSortieZone(animalId?: number): Observable<any> {
    const params = animalId ? `?animalId=${animalId}` : '';
    return this.http.post<any>(`${API_BASE_URL}/iot/simulation/sortie-zone${params}`, {});
  }

  simulationRetour(animalId?: number): Observable<any> {
    const params = animalId ? `?animalId=${animalId}` : '';
    return this.http.post<any>(`${API_BASE_URL}/iot/simulation/retour${params}`, {});
  }

  veterinaires(): Observable<VeterinairePublic[]> {
    return this.http.get<VeterinairePublic[]>(`${API_BASE_URL}/veterinaires`);
  }

  // ---- Gestion du cheptel (éleveur) ----

  mesBergeries(): Observable<BergerieChoix[]> {
    return this.http.get<BergerieChoix[]>(`${API_BASE_URL}/animaux/mes-bergeries`);
  }

  creerAnimal(form: AnimalForm): Observable<AnimalDto> {
    return this.http.post<AnimalDto>(`${API_BASE_URL}/animaux`, form);
  }

  modifierAnimal(id: number, form: AnimalForm): Observable<AnimalDto> {
    return this.http.put<AnimalDto>(`${API_BASE_URL}/animaux/${id}`, form);
  }

  supprimerAnimal(id: number): Observable<void> {
    return this.http.delete<void>(`${API_BASE_URL}/animaux/${id}`);
  }
}
