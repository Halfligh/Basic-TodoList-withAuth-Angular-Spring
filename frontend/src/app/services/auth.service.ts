// auth.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { catchError, map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private isAuthenticatedSubject = new BehaviorSubject<boolean>(false);
  isAuthenticated$ = this.isAuthenticatedSubject.asObservable();

  private apiUrl = 'http://localhost:8080/api/auth'; // Assurez-vous que c'est l'URL correcte de votre backend

  constructor(private http: HttpClient) {}

  login(username: string, password: string): Observable<boolean> {
    return this.http.post<{ token: string }>(`${this.apiUrl}/login`, { username, password }).pipe(
      map(response => {
        localStorage.setItem('token', response.token); // Stocke le token pour les requêtes futures
        this.isAuthenticatedSubject.next(true);
        return true; // Connexion réussie
      }),
      catchError(error => {
        console.error('Échec de la connexion', error);
        this.isAuthenticatedSubject.next(false);
        return of(false); // Connexion échouée
      })
    );
  }

  logout() {
    localStorage.removeItem('token');
    this.isAuthenticatedSubject.next(false);
  }

  isLoggedIn(): boolean {
    return !!localStorage.getItem('token');
  }
}
