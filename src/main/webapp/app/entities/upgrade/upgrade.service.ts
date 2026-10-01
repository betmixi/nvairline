import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { ITicketUpgradeInfo, IUpgradeResponse } from './upgrade.model';

@Injectable({ providedIn: 'root' })
export class UpgradeService {
  private readonly http = inject(HttpClient);
  private readonly applicationConfigService = inject(ApplicationConfigService);

  private readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/upgrades');

  /** Các hạng ghế có thể nâng cho một vé. */
  getUpgradeInfo(ticketId: number): Observable<ITicketUpgradeInfo> {
    return this.http.get<ITicketUpgradeInfo>(`${this.resourceUrl}/tickets/${ticketId}`);
  }

  /** Tạo yêu cầu nâng hạng, trả về bookingId để thanh toán phần chênh lệch. */
  requestUpgrade(ticketId: number, targetSeatType: string): Observable<IUpgradeResponse> {
    return this.http.post<IUpgradeResponse>(this.resourceUrl, { ticketId, targetSeatType });
  }
}
