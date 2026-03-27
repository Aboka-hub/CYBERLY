import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import {forkJoin, of} from 'rxjs';
import { catchError } from 'rxjs/operators';
import {AuthService} from '../../services/auth.service';
import {DashboardService, LoginEvent, RiskSnapshot, Subscription} from '../../services/dashboard.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css'
})
export class DashboardComponent implements OnInit {

  events: LoginEvent[]          = [];
  risk:   RiskSnapshot | null   = null;
  subscriptions: Subscription[] = [];

  stats = { total: 0, success: 0, failed: 0 };

  loading = true;
  error   = '';

  today = new Date().toLocaleDateString('ru', {
    weekday: 'long', year: 'numeric', month: 'long', day: 'numeric'
  });

  constructor(public auth: AuthService, private svc: DashboardService) {}

  ngOnInit() { this.loadMock(); }

  // ── MOCK данные для теста (замени на loadAll() когда бэкенд готов) ──
  loadMock() {
    this.loading = false;
    this.error   = '';

    this.events = [
      { id:1, user:{id:1,email:'admin@cyberly.io',status:'ACTIVE'},  type:'LOGIN_SUCCESS', ipAddress:'192.168.1.1',    country:'Kazakhstan', device:'Chrome / Windows',  createdAt: new Date(Date.now()-2*60000).toISOString() },
      { id:2, user:{id:2,email:'user@mail.com',   status:'ACTIVE'},  type:'LOGIN_FAILED',  ipAddress:'45.33.32.156',   country:'Russia',     device:'Firefox / Linux',   createdAt: new Date(Date.now()-5*60000).toISOString() },
      { id:3, user:{id:1,email:'admin@cyberly.io',status:'ACTIVE'},  type:'LOGIN_SUCCESS', ipAddress:'10.0.0.5',       country:'USA',        device:'Safari / Mac',      createdAt: new Date(Date.now()-10*60000).toISOString() },
      { id:4, user:{id:3,email:'test@mail.ru',    status:'BLOCKED'}, type:'LOGIN_FAILED',  ipAddress:'185.220.101.47', country:'Germany',    device:'Unknown',           createdAt: new Date(Date.now()-15*60000).toISOString() },
      { id:5, user:{id:2,email:'nureke@mail.com', status:'ACTIVE'},  type:'LOGIN_SUCCESS', ipAddress:'77.88.55.60',    country:'Kazakhstan', device:'Chrome / Android',  createdAt: new Date(Date.now()-30*60000).toISOString() },
      { id:6, user:{id:3,email:'test@mail.ru',    status:'BLOCKED'}, type:'LOGIN_FAILED',  ipAddress:'185.220.101.47', country:'Germany',    device:'Unknown',           createdAt: new Date(Date.now()-45*60000).toISOString() },
    ];

    this.risk = {
      id: 1,
      score: 65,
      level: 'HIGH',
      reasons: 'Multiple failed logins; New IP address;',
      calculatedAt: new Date(Date.now()-60000).toISOString()
    };

    this.subscriptions = [
      { id:1, serviceName:'Netflix',         amount:12.99, nextPaymentDate: this.addDays(5),  active:true,  createdAt: new Date().toISOString() },
      { id:2, serviceName:'Spotify',         amount:5.99,  nextPaymentDate: this.addDays(-2), active:true,  createdAt: new Date().toISOString() },
      { id:3, serviceName:'YouTube Premium', amount:9.99,  nextPaymentDate: this.addDays(10), active:true,  createdAt: new Date().toISOString() },
    ];

    this.stats = {
      total:   this.events.length,
      success: this.events.filter(e => e.type === 'LOGIN_SUCCESS').length,
      failed:  this.events.filter(e => e.type === 'LOGIN_FAILED').length
    };
  }

  // ── Реальная загрузка с бэкенда (раскомментируй когда API готов) ──
  loadAll() {
    const uid = this.auth.getUserId();
    this.loading = true;
    this.error   = '';

    forkJoin({
      events: this.svc.getLoginEvents(uid).pipe(catchError(() => of([]))),
      risk:   this.svc.getRiskSnapshot(uid).pipe(catchError(() => of(null))),
      subs:   this.svc.getSubscriptions(uid).pipe(catchError(() => of([])))
    }).subscribe({
      next: ({ events, risk, subs }) => {
        this.events        = events as LoginEvent[];
        this.risk          = risk   as RiskSnapshot | null;
        this.subscriptions = subs   as Subscription[];
        this.stats = {
          total:   this.events.length,
          success: this.events.filter(e => e.type === 'LOGIN_SUCCESS').length,
          failed:  this.events.filter(e => e.type === 'LOGIN_FAILED').length
        };
        this.loading = false;
      },
      error: () => { this.error = 'Не удалось загрузить данные'; this.loading = false; }
    });
  }

  private addDays(n: number): string {
    const d = new Date();
    d.setDate(d.getDate() + n);
    return d.toISOString().split('T')[0];
  }

  getRiskClass(level: string): string {
    return ({ LOW:'risk-low', MEDIUM:'risk-mid', HIGH:'risk-high', CRITICAL:'risk-critical' } as any)[level] ?? 'risk-low';
  }

  getRiskReasons(): string[] {
    return (this.risk?.reasons ?? '').split(';').map(r => r.trim()).filter(Boolean);
  }

  formatTime(iso: string): string {
    const diff = Math.floor((Date.now() - new Date(iso).getTime()) / 60000);
    if (diff < 1)    return 'только что';
    if (diff < 60)   return `${diff} мин назад`;
    if (diff < 1440) return `${Math.floor(diff/60)} ч назад`;
    return new Date(iso).toLocaleDateString('ru');
  }

  isExpiringSoon(d: string): boolean {
    const days = Math.ceil((new Date(d).getTime() - Date.now()) / 86400000);
    return days <= 3 && days >= 0;
  }

  isExpired(d: string): boolean {
    return new Date(d).getTime() < Date.now();
  }
}
