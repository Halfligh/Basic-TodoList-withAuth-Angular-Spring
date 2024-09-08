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

  constructor(public authService: AuthService) {
    // S'abonner à l'état de connexion
    this.authService.isAuthenticated$.subscribe((isLoggedIn) => {
      this.isAuthenticated = isLoggedIn;
      if (isLoggedIn) {
        this.animationEnded = false;
      }
    });
  }

  // Méthode appelée à la fin de l'animation normale
  onAnimationEnd() {
    this.animationEnded = true;
  }
}
