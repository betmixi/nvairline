import { ChangeDetectorRef, Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';

import { IEventTicketGroup, IMyTicket } from 'app/my-tickets/my-ticket.model';
import { MyTicketService } from 'app/my-tickets/my-ticket.service';
import { IAddonCatalogItem, ITicketAddonInfo } from 'app/entities/ticket-addon/ticket-addon.model';
import { TicketAddonService } from 'app/entities/ticket-addon/ticket-addon.service';
import { CheckoutService } from 'app/entities/payment/service/checkout.service';

/**
 * Trang dịch vụ bổ trợ dùng chung cho 4 loại: mua sắm, khách sạn & tour, bảo
 * hiểm du lịch, dịch vụ khác. Loại cụ thể + tiêu đề + icon lấy từ route data.
 */
@Component({
  standalone: true,
  selector: 'jhi-addon-tickets',
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './addon-tickets.html',
  styleUrl: './addon-tickets.scss',
})
export default class AddonTicketsComponent implements OnInit {
  addonType = '';
  pageTitle = '';
  pageIcon = '';

  groups: IEventTicketGroup[] = [];

  isLoading = true;

  errorMessage = '';

  private readonly addonInfos = new Map<number, ITicketAddonInfo>();

  openTicketId: number | null = null;

  /** Giỏ hàng đang chọn cho vé đang mở, khoá là itemCode. */
  cart: Record<string, number> = {};

  private readonly loadingInfo = new Set<number>();

  submittingTicketId: number | null = null;

  private readonly route = inject(ActivatedRoute);
  private readonly myTicketService = inject(MyTicketService);
  private readonly ticketAddonService = inject(TicketAddonService);
  private readonly checkoutService = inject(CheckoutService);
  private readonly cdr = inject(ChangeDetectorRef);

  ngOnInit(): void {
    this.addonType = this.route.snapshot.data['addonType'];
    this.pageTitle = this.route.snapshot.data['pageTitle'];
    this.pageIcon = this.route.snapshot.data['pageIcon'];

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

  addonInfoOf(ticketId: number): ITicketAddonInfo | null {
    return this.addonInfos.get(ticketId) ?? null;
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
    this.cart = {};
    this.errorMessage = '';

    if (this.loadingInfo.has(ticket.id)) {
      return;
    }

    this.addonInfos.delete(ticket.id);
    this.loadingInfo.add(ticket.id);

    this.ticketAddonService.getInfo(ticket.id, this.addonType).subscribe({
      next: info => {
        this.addonInfos.set(ticket.id, info);
        this.loadingInfo.delete(ticket.id);
        this.cdr.detectChanges();
      },
      error: err => {
        this.loadingInfo.delete(ticket.id);
        this.errorMessage = err.error?.title ?? 'Không tải được thông tin dịch vụ.';
        this.cdr.detectChanges();
      },
    });
  }

  quantityOf(itemCode: string): number {
    return this.cart[itemCode] ?? 0;
  }

  /** true nếu mặt hàng chỉ chọn được 1 lần (bảo hiểm, dịch vụ khác) thay vì có số lượng. */
  isToggleOnly(item: IAddonCatalogItem): boolean {
    return item.maxQuantity <= 1;
  }

  toggleItem(item: IAddonCatalogItem): void {
    this.cart[item.itemCode] = this.quantityOf(item.itemCode) > 0 ? 0 : 1;
  }

  changeQuantity(item: IAddonCatalogItem, delta: number): void {
    const next = this.quantityOf(item.itemCode) + delta;
    this.cart[item.itemCode] = Math.max(0, Math.min(item.maxQuantity, next));
  }

  cartTotal(catalog: IAddonCatalogItem[]): number {
    return catalog.reduce((sum, item) => sum + item.unitPrice * this.quantityOf(item.itemCode), 0);
  }

  hasCartItems(): boolean {
    return Object.values(this.cart).some(qty => qty > 0);
  }

  checkout(ticket: IMyTicket): void {
    if (this.submittingTicketId || !this.hasCartItems()) {
      return;
    }

    const items = Object.entries(this.cart)
      .filter(([, quantity]) => quantity > 0)
      .map(([itemCode, quantity]) => ({ itemCode, quantity }));

    this.submittingTicketId = ticket.id;
    this.errorMessage = '';

    this.ticketAddonService.requestPurchase(ticket.id, this.addonType, items).subscribe({
      next: response => {
        this.checkoutService.createPaymentUrl({ bookingId: response.bookingId }).subscribe({
          next: paymentUrl => {
            this.checkoutService.redirectToGateway(paymentUrl);
          },
          error: err => {
            this.submittingTicketId = null;
            this.errorMessage = err.error?.title ?? 'Không tạo được giao dịch thanh toán.';
            this.cdr.detectChanges();
          },
        });
      },
      error: err => {
        this.submittingTicketId = null;
        this.errorMessage = err.error?.title ?? 'Không tạo được yêu cầu mua dịch vụ.';
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
