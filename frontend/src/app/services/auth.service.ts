// auth.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject } from 'rxjs';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private isAuthenticatedSubject = new BehaviorSubject<boolean>(false);
  isAuthenticated$ = this.isAuthenticatedSubject.asObservable();

  private apiUrl = 'http://localhost:8080/api/auth'; // Assurez-vous que ce soit l'URL correcte de votre backend

  constructor(private http: HttpClient, private router: Router) {}

  login(username: string, password: string) {
    this.http.post(`${this.apiUrl}/login`, { username, password })
      .subscribe((token: any) => {
        localStorage.setItem('token', token); // Stockez le token pour les requêtes futures
        this.isAuthenticatedSubject.next(true);
        this.router.navigate(['/']); // Redirigez vers la page d'accueil après la connexion
      }, error => {
        console.error('Login failed', error);
        this.isAuthenticatedSubject.next(false);
      });
  }

  logout() {
    localStorage.removeItem('token');
    this.isAuthenticatedSubject.next(false);
    this.router.navigate(['/login']);
  }

  isLoggedIn(): boolean {
    return !!localStorage.getItem('token');
  }
}
