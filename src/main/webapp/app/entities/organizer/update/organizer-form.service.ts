import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IOrganizer, NewOrganizer } from '../organizer.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IOrganizer for edit and NewOrganizerFormGroupInput for create.
 */
type OrganizerFormGroupInput = IOrganizer | PartialWithRequiredKeyOf<NewOrganizer>;

type OrganizerFormDefaults = Pick<NewOrganizer, 'id' | 'verified'>;

type OrganizerFormGroupContent = {
  id: FormControl<IOrganizer['id'] | NewOrganizer['id']>;
  companyName: FormControl<IOrganizer['companyName']>;
  taxCode: FormControl<IOrganizer['taxCode']>;
  description: FormControl<IOrganizer['description']>;
  verified: FormControl<IOrganizer['verified']>;
  user: FormControl<IOrganizer['user']>;
};

export type OrganizerFormGroup = FormGroup<OrganizerFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class OrganizerFormService {
  createOrganizerFormGroup(organizer?: OrganizerFormGroupInput): OrganizerFormGroup {
    const organizerRawValue = {
      ...this.getFormDefaults(),
      ...(organizer ?? { id: null }),
    };

    return new FormGroup<OrganizerFormGroupContent>({
      id: new FormControl(
        { value: organizerRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      companyName: new FormControl(organizerRawValue.companyName, {
        validators: [Validators.required, Validators.maxLength(100)],
      }),
      taxCode: new FormControl(organizerRawValue.taxCode, {
        validators: [Validators.required, Validators.maxLength(50)],
      }),
      description: new FormControl(organizerRawValue.description),
      verified: new FormControl(organizerRawValue.verified),
      user: new FormControl(organizerRawValue.user),
    });
  }

  getOrganizer(form: OrganizerFormGroup): IOrganizer | NewOrganizer {
    return form.getRawValue();
  }

  resetForm(form: OrganizerFormGroup, organizer: OrganizerFormGroupInput): void {
    const organizerRawValue = { ...this.getFormDefaults(), ...organizer };
    form.reset({
      ...organizerRawValue,
      id: { value: organizerRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): OrganizerFormDefaults {
    return {
      id: null,
      verified: false,
    };
  }
}
