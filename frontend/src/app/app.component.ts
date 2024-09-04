// app.component.ts
import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthService } from './services/auth.service';
import { RouterOutlet } from '@angular/router';
import { LoginComponent } from './login/login.component';
import { LogoutComponent } from './logout/logout.component';
import { TodoListComponent } from './todo-list/todo-list.component'; // Import du TodoListComponent

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
  animationEnded = false; // Variable pour suivre la fin de l'animation

  constructor(public authService: AuthService) {
    // S'abonner à l'état de connexion
    this.authService.isAuthenticated$.subscribe((isLoggedIn) => {
      this.isAuthenticated = isLoggedIn;
      // Réinitialise l'état de l'animation lorsqu'on se connecte
      if (isLoggedIn) {
        this.animationEnded = false;
      }
    });
  }

  // Méthode appelée à la fin de l'animation
  onAnimationEnd() {
    this.animationEnded = true;
  }

  // Méthode de déconnexion (peut être utilisée pour réinitialiser l'état)
  onLogout() {
    this.authService.logout();
    this.isAuthenticated = false;
    this.animationEnded = false;
  }
}
