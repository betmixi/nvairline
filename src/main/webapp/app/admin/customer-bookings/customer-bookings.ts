import { ChangeDetectorRef, Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';

import { CustomerBookingService } from './customer-booking.service';
import { CustomerBookingType, ICustomerBooking } from './customer-booking.model';

const BOOKING_TYPE_LABELS: Record<CustomerBookingType, string> = {
  TICKET: '🎫 Đặt vé',
  BAGGAGE: '🧳 Hành lý',
  SEAT_UPGRADE: '💺 Nâng hạng',
  ADDON: '🛍️ Dịch vụ thêm',
};

/** Danh sach khach hang da dat ve - danh cho admin. */
@Component({
  standalone: true,
  selector: 'jhi-admin-customer-bookings',
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './customer-bookings.html',
  styleUrl: './customer-bookings.scss',
})
export class AdminCustomerBookingsComponent implements OnInit {
  bookings: ICustomerBooking[] = [];
  isLoading = true;

  keyword = '';
  statusFilter = 'all';
  typeFilter = 'all';

  private readonly customerBookingService = inject(CustomerBookingService);
  private readonly cdr = inject(ChangeDetectorRef);

  get statuses(): string[] {
    return Array.from(new Set(this.bookings.map(b => b.status))).filter(s => !!s);
  }

  get types(): CustomerBookingType[] {
    return Array.from(new Set(this.bookings.map(b => b.bookingType)));
  }

  get filteredBookings(): ICustomerBooking[] {
    const kw = this.keyword.trim().toLowerCase();

    return this.bookings.filter(b => {
      const matchStatus = this.statusFilter === 'all' || b.status === this.statusFilter;
      const matchType = this.typeFilter === 'all' || b.bookingType === this.typeFilter;

      if (!matchStatus || !matchType) {
        return false;
      }

      if (!kw) {
        return true;
      }

      return (
        this.customerName(b).toLowerCase().includes(kw) ||
        (b.customerEmail ?? '').toLowerCase().includes(kw) ||
        (b.flightTitle ?? '').toLowerCase().includes(kw)
      );
    });
  }

  get totalRevenue(): number {
    return this.bookings.reduce((sum, b) => sum + (b.totalAmount ?? 0), 0);
  }

  ngOnInit(): void {
    this.load();
  }

  customerName(booking: ICustomerBooking): string {
    const fullName = `${booking.customerFirstName ?? ''} ${booking.customerLastName ?? ''}`.trim();
    return fullName || (booking.customerLogin ?? 'Khách hàng');
  }

  route(booking: ICustomerBooking): string {
    return booking.legSummary ?? `${booking.departureAirportCode ?? '?'} → ${booking.arrivalAirportCode ?? '?'}`;
  }

  /** Thời gian mua chính xác nhất: ưu tiên thời điểm thanh toán thành công, chỉ dùng ngày đặt nếu chưa thanh toán. */
  purchaseTime(booking: ICustomerBooking): string {
    return booking.paymentDate ?? booking.bookingDate;
  }

  bookingTypeLabel(booking: ICustomerBooking): string {
    return this.bookingTypeLabelFor(booking.bookingType);
  }

  bookingTypeLabelFor(type: CustomerBookingType): string {
    return BOOKING_TYPE_LABELS[type] ?? type;
  }

  private load(): void {
    this.customerBookingService.getAll().subscribe({
      next: bookings => {
        this.bookings = bookings;
        this.isLoading = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.isLoading = false;
        this.cdr.detectChanges();
      },
    });
  }
}
