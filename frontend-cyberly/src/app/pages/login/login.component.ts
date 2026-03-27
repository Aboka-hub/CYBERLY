import { Component } from '@angular/core';
import {Router, RouterLink} from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import {AuthService} from "../../services/auth.service";
import {emailError} from "@angular/forms/signals";

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule, CommonModule, RouterLink],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {
  email    = '';
  password = '';
  loading  = false;
  error    = '';
  showPassword = false;
  passwordError = '';
  emailError = '';
  serverError: any;

  constructor(private auth: AuthService, private router: Router) {}

  onLogin() {
    if (!this.email || !this.password) {
      this.error = 'Заполните все поля';
      return;
    }
    this.loading = true;
    this.error   = '';

    this.auth.login({ email: this.email, password: this.password }).subscribe({
      next: (res) => {
        if (res.status === 'BLOCKED') {
          this.error   = 'Аккаунт заблокирован из-за высокого риска';
          this.loading = false;
          return;
        }
        this.router.navigate(['/dashboard']);
      },
      error: (err) => {
        if (err.status === 401) this.error = 'Неверный email или пароль';
        else if (err.status === 403) this.error = 'Аккаунт заблокирован';
        else this.error = err.error?.error ?? 'Ошибка сервера';
        this.loading = false;
      }
    });
  }
  goToDashboard() {
    this.router.navigate(['/dashboard']);
  }
}
