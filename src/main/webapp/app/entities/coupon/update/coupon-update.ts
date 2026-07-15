import { HttpResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable, finalize, map } from 'rxjs';

import { IEvent } from 'app/entities/event/event.model';
import { EventService } from 'app/entities/event/service/event.service';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { ICoupon } from '../coupon.model';
import { CouponService } from '../service/coupon.service';

import { CouponFormGroup, CouponFormService } from './coupon-form.service';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'jhi-coupon-update',
  templateUrl: './coupon-update.html',
  imports: [TranslateDirective, TranslatePipe, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class CouponUpdate implements OnInit {
  readonly isSaving = signal(false);
  coupon: ICoupon | null = null;

  eventsSharedCollection = signal<IEvent[]>([]);

  protected couponService = inject(CouponService);
  protected couponFormService = inject(CouponFormService);
  protected eventService = inject(EventService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: CouponFormGroup = this.couponFormService.createCouponFormGroup();

  compareEvent = (o1: IEvent | null, o2: IEvent | null): boolean => this.eventService.compareEvent(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ coupon }) => {
      this.coupon = coupon;
      if (coupon) {
        this.updateForm(coupon);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const coupon = this.couponFormService.getCoupon(this.editForm);
    if (coupon.id === null) {
      this.subscribeToSaveResponse(this.couponService.create(coupon));
    } else {
      this.subscribeToSaveResponse(this.couponService.update(coupon));
    }
  }

  protected subscribeToSaveResponse(result: Observable<ICoupon | null>): void {
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

  protected updateForm(coupon: ICoupon): void {
    this.coupon = coupon;
    this.couponFormService.resetForm(this.editForm, coupon);

    this.eventsSharedCollection.update(events => this.eventService.addEventToCollectionIfMissing<IEvent>(events, coupon.event));
  }

  protected loadRelationshipsOptions(): void {
    this.eventService
      .query()
      .pipe(map((res: HttpResponse<IEvent[]>) => res.body ?? []))
      .pipe(map((events: IEvent[]) => this.eventService.addEventToCollectionIfMissing<IEvent>(events, this.coupon?.event)))
      .subscribe((events: IEvent[]) => this.eventsSharedCollection.set(events));
  }
}
