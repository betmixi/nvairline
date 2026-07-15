import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../favorite.test-samples';

import { FavoriteFormService } from './favorite-form.service';

describe('Favorite Form Service', () => {
  let service: FavoriteFormService;

  beforeEach(() => {
    service = TestBed.inject(FavoriteFormService);
  });

  describe('Service methods', () => {
    describe('createFavoriteFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createFavoriteFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            user: expect.any(Object),
            event: expect.any(Object),
          }),
        );
      });

      it('passing IFavorite should create a new form with FormGroup', () => {
        const formGroup = service.createFavoriteFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            user: expect.any(Object),
            event: expect.any(Object),
          }),
        );
      });
    });

    describe('getFavorite', () => {
      it('should return NewFavorite for default Favorite initial value', () => {
        const formGroup = service.createFavoriteFormGroup(sampleWithNewData);

        const favorite = service.getFavorite(formGroup);

        expect(favorite).toMatchObject(sampleWithNewData);
      });

      it('should return NewFavorite for empty Favorite initial value', () => {
        const formGroup = service.createFavoriteFormGroup();

        const favorite = service.getFavorite(formGroup);

        expect(favorite).toMatchObject({});
      });

      it('should return IFavorite', () => {
        const formGroup = service.createFavoriteFormGroup(sampleWithRequiredData);

        const favorite = service.getFavorite(formGroup);

        expect(favorite).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IFavorite should not enable id FormControl', () => {
        const formGroup = service.createFavoriteFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewFavorite should disable id FormControl', () => {
        const formGroup = service.createFavoriteFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
