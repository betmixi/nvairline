import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { OrganizerDashboard } from './dashboard.model';

@Injectable({
  providedIn: 'root',
})
export class OrganizerDashboardService {
  private http = inject(HttpClient);

  private resourceUrl = '/api/organizers/dashboard';

  getDashboard(): Observable<OrganizerDashboard> {
    return this.http.get<OrganizerDashboard>(this.resourceUrl);
  }
}
