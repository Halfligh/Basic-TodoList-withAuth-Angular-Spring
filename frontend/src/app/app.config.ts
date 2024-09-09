
// app.config.ts
import { ApplicationConfig, provideZoneChangeDetection } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideHttpClient, withInterceptorsFromDi, HTTP_INTERCEPTORS } from '@angular/common/http'; // Importez HTTP_INTERCEPTORS ici
import { provideClientHydration } from '@angular/platform-browser';
import { routes } from './app.routes';
import { JwtInterceptor } from './interceptors/jwt.interceptor';

export const appConfig: ApplicationConfig = {
  providers: [
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes),
    provideClientHydration(),
    provideHttpClient(withInterceptorsFromDi()), // Fournir HttpClient avec les intercepteurs
    { provide: HTTP_INTERCEPTORS, useClass: JwtInterceptor, multi: true }, // Enregistrer l'intercepteur
  ],
};
