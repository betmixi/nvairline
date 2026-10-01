import { ChangeDetectorRef, Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';

import { IEventTicketGroup, IMyTicket } from 'app/my-tickets/my-ticket.model';
import { MyTicketService } from 'app/my-tickets/my-ticket.service';
import { ITicketUpgradeInfo } from 'app/entities/upgrade/upgrade.model';
import { UpgradeService } from 'app/entities/upgrade/upgrade.service';
import { CheckoutService } from 'app/entities/payment/service/checkout.service';

/**
 * Trang "Nâng hạng ghế": liệt kê vé đã mua, cho chọn hạng ghế cao hơn cùng
 * chuyến bay rồi thu thêm phần chênh lệch giá qua VNPay.
 */
@Component({
  standalone: true,
  selector: 'jhi-upgrade-tickets',
  imports: [CommonModule, RouterLink],
  templateUrl: './upgrade-tickets.html',
  styleUrl: './upgrade-tickets.scss',
})
export default class UpgradeTicketsComponent implements OnInit {
  groups: IEventTicketGroup[] = [];

  isLoading = true;

  errorMessage = '';

  /** Thông tin nâng hạng đã tải, khoá là id vé. */
  private readonly upgradeInfos = new Map<number, ITicketUpgradeInfo>();

  /** Vé đang mở khung chọn hạng ghế. */
  openTicketId: number | null = null;

  /** Vé đang tải thông tin nâng hạng. */
  private readonly loadingInfo = new Set<number>();

  /** Vé đang gửi yêu cầu nâng hạng / chuyển sang VNPay. */
  submittingTicketId: number | null = null;

  private readonly myTicketService = inject(MyTicketService);
  private readonly upgradeService = inject(UpgradeService);
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

  upgradeInfoOf(ticketId: number): ITicketUpgradeInfo | null {
    return this.upgradeInfos.get(ticketId) ?? null;
  }

  isLoadingInfo(ticketId: number): boolean {
    return this.loadingInfo.has(ticketId);
  }

  isOpen(ticketId: number): boolean {
    return this.openTicketId === ticketId;
  }

  /** Mở/đóng khung nâng hạng của một vé, tải thông tin nếu chưa có. */
  toggleUpgradePanel(ticket: IMyTicket): void {
    if (this.openTicketId === ticket.id) {
      this.openTicketId = null;
      return;
    }

    this.openTicketId = ticket.id;
    this.errorMessage = '';

    if (this.upgradeInfos.has(ticket.id) || this.loadingInfo.has(ticket.id)) {
      return;
    }

    this.loadingInfo.add(ticket.id);

    this.upgradeService.getUpgradeInfo(ticket.id).subscribe({
      next: info => {
        this.upgradeInfos.set(ticket.id, info);
        this.loadingInfo.delete(ticket.id);
        this.cdr.detectChanges();
      },
      error: err => {
        this.loadingInfo.delete(ticket.id);
        this.errorMessage = err.error?.title ?? 'Không tải được thông tin nâng hạng.';
        this.cdr.detectChanges();
      },
    });
  }

  /** Chọn hạng ghế mới: tạo yêu cầu nâng hạng rồi chuyển sang VNPay thu phần chênh lệch. */
  chooseUpgrade(ticket: IMyTicket, targetSeatType: string): void {
    if (this.submittingTicketId) {
      return;
    }

    this.submittingTicketId = ticket.id;
    this.errorMessage = '';

    this.upgradeService.requestUpgrade(ticket.id, targetSeatType).subscribe({
      next: response => {
        this.checkoutService.createPaymentUrl({ bookingId: response.bookingId }).subscribe({
          next: paymentUrl => {
            this.checkoutService.redirectToGateway(paymentUrl);
          },
          error: err => {
            this.submittingTicketId = null;
            this.errorMessage = err.error?.title ?? 'Không tạo được giao dịch thanh toán phần chênh lệch.';
            this.cdr.detectChanges();
          },
        });
      },
      error: err => {
        this.submittingTicketId = null;
        this.errorMessage = err.error?.title ?? 'Không tạo được yêu cầu nâng hạng.';
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
