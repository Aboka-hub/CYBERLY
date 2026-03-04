import { Component } from '@angular/core';
import {Router} from '@angular/router';

@Component({
  selector: 'app-dashboard',
  imports: [],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css',
})

export class DashboardComponent {

  userId = localStorage.getItem('userId');

  constructor(private router: Router) {}

  logout() {
    localStorage.removeItem('userId');
    this.router.navigate(['/login']);
  }
}
