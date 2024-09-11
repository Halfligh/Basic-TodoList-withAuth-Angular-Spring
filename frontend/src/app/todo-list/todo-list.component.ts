// todo-list.component.ts
import { Component, Input, OnInit } from '@angular/core';
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
export class TodoListComponent implements OnInit {
  @Input() tasks: Task[] = []; // Reçoit les tâches en entrée depuis le parent
  @Input() title: string = 'ToDoList'; // Pour afficher le titre avec le nom d'utilisateur
  newTask: string = '';

  constructor(private taskService: TaskService) {}

  ngOnInit() {
    if (!this.tasks.length) {
      // Charger les tâches si elles ne sont pas fournies (pour un utilisateur normal)
      this.loadTasks();
    }
  }

  loadTasks() {
    this.taskService.getTasks().subscribe((tasks) => {
      this.tasks = tasks;
    });
  }

  addTask() {
    if (this.newTask.trim()) {
      const newTask: Task = { text: this.newTask, completed: false };
      this.taskService.createTask(newTask).subscribe((task) => {
        if (task && task.id) {
          this.tasks.push(task);
        } else {
          console.error('Erreur : la tâche n’a pas d’ID');
        }
        this.newTask = '';
      });
    }
  }

  toggleTaskStatus(task: Task) {
    if (task.id !== undefined) {
      this.taskService.updateTaskStatus(task.id, task.completed).subscribe(
        (updatedTask) => {
          console.log(`Tâche mise à jour : ${updatedTask.text} - Complétée : ${updatedTask.completed}`);
        },
        (error) => {
          console.error('Erreur lors de la mise à jour de la tâche', error);
          task.completed = !task.completed; // Révertir l'état en cas d'erreur
        }
      );
    } else {
      console.error('Erreur : ID de la tâche est indéfini');
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
