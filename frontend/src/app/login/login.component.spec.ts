import { ComponentFixture, TestBed } from '@angular/core/testing';
import { LoginComponent } from './login.component';
import { provideHttpClient } from '@angular/common/http';
import { AuthService } from '../services/auth.service';
import { of } from 'rxjs';
import { FormsModule } from '@angular/forms';
import { By } from '@angular/platform-browser';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let authService: AuthService;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LoginComponent, FormsModule],
      providers: [provideHttpClient(), AuthService],
    }).compileComponents();

    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    authService = TestBed.inject(AuthService);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should have empty initial form fields', () => {
    expect(component.username).toBe('');
    expect(component.password).toBe('');
  });

  it('should call onLogin when the form is submitted', () => {
    spyOn(component, 'onLogin');
    const form = fixture.debugElement.query(By.css('form'));
    form.triggerEventHandler('ngSubmit', null);
    expect(component.onLogin).toHaveBeenCalled();
  });

  it('should call AuthService login on form submit', () => {
    spyOn(authService, 'login').and.returnValue(of(true));
    component.username = 'testuser';
    component.password = 'password';
    component.onLogin();
    expect(authService.login).toHaveBeenCalledWith('testuser', 'password');
  });

  it('should display an error message on failed login', () => {
    spyOn(authService, 'login').and.returnValue(of(false));
    component.onLogin();
    fixture.detectChanges();
    const errorMessage = fixture.debugElement.query(By.css('p'));
    expect(errorMessage.nativeElement.textContent).toContain('Identifiants incorrects');
  });

  it('should not display an error message on successful login', () => {
    spyOn(authService, 'login').and.returnValue(of(true));
    component.onLogin();
    fixture.detectChanges();
    const errorMessage = fixture.debugElement.query(By.css('p'));
    expect(errorMessage).toBeNull();
  });
});
