import { Component, OnInit, ChangeDetectorRef, PLATFORM_ID, Inject } from '@angular/core';
import { isPlatformBrowser, CommonModule } from '@angular/common';
import { AuthService } from '../../services/auth.service';
import { DashboardService, UserProfile } from '../../services/dashboard.service';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.css'
})
export class ProfileComponent implements OnInit {

  profile: UserProfile | null = null;
  loading = true;
  error = '';

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
      this.load();
    }
  }

  load() {
    this.loading = true;
    this.error = '';
    this.svc.getMe().subscribe({
      next: (data) => {
        this.profile = data;
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.error = 'Не удалось загрузить профиль';
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  getInitial(): string {
    return (this.profile?.email ?? this.auth.getEmail()).charAt(0).toUpperCase();
  }
}
