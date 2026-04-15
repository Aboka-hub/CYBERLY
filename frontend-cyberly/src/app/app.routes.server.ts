import { RenderMode, ServerRoute } from '@angular/ssr';

export const serverRoutes: ServerRoute[] = [
  { path: 'login',         renderMode: RenderMode.Prerender },
  { path: 'register',      renderMode: RenderMode.Prerender },
  { path: 'dashboard',     renderMode: RenderMode.Client },
  { path: 'events',        renderMode: RenderMode.Client },
  { path: 'risk',          renderMode: RenderMode.Client },
  { path: 'profile',       renderMode: RenderMode.Client },
  { path: 'subscriptions', renderMode: RenderMode.Client },
  { path: '**',            renderMode: RenderMode.Client }
];
