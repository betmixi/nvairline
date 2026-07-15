import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';

import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { ICheckIn, NewCheckIn } from '../check-in.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ICheckIn for edit and NewCheckInFormGroupInput for create.
 */
type CheckInFormGroupInput = ICheckIn | PartialWithRequiredKeyOf<NewCheckIn>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends ICheckIn | NewCheckIn> = Omit<T, 'checkInTime'> & {
  checkInTime?: string | null;
};

type CheckInFormRawValue = FormValueOf<ICheckIn>;

type NewCheckInFormRawValue = FormValueOf<NewCheckIn>;

type CheckInFormDefaults = Pick<NewCheckIn, 'id' | 'checkInTime'>;

type CheckInFormGroupContent = {
  id: FormControl<CheckInFormRawValue['id'] | NewCheckIn['id']>;
  checkInTime: FormControl<CheckInFormRawValue['checkInTime']>;
  ticket: FormControl<CheckInFormRawValue['ticket']>;
  checkedBy: FormControl<CheckInFormRawValue['checkedBy']>;
};

export type CheckInFormGroup = FormGroup<CheckInFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class CheckInFormService {
  createCheckInFormGroup(checkIn?: CheckInFormGroupInput): CheckInFormGroup {
    const checkInRawValue = this.convertCheckInToCheckInRawValue({
      ...this.getFormDefaults(),
      ...(checkIn ?? { id: null }),
    });

    return new FormGroup<CheckInFormGroupContent>({
      id: new FormControl(
        { value: checkInRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      checkInTime: new FormControl(checkInRawValue.checkInTime),
      ticket: new FormControl(checkInRawValue.ticket),
      checkedBy: new FormControl(checkInRawValue.checkedBy),
    });
  }

  getCheckIn(form: CheckInFormGroup): ICheckIn | NewCheckIn {
    return this.convertCheckInRawValueToCheckIn(form.getRawValue());
  }

  resetForm(form: CheckInFormGroup, checkIn: CheckInFormGroupInput): void {
    const checkInRawValue = this.convertCheckInToCheckInRawValue({ ...this.getFormDefaults(), ...checkIn });
    form.reset({
      ...checkInRawValue,
      id: { value: checkInRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): CheckInFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      checkInTime: currentTime,
    };
  }

  private convertCheckInRawValueToCheckIn(rawCheckIn: CheckInFormRawValue | NewCheckInFormRawValue): ICheckIn | NewCheckIn {
    return {
      ...rawCheckIn,
      checkInTime: dayjs(rawCheckIn.checkInTime, DATE_TIME_FORMAT),
    };
  }

  private convertCheckInToCheckInRawValue(
    checkIn: ICheckIn | (Partial<NewCheckIn> & CheckInFormDefaults),
  ): CheckInFormRawValue | PartialWithRequiredKeyOf<NewCheckInFormRawValue> {
    return {
      ...checkIn,
      checkInTime: checkIn.checkInTime ? checkIn.checkInTime.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
