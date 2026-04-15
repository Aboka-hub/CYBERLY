import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';


export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  email: string;
  password: string;
}

// Matches UserResponse.java exactly
export interface UserResponse {
  token: string;
  userId: number;
  email: string;
  status: 'ACTIVE' | 'BLOCKED';
  message: string;
}

@Injectable({ providedIn: 'root' })
export class AuthService {

  private base = '/api';

  constructor(private http: HttpClient, private router: Router) {}

  login(request: LoginRequest): Observable<UserResponse> {
    return this.http.post<UserResponse>(`${this.base}/user/login`, request).pipe(
      tap(res => {
        localStorage.setItem('token',  res.token);
        localStorage.setItem('userId', String(res.userId));
        localStorage.setItem('email',  res.email);
        localStorage.setItem('status', res.status);
      })
    );
  }

  register(request: RegisterRequest): Observable<string> {
    return this.http.post(`${this.base}/user/register`, request, { responseType: 'text' });
  }

  logout(): void {
    localStorage.clear();
    this.router.navigate(['/login']);
  }

  isLoggedIn(): boolean {
    return !!localStorage.getItem('token');
  }

  getToken(): string {
    return localStorage.getItem('token') ?? '';
  }

  getUserId(): number {
    return Number(localStorage.getItem('userId'));
  }

  getEmail(): string {
    return localStorage.getItem('email') ?? '';
  }

  getStatus(): string {
    return localStorage.getItem('status') ?? '';
  }
}
