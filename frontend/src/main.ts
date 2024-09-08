// main.ts
import { bootstrapApplication } from '@angular/platform-browser';
import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { AppComponent } from './app/app.component'; // Chemin ajusté pour correspondre à la structure de votre projet
import { HTTP_INTERCEPTORS } from '@angular/common/http';
import { JwtInterceptor } from './app/interceptors/jwt.interceptor'; // Assurez-vous que le chemin est correct

bootstrapApplication(AppComponent, {
  providers: [
    provideHttpClient(withInterceptorsFromDi()),
    { provide: HTTP_INTERCEPTORS, useClass: JwtInterceptor, multi: true },
  ],
}).catch((err) => console.error(err));
