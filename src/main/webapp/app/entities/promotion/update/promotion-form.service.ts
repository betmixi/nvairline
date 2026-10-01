import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IPromotion, NewPromotion } from '../promotion.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IPromotion for edit and NewPromotionFormGroupInput for create.
 */
type PromotionFormGroupInput = IPromotion | PartialWithRequiredKeyOf<NewPromotion>;

type PromotionFormDefaults = Pick<NewPromotion, 'id' | 'active'>;

type PromotionFormGroupContent = {
  id: FormControl<IPromotion['id'] | NewPromotion['id']>;
  title: FormControl<IPromotion['title']>;
  description: FormControl<IPromotion['description']>;
  icon: FormControl<IPromotion['icon']>;
  targetUrl: FormControl<IPromotion['targetUrl']>;
  displayOrder: FormControl<IPromotion['displayOrder']>;
  active: FormControl<IPromotion['active']>;
};

export type PromotionFormGroup = FormGroup<PromotionFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class PromotionFormService {
  createPromotionFormGroup(promotion?: PromotionFormGroupInput): PromotionFormGroup {
    const promotionRawValue = {
      ...this.getFormDefaults(),
      ...(promotion ?? { id: null }),
    };

    return new FormGroup<PromotionFormGroupContent>({
      id: new FormControl(
        { value: promotionRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      title: new FormControl(promotionRawValue.title, {
        validators: [Validators.required, Validators.maxLength(150)],
      }),
      description: new FormControl(promotionRawValue.description, {
        validators: [Validators.maxLength(255)],
      }),
      icon: new FormControl(promotionRawValue.icon, {
        validators: [Validators.maxLength(10)],
      }),
      targetUrl: new FormControl(promotionRawValue.targetUrl, {
        validators: [Validators.maxLength(255)],
      }),
      displayOrder: new FormControl(promotionRawValue.displayOrder, {
        validators: [Validators.required],
      }),
      active: new FormControl(promotionRawValue.active, {
        validators: [Validators.required],
      }),
    });
  }

  getPromotion(form: PromotionFormGroup): IPromotion | NewPromotion {
    return form.getRawValue();
  }

  resetForm(form: PromotionFormGroup, promotion: PromotionFormGroupInput): void {
    const promotionRawValue = { ...this.getFormDefaults(), ...promotion };
    form.reset({
      ...promotionRawValue,
      id: { value: promotionRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): PromotionFormDefaults {
    return {
      id: null,
      active: true,
    };
  }
}
