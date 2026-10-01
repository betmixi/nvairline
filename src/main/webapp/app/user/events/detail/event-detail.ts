import { ChangeDetectorRef, Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import dayjs from 'dayjs/esm';

import { AccountService } from 'app/core/auth/account.service';
import { EngagementService, IEventReview } from 'app/core/util/engagement.service';
import { EventService } from 'app/entities/event/service/event.service';
import { IEvent } from 'app/entities/event/event.model';
import { IShowtime } from 'app/entities/showtime/showtime.model';

/**
 * Trang xem chi tiet phim truoc khi mua ve. Khong can dang nhap.
 * Chi hien thi thong tin + gia ve tham khao; chon suat chieu, chon ghe va
 * thanh toan nam o cac trang /event/:id/showtimes va sau do.
 */
@Component({
  standalone: true,
  selector: 'jhi-user-event-detail',
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './event-detail.html',
  styleUrl: './event-detail.scss',
})
export default class UserEventDetailComponent implements OnInit {
  event: IEvent | null = null;

  isLoading = true;

  notFound = false;

  // ---- Yeu thich ----
  isFavorited = false;
  isTogglingFavorite = false;

  // ---- Danh gia ----
  reviews: IEventReview[] = [];
  averageRating: number | null = null;
  newRating = 5;
  newComment = '';
  isSubmittingReview = false;
  reviewError: string | null = null;
  reviewSuccess = false;

  /** Dang sua danh gia da co (khac null = dang o che do sua, gia tri la id danh gia). */
  editingReviewId: number | null = null;
  isDeletingReview = false;

  /** Suat chieu da duoc chon san tu the ket qua tim kiem (query param showtimeId), neu co. */
  private preselectedShowtimeId: number | null = null;

  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly eventService = inject(EventService);
  private readonly engagementService = inject(EngagementService);
  private readonly accountService = inject(AccountService);
  private readonly cdr = inject(ChangeDetectorRef);

  get isLoggedIn(): boolean {
    return this.accountService.isAuthenticated();
  }

  /**
   * Chi cac suat chieu con trong tuong lai (chua khoi hanh). event.showtimes[].startTime
   * khong duoc EventService boc lai thanh dayjs (chi ap dung cho truong ngay o cap Event),
   * nen o day co the la chuoi ISO thuan chu khong phai Dayjs.
   */
  get upcomingShowtimes(): IShowtime[] {
    return (this.event?.showtimes ?? []).filter(showtime => dayjs(showtime.startTime as never).isAfter(dayjs()));
  }

  get lowestPrice(): number | null {
    const prices = this.upcomingShowtimes
      .map(showtime => showtime.basePrice)
      .filter((price): price is number => price !== null && price !== undefined);

    return prices.length ? Math.min(...prices) : null;
  }

  /** Suat chieu cu the da chon tu the ket qua tim kiem, de hien ro ngay/gio/gia dang xac nhan mua. */
  get selectedShowtime(): IShowtime | null {
    if (this.preselectedShowtimeId === null) {
      return null;
    }
    return this.upcomingShowtimes.find(st => st.id === this.preselectedShowtimeId) ?? null;
  }

  formatDateTime(value: unknown): string {
    const d = dayjs(value as never);
    return d.isValid() ? d.format('HH:mm, dddd DD/MM/YYYY') : '';
  }

  get hasAvailableTickets(): boolean {
    return this.upcomingShowtimes.length > 0;
  }

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    const showtimeId = this.route.snapshot.queryParamMap.get('showtimeId');
    this.preselectedShowtimeId = showtimeId ? Number(showtimeId) : null;

    if (!id) {
      this.isLoading = false;
      this.notFound = true;
      return;
    }

    this.eventService.findPublic(id).subscribe({
      next: event => {
        this.event = event;
        this.isLoading = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.isLoading = false;
        this.notFound = true;
        this.cdr.detectChanges();
      },
    });

    this.loadReviews(id);

    // Trang thai yeu thich chi co y nghia khi da dang nhap.
    if (this.isLoggedIn) {
      this.engagementService.isFavorited(id).subscribe({
        next: favorited => {
          this.isFavorited = favorited;
          this.cdr.detectChanges();
        },
        error: () => undefined,
      });
    }
  }

  /** Ten hien thi cua nguoi danh gia: uu tien ho ten, khong co thi lay username. */
  reviewerName(review: IEventReview): string {
    const fullName = `${review.user?.firstName ?? ''} ${review.user?.lastName ?? ''}`.trim();
    return fullName || (review.user?.login ?? 'Người dùng');
  }

  isOwnReview(review: IEventReview): boolean {
    const login = this.accountService.account()?.login;
    return !!login && review.user?.login === login;
  }

  startEditReview(review: IEventReview): void {
    this.editingReviewId = review.id;
    this.newRating = review.rating;
    this.newComment = review.comment;
    this.reviewError = null;
    this.reviewSuccess = false;
  }

  cancelEditReview(): void {
    this.editingReviewId = null;
    this.newRating = 5;
    this.newComment = '';
    this.reviewError = null;
  }

  deleteReview(review: IEventReview): void {
    if (!this.event || this.isDeletingReview || !confirm('Xoá đánh giá này?')) {
      return;
    }

    this.isDeletingReview = true;
    const eventId = this.event.id;

    this.engagementService.deleteMyReview(review.id).subscribe({
      next: () => {
        this.isDeletingReview = false;
        this.loadReviews(eventId);
      },
      error: () => {
        this.isDeletingReview = false;
        this.cdr.detectChanges();
      },
    });
  }

  toggleFavorite(): void {
    if (!this.event || this.isTogglingFavorite) {
      return;
    }

    if (!this.isLoggedIn) {
      this.router.navigate(['/login']);
      return;
    }

    this.isTogglingFavorite = true;

    this.engagementService.toggleFavorite(this.event.id).subscribe({
      next: favorited => {
        this.isFavorited = favorited;
        this.isTogglingFavorite = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.isTogglingFavorite = false;
        this.cdr.detectChanges();
      },
    });
  }

  submitReview(): void {
    if (!this.event || this.isSubmittingReview) {
      return;
    }

    if (!this.newComment.trim()) {
      this.reviewError = 'Vui lòng nhập nhận xét.';
      return;
    }

    this.isSubmittingReview = true;
    this.reviewError = null;
    this.reviewSuccess = false;

    const eventId = this.event.id;
    const payload = { rating: this.newRating, comment: this.newComment.trim() };
    const request = this.editingReviewId
      ? this.engagementService.updateMyReview(this.editingReviewId, payload)
      : this.engagementService.createReview(eventId, payload);

    request.subscribe({
      next: () => {
        this.isSubmittingReview = false;
        this.reviewSuccess = true;
        this.newComment = '';
        this.newRating = 5;
        this.editingReviewId = null;
        this.loadReviews(eventId);
      },
      error: err => {
        this.isSubmittingReview = false;
        // Backend tra ve loi khi chua mua ve hoac da danh gia roi.
        const detail: string = err?.error?.detail ?? err?.error?.message ?? '';
        if (detail.includes('purchase')) {
          this.reviewError = 'Bạn cần mua vé sự kiện này trước khi đánh giá.';
        } else if (detail.includes('already reviewed')) {
          this.reviewError = 'Bạn đã đánh giá sự kiện này rồi.';
        } else {
          this.reviewError = 'Không gửi được đánh giá, vui lòng thử lại.';
        }
        this.cdr.detectChanges();
      },
    });
  }

  /**
   * Neu da chon san 1 gio bay cu the tu the ket qua tim kiem (query param showtimeId) thi
   * vao thang trang chon ghe cua dung gio bay do, khong bat chon lai lan nua. Tuong tu neu
   * su kien chi co dung 1 gio bay. Chi hien danh sach gio bay de chon khi co nhieu lua chon
   * va chua biet nguoi dung muon gio nao.
   */
  buyTicket(): void {
    if (!this.event) {
      return;
    }

    const upcoming = this.upcomingShowtimes;
    const preselected = this.preselectedShowtimeId !== null ? upcoming.find(st => st.id === this.preselectedShowtimeId) : undefined;

    if (preselected) {
      this.router.navigate(['/event', this.event.id, 'showtimes', preselected.id, 'seats']);
    } else if (upcoming.length === 1) {
      this.router.navigate(['/event', this.event.id, 'showtimes', upcoming[0].id, 'seats']);
    } else {
      this.router.navigate(['/event', this.event.id, 'showtimes']);
    }
  }

  private loadReviews(eventId: number): void {
    this.engagementService.getReviews(eventId).subscribe({
      next: reviews => {
        this.reviews = reviews;
        this.cdr.detectChanges();
      },
      error: () => undefined,
    });

    this.engagementService.getAverageRating(eventId).subscribe({
      next: rating => {
        this.averageRating = rating;
        this.cdr.detectChanges();
      },
      error: () => undefined,
    });
  }
}
