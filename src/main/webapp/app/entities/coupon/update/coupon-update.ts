import { HttpResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, ChangeDetectorRef, Component, OnInit, inject, signal } from '@angular/core';
import { AsyncValidatorFn, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable, catchError, finalize, map, of, switchMap, tap, timer } from 'rxjs';

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
  private readonly cdr = inject(ChangeDetectorRef);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: CouponFormGroup = this.couponFormService.createCouponFormGroup();

  compareEvent = (o1: IEvent | null, o2: IEvent | null): boolean => this.eventService.compareEvent(o1, o2);

  ngOnInit(): void {
    this.editForm.controls.code.addAsyncValidators(this.codeUniqueValidator());

    this.activatedRoute.data.subscribe(({ coupon }) => {
      this.coupon = coupon;
      if (coupon) {
        this.updateForm(coupon);
      }

      this.loadRelationshipsOptions();
    });
  }

  /** Bao loi neu ma uu dai da ton tai (khong phan biet hoa thuong), bo qua chinh uu dai dang sua. */
  private codeUniqueValidator(): AsyncValidatorFn {
    return control => {
      const value = ((control.value as string | null) ?? '').trim();
      if (!value) {
        return of(null);
      }
      return timer(400).pipe(
        switchMap(() => this.couponService.query({ 'code.contains': value, size: 50 })),
        map(res =>
          (res.body ?? []).some(c => c.id !== this.coupon?.id && (c.code ?? '').trim().toLowerCase() === value.toLowerCase())
            ? { codeExists: true }
            : null,
        ),
        catchError(() => of(null)),
        tap(() => this.cdr.markForCheck()),
      );
    };
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
    // Cuon len thong bao loi (jhi-alert-error) de nguoi dung nhin thay ly do luu that bai (trung, de trong, so am...).
    setTimeout(() => document.querySelector('jhi-alert-error')?.scrollIntoView({ behavior: 'smooth', block: 'center' }), 100);
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
