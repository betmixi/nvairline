import { ChangeDetectorRef, Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';

import { IEventTicketGroup, IMyTicket } from './my-ticket.model';
import { MyTicketService } from './my-ticket.service';

/**
 * Trang "Vé của tôi": liệt kê vé đã mua, nhóm theo sự kiện, kèm mã QR để quét
 * tại cổng sự kiện.
 */
@Component({
  standalone: true,
  selector: 'jhi-my-tickets',
  imports: [CommonModule, RouterLink],
  templateUrl: './my-tickets.html',
  styleUrl: './my-tickets.scss',
})
export default class MyTicketsComponent implements OnInit {
  groups: IEventTicketGroup[] = [];

  isLoading = true;

  errorMessage = '';

  /** Số vé tối đa tải về mỗi lần. */
  readonly pageSize = 100;

  /** Ảnh QR đã tải, khoá là id của vé. Tránh gọi lại API khi mở lần thứ hai. */
  private readonly qrImages = new Map<number, string>();

  /** Các vé đang chờ tải ảnh QR. */
  private readonly loadingQr = new Set<number>();

  private readonly myTicketService = inject(MyTicketService);
  private readonly cdr = inject(ChangeDetectorRef);

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.isLoading = true;
    this.errorMessage = '';

    this.myTicketService.query({ page: 0, size: this.pageSize, sort: ['id,desc'] }).subscribe({
      next: response => {
        this.groups = this.groupByEvent(response.body ?? []);
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

  /** Ảnh QR của vé, hoặc null nếu chưa tải xong. */
  qrImageOf(ticketId: number): string | null {
    return this.qrImages.get(ticketId) ?? null;
  }

  isQrLoading(ticketId: number): boolean {
    return this.loadingQr.has(ticketId);
  }

  /** Tải ảnh QR khi người dùng bấm xem. */
  showQr(ticket: IMyTicket): void {
    const id = ticket.id;

    if (this.qrImages.has(id) || this.loadingQr.has(id)) {
      return;
    }

    this.loadingQr.add(id);

    this.myTicketService.qrImage(id).subscribe({
      next: dataUri => {
        this.qrImages.set(id, dataUri);
        this.loadingQr.delete(id);
        this.cdr.detectChanges();
      },
      error: () => {
        this.loadingQr.delete(id);
        this.errorMessage = 'Không tải được mã QR của vé.';
        this.cdr.detectChanges();
      },
    });
  }

  /** Vé đã dùng thì không cho quét nữa. */
  isUsable(ticket: IMyTicket): boolean {
    return !ticket.checkedIn && ticket.status !== 'CANCELLED';
  }

  trackGroup(_index: number, group: IEventTicketGroup): string {
    return String(group.eventId ?? group.eventTitle);
  }

  trackTicket(_index: number, ticket: IMyTicket): number {
    return ticket.id;
  }

  /** Gom vé cùng một sự kiện vào một thẻ. */
  private groupByEvent(tickets: IMyTicket[]): IEventTicketGroup[] {
    const byEvent = new Map<string, IEventTicketGroup>();

    for (const ticket of tickets) {
      const key = String(ticket.eventId ?? ticket.eventTitle ?? 'unknown');
      let group = byEvent.get(key);

      if (!group) {
        group = {
          eventId: ticket.eventId ?? null,
          eventTitle: ticket.eventTitle ?? 'Sự kiện không xác định',
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
