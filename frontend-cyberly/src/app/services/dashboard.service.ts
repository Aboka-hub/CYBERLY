import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

// Matches LoginEventResponse.java
export interface LoginEvent {
  id: number;
  userEmail: string;
  type: 'LOGIN_SUCCESS' | 'LOGIN_FAILED';
  ipAddress: string;
  country: string;
  device: string;
  createdAt: string;
}

// Matches RiskSnapshot.java
export interface RiskSnapshot {
  id: number;
  score: number;
  level: 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL';
  reasons: string;
  calculatedAt: string;
}

// Matches Subscription.java
export interface Subscription {
  id: number;
  serviceName: string;
  amount: number;
  nextPaymentDate: string;
  active: boolean;
  createdAt: string;
}

export interface SubscriptionRequest {
  serviceName: string;
  amount: number;
  nextPaymentDate: string;
}

@Injectable({ providedIn: 'root' })
export class DashboardService {

  private base = '/api';

  constructor(private http: HttpClient) {}

  getLoginEvents(userId: number): Observable<LoginEvent[]> {
    return this.http.get<LoginEvent[]>(`${this.base}/login-events/${userId}`);
  }

  getRiskSnapshot(userId: number): Observable<RiskSnapshot> {
    return this.http.get<RiskSnapshot>(`${this.base}/risk/${userId}`);
  }

  getSubscriptions(userId: number): Observable<Subscription[]> {
    return this.http.get<Subscription[]>(`${this.base}/subscriptions/${userId}`);
  }

  addSubscription(userId: number, request: SubscriptionRequest): Observable<string> {
    return this.http.post(`${this.base}/subscriptions/${userId}`, request, { responseType: 'text' });
  }

  deleteSubscription(userId: number, subscriptionId: number): Observable<string> {
    return this.http.delete(`${this.base}/subscriptions/${userId}/${subscriptionId}`, { responseType: 'text' });
  }
}
