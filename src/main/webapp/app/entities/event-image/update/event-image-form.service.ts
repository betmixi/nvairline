import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IEventImage, NewEventImage } from '../event-image.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IEventImage for edit and NewEventImageFormGroupInput for create.
 */
type EventImageFormGroupInput = IEventImage | PartialWithRequiredKeyOf<NewEventImage>;

type EventImageFormDefaults = Pick<NewEventImage, 'id'>;

type EventImageFormGroupContent = {
  id: FormControl<IEventImage['id'] | NewEventImage['id']>;
  imageUrl: FormControl<IEventImage['imageUrl']>;
  event: FormControl<IEventImage['event']>;
};

export type EventImageFormGroup = FormGroup<EventImageFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class EventImageFormService {
  createEventImageFormGroup(eventImage?: EventImageFormGroupInput): EventImageFormGroup {
    const eventImageRawValue = {
      ...this.getFormDefaults(),
      ...(eventImage ?? { id: null }),
    };

    return new FormGroup<EventImageFormGroupContent>({
      id: new FormControl(
        { value: eventImageRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      imageUrl: new FormControl(eventImageRawValue.imageUrl, {
        validators: [Validators.required],
      }),
      event: new FormControl(eventImageRawValue.event),
    });
  }

  getEventImage(form: EventImageFormGroup): IEventImage | NewEventImage {
    return form.getRawValue();
  }

  resetForm(form: EventImageFormGroup, eventImage: EventImageFormGroupInput): void {
    const eventImageRawValue = { ...this.getFormDefaults(), ...eventImage };
    form.reset({
      ...eventImageRawValue,
      id: { value: eventImageRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): EventImageFormDefaults {
    return {
      id: null,
    };
  }
}
