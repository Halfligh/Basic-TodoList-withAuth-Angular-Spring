import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { catchError, map, tap } from 'rxjs/operators';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private isAuthenticatedSubject = new BehaviorSubject<boolean>(false);
  isAuthenticated$ = this.isAuthenticatedSubject.asObservable();
  private currentUserSubject = new BehaviorSubject<string | null>(null);
  currentUser$ = this.currentUserSubject.asObservable();
  private rolesSubject = new BehaviorSubject<string[]>([]);
  roles$ = this.rolesSubject.asObservable(); // Observable pour les rôles

  private apiUrl = 'http://localhost:8080/api/auth'; // Assurez-vous que c'est l'URL correcte de votre backend

  constructor(private http: HttpClient) {
    if (this.isClient()) {
      const token = localStorage.getItem('token');
      const isTokenValid = token && this.isTokenValid(token);

      if (isTokenValid) {
        this.isAuthenticatedSubject.next(true);
        this.currentUserSubject.next(this.getUsernameFromToken(token));
        this.rolesSubject.next(this.getRolesFromToken(token));
      } else {
        this.isAuthenticatedSubject.next(false);
        this.currentUserSubject.next(null);
        this.rolesSubject.next([]);
      }
    }
  }

  login(username: string, password: string): Observable<boolean> {
    return this.http.post<{ token: string }>(`${this.apiUrl}/login`, { username, password }).pipe(
      tap(response => {
        if (response.token && this.isClient()) {
          localStorage.setItem('token', response.token); // Stocke le token pour les requêtes futures
          this.isAuthenticatedSubject.next(true); // Met à jour l'état d'authentification
          this.rolesSubject.next(this.getRolesFromToken(response.token)); // Met à jour les rôles
          this.currentUserSubject.next(this.getUsernameFromToken(response.token)); // Met à jour l'utilisateur courant
        }
      }),
      map(response => !!response.token),
      catchError(error => {
        console.error('Échec de la connexion', error);
        this.isAuthenticatedSubject.next(false);
        this.currentUserSubject.next(null);
        this.rolesSubject.next([]);
        return of(false);
      })
    );
  }

  logout() {
    if (this.isClient()) {
      localStorage.removeItem('token');
    }
    this.isAuthenticatedSubject.next(false);
    this.currentUserSubject.next(null);
    this.rolesSubject.next([]);
  }

  isLoggedIn(): boolean {
    return this.isClient() && !!localStorage.getItem('token');
  }

  // Méthode pour vérifier si le token est valide
  private isTokenValid(token: string): boolean {
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      const expiration = payload.exp;
      if (expiration && Date.now() >= expiration * 1000) {
        console.warn('Token expiré');
        return false; // Token expiré
      }
      return true; // Token valide
    } catch (error) {
      console.error('Erreur lors de la validation du token JWT', error);
      return false;
    }
  }

  // Méthode pour extraire le nom d'utilisateur du token JWT
  private getUsernameFromToken(token?: string): string | null {
    const jwtToken = token || (this.isClient() ? localStorage.getItem('token') : null);
    if (!jwtToken || jwtToken.split('.').length < 3) {
      console.error('Token JWT invalide ou vide');
      return null;
    }

    try {
      const payload = JSON.parse(atob(jwtToken.split('.')[1]));
      return payload.sub || null;
    } catch (error) {
      console.error('Erreur lors du parsing du token JWT', error);
      return null;
    }
  }

  // Méthode pour extraire les rôles du token JWT
  private getRolesFromToken(token?: string): string[] {
    const jwtToken = token || (this.isClient() ? localStorage.getItem('token') : null);
    if (!jwtToken || jwtToken.split('.').length < 3) {
      console.error('Token JWT invalide ou vide');
      return [];
    }

    try {
      const payload = JSON.parse(atob(jwtToken.split('.')[1]));
      return payload.roles ? payload.roles.split(',') : [];
    } catch (error) {
      console.error('Erreur lors du parsing du token JWT', error);
      return [];
    }
  }

  // Vérifie si l'environnement est côté client (navigateur)
  private isClient(): boolean {
    return typeof window !== 'undefined' && typeof localStorage !== 'undefined';
  }

  // Méthode pour vérifier si l'utilisateur est un admin
  isAdmin(): boolean {
    const roles = this.rolesSubject.getValue();
    return roles.includes('ROLE_ADMIN'); // Vérifie si le rôle d'administrateur est présent
  }
}
