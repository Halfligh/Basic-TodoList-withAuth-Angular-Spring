// login.component.ts
import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  username: string = '';
  password: string = '';
  errorMessage: string = '';

  constructor(private authService: AuthService) {}

  onLogin() {
    this.authService.login(this.username, this.password).subscribe(success => {
      if (!success) {
        this.errorMessage = 'Identifiants incorrects'; // Message en cas d'erreur
      } else {
        // Rediriger vers une autre page ou effectuer une autre action en cas de succès
        this.errorMessage = '';
        // Par exemple, naviguer vers la page d'accueil
      }
    });
  }
}
