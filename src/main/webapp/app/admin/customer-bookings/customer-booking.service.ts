import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { ICustomerBooking } from './customer-booking.model';

/** Danh sach khach hang da dat ve - danh cho man hinh quan ly cua admin. */
@Injectable({ providedIn: 'root' })
export class CustomerBookingService {
  private readonly http = inject(HttpClient);

  getAll(): Observable<ICustomerBooking[]> {
    return this.http.get<ICustomerBooking[]>('/api/bookings/customer-list');
  }
}
