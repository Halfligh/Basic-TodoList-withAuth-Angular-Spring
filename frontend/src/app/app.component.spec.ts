// app.component.spec.ts
import { TestBed, ComponentFixture } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { of, BehaviorSubject } from 'rxjs';
import { AppComponent } from './app.component';
import { AuthService } from './services/auth.service';
import { TaskService } from './services/task.services';

describe('AppComponent', () => {
  let component: AppComponent;
  let fixture: ComponentFixture<AppComponent>;
  let authService: jasmine.SpyObj<AuthService>;
  let taskService: jasmine.SpyObj<TaskService>;

  // Créez des sujets pour remplacer les observables dans AuthService
  const isAuthenticatedSubject = new BehaviorSubject<boolean>(false);
  const currentUserSubject = new BehaviorSubject<string | null>(null);
  const rolesSubject = new BehaviorSubject<string[]>([]);

  beforeEach(async () => {
    // Créez un spyObj avec des méthodes spécifiques, et utilisez les sujets pour les observables
    const authSpy = jasmine.createSpyObj('AuthService', [], {
      isAuthenticated$: isAuthenticatedSubject.asObservable(),
      currentUser$: currentUserSubject.asObservable(),
      roles$: rolesSubject.asObservable(),
    });

    // Mock TaskService avec des retours d'observables simulés
    const taskSpy = jasmine.createSpyObj('TaskService', ['getTasks', 'getAllTasksForAdmin']);
    taskSpy.getTasks.and.returnValue(of([])); // Simule l'appel à getTasks
    taskSpy.getAllTasksForAdmin.and.returnValue(of({})); // Simule l'appel à getAllTasksForAdmin

    await TestBed.configureTestingModule({
      imports: [AppComponent],
      providers: [
        { provide: AuthService, useValue: authSpy },
        { provide: TaskService, useValue: taskSpy },
        provideHttpClient(),
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(AppComponent);
    component = fixture.componentInstance;
    authService = TestBed.inject(AuthService) as jasmine.SpyObj<AuthService>;
    taskService = TestBed.inject(TaskService) as jasmine.SpyObj<TaskService>;
  });

  it('should create the app', () => {
    expect(component).toBeTruthy();
  });

  it('should set isAuthenticated to true when user is logged in', () => {
    isAuthenticatedSubject.next(true);
    fixture.detectChanges();
    expect(component.isAuthenticated).toBeTrue();
  });

  it('should set the current user and roles when user logs in', () => {
    isAuthenticatedSubject.next(true);
    currentUserSubject.next('adminUser');
    rolesSubject.next(['ROLE_ADMIN']);
    fixture.detectChanges();

    expect(component.currentUser).toBe('adminUser');
    expect(component.roles).toContain('ROLE_ADMIN');
  });

  it('should load user tasks if user is not an admin', () => {
    isAuthenticatedSubject.next(true);
    currentUserSubject.next('regularUser');
    rolesSubject.next(['ROLE_USER']);
    fixture.detectChanges();

    expect(taskService.getTasks).toHaveBeenCalled();
  });

  it('should load all tasks for admin if user is an admin', () => {
    isAuthenticatedSubject.next(true);
    currentUserSubject.next('adminUser');
    rolesSubject.next(['ROLE_ADMIN']);
    fixture.detectChanges();

    expect(taskService.getAllTasksForAdmin).toHaveBeenCalled();
  });
});
