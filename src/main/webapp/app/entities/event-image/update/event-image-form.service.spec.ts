import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../event-image.test-samples';

import { EventImageFormService } from './event-image-form.service';

describe('EventImage Form Service', () => {
  let service: EventImageFormService;

  beforeEach(() => {
    service = TestBed.inject(EventImageFormService);
  });

  describe('Service methods', () => {
    describe('createEventImageFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createEventImageFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            imageUrl: expect.any(Object),
            event: expect.any(Object),
          }),
        );
      });

      it('passing IEventImage should create a new form with FormGroup', () => {
        const formGroup = service.createEventImageFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            imageUrl: expect.any(Object),
            event: expect.any(Object),
          }),
        );
      });
    });

    describe('getEventImage', () => {
      it('should return NewEventImage for default EventImage initial value', () => {
        const formGroup = service.createEventImageFormGroup(sampleWithNewData);

        const eventImage = service.getEventImage(formGroup);

        expect(eventImage).toMatchObject(sampleWithNewData);
      });

      it('should return NewEventImage for empty EventImage initial value', () => {
        const formGroup = service.createEventImageFormGroup();

        const eventImage = service.getEventImage(formGroup);

        expect(eventImage).toMatchObject({});
      });

      it('should return IEventImage', () => {
        const formGroup = service.createEventImageFormGroup(sampleWithRequiredData);

        const eventImage = service.getEventImage(formGroup);

        expect(eventImage).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IEventImage should not enable id FormControl', () => {
        const formGroup = service.createEventImageFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewEventImage should disable id FormControl', () => {
        const formGroup = service.createEventImageFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
