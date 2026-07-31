import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface IEventRevenue {
  eventId: number;
  eventName: string;
  revenue: number;
}

export interface IRecentPayment {
  paymentDate: string;
  buyer: string;
  amount: number;
}

export interface IOrganizerRevenue {
  totalRevenue: number;
  totalEvents: number;
  ticketsSold: number;
  eventRevenue: IEventRevenue[];
  recentPayments: IRecentPayment[];
}

@Injectable({
  providedIn: 'root',
})
export class RevenueService {
  private http = inject(HttpClient);

  getRevenue(): Observable<IOrganizerRevenue> {
    return this.http.get<IOrganizerRevenue>('/api/organizer/revenue');
  }
}
