// app.component.ts
import { Component } from '@angular/core';
import { CommonModule } from '@angular/common'; // Import du CommonModule
import { AuthService } from './services/auth.service';
import { RouterOutlet } from '@angular/router';
import { LoginComponent } from './login/login.component';
import { LogoutComponent } from './logout/logout.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterOutlet, LoginComponent, LogoutComponent],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'angular-to-do-list';
  isAuthenticated = false;

  constructor(public authService: AuthService) {
    // S'abonner à l'état de connexion
    this.authService.isAuthenticated$.subscribe((isLoggedIn) => {
      this.isAuthenticated = isLoggedIn;
    });
  }
}
