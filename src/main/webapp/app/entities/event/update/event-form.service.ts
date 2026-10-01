import { Injectable } from '@angular/core';
import { AbstractControl, FormControl, FormGroup, ValidationErrors, ValidatorFn, Validators } from '@angular/forms';

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

/** Khong cho phep chuoi chi gom khoang trang. */
const notBlank: ValidatorFn = (control: AbstractControl): ValidationErrors | null => {
  const value = control.value;
  return typeof value === 'string' && value.length > 0 && value.trim().length === 0 ? { blank: true } : null;
};

/** Gio ket thuc khong duoc som hon gio bat dau. */
const endNotBeforeStart: ValidatorFn = (group: AbstractControl): ValidationErrors | null => {
  const start = group.get('startTime')?.value as string | null | undefined;
  const end = group.get('endTime')?.value as string | null | undefined;
  if (!start || !end) {
    return null;
  }
  return dayjs(end, DATE_TIME_FORMAT).isBefore(dayjs(start, DATE_TIME_FORMAT)) ? { invalidTime: true } : null;
};

/** San bay di va san bay den phai khac nhau. */
const differentAirports: ValidatorFn = (group: AbstractControl): ValidationErrors | null => {
  const departure = group.get('departureAirport')?.value as { id?: number | null } | null | undefined;
  const arrival = group.get('arrivalAirport')?.value as { id?: number | null } | null | undefined;
  return departure?.id != null && arrival?.id != null && departure.id === arrival.id ? { sameAirport: true } : null;
};

@Injectable({ providedIn: 'root' })
export class EventFormService {
  createEventFormGroup(event?: EventFormGroupInput): EventFormGroup {
    const eventRawValue = this.convertEventToEventRawValue({
      ...this.getFormDefaults(),
      ...(event ?? { id: null }),
    });

    return new FormGroup<EventFormGroupContent>(
      {
        id: new FormControl(
          { value: eventRawValue.id, disabled: true },
          {
            nonNullable: true,
            validators: [Validators.required],
          },
        ),
        title: new FormControl(eventRawValue.title, {
          validators: [Validators.required, notBlank, Validators.maxLength(255)],
        }),
        description: new FormControl(eventRawValue.description),
        banner: new FormControl(eventRawValue.banner),
        startTime: new FormControl(eventRawValue.startTime, { validators: [Validators.required] }),
        endTime: new FormControl(eventRawValue.endTime, { validators: [Validators.required] }),
        status: new FormControl(eventRawValue.status),
        createdDate: new FormControl(eventRawValue.createdDate),
        supportsOneWay: new FormControl(eventRawValue.supportsOneWay),
        supportsRoundTrip: new FormControl(eventRawValue.supportsRoundTrip),
        supportsMultiCity: new FormControl(eventRawValue.supportsMultiCity),
        category: new FormControl(eventRawValue.category),
        address: new FormControl(eventRawValue.address),
        departureAirport: new FormControl(eventRawValue.departureAirport, { validators: [Validators.required] }),
        arrivalAirport: new FormControl(eventRawValue.arrivalAirport, { validators: [Validators.required] }),
      },
      { validators: [endNotBeforeStart, differentAirports] },
    );
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
