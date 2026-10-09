export type StatutValidation = 'EN_ATTENTE' | 'VALIDE' | 'REJETE' | 'SUSPENDU';

export interface VeterinaireAdmin {
  id: number;
  nom: string;
  prenom: string;
  email: string;
  telephone: string;
  specialite: string | null;
  statutValidation: StatutValidation;
  motifSuspension: string | null;
  dateCreation: string;
  justificatifPresent: boolean;
  justificatifNomFichier: string | null;
  justificatifTypeContenu: string | null;
  justificatifUrl: string | null;
}

export interface BergerieAdmin {
  id: number;
  nom: string;
  code: string | null;
  adresse: string | null;
  region: string | null;
  superficieTotale: number | null;
  eleveurId: number | null;
  eleveurNomComplet: string | null;
  eleveurEmail: string | null;
  eleveurTelephone: string | null;
  nombreZones: number;
  nombreAnimaux: number;
  zones: string[];
}

export interface EleveurAdmin {
  id: number;
  nom: string;
  prenom: string;
  email: string;
  telephone: string;
  numEleveur: string;
  codeConnexion: string | null;
}
