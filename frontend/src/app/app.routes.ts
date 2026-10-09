import { Routes } from '@angular/router';
import { Landing } from './features/landing/landing';
import { Login } from './features/login/login';
import { Inscription } from './features/inscription/inscription';
import { Shell } from './features/shell/shell';
import { DashboardEleveur } from './features/dashboard-eleveur/dashboard-eleveur';
import { DashboardVeterinaire } from './features/dashboard-veterinaire/dashboard-veterinaire';
import { Admin } from './features/admin/admin';
import { Animaux } from './features/animaux/animaux';
import { Alertes } from './features/alertes/alertes';
import { DonneesEnv } from './features/donnees-env/donnees-env';
import { Veterinaires } from './features/veterinaires/veterinaires';
import { Localisation } from './features/localisation/localisation';
import { authGuard, roleGuard, localisationGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  { path: '', pathMatch: 'full', component: Landing },
  { path: 'login', component: Login },
  { path: 'inscription', component: Inscription },
  {
    path: '',
    component: Shell,
    canActivate: [authGuard],
    children: [
      {
        path: 'dashboard/eleveur',
        component: DashboardEleveur,
        canActivate: [roleGuard],
        data: { role: 'ELEVEUR' }
      },
      {
        path: 'dashboard/veterinaire',
        component: DashboardVeterinaire,
        canActivate: [roleGuard],
        data: { role: 'VETERINAIRE' }
      },
      {
        path: 'admin',
        component: Admin,
        canActivate: [roleGuard],
        data: { role: 'ADMIN' }
      },
      { path: 'animaux', component: Animaux },
      { path: 'alertes', component: Alertes },
      { path: 'donnees-env', component: DonneesEnv },
      { path: 'veterinaires', component: Veterinaires },
      { path: 'localisation', component: Localisation, canActivate: [localisationGuard] }
    ]
  },
  { path: '**', redirectTo: 'login' }
];
