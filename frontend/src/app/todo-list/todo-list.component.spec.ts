// Corrected `todo-list.component.spec.ts`
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { TodoListComponent } from './todo-list.component';
import { TaskService, Task } from '../services/task.services';
import { of } from 'rxjs';
import { By } from '@angular/platform-browser';

describe('TodoListComponent', () => {
  let component: TodoListComponent;
  let fixture: ComponentFixture<TodoListComponent>;
  let taskService: jasmine.SpyObj<TaskService>;

  beforeEach(async () => {
    const taskServiceSpy = jasmine.createSpyObj('TaskService', ['getTasks', 'createTask', 'createTaskForUser', 'updateTaskStatus', 'deleteTask']);

    await TestBed.configureTestingModule({
      imports: [TodoListComponent],
      providers: [{ provide: TaskService, useValue: taskServiceSpy }],
    }).compileComponents();

    fixture = TestBed.createComponent(TodoListComponent);
    component = fixture.componentInstance;
    taskService = TestBed.inject(TaskService) as jasmine.SpyObj<TaskService>;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load tasks on init if tasks are not provided', () => {
    const mockTasks: Task[] = [{ id: 1, text: 'Test Task', completed: false, addByAdmin: false }];
    taskService.getTasks.and.returnValue(of(mockTasks));

    component.ngOnInit();
    expect(taskService.getTasks).toHaveBeenCalled();
    expect(component.tasks).toEqual(mockTasks);
  });

  it('should add a task for the current user', () => {
    component.newTask = 'New Task';
    component.currentUser = 'user1';
    component.username = 'user1';
    const newTask: Task = { text: 'New Task', completed: false, addByAdmin: false };
    const createdTask: Task = { ...newTask, id: 2 };
    
    taskService.createTask.and.returnValue(of(createdTask));

    component.addTask();
    expect(taskService.createTask).toHaveBeenCalledWith(newTask);
    expect(component.tasks).toContain(createdTask);
  });

  it('should add a task for another user when admin', () => {
    component.newTask = 'Admin Task';
    component.currentUser = 'adminUser';
    component.username = 'otherUser';
    component.isAdmin = true;
    const newTask: Task = { text: 'Admin Task', completed: false, addByAdmin: true };
    const createdTask: Task = { ...newTask, id: 3 };

    taskService.createTaskForUser.and.returnValue(of(createdTask));

    component.addTask();
    expect(taskService.createTaskForUser).toHaveBeenCalledWith(newTask, 'otherUser');
    expect(component.tasks).toContain(createdTask);
  });

  it('should update the task status', () => {
    const task: Task = { id: 1, text: 'Task 1', completed: false, addByAdmin: false };
    const updatedTask: Task = { ...task, completed: true };

    taskService.updateTaskStatus.and.returnValue(of(updatedTask));

    component.toggleTaskStatus(task);
    // Utilisation de `!` pour indiquer que l'ID n'est pas `undefined`.
    expect(taskService.updateTaskStatus).toHaveBeenCalledWith(task.id!, task.completed);
  });

  it('should delete a task', () => {
    const task: Task = { id: 1, text: 'Task 1', completed: false, addByAdmin: false };
    component.tasks = [task];
    
    taskService.deleteTask.and.returnValue(of(undefined));

    component.deleteTask(0, task.id);
    // Utilisation de `!` pour indiquer que l'ID n'est pas `undefined`.
    expect(taskService.deleteTask).toHaveBeenCalledWith(task.id!);
    expect(component.tasks.length).toBe(0);
  });

  it('should not add a task if the new task input is empty', () => {
    component.newTask = '';
    component.addTask();
    expect(taskService.createTask).not.toHaveBeenCalled();
  });
});
