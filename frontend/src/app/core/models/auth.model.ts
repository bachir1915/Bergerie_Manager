export type Role = 'ELEVEUR' | 'VETERINAIRE' | 'ADMIN';

export interface LoginRequest {
  email: string;
  code: string;
}

export interface UserInfo {
  id: number;
  role: Role;
  nom: string;
  prenom: string;
  email: string;
  statutValidation?: string | null;
}
