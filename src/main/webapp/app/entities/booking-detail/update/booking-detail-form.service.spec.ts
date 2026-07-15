import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../booking-detail.test-samples';

import { BookingDetailFormService } from './booking-detail-form.service';

describe('BookingDetail Form Service', () => {
  let service: BookingDetailFormService;

  beforeEach(() => {
    service = TestBed.inject(BookingDetailFormService);
  });

  describe('Service methods', () => {
    describe('createBookingDetailFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createBookingDetailFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            quantity: expect.any(Object),
            price: expect.any(Object),
            booking: expect.any(Object),
            ticketType: expect.any(Object),
          }),
        );
      });

      it('passing IBookingDetail should create a new form with FormGroup', () => {
        const formGroup = service.createBookingDetailFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            quantity: expect.any(Object),
            price: expect.any(Object),
            booking: expect.any(Object),
            ticketType: expect.any(Object),
          }),
        );
      });
    });

    describe('getBookingDetail', () => {
      it('should return NewBookingDetail for default BookingDetail initial value', () => {
        const formGroup = service.createBookingDetailFormGroup(sampleWithNewData);

        const bookingDetail = service.getBookingDetail(formGroup);

        expect(bookingDetail).toMatchObject(sampleWithNewData);
      });

      it('should return NewBookingDetail for empty BookingDetail initial value', () => {
        const formGroup = service.createBookingDetailFormGroup();

        const bookingDetail = service.getBookingDetail(formGroup);

        expect(bookingDetail).toMatchObject({});
      });

      it('should return IBookingDetail', () => {
        const formGroup = service.createBookingDetailFormGroup(sampleWithRequiredData);

        const bookingDetail = service.getBookingDetail(formGroup);

        expect(bookingDetail).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IBookingDetail should not enable id FormControl', () => {
        const formGroup = service.createBookingDetailFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewBookingDetail should disable id FormControl', () => {
        const formGroup = service.createBookingDetailFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
