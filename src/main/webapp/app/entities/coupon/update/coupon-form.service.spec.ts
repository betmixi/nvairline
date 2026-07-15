import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../coupon.test-samples';

import { CouponFormService } from './coupon-form.service';

describe('Coupon Form Service', () => {
  let service: CouponFormService;

  beforeEach(() => {
    service = TestBed.inject(CouponFormService);
  });

  describe('Service methods', () => {
    describe('createCouponFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createCouponFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            code: expect.any(Object),
            discount: expect.any(Object),
            startDate: expect.any(Object),
            endDate: expect.any(Object),
            quantity: expect.any(Object),
            event: expect.any(Object),
          }),
        );
      });

      it('passing ICoupon should create a new form with FormGroup', () => {
        const formGroup = service.createCouponFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            code: expect.any(Object),
            discount: expect.any(Object),
            startDate: expect.any(Object),
            endDate: expect.any(Object),
            quantity: expect.any(Object),
            event: expect.any(Object),
          }),
        );
      });
    });

    describe('getCoupon', () => {
      it('should return NewCoupon for default Coupon initial value', () => {
        const formGroup = service.createCouponFormGroup(sampleWithNewData);

        const coupon = service.getCoupon(formGroup);

        expect(coupon).toMatchObject(sampleWithNewData);
      });

      it('should return NewCoupon for empty Coupon initial value', () => {
        const formGroup = service.createCouponFormGroup();

        const coupon = service.getCoupon(formGroup);

        expect(coupon).toMatchObject({});
      });

      it('should return ICoupon', () => {
        const formGroup = service.createCouponFormGroup(sampleWithRequiredData);

        const coupon = service.getCoupon(formGroup);

        expect(coupon).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ICoupon should not enable id FormControl', () => {
        const formGroup = service.createCouponFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewCoupon should disable id FormControl', () => {
        const formGroup = service.createCouponFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
