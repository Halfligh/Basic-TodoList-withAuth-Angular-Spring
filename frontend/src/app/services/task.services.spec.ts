// task.service.spec.ts
import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { TaskService, Task } from '../services/task.services'; // Vérifiez le chemin correct vers votre service

describe('TaskService', () => {
  let service: TaskService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [TaskService],
    });

    service = TestBed.inject(TaskService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should fetch tasks', () => {
    const mockTasks: Task[] = [
      { id: 1, text: 'Task 1', completed: false },
      { id: 2, text: 'Task 2', completed: true },
    ];

    service.getTasks().subscribe((tasks: Task[]) => {
      expect(tasks.length).toBe(2);
      expect(tasks).toEqual(mockTasks);
    });

    const req = httpMock.expectOne('http://localhost:8080/api/tasks');
    expect(req.request.method).toBe('GET');
    req.flush(mockTasks);
  });

  it('should create a task', () => {
    const newTask: Task = { text: 'New Task', completed: false };

    service.createTask(newTask).subscribe((task: Task) => {
      expect(task).toEqual({ ...newTask, id: 1 });
    });

    const req = httpMock.expectOne('http://localhost:8080/api/tasks');
    expect(req.request.method).toBe('POST');
    req.flush({ ...newTask, id: 1 });
  });

  it('should delete a task', () => {
    service.deleteTask(1).subscribe((response: void) => {
      expect(response).toBeUndefined();
    });

    const req = httpMock.expectOne('http://localhost:8080/api/tasks/1');
    expect(req.request.method).toBe('DELETE');
    req.flush(null);
  });
});
