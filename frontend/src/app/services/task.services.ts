import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpErrorResponse } from '@angular/common/http';
import { Observable, catchError } from 'rxjs';

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
  
    const token = localStorage.getItem('token');
    // Log pour vérifier la présence du token avant la requête
    console.log('Tentative de création d\'une tâche avec le token:', token);

    const headers = new HttpHeaders({
      'Authorization': `Bearer ${localStorage.getItem('token')}`, // Assurez-vous que le token est présent ici
    });

     // Log pour afficher la tâche à envoyer
     console.log('Tâche à créer:', task);

      // Exécution de la requête HTTP avec des logs pour chaque étape
      return this.http.post<Task>(this.apiUrl, task, { headers }).pipe(
        catchError((error: HttpErrorResponse) => {
          // Log en cas d'erreur lors de la requête
          console.error('Erreur lors de la création de la tâche:', error.message);
          throw error; // Rethrow pour que l'erreur soit gérée ailleurs si nécessaire
        })
      );
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
