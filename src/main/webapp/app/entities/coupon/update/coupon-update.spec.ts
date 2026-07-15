import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { provideTranslateService } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { IEvent } from 'app/entities/event/event.model';
import { EventService } from 'app/entities/event/service/event.service';
import { ICoupon } from '../coupon.model';
import { CouponService } from '../service/coupon.service';

import { CouponFormService } from './coupon-form.service';
import { CouponUpdate } from './coupon-update';

describe('Coupon Management Update Component', () => {
  let comp: CouponUpdate;
  let fixture: ComponentFixture<CouponUpdate>;
  let activatedRoute: ActivatedRoute;
  let couponFormService: CouponFormService;
  let couponService: CouponService;
  let eventService: EventService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideTranslateService(),
        provideHttpClientTesting(),
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    });

    fixture = TestBed.createComponent(CouponUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    couponFormService = TestBed.inject(CouponFormService);
    couponService = TestBed.inject(CouponService);
    eventService = TestBed.inject(EventService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call Event query and add missing value', () => {
      const coupon: ICoupon = { id: 29129 };
      const event: IEvent = { id: 22576 };
      coupon.event = event;

      const eventCollection: IEvent[] = [{ id: 22576 }];
      vitest.spyOn(eventService, 'query').mockReturnValue(of(new HttpResponse({ body: eventCollection })));
      const additionalEvents = [event];
      const expectedCollection: IEvent[] = [...additionalEvents, ...eventCollection];
      vitest.spyOn(eventService, 'addEventToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ coupon });
      comp.ngOnInit();

      expect(eventService.query).toHaveBeenCalled();
      expect(eventService.addEventToCollectionIfMissing).toHaveBeenCalledWith(
        eventCollection,
        ...additionalEvents.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.eventsSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const coupon: ICoupon = { id: 29129 };
      const event: IEvent = { id: 22576 };
      coupon.event = event;

      activatedRoute.data = of({ coupon });
      comp.ngOnInit();

      expect(comp.eventsSharedCollection()).toContainEqual(event);
      expect(comp.coupon).toEqual(coupon);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<ICoupon>();
      const coupon = { id: 28448 };
      vitest.spyOn(couponFormService, 'getCoupon').mockReturnValue(coupon);
      vitest.spyOn(couponService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ coupon });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(coupon);
      saveSubject.complete();

      // THEN
      expect(couponFormService.getCoupon).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(couponService.update).toHaveBeenCalledWith(expect.objectContaining(coupon));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<ICoupon>();
      const coupon = { id: 28448 };
      vitest.spyOn(couponFormService, 'getCoupon').mockReturnValue({ id: null });
      vitest.spyOn(couponService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ coupon: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(coupon);
      saveSubject.complete();

      // THEN
      expect(couponFormService.getCoupon).toHaveBeenCalled();
      expect(couponService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<ICoupon>();
      const coupon = { id: 28448 };
      vitest.spyOn(couponService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ coupon });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(couponService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareEvent', () => {
      it('should forward to eventService', () => {
        const entity = { id: 22576 };
        const entity2 = { id: 3268 };
        vitest.spyOn(eventService, 'compareEvent');
        comp.compareEvent(entity, entity2);
        expect(eventService.compareEvent).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
