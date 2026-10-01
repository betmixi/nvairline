import { HttpResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable, finalize, map } from 'rxjs';

import { IBooking } from 'app/entities/booking/booking.model';
import { BookingService } from 'app/entities/booking/service/booking.service';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { IBookingDetail } from '../booking-detail.model';
import { BookingDetailService } from '../service/booking-detail.service';

import { BookingDetailFormGroup, BookingDetailFormService } from './booking-detail-form.service';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'jhi-booking-detail-update',
  templateUrl: './booking-detail-update.html',
  imports: [TranslateDirective, TranslatePipe, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class BookingDetailUpdate implements OnInit {
  readonly isSaving = signal(false);
  bookingDetail: IBookingDetail | null = null;

  bookingsSharedCollection = signal<IBooking[]>([]);

  protected bookingDetailService = inject(BookingDetailService);
  protected bookingDetailFormService = inject(BookingDetailFormService);
  protected bookingService = inject(BookingService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: BookingDetailFormGroup = this.bookingDetailFormService.createBookingDetailFormGroup();

  compareBooking = (o1: IBooking | null, o2: IBooking | null): boolean => this.bookingService.compareBooking(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ bookingDetail }) => {
      this.bookingDetail = bookingDetail;
      if (bookingDetail) {
        this.updateForm(bookingDetail);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const bookingDetail = this.bookingDetailFormService.getBookingDetail(this.editForm);
    if (bookingDetail.id === null) {
      this.subscribeToSaveResponse(this.bookingDetailService.create(bookingDetail));
    } else {
      this.subscribeToSaveResponse(this.bookingDetailService.update(bookingDetail));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IBookingDetail | null>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Cuon len thong bao loi (jhi-alert-error) de nguoi dung nhin thay ly do luu that bai (trung, de trong, so am...).
    setTimeout(() => document.querySelector('jhi-alert-error')?.scrollIntoView({ behavior: 'smooth', block: 'center' }), 100);
  }

  protected onSaveFinalize(): void {
    this.isSaving.set(false);
  }

  protected updateForm(bookingDetail: IBookingDetail): void {
    this.bookingDetail = bookingDetail;
    this.bookingDetailFormService.resetForm(this.editForm, bookingDetail);

    this.bookingsSharedCollection.update(bookings =>
      this.bookingService.addBookingToCollectionIfMissing<IBooking>(bookings, bookingDetail.booking),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.bookingService
      .query()
      .pipe(map((res: HttpResponse<IBooking[]>) => res.body ?? []))
      .pipe(
        map((bookings: IBooking[]) => this.bookingService.addBookingToCollectionIfMissing<IBooking>(bookings, this.bookingDetail?.booking)),
      )
      .subscribe((bookings: IBooking[]) => this.bookingsSharedCollection.set(bookings));
  }
}
