import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, map } from 'rxjs';

import { IEvent } from 'app/entities/event/event.model';

export interface IEventReview {
  id: number;
  rating: number;
  comment: string;
  createdDate: string;
  reply?: string | null;
  repliedDate?: string | null;
  user?: { id: number; login?: string | null; firstName?: string | null; lastName?: string | null } | null;
}

export interface INewReview {
  rating: number;
  comment: string;
}

/** Danh gia kem thong tin su kien - dung cho trang quan ly cua admin. */
export interface IManagedReview extends IEventReview {
  event?: { id: number; title?: string | null } | null;
  hidden?: boolean | null;
}

/**
 * Danh gia va yeu thich su kien cho nguoi dung thuong.
 */
@Injectable({ providedIn: 'root' })
export class EngagementService {
  private readonly http = inject(HttpClient);

  // ---------- Reviews ----------

  getReviews(eventId: number): Observable<IEventReview[]> {
    return this.http.get<IEventReview[]>(`/api/reviews/events/${eventId}`);
  }

  getAverageRating(eventId: number): Observable<number | null> {
    return this.http.get<number | null>(`/api/reviews/events/${eventId}/rating`);
  }

  createReview(eventId: number, review: INewReview): Observable<IEventReview> {
    return this.http.post<IEventReview>(`/api/reviews/events/${eventId}`, review);
  }

  /** Sua danh gia cua chinh minh. */
  updateMyReview(reviewId: number, review: INewReview): Observable<IEventReview> {
    return this.http.put<IEventReview>(`/api/reviews/mine/${reviewId}`, review);
  }

  /** Xoa danh gia cua chinh minh. */
  deleteMyReview(reviewId: number): Observable<void> {
    return this.http.delete<void>(`/api/reviews/mine/${reviewId}`);
  }

  // ---------- Quan ly danh gia (admin) ----------

  /** Toan bo danh gia trong he thong (chi admin). */
  getAllReviewsForAdmin(): Observable<IManagedReview[]> {
    return this.http.get<IManagedReview[]>('/api/reviews/admin/all');
  }

  deleteReview(reviewId: number): Observable<void> {
    return this.http.delete<void>(`/api/reviews/${reviewId}`);
  }

  setReviewHidden(reviewId: number, hidden: boolean): Observable<IManagedReview> {
    return this.http.patch<IManagedReview>(`/api/reviews/${reviewId}/hidden`, { hidden });
  }

  replyToReview(reviewId: number, reply: string): Observable<IManagedReview> {
    return this.http.patch<IManagedReview>(`/api/reviews/${reviewId}/reply`, { reply });
  }

  // ---------- Favorites ----------

  isFavorited(eventId: number): Observable<boolean> {
    return this.http.get<{ favorited: boolean }>(`/api/favorites/events/${eventId}`).pipe(map(res => res.favorited));
  }

  toggleFavorite(eventId: number): Observable<boolean> {
    return this.http.post<{ favorited: boolean }>(`/api/favorites/events/${eventId}/toggle`, {}).pipe(map(res => res.favorited));
  }

  myFavorites(): Observable<IEvent[]> {
    return this.http.get<IEvent[]>('/api/favorites/my');
  }
}
