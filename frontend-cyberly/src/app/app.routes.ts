import { Routes } from '@angular/router';
import { authGuard } from './pages/guards/auth.guard';
import { LayoutComponent } from './layout/layout.component';

export const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: 'login',    loadComponent: () => import('./pages/login/login.component').then(m => m.LoginComponent) },
  { path: 'register', loadComponent: () => import('./pages/register/register.component').then(m => m.RegisterComponent) },
  {
    path: '',
    component: LayoutComponent,
    canActivate: [authGuard],
    children: [
      { path: 'dashboard',     loadComponent: () => import('./pages/dashboard/dashboard.component').then(m => m.DashboardComponent) },
      { path: 'events',        loadComponent: () => import('./pages/events/events.component').then(m => m.EventsComponent) },
      { path: 'risk',          loadComponent: () => import('./pages/risk/risk.component').then(m => m.RiskComponent) },
      { path: 'profile',       loadComponent: () => import('./pages/profile/profile.component').then(m => m.ProfileComponent) },
      { path: 'subscriptions', loadComponent: () => import('./pages/subscriptions/subscriptions.component').then(m => m.SubscriptionsComponent) },
    ]
  },
  { path: '**', redirectTo: 'login' }
];
