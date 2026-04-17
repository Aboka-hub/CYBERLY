import { Component } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [FormsModule, CommonModule, RouterLink],
  templateUrl: './register.component.html',
  styleUrl: './register.component.css'
})
export class RegisterComponent {
  email           = '';
  password        = '';
  confirmPassword = '';
  loading         = false;
  error           = '';
  step            = 1;
  showPassword    = false;
  strengthWidth   = 0;

  constructor(private auth: AuthService, private router: Router) {}

  checkStrength() {
    const p = this.password;
    let score = 0;
    if (p.length >= 8)          score += 25;
    if (p.length >= 12)         score += 15;
    if (/[A-Z]/.test(p))       score += 20;
    if (/[0-9]/.test(p))       score += 20;
    if (/[^A-Za-z0-9]/.test(p)) score += 20;
    this.strengthWidth = Math.min(score, 100);
  }

  get strengthColor(): string {
    if (this.strengthWidth >= 80) return '#16a34a';
    if (this.strengthWidth >= 50) return '#d97706';
    return '#dc2626';
  }

  get strengthLabel(): string {
    if (this.strengthWidth >= 80) return 'Надёжный';
    if (this.strengthWidth >= 50) return 'Средний';
    if (this.strengthWidth >  0)  return 'Слабый';
    return '';
  }

  get passwordMismatch(): boolean {
    return this.confirmPassword.length > 0 && this.password !== this.confirmPassword;
  }

  // Step 1: only email
  get isEmailValid(): boolean {
    return this.email.length > 0 && this.email.includes('@');
  }

  // Step 2: password fields
  get isPasswordValid(): boolean {
    return this.password.length >= 8 && this.password === this.confirmPassword;
  }

  nextStep() {
    this.error = '';
    if (this.isEmailValid) this.step = 2;
    else this.error = 'Введите корректный email';
  }

  onRegister() {
    if (!this.isPasswordValid) return;
    this.loading = true;
    this.error   = '';

    this.auth.register({ email: this.email, password: this.password }).subscribe({
      next: () => {
        this.step = 3;
        setTimeout(() => this.router.navigate(['/login']), 1800);
      },
      error: (err) => {
        if (err.status === 409) this.error = 'Пользователь с таким email уже существует';
        else this.error = err.error?.error ?? 'Ошибка регистрации';
        this.loading = false;
      }
    });
  }
}
