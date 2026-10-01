import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { map, Observable } from 'rxjs';
import dayjs from 'dayjs/esm';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { AdminDashboard } from './admin-dashboard.model';

@Injectable({
  providedIn: 'root',
})
export class AdminDashboardService {
  private readonly http = inject(HttpClient);
  private readonly applicationConfigService = inject(ApplicationConfigService);

  /**
   * Dashboard
   */
  getDashboard(): Observable<AdminDashboard> {
    return this.http.get<AdminDashboard>(this.applicationConfigService.getEndpointFor('api/admin/dashboard')).pipe(
      map(res => ({
        ...res,
        recentEvents: res.recentEvents.map(event => ({
          ...event,
          startTime: event.startTime ? dayjs(event.startTime) : null,
          endTime: event.endTime ? dayjs(event.endTime) : null,
          createdDate: event.createdDate ? dayjs(event.createdDate) : null,
        })),
      })),
    );
  }
}
