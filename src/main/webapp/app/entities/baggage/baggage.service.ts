import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { IBaggageResponse, ITicketBaggageInfo } from './baggage.model';

@Injectable({ providedIn: 'root' })
export class BaggageService {
  private readonly http = inject(HttpClient);
  private readonly applicationConfigService = inject(ApplicationConfigService);

  private readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/baggage');

  /** Hành lý đã mua và các gói có thể mua thêm cho một vé. */
  getBaggageInfo(ticketId: number): Observable<ITicketBaggageInfo> {
    return this.http.get<ITicketBaggageInfo>(`${this.resourceUrl}/tickets/${ticketId}`);
  }

  /** Tạo yêu cầu mua hành lý, trả về bookingId để thanh toán. */
  requestPurchase(ticketId: number, weightKg: number): Observable<IBaggageResponse> {
    return this.http.post<IBaggageResponse>(this.resourceUrl, { ticketId, weightKg });
  }
}
