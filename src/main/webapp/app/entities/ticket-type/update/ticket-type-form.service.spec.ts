import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../ticket-type.test-samples';

import { TicketTypeFormService } from './ticket-type-form.service';

describe('TicketType Form Service', () => {
  let service: TicketTypeFormService;

  beforeEach(() => {
    service = TestBed.inject(TicketTypeFormService);
  });

  describe('Service methods', () => {
    describe('createTicketTypeFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createTicketTypeFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            price: expect.any(Object),
            quantity: expect.any(Object),
            remaining: expect.any(Object),
            saleStart: expect.any(Object),
            saleEnd: expect.any(Object),
            event: expect.any(Object),
          }),
        );
      });

      it('passing ITicketType should create a new form with FormGroup', () => {
        const formGroup = service.createTicketTypeFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            price: expect.any(Object),
            quantity: expect.any(Object),
            remaining: expect.any(Object),
            saleStart: expect.any(Object),
            saleEnd: expect.any(Object),
            event: expect.any(Object),
          }),
        );
      });
    });

    describe('getTicketType', () => {
      it('should return NewTicketType for default TicketType initial value', () => {
        const formGroup = service.createTicketTypeFormGroup(sampleWithNewData);

        const ticketType = service.getTicketType(formGroup);

        expect(ticketType).toMatchObject(sampleWithNewData);
      });

      it('should return NewTicketType for empty TicketType initial value', () => {
        const formGroup = service.createTicketTypeFormGroup();

        const ticketType = service.getTicketType(formGroup);

        expect(ticketType).toMatchObject({});
      });

      it('should return ITicketType', () => {
        const formGroup = service.createTicketTypeFormGroup(sampleWithRequiredData);

        const ticketType = service.getTicketType(formGroup);

        expect(ticketType).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ITicketType should not enable id FormControl', () => {
        const formGroup = service.createTicketTypeFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewTicketType should disable id FormControl', () => {
        const formGroup = service.createTicketTypeFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
