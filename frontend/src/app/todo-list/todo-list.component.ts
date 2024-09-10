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

  // Méthode pour vérifier si id bien présent et mettre à jour l'état de la tâche
  toggleTaskStatus(task: Task) {
    if (task.id !== undefined) {
      this.taskService.updateTaskStatus(task.id, task.completed).subscribe(
        updatedTask => {
          console.log(`Tâche mise à jour : ${updatedTask.text} - Complétée : ${updatedTask.completed}`);
        },
        error => {
          console.error('Erreur lors de la mise à jour de la tâche', error);
          // Revertir l'état si la mise à jour échoue
          task.completed = !task.completed;
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
