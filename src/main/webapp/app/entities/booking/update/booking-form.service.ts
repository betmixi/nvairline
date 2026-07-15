import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';

import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IBooking, NewBooking } from '../booking.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IBooking for edit and NewBookingFormGroupInput for create.
 */
type BookingFormGroupInput = IBooking | PartialWithRequiredKeyOf<NewBooking>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IBooking | NewBooking> = Omit<T, 'bookingDate'> & {
  bookingDate?: string | null;
};

type BookingFormRawValue = FormValueOf<IBooking>;

type NewBookingFormRawValue = FormValueOf<NewBooking>;

type BookingFormDefaults = Pick<NewBooking, 'id' | 'bookingDate'>;

type BookingFormGroupContent = {
  id: FormControl<BookingFormRawValue['id'] | NewBooking['id']>;
  bookingDate: FormControl<BookingFormRawValue['bookingDate']>;
  totalAmount: FormControl<BookingFormRawValue['totalAmount']>;
  status: FormControl<BookingFormRawValue['status']>;
  user: FormControl<BookingFormRawValue['user']>;
};

export type BookingFormGroup = FormGroup<BookingFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class BookingFormService {
  createBookingFormGroup(booking?: BookingFormGroupInput): BookingFormGroup {
    const bookingRawValue = this.convertBookingToBookingRawValue({
      ...this.getFormDefaults(),
      ...(booking ?? { id: null }),
    });

    return new FormGroup<BookingFormGroupContent>({
      id: new FormControl(
        { value: bookingRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      bookingDate: new FormControl(bookingRawValue.bookingDate),
      totalAmount: new FormControl(bookingRawValue.totalAmount),
      status: new FormControl(bookingRawValue.status),
      user: new FormControl(bookingRawValue.user),
    });
  }

  getBooking(form: BookingFormGroup): IBooking | NewBooking {
    return this.convertBookingRawValueToBooking(form.getRawValue());
  }

  resetForm(form: BookingFormGroup, booking: BookingFormGroupInput): void {
    const bookingRawValue = this.convertBookingToBookingRawValue({ ...this.getFormDefaults(), ...booking });
    form.reset({
      ...bookingRawValue,
      id: { value: bookingRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): BookingFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      bookingDate: currentTime,
    };
  }

  private convertBookingRawValueToBooking(rawBooking: BookingFormRawValue | NewBookingFormRawValue): IBooking | NewBooking {
    return {
      ...rawBooking,
      bookingDate: dayjs(rawBooking.bookingDate, DATE_TIME_FORMAT),
    };
  }

  private convertBookingToBookingRawValue(
    booking: IBooking | (Partial<NewBooking> & BookingFormDefaults),
  ): BookingFormRawValue | PartialWithRequiredKeyOf<NewBookingFormRawValue> {
    return {
      ...booking,
      bookingDate: booking.bookingDate ? booking.bookingDate.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
