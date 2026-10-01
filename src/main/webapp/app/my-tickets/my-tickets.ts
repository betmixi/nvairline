import { ChangeDetectorRef, Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';

import { AccountService } from 'app/core/auth/account.service';
import { EngagementService } from 'app/core/util/engagement.service';
import { IEventTicketGroup, IMyTicket } from './my-ticket.model';
import { MyTicketService } from './my-ticket.service';

/**
 * Trang "Vé của tôi": liệt kê vé đã mua, nhóm theo sự kiện, kèm đủ thông tin
 * để đối chiếu (số vé, khách hàng, số hiệu máy bay, mã ghế).
 */
@Component({
  standalone: true,
  selector: 'jhi-my-tickets',
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './my-tickets.html',
  styleUrl: './my-tickets.scss',
})
export default class MyTicketsComponent implements OnInit {
  groups: IEventTicketGroup[] = [];

  /** Id cua cac booking co nhieu hon 1 chang (khu hoi/nhieu chang), dung de hien thi tag "Chặng x". */
  private multiLegBookingIds = new Set<number>();

  isLoading = true;

  errorMessage = '';

  /** Số vé tối đa tải về mỗi lần. */
  readonly pageSize = 100;

  private readonly myTicketService = inject(MyTicketService);
  private readonly engagementService = inject(EngagementService);
  private readonly accountService = inject(AccountService);
  private readonly cdr = inject(ChangeDetectorRef);

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.isLoading = true;
    this.errorMessage = '';

    this.myTicketService.query({ page: 0, size: this.pageSize, sort: ['id,desc'] }).subscribe({
      next: response => {
        const tickets = response.body ?? [];
        this.multiLegBookingIds = this.findMultiLegBookingIds(tickets);
        this.groups = this.groupByEvent(tickets);
        this.isLoading = false;
        this.cdr.detectChanges();
        this.loadMyReviews();
      },
      error: () => {
        this.errorMessage = 'Không tải được danh sách vé.';
        this.isLoading = false;
        this.cdr.detectChanges();
      },
    });
  }

  /** Vé đã dùng thì không cho quét nữa. */
  isUsable(ticket: IMyTicket): boolean {
    return !ticket.checkedIn && ticket.status !== 'CANCELLED';
  }

  /** Vé này thuộc một booking khứ hồi/nhiều chặng (đặt cùng lúc nhiều chuyến bay khác nhau). */
  isMultiLeg(ticket: IMyTicket): boolean {
    return ticket.bookingId != null && this.multiLegBookingIds.has(ticket.bookingId);
  }

  /** Nhãn hiển thị "Chặng x" cho vé thuộc booking nhiều chặng. */
  legLabel(ticket: IMyTicket): string {
    return `Chặng ${(ticket.legIndex ?? 0) + 1}`;
  }

  /** Tìm id các booking có nhiều hơn 1 giá trị legIndex khác nhau (tức khứ hồi/nhiều chặng). */
  private findMultiLegBookingIds(tickets: IMyTicket[]): Set<number> {
    const legIndexesByBooking = new Map<number, Set<number>>();

    for (const ticket of tickets) {
      if (ticket.bookingId == null || ticket.legIndex == null) {
        continue;
      }
      const legIndexes = legIndexesByBooking.get(ticket.bookingId) ?? new Set<number>();
      legIndexes.add(ticket.legIndex);
      legIndexesByBooking.set(ticket.bookingId, legIndexes);
    }

    const multiLegIds = new Set<number>();
    for (const [bookingId, legIndexes] of legIndexesByBooking) {
      if (legIndexes.size > 1) {
        multiLegIds.add(bookingId);
      }
    }
    return multiLegIds;
  }

  trackGroup(_index: number, group: IEventTicketGroup): string {
    return String(group.eventId ?? group.eventTitle);
  }

  /** Tải đánh giá của chính người dùng hiện tại cho từng sự kiện đã có vé, để hiện sẵn nếu đã đánh giá rồi. */
  private loadMyReviews(): void {
    const login = this.accountService.account()?.login;
    if (!login) {
      return;
    }

    for (const group of this.groups) {
      if (group.eventId == null) {
        continue;
      }

      this.engagementService.getReviews(group.eventId).subscribe(reviews => {
        const mine = reviews.find(review => review.user?.login === login);
        if (mine) {
          group.myReview = mine;
          group.reviewRating = mine.rating;
          group.reviewComment = mine.comment;
        }
        this.cdr.detectChanges();
      });
    }
  }

  toggleReviewForm(group: IEventTicketGroup): void {
    group.reviewFormOpen = !group.reviewFormOpen;
    group.reviewError = null;

    if (group.reviewFormOpen && group.reviewRating == null) {
      group.reviewRating = group.myReview?.rating ?? 5;
      group.reviewComment = group.myReview?.comment ?? '';
    }
  }

  setRating(group: IEventTicketGroup, star: number): void {
    group.reviewRating = star;
  }

  submitReview(group: IEventTicketGroup): void {
    if (group.eventId == null || group.isSubmittingReview) {
      return;
    }

    const comment = (group.reviewComment ?? '').trim();
    if (!comment) {
      group.reviewError = 'Vui lòng nhập nhận xét.';
      return;
    }

    group.isSubmittingReview = true;
    group.reviewError = null;

    const payload = { rating: group.reviewRating ?? 5, comment };
    const request = group.myReview
      ? this.engagementService.updateMyReview(group.myReview.id, payload)
      : this.engagementService.createReview(group.eventId, payload);

    request.subscribe({
      next: review => {
        group.myReview = review;
        group.reviewFormOpen = false;
        group.isSubmittingReview = false;
        this.cdr.detectChanges();
      },
      error: (err: { error?: { detail?: string } }) => {
        const detail = err.error?.detail ?? '';
        group.reviewError = detail.includes('already reviewed')
          ? 'Bạn đã đánh giá chuyến bay này rồi.'
          : 'Không gửi được đánh giá, vui lòng thử lại.';
        group.isSubmittingReview = false;
        this.cdr.detectChanges();
      },
    });
  }

  deleteMyReview(group: IEventTicketGroup): void {
    if (!group.myReview || group.isDeletingReview || !confirm('Xoá đánh giá này?')) {
      return;
    }

    group.isDeletingReview = true;

    this.engagementService.deleteMyReview(group.myReview.id).subscribe({
      next: () => {
        group.myReview = null;
        group.reviewRating = undefined;
        group.reviewComment = '';
        group.isDeletingReview = false;
        this.cdr.detectChanges();
      },
      error: () => {
        group.isDeletingReview = false;
        this.cdr.detectChanges();
      },
    });
  }

  cancelReviewForm(group: IEventTicketGroup): void {
    group.reviewFormOpen = false;
    group.reviewError = null;
    if (group.myReview) {
      group.reviewRating = group.myReview.rating;
      group.reviewComment = group.myReview.comment;
    }
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
