import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpErrorResponse } from '@angular/common/http';
import { Observable, catchError, throwError, tap } from 'rxjs';

export interface Task {
  id?: number;
  text: string;
  completed: boolean;
  addByAdmin: boolean;
}

@Injectable({
  providedIn: 'root',
})
export class TaskService {
  private apiUrl = 'http://localhost:8080/api/tasks'; // URL de l'API backend

  constructor(private http: HttpClient) {}

    // Créer une nouvelle tâche pour l'utilisateur courant
    createTask(task: Task): Observable<Task> {
      const token = localStorage.getItem('token');
      const headers = new HttpHeaders({
        'Authorization': `Bearer ${token}`,
      });
  
      // Appel à l'API standard pour créer une tâche pour l'utilisateur actuel
      return this.http.post<Task>(`${this.apiUrl}`, task, { headers }).pipe(
        catchError((error: HttpErrorResponse) => {
          console.error('Erreur lors de la création de la tâche pour l\'utilisateur courant :', error.message);
          return throwError(() => new Error('Erreur lors de la création de la tâche'));
        })
      );
    }
  
    // Créer une nouvelle tâche pour un utilisateur spécifique (utilisé par les administrateurs)
    createTaskForUser(task: Task, username: string): Observable<Task> {
      const token = localStorage.getItem('token');
      const headers = new HttpHeaders({
        'Authorization': `Bearer ${token}`,
      });
  
      // Appel à l'API pour créer une tâche pour un autre utilisateur (requiert le rôle ADMIN)
      return this.http.post<Task>(`${this.apiUrl}/${username}/create`, task, { headers }).pipe(
        catchError((error: HttpErrorResponse) => {
          console.error('Erreur lors de la création de la tâche pour un autre utilisateur :', error.message);
          return throwError(() => new Error('Erreur lors de la création de la tâche pour un autre utilisateur'));
        })
      );
    }

  // Récupérer toutes les tâches de l'utilisateur courant
  getTasks(): Observable<Task[]> {
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`,
    });

    return this.http.get<Task[]>(this.apiUrl, { headers }).pipe(
      catchError((error: HttpErrorResponse) => {
        console.error('Erreur lors de la récupération des tâches:', error);
        throw error;
      })
    );
  }

  // Mettre à jour le statut complété d'une tâche
  updateTaskStatus(id: number, completed: boolean): Observable<Task> {
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`,
    });

    return this.http.put<Task>(`${this.apiUrl}/${id}/status`, completed, { headers }).pipe(
      catchError((error: HttpErrorResponse) => {
        console.error('Erreur lors de la mise à jour du statut de la tâche:', error);
        throw error;
      })
    );
  }

  // Supprimer une tâche par son ID
  deleteTask(id: number): Observable<void> {
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`,
    });

    return this.http.delete<void>(`${this.apiUrl}/${id}`, { headers }).pipe(
      catchError((error: HttpErrorResponse) => {
        console.error('Erreur lors de la suppression de la tâche:', error);
        throw error;
      })
    );
  }

  // Récupérer toutes les tâches regroupées par utilisateur pour les administrateurs
  getAllTasksForAdmin(): Observable<{ [username: string]: Task[] }> {
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`,
    });

    console.log('Tentative de récupération des tâches pour l\'admin avec le token:', token);
    console.log('En-têtes envoyés:', headers);

    return this.http.get<{ [username: string]: Task[] }>(`${this.apiUrl}/all`, { headers }).pipe(
      tap((response) => {
        // Log des données résultantes
        console.log('Données récupérées:', response);
      }),
      catchError((error: HttpErrorResponse) => {
        console.error('Erreur lors de la récupération des tâches des utilisateurs pour l\'admin:', error);
        if (error.status === 403) {
          console.error('Accès interdit. Vérifiez les permissions et le token.');
        }
        throw error;
      })
    );
  }
}
