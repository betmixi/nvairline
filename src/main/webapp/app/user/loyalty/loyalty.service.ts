import { HttpClient, HttpResponse } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { ILoyaltyBalance, ILoyaltyOffer, IPointsHistory } from './loyalty.model';

/** Tich diem Lotusmiles cua nguoi dung dang dang nhap. */
@Injectable({ providedIn: 'root' })
export class LoyaltyService {
  private readonly http = inject(HttpClient);

  getMyBalance(): Observable<ILoyaltyBalance> {
    return this.http.get<ILoyaltyBalance>('/api/loyalty/me');
  }

  getMyHistory(): Observable<HttpResponse<IPointsHistory[]>> {
    return this.http.get<IPointsHistory[]>('/api/loyalty/me/history', {
      params: { page: 0, size: 50, sort: 'id,desc' },
      observe: 'response',
    });
  }

  getOffers(): Observable<ILoyaltyOffer[]> {
    return this.http.get<ILoyaltyOffer[]>('/api/loyalty/offers');
  }

  redeem(offerId: string): Observable<ILoyaltyBalance> {
    return this.http.post<ILoyaltyBalance>(`/api/loyalty/redeem/${encodeURIComponent(offerId)}`, {});
  }
}
