// app.component.ts
import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthService } from './services/auth.service';
import { RouterOutlet } from '@angular/router';
import { LoginComponent } from './login/login.component';
import { LogoutComponent } from './logout/logout.component';
import { TodoListComponent } from './todo-list/todo-list.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterOutlet, LoginComponent, LogoutComponent, TodoListComponent],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'angular-to-do-list';
  isAuthenticated = false;
  animationEnded = false;
  reverseAnimation = false;

  constructor(public authService: AuthService) {
    // S'abonner à l'état de connexion
    this.authService.isAuthenticated$.subscribe((isLoggedIn) => {
      this.isAuthenticated = isLoggedIn;
      if (isLoggedIn) {
        this.animationEnded = false;
        this.reverseAnimation = false;
      }
    });
  }

  // Méthode appelée à la fin de l'animation normale
  onAnimationEnd() {
    this.animationEnded = true;
  }

  // Méthode pour déclencher l'animation de déconnexion
  onLogout() {
    this.reverseAnimation = true; // Déclenche l'animation inverse
  }

  // Méthode appelée à la fin de l'animation inverse
  onReverseAnimationEnd() {
    if (this.reverseAnimation) {
      this.authService.logout(); // Déclenche la déconnexion une fois l'animation terminée
      this.isAuthenticated = false; // Réinitialise l'état
      this.animationEnded = false;
      this.reverseAnimation = false;
    }
  }
}
