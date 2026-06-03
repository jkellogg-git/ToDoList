import { Routes } from '@angular/router';

import { authGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  {
    path: '',
    loadChildren: () => import('./pages/public/public.routes').then(m => m.PUBLIC_ROUTES)
  },

  {
    path: 'app',
    canActivate: [authGuard], // Apply the guard here
    loadChildren: () => import('./pages/authenticated/authenticated.routes').then(m => m.AUTHENTICATED_ROUTES)
  },

  { path: '**', redirectTo: '' }
];
