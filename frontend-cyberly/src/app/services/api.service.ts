import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class ApiService {

  constructor(private http: HttpClient) {}

  login(data: any) {
    return this.http.post('/api/user/login', data);
  }

  register(data: any) {
    return this.http.post('/api/user/register', data);
  }
}
