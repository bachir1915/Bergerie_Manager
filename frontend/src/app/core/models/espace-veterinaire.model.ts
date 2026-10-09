import { AlerteDto, DonneeEnvDto } from './dashboard.model';

export interface BergerieInfo {
  id: number;
  nom: string;
  code: string;
  region: string | null;
  adresse: string | null;
  superficieTotale: number | null;
  eleveurNomComplet: string | null;
  eleveurTelephone: string | null;
  eleveurEmail: string | null;
  zones: string[];
}

export interface AnimalItem {
  id: number;
  nom: string;
  race: string;
  sexe: string;
  poids: number | null;
  dateNaissance: string | null;
  statut: string;
  statutSante: string;
  numeroCapteur: string | null;
}

export interface ActeItem {
  id: number;
  animalNom: string | null;
  typeSoin: string;
  description: string | null;
  medicament: string | null;
  dose: string | null;
  dateSoin: string;
  prochainRappel: string | null;
}

export interface EspaceVeterinaireResponse {
  bergerie: BergerieInfo;
  totalAnimaux: number;
  animauxMalades: number;
  animauxCritiques: number;
  animaux: AnimalItem[];
  alertes: AlerteDto[];
  derniersActes: ActeItem[];
  dernieresMesures: DonneeEnvDto[];
}
