// auth.service.ts
import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private isAuthenticatedSubject = new BehaviorSubject<boolean>(false);
  isAuthenticated$ = this.isAuthenticatedSubject.asObservable();

  constructor(private router: Router) {}

  login(username: string, password: string): boolean {
    // Logique fictive de connexion
    if (username === 'admin' && password === 'admin') {
      this.isAuthenticatedSubject.next(true);
      return true;
    }
    return false;
  }

  logout() {
    this.isAuthenticatedSubject.next(false);
    this.router.navigate(['/login']); // Redirige vers une page de login ou accueil
  }

  isLoggedIn(): boolean {
    return this.isAuthenticatedSubject.value;
  }
}
