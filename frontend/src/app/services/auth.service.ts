// auth.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { catchError, map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private isAuthenticatedSubject = new BehaviorSubject<boolean>(this.isLoggedIn());
  isAuthenticated$ = this.isAuthenticatedSubject.asObservable();

  private apiUrl = 'http://localhost:8080/api/auth'; // Assurez-vous que c'est l'URL correcte de votre backend

  constructor(private http: HttpClient) {}
  login(username: string, password: string): Observable<boolean> {
    return this.http.post<{ token: string }>(`${this.apiUrl}/login`, { username, password }).pipe(
      map(response => {
        if (typeof window !== 'undefined' && response.token) {
          localStorage.setItem('token', response.token); // Stocke le token pour les requêtes futures
          this.isAuthenticatedSubject.next(true); // Met à jour l'état d'authentification
          return true; // Connexion réussie
        }
        return false; // Pas de token trouvé
      }),
      catchError(error => {
        console.error('Échec de la connexion', error);
        this.isAuthenticatedSubject.next(false); // Mise à jour en cas d'échec de connexion
        return of(false); // Connexion échouée
      })
    );
  }
  

  logout() {
    if (typeof window !== 'undefined') {
      localStorage.removeItem('token'); // Supprime le token du localStorage lors de la déconnexion
    }
    this.isAuthenticatedSubject.next(false); // Met à jour l'état d'authentification
  }

  isLoggedIn(): boolean {
    // Vérifie si `localStorage` est disponible
    if (typeof window === 'undefined') {
      return false;
    }
    return !!localStorage.getItem('token');
  }
}
