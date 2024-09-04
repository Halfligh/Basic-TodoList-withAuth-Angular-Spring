// todo-list.component.ts
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms'; // Import de FormsModule
import { CommonModule } from '@angular/common'; // Import de CommonModule

interface Task {
  text: string;
  completed: boolean;
}

@Component({
  selector: 'app-todo-list',
  standalone: true,
  imports: [CommonModule, FormsModule], // Ajout de FormsModule et CommonModule
  templateUrl: './todo-list.component.html',
  styleUrls: ['./todo-list.component.css'],
})
export class TodoListComponent {
  tasks: Task[] = [];
  newTask: string = '';

  // Ajouter une tâche à la liste
  addTask() {
    if (this.newTask.trim()) {
      this.tasks.push({ text: this.newTask, completed: false });
      this.newTask = '';
    }
  }

  // Supprimer une tâche de la liste
  deleteTask(index: number) {
    this.tasks.splice(index, 1);
  }
}
