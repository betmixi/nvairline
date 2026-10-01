import { ChangeDetectorRef, Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';

import { BookingService } from 'app/entities/booking/service/booking.service';
import { IBooking } from 'app/entities/booking/booking.model';
import { IMyTicket } from 'app/my-tickets/my-ticket.model';
import { MyTicketService } from 'app/my-tickets/my-ticket.service';

/**
 * Trang hiển thị kết quả sau khi người dùng thanh toán xong trên VNPay.
 *
 * Backend (`/api/payments/vnpay-return`) sẽ redirect về đây kèm query param:
 * `success`, `bookingId`, `message`.
 */
@Component({
  standalone: true,
  selector: 'jhi-payment-result',
  imports: [CommonModule],
  templateUrl: './payment-result.html',
  styleUrl: './payment-result.scss',
})
export default class PaymentResultComponent implements OnInit {
  success = false;

  message = '';

  bookingId: number | null = null;

  booking: IBooking | null = null;

  tickets: IMyTicket[] = [];

  isLoading = true;

  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly cdr = inject(ChangeDetectorRef);
  private readonly bookingService = inject(BookingService);
  private readonly myTicketService = inject(MyTicketService);

  ngOnInit(): void {
    const params = this.route.snapshot.queryParamMap;

    this.success = params.get('success') === 'true';
    this.message = params.get('message') ?? (this.success ? 'Thanh toán thành công' : 'Thanh toán không thành công');

    const bookingId = Number(params.get('bookingId'));
    this.bookingId = Number.isFinite(bookingId) && bookingId > 0 ? bookingId : null;

    if (this.bookingId === null) {
      this.isLoading = false;
      return;
    }

    this.loadBooking(this.bookingId);

    if (this.success) {
      this.loadTickets(this.bookingId);
    }
  }

  /** Tải lại booking để hiển thị số tiền và trạng thái đã chốt ở server. */
  loadBooking(bookingId: number): void {
    this.bookingService.findMine(bookingId).subscribe({
      next: booking => {
        this.booking = booking;
        this.isLoading = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.isLoading = false;
        this.cdr.detectChanges();
      },
    });
  }

  /** Lấy vé vừa phát hành để hiện QR ngay tại trang kết quả, không cần qua "Vé của tôi". */
  loadTickets(bookingId: number): void {
    this.myTicketService.query({ page: 0, size: 100, sort: ['id,asc'] }).subscribe({
      next: response => {
        this.tickets = (response.body ?? []).filter(ticket => ticket.bookingId === bookingId);
        this.cdr.detectChanges();
      },
    });
  }

  goHome(): void {
    this.router.navigate(['/']);
  }

  goToMyTickets(): void {
    this.router.navigate(['/my-tickets']);
  }
}
