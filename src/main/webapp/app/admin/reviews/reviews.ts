import { ChangeDetectorRef, Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';

import { EngagementService, IManagedReview } from 'app/core/util/engagement.service';

/** Quan ly toan bo danh gia trong he thong - danh cho admin. */
@Component({
  standalone: true,
  selector: 'jhi-admin-reviews',
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './reviews.html',
  styleUrl: './reviews.scss',
})
export class AdminReviewsComponent implements OnInit {
  reviews: IManagedReview[] = [];
  isLoading = true;

  /** Bo loc: 0 = tat ca sao. */
  ratingFilter = 0;
  keyword = '';

  togglingId: number | null = null;

  replyingId: number | null = null;
  replyDraft = '';
  isSavingReply = false;

  private readonly engagementService = inject(EngagementService);
  private readonly cdr = inject(ChangeDetectorRef);

  get filteredReviews(): IManagedReview[] {
    const kw = this.keyword.trim().toLowerCase();

    return this.reviews.filter(r => {
      const matchRating = this.ratingFilter === 0 || r.rating === this.ratingFilter;

      if (!matchRating) {
        return false;
      }

      if (!kw) {
        return true;
      }

      return (
        (r.event?.title ?? '').toLowerCase().includes(kw) ||
        r.comment.toLowerCase().includes(kw) ||
        this.reviewerName(r).toLowerCase().includes(kw)
      );
    });
  }

  get averageRating(): number {
    if (this.reviews.length === 0) {
      return 0;
    }
    return this.reviews.reduce((sum, r) => sum + r.rating, 0) / this.reviews.length;
  }

  ngOnInit(): void {
    this.load();
  }

  reviewerName(review: IManagedReview): string {
    const fullName = `${review.user?.firstName ?? ''} ${review.user?.lastName ?? ''}`.trim();
    return fullName || (review.user?.login ?? 'Người dùng');
  }

  toggleHidden(review: IManagedReview): void {
    if (this.togglingId !== null) {
      return;
    }

    this.togglingId = review.id;
    const nextHidden = !review.hidden;

    this.engagementService.setReviewHidden(review.id, nextHidden).subscribe({
      next: () => {
        review.hidden = nextHidden;
        this.togglingId = null;
        this.cdr.detectChanges();
      },
      error: () => {
        this.togglingId = null;
        this.cdr.detectChanges();
      },
    });
  }

  startReply(review: IManagedReview): void {
    this.replyingId = review.id;
    this.replyDraft = review.reply ?? '';
  }

  cancelReply(): void {
    this.replyingId = null;
    this.replyDraft = '';
  }

  submitReply(review: IManagedReview): void {
    if (this.isSavingReply || !this.replyDraft.trim()) {
      return;
    }

    this.isSavingReply = true;

    this.engagementService.replyToReview(review.id, this.replyDraft.trim()).subscribe({
      next: updated => {
        review.reply = updated.reply;
        review.repliedDate = updated.repliedDate;
        this.isSavingReply = false;
        this.replyingId = null;
        this.replyDraft = '';
        this.cdr.detectChanges();
      },
      error: () => {
        this.isSavingReply = false;
        this.cdr.detectChanges();
      },
    });
  }

  private load(): void {
    this.engagementService.getAllReviewsForAdmin().subscribe({
      next: reviews => {
        this.reviews = reviews;
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
