export interface AnimalDto {
  id: number;
  nom: string;
  race: string;
  sexe: string;
  poids: number | null;
  dateNaissance: string | null;
  statut: string | null;
  statutSante: string | null;
  numeroCapteur: string | null;
  bergerieNom: string | null;
}

export interface VeterinairePublic {
  id: number;
  nom: string;
  prenom: string;
  specialite: string | null;
  telephone: string | null;
  email: string | null;
}
