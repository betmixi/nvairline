import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { ICheckInTicketInfo } from './check-in.model';

/** Tra cuu ve theo so ve va xac nhan lam thu tuc (check-in) - danh cho Quan ly/Nhan vien. */
@Injectable({ providedIn: 'root' })
export class AdminCheckInService {
  private readonly http = inject(HttpClient);

  lookupByTicketNumber(ticketNumber: string): Observable<ICheckInTicketInfo> {
    return this.http.get<ICheckInTicketInfo>(`/api/tickets/qr/${encodeURIComponent(ticketNumber)}`);
  }

  confirmCheckIn(ticketId: number): Observable<unknown> {
    return this.http.post('/api/check-ins/check-in', { ticketId });
  }
}
