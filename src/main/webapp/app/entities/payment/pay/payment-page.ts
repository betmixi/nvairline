import { ChangeDetectorRef, Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';

import { TicketTypeService } from 'app/entities/ticket-type/service/ticket-type.service';
import { ITicketType } from 'app/entities/ticket-type/ticket-type.model';
import { CheckoutService } from '../service/checkout.service';

/**
 * Trang xác nhận đơn hàng trước khi chuyển sang cổng thanh toán VNPay.
 */
@Component({
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './payment-page.html',
  styleUrl: './payment-page.scss',
})
export default class PaymentPageComponent implements OnInit {
  ticket?: ITicketType;

  quantity = 1;

  couponCode = '';

  isLoading = true;

  /** Đang gọi API tạo link thanh toán. */
  isSubmitting = false;

  errorMessage = '';

  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly ticketService = inject(TicketTypeService);
  private readonly checkoutService = inject(CheckoutService);
  private readonly cdr = inject(ChangeDetectorRef);

  ngOnInit(): void {
    const ticketId = Number(this.route.snapshot.queryParamMap.get('ticketTypeId'));

    this.quantity = Number(this.route.snapshot.queryParamMap.get('quantity') ?? 1);

    if (!ticketId) {
      this.isLoading = false;
      return;
    }

    this.loadTicket(ticketId);
  }

  loadTicket(ticketId: number): void {
    this.isLoading = true;

    this.ticketService.find(ticketId).subscribe({
      next: ticket => {
        this.ticket = ticket;
        this.isLoading = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.errorMessage = 'Không tải được thông tin vé.';
        this.isLoading = false;
        this.cdr.detectChanges();
      },
    });
  }

  get total(): number {
    const price = this.ticket ? (this.ticket.price ?? 0) : 0;
    return Number(price) * this.quantity;
  }

  /**
   * Gọi backend tạo booking PENDING + link VNPay, rồi chuyển hướng người dùng
   * sang cổng thanh toán.
   */
  pay(): void {
    const ticketTypeId = this.ticket ? this.ticket.id : null;

    if (!ticketTypeId || this.isSubmitting) {
      return;
    }

    this.isSubmitting = true;
    this.errorMessage = '';

    this.checkoutService
      .createPaymentUrl({
        ticketTypeId,
        quantity: this.quantity,
        couponCode: this.couponCode.trim() || null,
      })
      .subscribe({
        next: paymentUrl => {
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
    this.router.navigate(['/']);
  }
}
