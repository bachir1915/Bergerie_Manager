export interface AlerteDto {
  id: number;
  type: string;
  message: string;
  niveau: 'NORMAL' | 'MODERE' | 'ELEVE' | 'CRITIQUE';
  statutAlerte: 'NOUVELLE' | 'EN_COURS' | 'TRAITEE' | 'IGNOREE';
  dateHeure: string;
  animalNom: string | null;
}

export interface DonneeEnvDto {
  id: number;
  zoneNom: string | null;
  temperature: number;
  humidite: number;
  qualiteAir: number | null;
  dateHeure: string;
}

export interface StatEntry {
  label: string;
  count: number;
}

export interface DashboardEleveurResponse {
  totalAnimaux: number;
  animauxSains: number;
  animauxMalades: number;
  animauxCritiques: number;
  alertesNonLues: number;
  dernieresAlertes: AlerteDto[];
  donneesMesures: DonneeEnvDto[];
  statsSante: StatEntry[];
  statsRace: StatEntry[];
}

export interface DashboardVeterinaireResponse {
  totalAnimaux: number;
  animauxMalades: number;
  animauxCritiques: number;
  totalVaccinations: number;
  totalTraitements: number;
  totalAnalyses: number;
  totalConsultations: number;
  dernieresAlertes: AlerteDto[];
  statsSante: StatEntry[];
}
