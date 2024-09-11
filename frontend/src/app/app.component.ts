// app.component.ts
import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthService } from './services/auth.service';
import { LoginComponent } from './login/login.component';
import { LogoutComponent } from './logout/logout.component';
import { TodoListComponent } from './todo-list/todo-list.component';
import { TaskService, Task } from './services/task.services';
import { RolePipe } from '../app/pipes/roles.pipe'; 

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, LoginComponent, LogoutComponent, TodoListComponent, RolePipe],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
})
export class AppComponent {
  title = 'angular-to-do-list';
  isAuthenticated = false;
  animationEnded = false;
  currentUser: string | null = null;
  roles: string[] = [];
  tasksGroupedByUser: { [username: string]: Task[] } = {};
  userTasks: Task[] = []; // Stocke les tâches de l'utilisateur courant

  constructor(public authService: AuthService, private taskService: TaskService) {
    this.authService.isAuthenticated$.subscribe((isLoggedIn) => {
      this.isAuthenticated = isLoggedIn;
      if (isLoggedIn) {
        this.animationEnded = false;
        this.authService.currentUser$.subscribe((user) => {
          this.currentUser = user;
          this.authService.roles$.subscribe((roles) => {
            this.roles = roles; 
            this.loadUserTasks(); // Charge les tâches de l'utilisateur courant, même pour l'admin
            if (this.isAdmin()) {
              this.loadAllTasksForAdmin(); // Charge les tâches des autres utilisateurs si admin
            }
          });
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

  // Méthode pour charger les tâches de l'utilisateur courant
  loadUserTasks() {
    this.taskService.getTasks().subscribe((tasks) => {
      this.userTasks = tasks;
      // Associer les tâches de l'utilisateur courant dans tasksGroupedByUser pour éviter la duplication
      if (this.currentUser) {
        this.tasksGroupedByUser[this.currentUser] = tasks;
      }
    });
  }

  // Méthode appelée à la fin de l'animation normale
  onAnimationEnd() {
    this.animationEnded = true;
  }
}
