import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { IAddonCartItem, IAddonPurchaseResponse, ITicketAddonInfo } from './ticket-addon.model';

@Injectable({ providedIn: 'root' })
export class TicketAddonService {
  private readonly http = inject(HttpClient);
  private readonly applicationConfigService = inject(ApplicationConfigService);

  private readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/ticket-addons');

  /** Danh mục và các dịch vụ (theo loại) đã mua cho một vé. */
  getInfo(ticketId: number, addonType: string): Observable<ITicketAddonInfo> {
    return this.http.get<ITicketAddonInfo>(`${this.resourceUrl}/tickets/${ticketId}`, { params: { type: addonType } });
  }

  /** Tạo yêu cầu mua dịch vụ bổ trợ, trả về bookingId để thanh toán. */
  requestPurchase(ticketId: number, addonType: string, items: IAddonCartItem[]): Observable<IAddonPurchaseResponse> {
    return this.http.post<IAddonPurchaseResponse>(this.resourceUrl, { ticketId, addonType, items });
  }
}
