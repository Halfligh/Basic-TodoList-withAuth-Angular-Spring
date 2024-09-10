// src/app/interceptors/jwt.interceptor.ts (modifié)
import { Injectable } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable()
export class JwtInterceptor implements HttpInterceptor {
  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    // Récupère le token JWT du localStorage
    const token = localStorage.getItem('token');

    // Ajoute des logs pour voir le token et la requête interceptée
    console.log('Intercepting request:', request.url);
    if (token) {
      console.log('Token présent:', token);
      request = request.clone({
        setHeaders: {
          Authorization: `Bearer ${token}`,
        },
      });
    } else {
      console.warn('Aucun token trouvé dans le localStorage');
    }

    // Passe la requête au prochain gestionnaire
    return next.handle(request);
  }
}
