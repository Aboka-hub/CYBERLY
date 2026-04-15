import { Component, OnInit, ChangeDetectorRef, PLATFORM_ID, Inject } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { forkJoin, of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { AuthService } from '../../services/auth.service';
import { DashboardService, LoginEvent, RiskSnapshot, Subscription, SubscriptionRequest } from '../../services/dashboard.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule],
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

  showAddForm    = false;
  addLoading     = false;
  newSub: SubscriptionRequest = { serviceName: '', amount: 0, nextPaymentDate: '' };

  today = new Date().toLocaleDateString('ru', {
    weekday: 'long', year: 'numeric', month: 'long', day: 'numeric'
  });

  constructor(
    public auth: AuthService,
    private svc: DashboardService,
    private cdr: ChangeDetectorRef,
    @Inject(PLATFORM_ID) private platformId: Object
  ) {}

  ngOnInit() {
    if (isPlatformBrowser(this.platformId)) {
      this.loadAll();
    }
  }

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
        this.cdr.detectChanges();
      },
      error: () => {
        this.error = 'Не удалось загрузить данные';
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  addSubscription() {
    if (!this.newSub.serviceName || !this.newSub.amount || !this.newSub.nextPaymentDate) return;
    this.addLoading = true;

    this.svc.addSubscription(this.auth.getUserId(), this.newSub).subscribe({
      next: () => {
        this.newSub = { serviceName: '', amount: 0, nextPaymentDate: '' };
        this.showAddForm = false;
        this.addLoading  = false;
        this.loadAll();
      },
      error: () => { this.addLoading = false; }
    });
  }

  deleteSubscription(subId: number) {
    this.svc.deleteSubscription(this.auth.getUserId(), subId).subscribe({
      next: () => this.loadAll()
    });
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
