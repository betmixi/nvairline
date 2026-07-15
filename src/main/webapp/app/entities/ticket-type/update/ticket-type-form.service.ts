import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';

import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { ITicketType, NewTicketType } from '../ticket-type.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ITicketType for edit and NewTicketTypeFormGroupInput for create.
 */
type TicketTypeFormGroupInput = ITicketType | PartialWithRequiredKeyOf<NewTicketType>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends ITicketType | NewTicketType> = Omit<T, 'saleStart' | 'saleEnd'> & {
  saleStart?: string | null;
  saleEnd?: string | null;
};

type TicketTypeFormRawValue = FormValueOf<ITicketType>;

type NewTicketTypeFormRawValue = FormValueOf<NewTicketType>;

type TicketTypeFormDefaults = Pick<NewTicketType, 'id' | 'saleStart' | 'saleEnd'>;

type TicketTypeFormGroupContent = {
  id: FormControl<TicketTypeFormRawValue['id'] | NewTicketType['id']>;
  name: FormControl<TicketTypeFormRawValue['name']>;
  price: FormControl<TicketTypeFormRawValue['price']>;
  quantity: FormControl<TicketTypeFormRawValue['quantity']>;
  remaining: FormControl<TicketTypeFormRawValue['remaining']>;
  saleStart: FormControl<TicketTypeFormRawValue['saleStart']>;
  saleEnd: FormControl<TicketTypeFormRawValue['saleEnd']>;
  event: FormControl<TicketTypeFormRawValue['event']>;
};

export type TicketTypeFormGroup = FormGroup<TicketTypeFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class TicketTypeFormService {
  createTicketTypeFormGroup(ticketType?: TicketTypeFormGroupInput): TicketTypeFormGroup {
    const ticketTypeRawValue = this.convertTicketTypeToTicketTypeRawValue({
      ...this.getFormDefaults(),
      ...(ticketType ?? { id: null }),
    });

    return new FormGroup<TicketTypeFormGroupContent>({
      id: new FormControl(
        { value: ticketTypeRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      name: new FormControl(ticketTypeRawValue.name, {
        validators: [Validators.required],
      }),
      price: new FormControl(ticketTypeRawValue.price, {
        validators: [Validators.required],
      }),
      quantity: new FormControl(ticketTypeRawValue.quantity, {
        validators: [Validators.required],
      }),
      remaining: new FormControl(ticketTypeRawValue.remaining, {
        validators: [Validators.required],
      }),
      saleStart: new FormControl(ticketTypeRawValue.saleStart),
      saleEnd: new FormControl(ticketTypeRawValue.saleEnd),
      event: new FormControl(ticketTypeRawValue.event),
    });
  }

  getTicketType(form: TicketTypeFormGroup): ITicketType | NewTicketType {
    return this.convertTicketTypeRawValueToTicketType(form.getRawValue());
  }

  resetForm(form: TicketTypeFormGroup, ticketType: TicketTypeFormGroupInput): void {
    const ticketTypeRawValue = this.convertTicketTypeToTicketTypeRawValue({ ...this.getFormDefaults(), ...ticketType });
    form.reset({
      ...ticketTypeRawValue,
      id: { value: ticketTypeRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): TicketTypeFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      saleStart: currentTime,
      saleEnd: currentTime,
    };
  }

  private convertTicketTypeRawValueToTicketType(
    rawTicketType: TicketTypeFormRawValue | NewTicketTypeFormRawValue,
  ): ITicketType | NewTicketType {
    return {
      ...rawTicketType,
      saleStart: dayjs(rawTicketType.saleStart, DATE_TIME_FORMAT),
      saleEnd: dayjs(rawTicketType.saleEnd, DATE_TIME_FORMAT),
    };
  }

  private convertTicketTypeToTicketTypeRawValue(
    ticketType: ITicketType | (Partial<NewTicketType> & TicketTypeFormDefaults),
  ): TicketTypeFormRawValue | PartialWithRequiredKeyOf<NewTicketTypeFormRawValue> {
    return {
      ...ticketType,
      saleStart: ticketType.saleStart ? ticketType.saleStart.format(DATE_TIME_FORMAT) : undefined,
      saleEnd: ticketType.saleEnd ? ticketType.saleEnd.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
