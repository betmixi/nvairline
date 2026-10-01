import { ChangeDetectorRef, Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';

import { IEventTicketGroup, IMyTicket } from 'app/my-tickets/my-ticket.model';
import { MyTicketService } from 'app/my-tickets/my-ticket.service';
import { ITicketBaggageInfo } from 'app/entities/baggage/baggage.model';
import { BaggageService } from 'app/entities/baggage/baggage.service';
import { CheckoutService } from 'app/entities/payment/service/checkout.service';

/**
 * Trang "Hành lý trả trước": liệt kê vé đã mua, cho mua thêm hành lý ký gửi
 * cho từng vé rồi thanh toán qua VNPay.
 */
@Component({
  standalone: true,
  selector: 'jhi-baggage-tickets',
  imports: [CommonModule, RouterLink],
  templateUrl: './baggage-tickets.html',
  styleUrl: './baggage-tickets.scss',
})
export default class BaggageTicketsComponent implements OnInit {
  groups: IEventTicketGroup[] = [];

  isLoading = true;

  errorMessage = '';

  private readonly baggageInfos = new Map<number, ITicketBaggageInfo>();

  openTicketId: number | null = null;

  private readonly loadingInfo = new Set<number>();

  submittingTicketId: number | null = null;

  private readonly myTicketService = inject(MyTicketService);
  private readonly baggageService = inject(BaggageService);
  private readonly checkoutService = inject(CheckoutService);
  private readonly cdr = inject(ChangeDetectorRef);

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.isLoading = true;
    this.errorMessage = '';

    this.myTicketService.query({ page: 0, size: 100, sort: ['id,desc'] }).subscribe({
      next: response => {
        this.groups = this.groupByEvent((response.body ?? []).filter(ticket => ticket.status !== 'CANCELLED'));
        this.isLoading = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.errorMessage = 'Không tải được danh sách vé.';
        this.isLoading = false;
        this.cdr.detectChanges();
      },
    });
  }

  get totalTicketsCount(): number {
    return this.groups.reduce((sum, group) => sum + group.tickets.length, 0);
  }

  trackGroup(_index: number, group: IEventTicketGroup): string {
    return String(group.eventId ?? group.eventTitle);
  }

  trackTicket(_index: number, ticket: IMyTicket): number {
    return ticket.id;
  }

  baggageInfoOf(ticketId: number): ITicketBaggageInfo | null {
    return this.baggageInfos.get(ticketId) ?? null;
  }

  isLoadingInfo(ticketId: number): boolean {
    return this.loadingInfo.has(ticketId);
  }

  isOpen(ticketId: number): boolean {
    return this.openTicketId === ticketId;
  }

  togglePanel(ticket: IMyTicket): void {
    if (this.openTicketId === ticket.id) {
      this.openTicketId = null;
      return;
    }

    this.openTicketId = ticket.id;
    this.errorMessage = '';

    if (this.loadingInfo.has(ticket.id)) {
      return;
    }

    // Luôn tải lại: có thể vừa mua thêm hành lý ở lần mở trước.
    this.baggageInfos.delete(ticket.id);
    this.loadingInfo.add(ticket.id);

    this.baggageService.getBaggageInfo(ticket.id).subscribe({
      next: info => {
        this.baggageInfos.set(ticket.id, info);
        this.loadingInfo.delete(ticket.id);
        this.cdr.detectChanges();
      },
      error: err => {
        this.loadingInfo.delete(ticket.id);
        this.errorMessage = err.error?.title ?? 'Không tải được thông tin hành lý.';
        this.cdr.detectChanges();
      },
    });
  }

  choosePackage(ticket: IMyTicket, weightKg: number): void {
    if (this.submittingTicketId) {
      return;
    }

    this.submittingTicketId = ticket.id;
    this.errorMessage = '';

    this.baggageService.requestPurchase(ticket.id, weightKg).subscribe({
      next: response => {
        this.checkoutService.createPaymentUrl({ bookingId: response.bookingId }).subscribe({
          next: paymentUrl => {
            this.checkoutService.redirectToGateway(paymentUrl);
          },
          error: err => {
            this.submittingTicketId = null;
            this.errorMessage = err.error?.title ?? 'Không tạo được giao dịch thanh toán hành lý.';
            this.cdr.detectChanges();
          },
        });
      },
      error: err => {
        this.submittingTicketId = null;
        this.errorMessage = err.error?.title ?? 'Không tạo được yêu cầu mua hành lý.';
        this.cdr.detectChanges();
      },
    });
  }

  private groupByEvent(tickets: IMyTicket[]): IEventTicketGroup[] {
    const byEvent = new Map<string, IEventTicketGroup>();

    for (const ticket of tickets) {
      const key = String(ticket.eventId ?? ticket.eventTitle ?? 'unknown');
      let group = byEvent.get(key);

      if (!group) {
        group = {
          eventId: ticket.eventId ?? null,
          eventTitle: ticket.eventTitle ?? 'Chuyến bay không xác định',
          eventBanner: ticket.eventBanner,
          eventStartTime: ticket.eventStartTime,
          location: ticket.location,
          city: ticket.city,
          tickets: [],
        };
        byEvent.set(key, group);
      }

      group.tickets.push(ticket);
    }

    return [...byEvent.values()];
  }
}
