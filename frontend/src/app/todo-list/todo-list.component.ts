import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { TaskService, Task } from '../services/task.services'; // Assurez-vous que le chemin est correct

@Component({
  selector: 'app-todo-list',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './todo-list.component.html',
  styleUrls: ['./todo-list.component.css'],
})
export class TodoListComponent {
  tasks: Task[] = [];
  newTask: string = '';

  constructor(private taskService: TaskService) {
    this.loadTasks();
  }

  loadTasks() {
    this.taskService.getTasks().subscribe(tasks => {
      this.tasks = tasks;
    });
  }

  addTask() {
    if (this.newTask.trim()) {
      const newTask: Task = { text: this.newTask, completed: false };
      this.taskService.createTask(newTask).subscribe(task => {
        if (task && task.id) {
          this.tasks.push(task); // Ajoutez la tâche à la liste seulement si l'ID est défini
        } else {
          console.error('Erreur : la tâche n’a pas d’ID');
        }
        this.newTask = '';
      });
    }
  }

  deleteTask(index: number, taskId?: number) {
    if (taskId !== undefined) {
      this.taskService.deleteTask(taskId).subscribe(() => {
        this.tasks.splice(index, 1);
      });
    } else {
      console.error('Erreur : tâche non trouvée ou ID manquant');
    }
  }
}
