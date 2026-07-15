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
import { IPayment } from '../payment.model';
import { PaymentService } from '../service/payment.service';

import { PaymentFormGroup, PaymentFormService } from './payment-form.service';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'jhi-payment-update',
  templateUrl: './payment-update.html',
  imports: [TranslateDirective, TranslatePipe, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class PaymentUpdate implements OnInit {
  readonly isSaving = signal(false);
  payment: IPayment | null = null;

  bookingsSharedCollection = signal<IBooking[]>([]);

  protected paymentService = inject(PaymentService);
  protected paymentFormService = inject(PaymentFormService);
  protected bookingService = inject(BookingService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: PaymentFormGroup = this.paymentFormService.createPaymentFormGroup();

  compareBooking = (o1: IBooking | null, o2: IBooking | null): boolean => this.bookingService.compareBooking(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ payment }) => {
      this.payment = payment;
      if (payment) {
        this.updateForm(payment);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const payment = this.paymentFormService.getPayment(this.editForm);
    if (payment.id === null) {
      this.subscribeToSaveResponse(this.paymentService.create(payment));
    } else {
      this.subscribeToSaveResponse(this.paymentService.update(payment));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IPayment | null>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving.set(false);
  }

  protected updateForm(payment: IPayment): void {
    this.payment = payment;
    this.paymentFormService.resetForm(this.editForm, payment);

    this.bookingsSharedCollection.update(bookings =>
      this.bookingService.addBookingToCollectionIfMissing<IBooking>(bookings, payment.booking),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.bookingService
      .query()
      .pipe(map((res: HttpResponse<IBooking[]>) => res.body ?? []))
      .pipe(map((bookings: IBooking[]) => this.bookingService.addBookingToCollectionIfMissing<IBooking>(bookings, this.payment?.booking)))
      .subscribe((bookings: IBooking[]) => this.bookingsSharedCollection.set(bookings));
  }
}
