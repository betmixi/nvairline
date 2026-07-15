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
  quantity: FormControl<IBookingDetail['quantity']>;
  price: FormControl<IBookingDetail['price']>;
  booking: FormControl<IBookingDetail['booking']>;
  ticketType: FormControl<IBookingDetail['ticketType']>;
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
      quantity: new FormControl(bookingDetailRawValue.quantity),
      price: new FormControl(bookingDetailRawValue.price),
      booking: new FormControl(bookingDetailRawValue.booking),
      ticketType: new FormControl(bookingDetailRawValue.ticketType),
    });
  }

  getBookingDetail(form: BookingDetailFormGroup): IBookingDetail | NewBookingDetail {
    return form.getRawValue();
  }

  resetForm(form: BookingDetailFormGroup, bookingDetail: BookingDetailFormGroupInput): void {
    const bookingDetailRawValue = { ...this.getFormDefaults(), ...bookingDetail };
    form.reset({
      ...bookingDetailRawValue,
      id: { value: bookingDetailRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): BookingDetailFormDefaults {
    return {
      id: null,
    };
  }
}
