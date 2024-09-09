import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Task {
  id?: number;
  text: string;
  completed: boolean;
}

@Injectable({
  providedIn: 'root',
})
export class TaskService {
  private apiUrl = 'http://localhost:8080/api/tasks'; // URL de l'API backend

  constructor(private http: HttpClient) {}

  // Créer une nouvelle tâche
  createTask(task: Task): Observable<Task> {
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${localStorage.getItem('token')}`, // Assurez-vous que le token est présent ici
    });

    return this.http.post<Task>(this.apiUrl, task, { headers });
  }

  // Récupérer toutes les tâches
  getTasks(): Observable<Task[]> {
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${localStorage.getItem('token')}`,
    });

    return this.http.get<Task[]>(this.apiUrl, { headers });
  }

  // Supprimer une tâche par son ID
  deleteTask(id: number): Observable<void> {
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${localStorage.getItem('token')}`,
    });

    return this.http.delete<void>(`${this.apiUrl}/${id}`, { headers });
  }
}
