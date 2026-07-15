import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';

import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { ICoupon, NewCoupon } from '../coupon.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ICoupon for edit and NewCouponFormGroupInput for create.
 */
type CouponFormGroupInput = ICoupon | PartialWithRequiredKeyOf<NewCoupon>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends ICoupon | NewCoupon> = Omit<T, 'startDate' | 'endDate'> & {
  startDate?: string | null;
  endDate?: string | null;
};

type CouponFormRawValue = FormValueOf<ICoupon>;

type NewCouponFormRawValue = FormValueOf<NewCoupon>;

type CouponFormDefaults = Pick<NewCoupon, 'id' | 'startDate' | 'endDate'>;

type CouponFormGroupContent = {
  id: FormControl<CouponFormRawValue['id'] | NewCoupon['id']>;
  code: FormControl<CouponFormRawValue['code']>;
  discount: FormControl<CouponFormRawValue['discount']>;
  startDate: FormControl<CouponFormRawValue['startDate']>;
  endDate: FormControl<CouponFormRawValue['endDate']>;
  quantity: FormControl<CouponFormRawValue['quantity']>;
  event: FormControl<CouponFormRawValue['event']>;
};

export type CouponFormGroup = FormGroup<CouponFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class CouponFormService {
  createCouponFormGroup(coupon?: CouponFormGroupInput): CouponFormGroup {
    const couponRawValue = this.convertCouponToCouponRawValue({
      ...this.getFormDefaults(),
      ...(coupon ?? { id: null }),
    });

    return new FormGroup<CouponFormGroupContent>({
      id: new FormControl(
        { value: couponRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      code: new FormControl(couponRawValue.code, {
        validators: [Validators.required],
      }),
      discount: new FormControl(couponRawValue.discount),
      startDate: new FormControl(couponRawValue.startDate),
      endDate: new FormControl(couponRawValue.endDate),
      quantity: new FormControl(couponRawValue.quantity),
      event: new FormControl(couponRawValue.event),
    });
  }

  getCoupon(form: CouponFormGroup): ICoupon | NewCoupon {
    return this.convertCouponRawValueToCoupon(form.getRawValue());
  }

  resetForm(form: CouponFormGroup, coupon: CouponFormGroupInput): void {
    const couponRawValue = this.convertCouponToCouponRawValue({ ...this.getFormDefaults(), ...coupon });
    form.reset({
      ...couponRawValue,
      id: { value: couponRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): CouponFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      startDate: currentTime,
      endDate: currentTime,
    };
  }

  private convertCouponRawValueToCoupon(rawCoupon: CouponFormRawValue | NewCouponFormRawValue): ICoupon | NewCoupon {
    return {
      ...rawCoupon,
      startDate: dayjs(rawCoupon.startDate, DATE_TIME_FORMAT),
      endDate: dayjs(rawCoupon.endDate, DATE_TIME_FORMAT),
    };
  }

  private convertCouponToCouponRawValue(
    coupon: ICoupon | (Partial<NewCoupon> & CouponFormDefaults),
  ): CouponFormRawValue | PartialWithRequiredKeyOf<NewCouponFormRawValue> {
    return {
      ...coupon,
      startDate: coupon.startDate ? coupon.startDate.format(DATE_TIME_FORMAT) : undefined,
      endDate: coupon.endDate ? coupon.endDate.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
