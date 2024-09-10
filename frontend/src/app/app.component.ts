// app.component.ts
import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthService } from './services/auth.service';
import { LoginComponent } from './login/login.component';
import { LogoutComponent } from './logout/logout.component';
import { TodoListComponent } from './todo-list/todo-list.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, LoginComponent, LogoutComponent, TodoListComponent],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
})
export class AppComponent {
  title = 'angular-to-do-list';
  isAuthenticated = false;
  animationEnded = false;
  currentUser: string | null = null; // Ajout de la variable pour stocker l'utilisateur actuel

  constructor(public authService: AuthService) {
    // S'abonner à l'état de connexion
    this.authService.isAuthenticated$.subscribe((isLoggedIn) => {
      this.isAuthenticated = isLoggedIn;
      if (isLoggedIn) {
        this.animationEnded = false;
      }
    });

    // S'abonner pour obtenir l'utilisateur actuel depuis AuthService
    this.authService.currentUser$.subscribe((user) => {
      this.currentUser = user;
    });
  }

  // Vérifie si l'utilisateur est admin
  isAdmin(): boolean {
    return this.currentUser === 'admin';
  }

  // Méthode appelée à la fin de l'animation normale
  onAnimationEnd() {
    this.animationEnded = true;
  }
}
