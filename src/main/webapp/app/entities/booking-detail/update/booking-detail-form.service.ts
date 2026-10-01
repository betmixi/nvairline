import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IBookingDetail, NewBookingDetail } from '../booking-detail.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IBookingDetail for edit and NewBookingDetailFormGroupInput for create.
 */
type BookingDetailFormGroupInput = IBookingDetail | PartialWithRequiredKeyOf<NewBookingDetail>;

type BookingDetailFormDefaults = Pick<NewBookingDetail, 'id'>;

type BookingDetailFormGroupContent = {
  id: FormControl<IBookingDetail['id'] | NewBookingDetail['id']>;
  price: FormControl<IBookingDetail['price']>;
  booking: FormControl<IBookingDetail['booking']>;
  showtimeSeatId: FormControl<number | null>;
};

export type BookingDetailFormGroup = FormGroup<BookingDetailFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class BookingDetailFormService {
  createBookingDetailFormGroup(bookingDetail?: BookingDetailFormGroupInput): BookingDetailFormGroup {
    const bookingDetailRawValue = {
      ...this.getFormDefaults(),
      ...(bookingDetail ?? { id: null }),
    };

    return new FormGroup<BookingDetailFormGroupContent>({
      id: new FormControl(
        { value: bookingDetailRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      price: new FormControl(bookingDetailRawValue.price, { validators: [Validators.min(0)] }),
      booking: new FormControl(bookingDetailRawValue.booking),
      showtimeSeatId: new FormControl(bookingDetailRawValue.showtimeSeat?.id ?? null),
    });
  }

  getBookingDetail(form: BookingDetailFormGroup): IBookingDetail | NewBookingDetail {
    const raw = form.getRawValue();
    return {
      id: raw.id,
      price: raw.price,
      booking: raw.booking,
      showtimeSeat: raw.showtimeSeatId != null ? { id: raw.showtimeSeatId } : null,
    };
  }

  resetForm(form: BookingDetailFormGroup, bookingDetail: BookingDetailFormGroupInput): void {
    const bookingDetailRawValue = { ...this.getFormDefaults(), ...bookingDetail };
    form.reset({
      ...bookingDetailRawValue,
      id: { value: bookingDetailRawValue.id, disabled: true },
      showtimeSeatId: bookingDetailRawValue.showtimeSeat?.id ?? null,
    });
  }

  private getFormDefaults(): BookingDetailFormDefaults {
    return {
      id: null,
    };
  }
}
