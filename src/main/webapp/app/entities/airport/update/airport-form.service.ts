import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IAirport, NewAirport } from '../airport.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IAirport for edit and NewAirportFormGroupInput for create.
 */
type AirportFormGroupInput = IAirport | PartialWithRequiredKeyOf<NewAirport>;

type AirportFormDefaults = Pick<NewAirport, 'id'>;

type AirportFormGroupContent = {
  id: FormControl<IAirport['id'] | NewAirport['id']>;
  code: FormControl<IAirport['code']>;
  name: FormControl<IAirport['name']>;
  city: FormControl<IAirport['city']>;
};

export type AirportFormGroup = FormGroup<AirportFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class AirportFormService {
  createAirportFormGroup(airport?: AirportFormGroupInput): AirportFormGroup {
    const airportRawValue = {
      ...this.getFormDefaults(),
      ...(airport ?? { id: null }),
    };

    return new FormGroup<AirportFormGroupContent>({
      id: new FormControl(
        { value: airportRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      code: new FormControl(airportRawValue.code, {
        validators: [Validators.required, Validators.maxLength(10)],
      }),
      name: new FormControl(airportRawValue.name, {
        validators: [Validators.required, Validators.maxLength(150)],
      }),
      city: new FormControl(airportRawValue.city, {
        validators: [Validators.maxLength(100)],
      }),
    });
  }

  getAirport(form: AirportFormGroup): IAirport | NewAirport {
    return form.getRawValue();
  }

  resetForm(form: AirportFormGroup, airport: AirportFormGroupInput): void {
    const airportRawValue = { ...this.getFormDefaults(), ...airport };
    form.reset({
      ...airportRawValue,
      id: { value: airportRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): AirportFormDefaults {
    return {
      id: null,
    };
  }
}
