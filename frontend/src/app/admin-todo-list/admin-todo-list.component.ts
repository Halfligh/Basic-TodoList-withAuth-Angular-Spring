// src/app/admin-todo-list/admin-todo-list.component.ts
import { Component, Input, OnInit } from '@angular/core';
import { TaskService, Task } from '../services/task.services';

@Component({
  selector: 'app-admin-todo-list',
  standalone: true,
  templateUrl: './admin-todo-list.component.html',
  styleUrls: ['./admin-todo-list.component.css'],
})
export class AdminTodoListComponent implements OnInit {
  tasksGroupedByUser: { [username: string]: Task[] } = {};

  constructor(private taskService: TaskService) {}

  ngOnInit(): void {
    // Charger toutes les tâches pour chaque utilisateur
    this.taskService.getAllTasksForAdmin().subscribe((data) => {
      this.tasksGroupedByUser = data;
    });
  }

  // Méthode pour obtenir les noms d'utilisateurs
  getUsernames(): string[] {
    return Object.keys(this.tasksGroupedByUser);
  }
}
