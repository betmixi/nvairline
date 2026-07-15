import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../organizer.test-samples';

import { OrganizerFormService } from './organizer-form.service';

describe('Organizer Form Service', () => {
  let service: OrganizerFormService;

  beforeEach(() => {
    service = TestBed.inject(OrganizerFormService);
  });

  describe('Service methods', () => {
    describe('createOrganizerFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createOrganizerFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            companyName: expect.any(Object),
            taxCode: expect.any(Object),
            description: expect.any(Object),
            verified: expect.any(Object),
            user: expect.any(Object),
          }),
        );
      });

      it('passing IOrganizer should create a new form with FormGroup', () => {
        const formGroup = service.createOrganizerFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            companyName: expect.any(Object),
            taxCode: expect.any(Object),
            description: expect.any(Object),
            verified: expect.any(Object),
            user: expect.any(Object),
          }),
        );
      });
    });

    describe('getOrganizer', () => {
      it('should return NewOrganizer for default Organizer initial value', () => {
        const formGroup = service.createOrganizerFormGroup(sampleWithNewData);

        const organizer = service.getOrganizer(formGroup);

        expect(organizer).toMatchObject(sampleWithNewData);
      });

      it('should return NewOrganizer for empty Organizer initial value', () => {
        const formGroup = service.createOrganizerFormGroup();

        const organizer = service.getOrganizer(formGroup);

        expect(organizer).toMatchObject({});
      });

      it('should return IOrganizer', () => {
        const formGroup = service.createOrganizerFormGroup(sampleWithRequiredData);

        const organizer = service.getOrganizer(formGroup);

        expect(organizer).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IOrganizer should not enable id FormControl', () => {
        const formGroup = service.createOrganizerFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewOrganizer should disable id FormControl', () => {
        const formGroup = service.createOrganizerFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
