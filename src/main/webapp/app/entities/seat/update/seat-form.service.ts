import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { ISeat, NewSeat } from '../seat.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ISeat for edit and NewSeatFormGroupInput for create.
 */
type SeatFormGroupInput = ISeat | PartialWithRequiredKeyOf<NewSeat>;

type SeatFormDefaults = Pick<NewSeat, 'id'>;

type SeatFormGroupContent = {
  id: FormControl<ISeat['id'] | NewSeat['id']>;
  rowLabel: FormControl<ISeat['rowLabel']>;
  seatNumber: FormControl<ISeat['seatNumber']>;
  seatType: FormControl<ISeat['seatType']>;
  aircraft: FormControl<ISeat['aircraft']>;
};

export type SeatFormGroup = FormGroup<SeatFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class SeatFormService {
  createSeatFormGroup(seat?: SeatFormGroupInput): SeatFormGroup {
    const seatRawValue = {
      ...this.getFormDefaults(),
      ...(seat ?? { id: null }),
    };

    return new FormGroup<SeatFormGroupContent>({
      id: new FormControl(
        { value: seatRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      rowLabel: new FormControl(seatRawValue.rowLabel, {
        validators: [Validators.required, Validators.maxLength(5)],
      }),
      seatNumber: new FormControl(seatRawValue.seatNumber, {
        validators: [Validators.required, Validators.min(1)],
      }),
      seatType: new FormControl(seatRawValue.seatType, {
        validators: [Validators.required],
      }),
      aircraft: new FormControl(seatRawValue.aircraft, {
        validators: [Validators.required],
      }),
    });
  }

  getSeat(form: SeatFormGroup): ISeat | NewSeat {
    return form.getRawValue();
  }

  resetForm(form: SeatFormGroup, seat: SeatFormGroupInput): void {
    const seatRawValue = { ...this.getFormDefaults(), ...seat };
    form.reset({
      ...seatRawValue,
      id: { value: seatRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): SeatFormDefaults {
    return {
      id: null,
    };
  }
}
