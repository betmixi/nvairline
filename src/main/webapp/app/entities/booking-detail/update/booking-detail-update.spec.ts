import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { provideTranslateService } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { IBooking } from 'app/entities/booking/booking.model';
import { BookingService } from 'app/entities/booking/service/booking.service';
import { IBookingDetail } from '../booking-detail.model';
import { BookingDetailService } from '../service/booking-detail.service';

import { BookingDetailFormService } from './booking-detail-form.service';
import { BookingDetailUpdate } from './booking-detail-update';

describe('BookingDetail Management Update Component', () => {
  let comp: BookingDetailUpdate;
  let fixture: ComponentFixture<BookingDetailUpdate>;
  let activatedRoute: ActivatedRoute;
  let bookingDetailFormService: BookingDetailFormService;
  let bookingDetailService: BookingDetailService;
  let bookingService: BookingService;

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

    fixture = TestBed.createComponent(BookingDetailUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    bookingDetailFormService = TestBed.inject(BookingDetailFormService);
    bookingDetailService = TestBed.inject(BookingDetailService);
    bookingService = TestBed.inject(BookingService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call Booking query and add missing value', () => {
      const bookingDetail: IBookingDetail = { id: 6599 };
      const booking: IBooking = { id: 1408 };
      bookingDetail.booking = booking;

      const bookingCollection: IBooking[] = [{ id: 1408 }];
      vitest.spyOn(bookingService, 'query').mockReturnValue(of(new HttpResponse({ body: bookingCollection })));
      const additionalBookings = [booking];
      const expectedCollection: IBooking[] = [...additionalBookings, ...bookingCollection];
      vitest.spyOn(bookingService, 'addBookingToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ bookingDetail });
      comp.ngOnInit();

      expect(bookingService.query).toHaveBeenCalled();
      expect(bookingService.addBookingToCollectionIfMissing).toHaveBeenCalledWith(
        bookingCollection,
        ...additionalBookings.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.bookingsSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const bookingDetail: IBookingDetail = { id: 6599 };
      const booking: IBooking = { id: 1408 };
      bookingDetail.booking = booking;
      bookingDetail.showtimeSeat = { id: 23701 };

      activatedRoute.data = of({ bookingDetail });
      comp.ngOnInit();

      expect(comp.bookingsSharedCollection()).toContainEqual(booking);
      expect(comp.bookingDetail).toEqual(bookingDetail);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IBookingDetail>();
      const bookingDetail = { id: 17036 };
      vitest.spyOn(bookingDetailFormService, 'getBookingDetail').mockReturnValue(bookingDetail);
      vitest.spyOn(bookingDetailService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ bookingDetail });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(bookingDetail);
      saveSubject.complete();

      // THEN
      expect(bookingDetailFormService.getBookingDetail).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(bookingDetailService.update).toHaveBeenCalledWith(expect.objectContaining(bookingDetail));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IBookingDetail>();
      const bookingDetail = { id: 17036 };
      vitest.spyOn(bookingDetailFormService, 'getBookingDetail').mockReturnValue({ id: null });
      vitest.spyOn(bookingDetailService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ bookingDetail: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(bookingDetail);
      saveSubject.complete();

      // THEN
      expect(bookingDetailFormService.getBookingDetail).toHaveBeenCalled();
      expect(bookingDetailService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IBookingDetail>();
      const bookingDetail = { id: 17036 };
      vitest.spyOn(bookingDetailService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ bookingDetail });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(bookingDetailService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareBooking', () => {
      it('should forward to bookingService', () => {
        const entity = { id: 1408 };
        const entity2 = { id: 4697 };
        vitest.spyOn(bookingService, 'compareBooking');
        comp.compareBooking(entity, entity2);
        expect(bookingService.compareBooking).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
