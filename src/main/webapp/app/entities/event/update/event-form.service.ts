import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';

import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IEvent, NewEvent } from '../event.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IEvent for edit and NewEventFormGroupInput for create.
 */
type EventFormGroupInput = IEvent | PartialWithRequiredKeyOf<NewEvent>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IEvent | NewEvent> = Omit<T, 'startTime' | 'endTime' | 'createdDate'> & {
  startTime?: string | null;
  endTime?: string | null;
  createdDate?: string | null;
};

type EventFormRawValue = FormValueOf<IEvent>;

type NewEventFormRawValue = FormValueOf<NewEvent>;

type EventFormDefaults = Pick<
  NewEvent,
  'id' | 'startTime' | 'endTime' | 'status' | 'createdDate' | 'supportsOneWay' | 'supportsRoundTrip' | 'supportsMultiCity'
>;

type EventFormGroupContent = {
  id: FormControl<EventFormRawValue['id'] | NewEvent['id']>;
  title: FormControl<EventFormRawValue['title']>;
  description: FormControl<EventFormRawValue['description']>;
  banner: FormControl<EventFormRawValue['banner']>;
  startTime: FormControl<EventFormRawValue['startTime']>;
  endTime: FormControl<EventFormRawValue['endTime']>;
  status: FormControl<EventFormRawValue['status']>;
  createdDate: FormControl<EventFormRawValue['createdDate']>;
  supportsOneWay: FormControl<EventFormRawValue['supportsOneWay']>;
  supportsRoundTrip: FormControl<EventFormRawValue['supportsRoundTrip']>;
  supportsMultiCity: FormControl<EventFormRawValue['supportsMultiCity']>;
  category: FormControl<EventFormRawValue['category']>;
  address: FormControl<EventFormRawValue['address']>;
  departureAirport: FormControl<EventFormRawValue['departureAirport']>;
  arrivalAirport: FormControl<EventFormRawValue['arrivalAirport']>;
};

export type EventFormGroup = FormGroup<EventFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class EventFormService {
  createEventFormGroup(event?: EventFormGroupInput): EventFormGroup {
    const eventRawValue = this.convertEventToEventRawValue({
      ...this.getFormDefaults(),
      ...(event ?? { id: null }),
    });

    return new FormGroup<EventFormGroupContent>({
      id: new FormControl(
        { value: eventRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      title: new FormControl(eventRawValue.title, {
        validators: [Validators.required],
      }),
      description: new FormControl(eventRawValue.description),
      banner: new FormControl(eventRawValue.banner),
      startTime: new FormControl(eventRawValue.startTime),
      endTime: new FormControl(eventRawValue.endTime),
      status: new FormControl(eventRawValue.status),
      createdDate: new FormControl(eventRawValue.createdDate),
      supportsOneWay: new FormControl(eventRawValue.supportsOneWay),
      supportsRoundTrip: new FormControl(eventRawValue.supportsRoundTrip),
      supportsMultiCity: new FormControl(eventRawValue.supportsMultiCity),
      category: new FormControl(eventRawValue.category),
      address: new FormControl(eventRawValue.address),
      departureAirport: new FormControl(eventRawValue.departureAirport),
      arrivalAirport: new FormControl(eventRawValue.arrivalAirport),
    });
  }

  getEvent(form: EventFormGroup): IEvent | NewEvent {
    return this.convertEventRawValueToEvent(form.getRawValue());
  }

  resetForm(form: EventFormGroup, event: EventFormGroupInput): void {
    const eventRawValue = this.convertEventToEventRawValue({ ...this.getFormDefaults(), ...event });
    form.reset({
      ...eventRawValue,
      id: { value: eventRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): EventFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      startTime: currentTime,
      endTime: currentTime,
      status: false,
      createdDate: currentTime,
      supportsOneWay: true,
      supportsRoundTrip: true,
      supportsMultiCity: true,
    };
  }

  private convertEventRawValueToEvent(rawEvent: EventFormRawValue | NewEventFormRawValue): IEvent | NewEvent {
    return {
      ...rawEvent,
      startTime: dayjs(rawEvent.startTime, DATE_TIME_FORMAT),
      endTime: dayjs(rawEvent.endTime, DATE_TIME_FORMAT),
      createdDate: dayjs(rawEvent.createdDate, DATE_TIME_FORMAT),
    };
  }

  private convertEventToEventRawValue(
    event: IEvent | (Partial<NewEvent> & EventFormDefaults),
  ): EventFormRawValue | PartialWithRequiredKeyOf<NewEventFormRawValue> {
    return {
      ...event,
      startTime: event.startTime ? event.startTime.format(DATE_TIME_FORMAT) : undefined,
      endTime: event.endTime ? event.endTime.format(DATE_TIME_FORMAT) : undefined,
      createdDate: event.createdDate ? event.createdDate.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
