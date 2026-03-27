import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from './auth.service';

export interface LoginEvent {
  id: number;
  user: { id: number; email: string; status: string };
  type: 'LOGIN_SUCCESS' | 'LOGIN_FAILED';
  ipAddress: string;
  country: string;
  device: string;
  createdAt: string; // LocalDateTime → ISO string
}

// Matches RiskSnapshot.java
export interface RiskSnapshot {
  id: number;
  score: number;                              // 0–100
  level: 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL';
  reasons: string;
  calculatedAt: string;
}

// Matches Subscription.java
export interface Subscription {
  id: number;
  serviceName: string;
  amount: number;
  nextPaymentDate: string;  // LocalDate → "YYYY-MM-DD"
  active: boolean;
  createdAt: string;
}

@Injectable({ providedIn: 'root' })
export class DashboardService {

  private base = 'http://localhost:8080/api';

  constructor(private http: HttpClient, private auth: AuthService) {}

  private get headers(): HttpHeaders {
    return new HttpHeaders({ Authorization: `Bearer ${this.auth.getToken()}` });
  }

  // GET /api/login-events/{userId}  → top 20 events desc
  getLoginEvents(userId: number): Observable<LoginEvent[]> {
    return this.http.get<LoginEvent[]>(
      `${this.base}/login-events/${userId}`,
      { headers: this.headers }
    );
  }

  // GET /api/risk/{userId}
  getRiskSnapshot(userId: number): Observable<RiskSnapshot> {
    return this.http.get<RiskSnapshot>(
      `${this.base}/risk/${userId}`,
      { headers: this.headers }
    );
  }

  // GET /api/subscriptions/{userId}
  getSubscriptions(userId: number): Observable<Subscription[]> {
    return this.http.get<Subscription[]>(
      `${this.base}/subscriptions/${userId}`,
      { headers: this.headers }
    );
  }
}
