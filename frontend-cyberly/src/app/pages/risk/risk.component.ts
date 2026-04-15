import { Component, OnInit, ChangeDetectorRef, PLATFORM_ID, Inject } from '@angular/core';
import { isPlatformBrowser, CommonModule } from '@angular/common';
import { AuthService } from '../../services/auth.service';
import { DashboardService, RiskSnapshot } from '../../services/dashboard.service';

@Component({
  selector: 'app-risk',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './risk.component.html',
  styleUrl: './risk.component.css'
})
export class RiskComponent implements OnInit {

  risk: RiskSnapshot | null = null;
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
    this.svc.getRiskSnapshot(this.auth.getUserId()).subscribe({
      next: (data) => {
        this.risk = data;
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.risk = null;
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  getRiskClass(level: string): string {
    return ({ LOW:'risk-low', MEDIUM:'risk-mid', HIGH:'risk-high', CRITICAL:'risk-critical' } as any)[level] ?? 'risk-low';
  }

  getRiskReasons(): string[] {
    return (this.risk?.reasons ?? '').split(';').map(r => r.trim()).filter(Boolean);
  }

  formatTime(iso: string): string {
    return new Date(iso).toLocaleString('ru');
  }
}
