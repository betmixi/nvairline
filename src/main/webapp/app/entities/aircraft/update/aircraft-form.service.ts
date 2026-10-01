import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IAircraft, NewAircraft } from '../aircraft.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IAircraft for edit and NewAircraftFormGroupInput for create.
 */
type AircraftFormGroupInput = IAircraft | PartialWithRequiredKeyOf<NewAircraft>;

type AircraftFormDefaults = Pick<NewAircraft, 'id'>;

type AircraftFormGroupContent = {
  id: FormControl<IAircraft['id'] | NewAircraft['id']>;
  name: FormControl<IAircraft['name']>;
  totalRows: FormControl<IAircraft['totalRows']>;
  totalColumns: FormControl<IAircraft['totalColumns']>;
  roomType: FormControl<IAircraft['roomType']>;
};

export type AircraftFormGroup = FormGroup<AircraftFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class AircraftFormService {
  createAircraftFormGroup(aircraft?: AircraftFormGroupInput): AircraftFormGroup {
    const aircraftRawValue = {
      ...this.getFormDefaults(),
      ...(aircraft ?? { id: null }),
    };

    return new FormGroup<AircraftFormGroupContent>({
      id: new FormControl(
        { value: aircraftRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      name: new FormControl(aircraftRawValue.name, {
        validators: [Validators.required, Validators.maxLength(100)],
      }),
      totalRows: new FormControl(aircraftRawValue.totalRows, {
        validators: [Validators.min(1)],
      }),
      totalColumns: new FormControl(aircraftRawValue.totalColumns, {
        validators: [Validators.min(1)],
      }),
      roomType: new FormControl(aircraftRawValue.roomType),
    });
  }

  getAircraft(form: AircraftFormGroup): IAircraft | NewAircraft {
    return form.getRawValue();
  }

  resetForm(form: AircraftFormGroup, aircraft: AircraftFormGroupInput): void {
    const aircraftRawValue = { ...this.getFormDefaults(), ...aircraft };
    form.reset({
      ...aircraftRawValue,
      id: { value: aircraftRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): AircraftFormDefaults {
    return {
      id: null,
    };
  }
}
