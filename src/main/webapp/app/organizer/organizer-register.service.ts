import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface OrganizerRegister {
  companyName: string | null;
  taxCode: string | null;
  description: string | null;
}

@Injectable({
  providedIn: 'root',
})
export class OrganizerRegisterService {
  private readonly http = inject(HttpClient);

  register(data: OrganizerRegister): Observable<any> {
    return this.http.post('/api/organizers/register', data);
  }

  getMyRequest(): Observable<any> {
    return this.http.get('/api/organizers/my-request');
  }
}
