export interface LocalisationDto {
  id: number;
  animalId: number;
  animalNom: string;
  animalRace: string;
  animalStatutSante: string;
  latitude: number;
  longitude: number;
  dateHeure: string;
  vitesse: number | null;
}

export interface ZoneDto {
  id: number;
  nom: string;
  typeZone: string;
  latitude: number;
  longitude: number;
  superficie: number;
  rayonMetres: number;
  bergerieId: number | null;
  bergerieNom: string | null;
}

export interface SimulationStatusDto {
  active: boolean;
  gpsTicks: number;
  envTicks: number;
  dernierGpsTick: string | null;
  dernierEnvTick: string | null;
}
