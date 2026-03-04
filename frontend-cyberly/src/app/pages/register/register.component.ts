import { Component } from '@angular/core';
import {FormsModule} from '@angular/forms';
import {ApiService} from '../../services/api.service';
import {Router, RouterLink} from '@angular/router';
import {NgIf} from '@angular/common';

@Component({
  selector: 'app-register',
  imports: [
    FormsModule,
    NgIf,
    RouterLink
  ],
  templateUrl: './register.component.html',
  styleUrl: './register.component.css',
})

export class RegisterComponent {

  email = '';
  password = '';
  errorMessage = '';

  constructor(private api: ApiService,
              private router: Router) {}

  register() {
    this.api.register({
      email: this.email,
      password: this.password
    }).subscribe({
      next: () => {
        this.router.navigate(['/login']);
      },
      error: (err) => {
        this.errorMessage = err.error?.error || 'Registration failed';
      }
    });
  }
}
