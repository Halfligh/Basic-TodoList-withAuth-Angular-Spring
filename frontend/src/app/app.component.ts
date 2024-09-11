// app.component.ts
import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthService } from './services/auth.service';
import { LoginComponent } from './login/login.component';
import { LogoutComponent } from './logout/logout.component';
import { TodoListComponent } from './todo-list/todo-list.component';
import { TaskService, Task } from './services/task.services';

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
  currentUser: string | null = null;
  roles: string[] = []; // Ajoutez cette variable pour stocker les rôles
  tasksGroupedByUser: { [username: string]: Task[] } = {};

  constructor(public authService: AuthService, private taskService: TaskService) {
    this.authService.isAuthenticated$.subscribe((isLoggedIn) => {
      this.isAuthenticated = isLoggedIn;
      if (isLoggedIn) {
        this.animationEnded = false;
        this.authService.currentUser$.subscribe((user) => {
          this.currentUser = user;
          this.authService.roles$.subscribe((roles) => {
            this.roles = roles; // Mettez à jour les rôles ici
          });
          if (this.isAdmin()) {
            this.loadAllTasksForAdmin();
          }
        });
      }
    });
  }

  // Méthode pour vérifier si l'utilisateur est un admin
  isAdmin(): boolean {
    return this.roles.includes('ROLE_ADMIN');
  }

  // Méthode pour obtenir les noms d'utilisateurs
  getUsernames(): string[] {
    return Object.keys(this.tasksGroupedByUser);
  }

  // Méthode pour charger toutes les tâches si l'on est admin
  loadAllTasksForAdmin() {
    this.taskService.getAllTasksForAdmin().subscribe((data) => {
      this.tasksGroupedByUser = data;
    });
  }

  // Méthode appelée à la fin de l'animation normale
  onAnimationEnd() {
    this.animationEnded = true;
  }
}
