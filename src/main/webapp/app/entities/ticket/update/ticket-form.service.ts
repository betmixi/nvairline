import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { ITicket, NewTicket } from '../ticket.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ITicket for edit and NewTicketFormGroupInput for create.
 */
type TicketFormGroupInput = ITicket | PartialWithRequiredKeyOf<NewTicket>;

type TicketFormDefaults = Pick<NewTicket, 'id' | 'checkedIn'>;

type TicketFormGroupContent = {
  id: FormControl<ITicket['id'] | NewTicket['id']>;
  qrCode: FormControl<ITicket['qrCode']>;
  status: FormControl<ITicket['status']>;
  checkedIn: FormControl<ITicket['checkedIn']>;
  bookingDetail: FormControl<ITicket['bookingDetail']>;
};

export type TicketFormGroup = FormGroup<TicketFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class TicketFormService {
  createTicketFormGroup(ticket?: TicketFormGroupInput): TicketFormGroup {
    const ticketRawValue = {
      ...this.getFormDefaults(),
      ...(ticket ?? { id: null }),
    };

    return new FormGroup<TicketFormGroupContent>({
      id: new FormControl(
        { value: ticketRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      qrCode: new FormControl(ticketRawValue.qrCode),
      status: new FormControl(ticketRawValue.status),
      checkedIn: new FormControl(ticketRawValue.checkedIn),
      bookingDetail: new FormControl(ticketRawValue.bookingDetail),
    });
  }

  getTicket(form: TicketFormGroup): ITicket | NewTicket {
    return form.getRawValue();
  }

  resetForm(form: TicketFormGroup, ticket: TicketFormGroupInput): void {
    const ticketRawValue = { ...this.getFormDefaults(), ...ticket };
    form.reset({
      ...ticketRawValue,
      id: { value: ticketRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): TicketFormDefaults {
    return {
      id: null,
      checkedIn: false,
    };
  }
}
