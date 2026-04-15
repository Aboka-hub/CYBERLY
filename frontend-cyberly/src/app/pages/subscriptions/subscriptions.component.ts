import { Component, OnInit, ChangeDetectorRef, PLATFORM_ID, Inject } from '@angular/core';
import { isPlatformBrowser, CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../services/auth.service';
import { DashboardService, Subscription, SubscriptionRequest } from '../../services/dashboard.service';

@Component({
  selector: 'app-subscriptions',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './subscriptions.component.html',
  styleUrl: './subscriptions.component.css'
})
export class SubscriptionsComponent implements OnInit {

  subscriptions: Subscription[] = [];
  loading = true;
  error = '';
  showAddForm = false;
  addLoading = false;
  newSub: SubscriptionRequest = { serviceName: '', amount: 0, nextPaymentDate: '' };

  today = new Date().toLocaleDateString('ru', {
    weekday: 'long', year: 'numeric', month: 'long', day: 'numeric'
  });

  constructor(
    private auth: AuthService,
    private svc: DashboardService,
    private cdr: ChangeDetectorRef,
    @Inject(PLATFORM_ID) private platformId: Object
  ) {}

  ngOnInit() {
    if (isPlatformBrowser(this.platformId)) {
      this.load();
    }
  }

  load() {
    this.loading = true;
    this.error = '';
    this.svc.getSubscriptions(this.auth.getUserId()).subscribe({
      next: (data) => {
        this.subscriptions = data;
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.error = 'Не удалось загрузить подписки';
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
        this.addLoading = false;
        this.load();
      },
      error: () => { this.addLoading = false; }
    });
  }

  deleteSubscription(subId: number) {
    this.svc.deleteSubscription(this.auth.getUserId(), subId).subscribe({
      next: () => this.load()
    });
  }

  get activeCount()   { return this.subscriptions.filter(s => s.active).length; }
  get totalMonthly()  { return this.subscriptions.filter(s => s.active).reduce((sum, s) => sum + s.amount, 0); }

  isExpiringSoon(d: string): boolean {
    const days = Math.ceil((new Date(d).getTime() - Date.now()) / 86400000);
    return days <= 3 && days >= 0;
  }

  isExpired(d: string): boolean {
    return new Date(d).getTime() < Date.now();
  }
}
