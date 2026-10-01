import { ChangeDetectorRef, Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { forkJoin, map, switchMap } from 'rxjs';

import { ShowtimeService } from 'app/entities/showtime/service/showtime.service';
import { IShowtime, IShowtimeSeat } from 'app/entities/showtime/showtime.model';
import { BAGGAGE_CATALOG } from 'app/entities/baggage/baggage.model';
import { TripBuilderService } from 'app/booking/trip-builder.service';
import { AccountService } from 'app/core/auth/account.service';
import { LoyaltyService } from 'app/user/loyalty/loyalty.service';
import { ILoyaltyCoupon } from 'app/user/loyalty/loyalty.model';
import { CheckoutService, ICheckoutLegRequest } from '../service/checkout.service';

interface ILegView {
  showtime: IShowtime;
  seats: IShowtimeSeat[];
}

/**
 * Trang xác nhận đơn hàng trước khi chuyển sang cổng thanh toán VNPay.
 *
 * Đọc toàn bộ chặng đã chọn từ TripBuilderService (một chiều chỉ có 1 chặng,
 * khứ hồi/nhiều chặng có nhiều chặng) thay vì chỉ đọc showtimeId/seatIds đơn lẻ.
 */
@Component({
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './payment-page.html',
  styleUrl: './payment-page.scss',
})
export default class PaymentPageComponent implements OnInit {
  legs: ILegView[] = [];

  couponCode = '';

  /** Các gói hành lý cố định có thể chọn thêm cho mỗi ghế. */
  readonly baggageOptions = BAGGAGE_CATALOG;

  /** Số kg hành lý đã chọn theo từng ghế (id ghế là duy nhất toàn hệ thống nên dùng chung 1 map cho mọi chặng), 0 = không thêm. */
  baggageBySeat: Record<number, number> = {};

  isLoading = true;

  /** Đang gọi API tạo link thanh toán. */
  isSubmitting = false;

  errorMessage = '';

  /** Cac coupon ca nhan (da doi bang diem Lotusmiles) con dung duoc, de chon nhanh thay vi go tay. */
  myCoupons: ILoyaltyCoupon[] = [];

  private readonly router = inject(Router);
  private readonly showtimeService = inject(ShowtimeService);
  private readonly checkoutService = inject(CheckoutService);
  private readonly tripBuilder = inject(TripBuilderService);
  private readonly accountService = inject(AccountService);
  private readonly loyaltyService = inject(LoyaltyService);
  private readonly cdr = inject(ChangeDetectorRef);

  ngOnInit(): void {
    const legSelections = this.tripBuilder.legs();

    if (legSelections.length === 0) {
      this.isLoading = false;
      return;
    }

    if (this.accountService.isAuthenticated()) {
      this.loyaltyService.getMyCoupons().subscribe({
        next: coupons => {
          this.myCoupons = coupons;
          this.cdr.detectChanges();
        },
      });
    }

    const requests = legSelections.map(selection =>
      this.showtimeService
        .find(selection.showtimeId)
        .pipe(
          switchMap(showtime =>
            this.showtimeService
              .getSeats(selection.showtimeId)
              .pipe(map(seats => ({ showtime, seats: seats.filter(seat => selection.seatIds.includes(seat.id)) }))),
          ),
        ),
    );

    forkJoin(requests).subscribe({
      next: legs => {
        this.legs = legs;
        this.isLoading = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.errorMessage = 'Không tải được thông tin đơn hàng.';
        this.isLoading = false;
        this.cdr.detectChanges();
      },
    });
  }

  get isMultiLeg(): boolean {
    return this.legs.length > 1;
  }

  get canAddMoreLegs(): boolean {
    return this.tripBuilder.canAddMoreLegs;
  }

  seatLabel(seat: IShowtimeSeat): string {
    return `${seat.seat?.rowLabel ?? ''}${seat.seat?.seatNumber ?? ''}`;
  }

  seatLabels(leg: ILegView): string {
    return leg.seats.map(seat => this.seatLabel(seat)).join(', ');
  }

  private get allSeats(): IShowtimeSeat[] {
    return this.legs.flatMap(leg => leg.seats);
  }

  get seatsTotal(): number {
    return this.allSeats.reduce((sum, seat) => sum + Number(seat.price ?? 0), 0);
  }

  get baggageTotal(): number {
    return Object.values(this.baggageBySeat).reduce((sum, kg) => sum + (this.priceForBaggage(kg) ?? 0), 0);
  }

  /** Neu ma dang nhap/dang chon khop voi 1 coupon ca nhan cua tai khoan thi tru ngay vao tong tien
   * hien thi (giong het cach BookingService.book() tinh o backend), khong can doi den luc thanh toan
   * moi thay gia thay doi. */
  get couponDiscount(): number {
    const matched = this.myCoupons.find(c => c.code === this.couponCode.trim());
    return matched ? matched.discount : 0;
  }

  get total(): number {
    return Math.max(0, this.seatsTotal + this.baggageTotal - this.couponDiscount);
  }

  baggageOf(seatId: number): number {
    return this.baggageBySeat[seatId] ?? 0;
  }

  /** Chon nhanh 1 coupon ca nhan (bam lai de bo chon) thay vi phai go tay ma giam gia. */
  selectCoupon(coupon: ILoyaltyCoupon): void {
    this.couponCode = this.couponCode === coupon.code ? '' : coupon.code;
  }

  /** Chọn (hoặc bỏ chọn nếu bấm lại) một gói hành lý cho một ghế. */
  toggleBaggage(seatId: number, weightKg: number): void {
    this.baggageBySeat[seatId] = this.baggageOf(seatId) === weightKg ? 0 : weightKg;
  }

  private priceForBaggage(weightKg: number): number {
    return this.baggageOptions.find(option => option.weightKg === weightKg)?.price ?? 0;
  }

  /** Quay lại trang tìm chuyến bay để chọn thêm một chặng nữa (chỉ áp dụng cho vé nhiều chặng). */
  addAnotherLeg(): void {
    // /events se tao lai component moi, phai truyen tripType tuong minh de loc dung theo cot
    // supportsMultiCity (lay tu TripBuilderService vi day la trang thai con song ca chuyen).
    this.router.navigate(['/events'], { queryParams: { tripType: this.tripBuilder.tripType() } });
  }

  /**
   * Gọi backend tạo booking PENDING + link VNPay, rồi chuyển hướng người dùng
   * sang cổng thanh toán.
   */
  pay(): void {
    if (this.legs.length === 0 || this.isSubmitting) {
      return;
    }

    this.isSubmitting = true;
    this.errorMessage = '';

    const legsRequest: ICheckoutLegRequest[] = this.legs.map(leg => {
      const baggageBySeat: Record<number, number> = {};
      for (const seat of leg.seats) {
        const weightKg = this.baggageOf(seat.id);
        if (weightKg > 0) {
          baggageBySeat[seat.id] = weightKg;
        }
      }
      return {
        showtimeId: leg.showtime.id,
        seatIds: leg.seats.map(seat => seat.id),
        baggageBySeat: Object.keys(baggageBySeat).length > 0 ? baggageBySeat : null,
      };
    });

    this.checkoutService
      .createPaymentUrl({
        legs: legsRequest,
        couponCode: this.couponCode.trim() || null,
      })
      .subscribe({
        next: paymentUrl => {
          this.tripBuilder.reset();
          this.checkoutService.redirectToGateway(paymentUrl);
        },
        error: (err: { error?: { title?: string; detail?: string } }) => {
          this.errorMessage = err.error?.title ?? err.error?.detail ?? 'Không tạo được giao dịch thanh toán.';
          this.isSubmitting = false;
          this.cdr.detectChanges();
        },
      });
  }

  cancel(): void {
    this.tripBuilder.reset();
    this.router.navigate(['/']);
  }
}
