// task.service.spec.ts
import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { TaskService, Task } from '../services/task.services';

describe('TaskService', () => {
  let service: TaskService;
  let httpMock: HttpTestingController;
  const apiUrl = 'http://localhost:8080/api/tasks';

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        TaskService,
      ],
    });

    service = TestBed.inject(TaskService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify(); // Vérifie qu'aucune requête non traitée ne persiste
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should fetch tasks', () => {
    const mockTasks: Task[] = [
      { id: 1, text: 'Task 1', completed: false, addByAdmin: false },
      { id: 2, text: 'Task 2', completed: true, addByAdmin: false },
    ];

    service.getTasks().subscribe((tasks: Task[]) => {
      expect(tasks.length).toBe(2);
      expect(tasks).toEqual(mockTasks);
    });

    // Capture et simule la requête GET
    const req = httpMock.expectOne(apiUrl);
    expect(req.request.method).toBe('GET');
    expect(req.request.headers.get('Authorization')).toContain('Bearer');
    req.flush(mockTasks);
  });

  it('should create a task', () => {
    const newTask: Task = { text: 'New Task', completed: false, addByAdmin: false };

    service.createTask(newTask).subscribe((task: Task) => {
      expect(task).toEqual({ ...newTask, id: 1 });
    });

    // Capture et simule la requête POST
    const req = httpMock.expectOne(apiUrl);
    expect(req.request.method).toBe('POST');
    expect(req.request.headers.get('Authorization')).toContain('Bearer');
    req.flush({ ...newTask, id: 1 });
  });

  it('should create a task for a specific user (admin)', () => {
    const newTask: Task = { text: 'Admin Task', completed: false, addByAdmin: true };
    const username = 'testUser';

    service.createTaskForUser(newTask, username).subscribe((task: Task) => {
      expect(task).toEqual({ ...newTask, id: 2 });
    });

    // Capture et simule la requête POST
    const req = httpMock.expectOne(`${apiUrl}/${username}/create`);
    expect(req.request.method).toBe('POST');
    expect(req.request.headers.get('Authorization')).toContain('Bearer');
    req.flush({ ...newTask, id: 2 });
  });

  it('should update task status', () => {
    const taskId = 1;
    const updatedTask: Task = { id: taskId, text: 'Task 1', completed: true, addByAdmin: false };

    service.updateTaskStatus(taskId, true).subscribe((task: Task) => {
      expect(task).toEqual(updatedTask);
    });

    // Capture et simule la requête PUT
    const req = httpMock.expectOne(`${apiUrl}/${taskId}/status`);
    expect(req.request.method).toBe('PUT');
    expect(req.request.headers.get('Authorization')).toContain('Bearer');
    req.flush(updatedTask);
  });

  it('should delete a task', () => {
    const taskId = 1;

    service.deleteTask(taskId).subscribe((response: void | null) => {
      expect(response).toBeNull();
    });

    // Capture et simule la requête DELETE
    const req = httpMock.expectOne(`${apiUrl}/${taskId}`);
    expect(req.request.method).toBe('DELETE');
    expect(req.request.headers.get('Authorization')).toContain('Bearer');
    req.flush(null);
  });

  it('should fetch all tasks for admin', () => {
    const mockResponse = {
      user1: [{ id: 1, text: 'Task 1', completed: false, addByAdmin: true }],
      user2: [{ id: 2, text: 'Task 2', completed: true, addByAdmin: true }],
    };

    service.getAllTasksForAdmin().subscribe((tasks) => {
      expect(tasks).toEqual(mockResponse);
    });

    // Capture et simule la requête GET pour les admins
    const req = httpMock.expectOne(`${apiUrl}/all`);
    expect(req.request.method).toBe('GET');
    expect(req.request.headers.get('Authorization')).toContain('Bearer');
    req.flush(mockResponse);
  });
});
