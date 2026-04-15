import { Component, OnInit, ChangeDetectorRef, PLATFORM_ID, Inject } from '@angular/core';
import { isPlatformBrowser, CommonModule } from '@angular/common';
import { AuthService } from '../../services/auth.service';
import { DashboardService, LoginEvent } from '../../services/dashboard.service';

type FilterType = 'ALL' | 'LOGIN_SUCCESS' | 'LOGIN_FAILED';

@Component({
  selector: 'app-events',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './events.component.html',
  styleUrl: './events.component.css'
})
export class EventsComponent implements OnInit {

  events: LoginEvent[] = [];
  filter: FilterType = 'ALL';
  loading = true;
  error = '';

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
    this.svc.getLoginEvents(this.auth.getUserId()).subscribe({
      next: (data) => {
        this.events = data;
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.error = 'Не удалось загрузить события';
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  setFilter(f: FilterType) {
    this.filter = f;
  }

  get filtered(): LoginEvent[] {
    if (this.filter === 'ALL') return this.events;
    return this.events.filter(e => e.type === this.filter);
  }

  get successCount() { return this.events.filter(e => e.type === 'LOGIN_SUCCESS').length; }
  get failedCount()  { return this.events.filter(e => e.type === 'LOGIN_FAILED').length; }

  formatTime(iso: string): string {
    const diff = Math.floor((Date.now() - new Date(iso).getTime()) / 60000);
    if (diff < 1)    return 'только что';
    if (diff < 60)   return `${diff} мин назад`;
    if (diff < 1440) return `${Math.floor(diff/60)} ч назад`;
    return new Date(iso).toLocaleDateString('ru');
  }
}
