import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { IBookingDetail } from '../booking-detail.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../booking-detail.test-samples';

import { BookingDetailService } from './booking-detail.service';

const requireRestSample: IBookingDetail = {
  ...sampleWithRequiredData,
};

describe('BookingDetail Service', () => {
  let service: BookingDetailService;
  let httpMock: HttpTestingController;
  let expectedResult: IBookingDetail | IBookingDetail[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(BookingDetailService);
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

    it('should create a BookingDetail', () => {
      const bookingDetail = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(bookingDetail).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a BookingDetail', () => {
      const bookingDetail = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(bookingDetail).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a BookingDetail', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of BookingDetail', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a BookingDetail', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests).toHaveLength(1);
    });

    describe('addBookingDetailToCollectionIfMissing', () => {
      it('should add a BookingDetail to an empty array', () => {
        const bookingDetail: IBookingDetail = sampleWithRequiredData;
        expectedResult = service.addBookingDetailToCollectionIfMissing([], bookingDetail);
        expect(expectedResult).toEqual([bookingDetail]);
      });

      it('should not add a BookingDetail to an array that contains it', () => {
        const bookingDetail: IBookingDetail = sampleWithRequiredData;
        const bookingDetailCollection: IBookingDetail[] = [
          {
            ...bookingDetail,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addBookingDetailToCollectionIfMissing(bookingDetailCollection, bookingDetail);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a BookingDetail to an array that doesn't contain it", () => {
        const bookingDetail: IBookingDetail = sampleWithRequiredData;
        const bookingDetailCollection: IBookingDetail[] = [sampleWithPartialData];
        expectedResult = service.addBookingDetailToCollectionIfMissing(bookingDetailCollection, bookingDetail);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(bookingDetail);
      });

      it('should add only unique BookingDetail to an array', () => {
        const bookingDetailArray: IBookingDetail[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const bookingDetailCollection: IBookingDetail[] = [sampleWithRequiredData];
        expectedResult = service.addBookingDetailToCollectionIfMissing(bookingDetailCollection, ...bookingDetailArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const bookingDetail: IBookingDetail = sampleWithRequiredData;
        const bookingDetail2: IBookingDetail = sampleWithPartialData;
        expectedResult = service.addBookingDetailToCollectionIfMissing([], bookingDetail, bookingDetail2);
        expect(expectedResult).toEqual([bookingDetail, bookingDetail2]);
      });

      it('should accept null and undefined values', () => {
        const bookingDetail: IBookingDetail = sampleWithRequiredData;
        expectedResult = service.addBookingDetailToCollectionIfMissing([], null, bookingDetail, undefined);
        expect(expectedResult).toEqual([bookingDetail]);
      });

      it('should return initial array if no BookingDetail is added', () => {
        const bookingDetailCollection: IBookingDetail[] = [sampleWithRequiredData];
        expectedResult = service.addBookingDetailToCollectionIfMissing(bookingDetailCollection, undefined, null);
        expect(expectedResult).toEqual(bookingDetailCollection);
      });
    });

    describe('compareBookingDetail', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareBookingDetail(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 17036 };
        const entity2 = null;

        const compareResult1 = service.compareBookingDetail(entity1, entity2);
        const compareResult2 = service.compareBookingDetail(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 17036 };
        const entity2 = { id: 6599 };

        const compareResult1 = service.compareBookingDetail(entity1, entity2);
        const compareResult2 = service.compareBookingDetail(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 17036 };
        const entity2 = { id: 17036 };

        const compareResult1 = service.compareBookingDetail(entity1, entity2);
        const compareResult2 = service.compareBookingDetail(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
