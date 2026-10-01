import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { notBlank } from 'app/shared/validators/not-blank.validator';

import { IVenue, NewVenue } from '../venue.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IVenue for edit and NewVenueFormGroupInput for create.
 */
type VenueFormGroupInput = IVenue | PartialWithRequiredKeyOf<NewVenue>;

type VenueFormDefaults = Pick<NewVenue, 'id'>;

type VenueFormGroupContent = {
  id: FormControl<IVenue['id'] | NewVenue['id']>;
  name: FormControl<IVenue['name']>;
  address: FormControl<IVenue['address']>;
  city: FormControl<IVenue['city']>;
  country: FormControl<IVenue['country']>;
  capacity: FormControl<IVenue['capacity']>;
};

export type VenueFormGroup = FormGroup<VenueFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class VenueFormService {
  createVenueFormGroup(venue?: VenueFormGroupInput): VenueFormGroup {
    const venueRawValue = {
      ...this.getFormDefaults(),
      ...(venue ?? { id: null }),
    };

    return new FormGroup<VenueFormGroupContent>({
      id: new FormControl(
        { value: venueRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      name: new FormControl(venueRawValue.name, {
        validators: [Validators.required, notBlank],
      }),
      address: new FormControl(venueRawValue.address, {
        validators: [Validators.required, notBlank],
      }),
      city: new FormControl(venueRawValue.city, {
        validators: [Validators.required, notBlank],
      }),
      country: new FormControl(venueRawValue.country, {
        validators: [Validators.required, notBlank],
      }),
      capacity: new FormControl(venueRawValue.capacity, {
        validators: [Validators.required, Validators.min(1)],
      }),
    });
  }

  getVenue(form: VenueFormGroup): IVenue | NewVenue {
    return form.getRawValue();
  }

  resetForm(form: VenueFormGroup, venue: VenueFormGroupInput): void {
    const venueRawValue = { ...this.getFormDefaults(), ...venue };
    form.reset({
      ...venueRawValue,
      id: { value: venueRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): VenueFormDefaults {
    return {
      id: null,
    };
  }
}
