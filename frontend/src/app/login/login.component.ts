// login.component.ts
import { Component } from '@angular/core';
import { CommonModule } from '@angular/common'; // Import pour *ngIf
import { FormsModule } from '@angular/forms'; // Import pour ngModel
import { AuthService } from '../services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule], // Ajoutez CommonModule et FormsModule ici
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
        this.errorMessage = 'Identifiants incorrects';
      }
    });
  }
}
