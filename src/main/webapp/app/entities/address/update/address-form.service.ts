import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { notBlank } from 'app/shared/validators/not-blank.validator';

import { IAddress, NewAddress } from '../address.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IAddress for edit and NewAddressFormGroupInput for create.
 */
type AddressFormGroupInput = IAddress | PartialWithRequiredKeyOf<NewAddress>;

type AddressFormDefaults = Pick<NewAddress, 'id'>;

type AddressFormGroupContent = {
  id: FormControl<IAddress['id'] | NewAddress['id']>;
  location: FormControl<IAddress['location']>;
  address: FormControl<IAddress['address']>;
  city: FormControl<IAddress['city']>;
  capacity: FormControl<IAddress['capacity']>;
};

export type AddressFormGroup = FormGroup<AddressFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class AddressFormService {
  createAddressFormGroup(address?: AddressFormGroupInput): AddressFormGroup {
    const addressRawValue = {
      ...this.getFormDefaults(),
      ...(address ?? { id: null }),
    };

    return new FormGroup<AddressFormGroupContent>({
      id: new FormControl(
        { value: addressRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      location: new FormControl(addressRawValue.location, {
        validators: [Validators.required, notBlank],
      }),
      address: new FormControl(addressRawValue.address, {
        validators: [Validators.required, notBlank],
      }),
      city: new FormControl(addressRawValue.city, {
        validators: [Validators.required, notBlank],
      }),
      capacity: new FormControl(addressRawValue.capacity, {
        validators: [Validators.required, Validators.min(1)],
      }),
    });
  }

  getAddress(form: AddressFormGroup): IAddress | NewAddress {
    return form.getRawValue();
  }

  resetForm(form: AddressFormGroup, address: AddressFormGroupInput): void {
    const addressRawValue = { ...this.getFormDefaults(), ...address };
    form.reset({
      ...addressRawValue,
      id: { value: addressRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): AddressFormDefaults {
    return {
      id: null,
    };
  }
}
