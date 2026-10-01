import { ChangeDetectionStrategy, Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable, finalize } from 'rxjs';

import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { IPromotion } from '../promotion.model';
import { PromotionService } from '../service/promotion.service';

import { PromotionFormGroup, PromotionFormService } from './promotion-form.service';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'jhi-promotion-update',
  templateUrl: './promotion-update.html',
  imports: [TranslateDirective, TranslatePipe, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class PromotionUpdate implements OnInit {
  readonly isSaving = signal(false);
  promotion: IPromotion | null = null;

  protected promotionService = inject(PromotionService);
  protected promotionFormService = inject(PromotionFormService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: PromotionFormGroup = this.promotionFormService.createPromotionFormGroup();

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ promotion }) => {
      this.promotion = promotion;
      if (promotion) {
        this.updateForm(promotion);
      }
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const promotion = this.promotionFormService.getPromotion(this.editForm);
    if (promotion.id === null) {
      this.subscribeToSaveResponse(this.promotionService.create(promotion));
    } else {
      this.subscribeToSaveResponse(this.promotionService.update(promotion));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IPromotion | null>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving.set(false);
  }

  protected updateForm(promotion: IPromotion): void {
    this.promotion = promotion;
    this.promotionFormService.resetForm(this.editForm, promotion);
  }
}
