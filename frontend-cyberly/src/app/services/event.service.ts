import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import {AuthService} from './auth.service';
import {LoginEvent} from './dashboard.service';

@Injectable({ providedIn: 'root' })
export class EventService {
  private readonly API = 'http://localhost:8080';

  constructor(private http: HttpClient, private authService: AuthService) {}

  private headers(): HttpHeaders {
    return new HttpHeaders({
      Authorization: `Bearer ${this.authService.getToken()}`
    });
  }

  getEvents(): Observable<LoginEvent[]> {
    return this.http.get<LoginEvent[]>(`${this.API}/events`, {
      headers: this.headers()
    });
  }
}
