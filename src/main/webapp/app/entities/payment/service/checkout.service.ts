import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, map } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';

/**
 * Yêu cầu tạo link thanh toán.
 *
 * Có hai cách dùng:
 * - Truyền `bookingId` cho một booking PENDING đã tạo trước đó.
 * - Hoặc truyền `ticketTypeId` + `quantity` (+ `couponCode`) để backend tự tạo
 *   booking rồi sinh link thanh toán trong cùng một lần gọi.
 */
export interface ICheckoutRequest {
  bookingId?: number | null;
  ticketTypeId?: number | null;
  quantity?: number | null;
  couponCode?: string | null;
}

/**
 * Gọi API thanh toán VNPay.
 */
@Injectable({ providedIn: 'root' })
export class CheckoutService {
  private readonly http = inject(HttpClient);
  private readonly applicationConfigService = inject(ApplicationConfigService);

  private readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/payments');

  /**
   * Tạo link thanh toán VNPay cho đơn hàng.
   *
   * @returns URL của cổng thanh toán (hoặc URL trang kết quả nếu đơn 0 đồng).
   */
  createPaymentUrl(request: ICheckoutRequest): Observable<string> {
    return this.http
      .post<{ paymentUrl: string }>(`${this.resourceUrl}/vnpay/create-url`, request)
      .pipe(map(response => response.paymentUrl));
  }

  /**
   * Chuyển trình duyệt sang cổng thanh toán.
   *
   * Dùng `window.location.href` chứ không dùng Router vì đây là domain ngoài.
   */
  redirectToGateway(paymentUrl: string): void {
    window.location.href = paymentUrl;
  }
}
