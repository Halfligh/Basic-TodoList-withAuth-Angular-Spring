// auth.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { catchError, map, tap } from 'rxjs/operators';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private isAuthenticatedSubject = new BehaviorSubject<boolean>(this.isLoggedIn());
  isAuthenticated$ = this.isAuthenticatedSubject.asObservable();
  private currentUserSubject = new BehaviorSubject<string | null>(this.getUsernameFromToken());
  currentUser$ = this.currentUserSubject.asObservable();

  private apiUrl = 'http://localhost:8080/api/auth'; // Assurez-vous que c'est l'URL correcte de votre backend

  constructor(private http: HttpClient) {}

  login(username: string, password: string): Observable<boolean> {
    return this.http.post<{ token: string }>(`${this.apiUrl}/login`, { username, password }).pipe(
      tap(response => {
        if (response.token) {
          this.setToken(response.token); // Utilise une méthode sécurisée pour stocker le token
          this.isAuthenticatedSubject.next(true); // Met à jour l'état d'authentification
          this.currentUserSubject.next(this.getUsernameFromToken(response.token)); // Récupère le nom d'utilisateur
        }
      }),
      map(response => !!response.token), // Retourne true si le token est présent
      catchError(error => {
        console.error('Échec de la connexion', error);
        this.isAuthenticatedSubject.next(false); // Mise à jour en cas d'échec de connexion
        this.currentUserSubject.next(null); // Réinitialise l'utilisateur courant
        return of(false); // Connexion échouée
      })
    );
  }

  logout() {
    this.removeToken(); // Utilise une méthode sécurisée pour supprimer le token
    this.isAuthenticatedSubject.next(false); // Met à jour l'état d'authentification
    this.currentUserSubject.next(null); // Réinitialise l'utilisateur courant
  }

  isLoggedIn(): boolean {
    return !!this.getToken(); // Utilise une méthode sécurisée pour vérifier la présence du token
  }

  // Méthode pour extraire le nom d'utilisateur du token JWT
  private getUsernameFromToken(token?: string): string | null {
    const jwtToken = token || this.getToken();
    if (!jwtToken) return null;

    // Décodage du token JWT pour extraire le payload
    try {
      const payload = JSON.parse(atob(jwtToken.split('.')[1]));
      return payload.sub || null; // Retourne le champ 'sub' qui contient souvent le nom d'utilisateur
    } catch (error) {
      console.error('Erreur lors du décodage du token JWT', error);
      return null;
    }
  }

  // Méthode sécurisée pour obtenir le token depuis localStorage
  private getToken(): string | null {
    if (this.isBrowserEnvironment() && this.isLocalStorageAvailable()) {
      return localStorage.getItem('token');
    }
    return null;
  }

  // Méthode sécurisée pour définir le token dans localStorage
  private setToken(token: string): void {
    if (this.isBrowserEnvironment() && this.isLocalStorageAvailable()) {
      localStorage.setItem('token', token);
    }
  }

  // Méthode sécurisée pour supprimer le token de localStorage
  private removeToken(): void {
    if (this.isBrowserEnvironment() && this.isLocalStorageAvailable()) {
      localStorage.removeItem('token');
    }
  }

  // Vérifie si on est dans un environnement de navigateur
  private isBrowserEnvironment(): boolean {
    return typeof window !== 'undefined' && typeof localStorage !== 'undefined';
  }

  // Méthode pour vérifier si localStorage est disponible
  private isLocalStorageAvailable(): boolean {
    try {
      const test = '__localStorageTest__';
      localStorage.setItem(test, test);
      localStorage.removeItem(test);
      return true;
    } catch (error) {
      console.warn('localStorage n\'est pas disponible.', error);
      return false;
    }
  }
}
