import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { IFavorite } from '../favorite.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../favorite.test-samples';

import { FavoriteService } from './favorite.service';

const requireRestSample: IFavorite = {
  ...sampleWithRequiredData,
};

describe('Favorite Service', () => {
  let service: FavoriteService;
  let httpMock: HttpTestingController;
  let expectedResult: IFavorite | IFavorite[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(FavoriteService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find(123).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should create a Favorite', () => {
      const favorite = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(favorite).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Favorite', () => {
      const favorite = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(favorite).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Favorite', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Favorite', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Favorite', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests).toHaveLength(1);
    });

    describe('addFavoriteToCollectionIfMissing', () => {
      it('should add a Favorite to an empty array', () => {
        const favorite: IFavorite = sampleWithRequiredData;
        expectedResult = service.addFavoriteToCollectionIfMissing([], favorite);
        expect(expectedResult).toEqual([favorite]);
      });

      it('should not add a Favorite to an array that contains it', () => {
        const favorite: IFavorite = sampleWithRequiredData;
        const favoriteCollection: IFavorite[] = [
          {
            ...favorite,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addFavoriteToCollectionIfMissing(favoriteCollection, favorite);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Favorite to an array that doesn't contain it", () => {
        const favorite: IFavorite = sampleWithRequiredData;
        const favoriteCollection: IFavorite[] = [sampleWithPartialData];
        expectedResult = service.addFavoriteToCollectionIfMissing(favoriteCollection, favorite);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(favorite);
      });

      it('should add only unique Favorite to an array', () => {
        const favoriteArray: IFavorite[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const favoriteCollection: IFavorite[] = [sampleWithRequiredData];
        expectedResult = service.addFavoriteToCollectionIfMissing(favoriteCollection, ...favoriteArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const favorite: IFavorite = sampleWithRequiredData;
        const favorite2: IFavorite = sampleWithPartialData;
        expectedResult = service.addFavoriteToCollectionIfMissing([], favorite, favorite2);
        expect(expectedResult).toEqual([favorite, favorite2]);
      });

      it('should accept null and undefined values', () => {
        const favorite: IFavorite = sampleWithRequiredData;
        expectedResult = service.addFavoriteToCollectionIfMissing([], null, favorite, undefined);
        expect(expectedResult).toEqual([favorite]);
      });

      it('should return initial array if no Favorite is added', () => {
        const favoriteCollection: IFavorite[] = [sampleWithRequiredData];
        expectedResult = service.addFavoriteToCollectionIfMissing(favoriteCollection, undefined, null);
        expect(expectedResult).toEqual(favoriteCollection);
      });
    });

    describe('compareFavorite', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareFavorite(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 4951 };
        const entity2 = null;

        const compareResult1 = service.compareFavorite(entity1, entity2);
        const compareResult2 = service.compareFavorite(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 4951 };
        const entity2 = { id: 14498 };

        const compareResult1 = service.compareFavorite(entity1, entity2);
        const compareResult2 = service.compareFavorite(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 4951 };
        const entity2 = { id: 4951 };

        const compareResult1 = service.compareFavorite(entity1, entity2);
        const compareResult2 = service.compareFavorite(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
