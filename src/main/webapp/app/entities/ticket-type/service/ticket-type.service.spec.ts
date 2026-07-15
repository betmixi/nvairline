import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { ITicketType } from '../ticket-type.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../ticket-type.test-samples';

import { RestTicketType, TicketTypeService } from './ticket-type.service';

const requireRestSample: RestTicketType = {
  ...sampleWithRequiredData,
  saleStart: sampleWithRequiredData.saleStart?.toJSON(),
  saleEnd: sampleWithRequiredData.saleEnd?.toJSON(),
};

describe('TicketType Service', () => {
  let service: TicketTypeService;
  let httpMock: HttpTestingController;
  let expectedResult: ITicketType | ITicketType[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(TicketTypeService);
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

    it('should create a TicketType', () => {
      const ticketType = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(ticketType).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a TicketType', () => {
      const ticketType = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(ticketType).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a TicketType', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of TicketType', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a TicketType', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests).toHaveLength(1);
    });

    describe('addTicketTypeToCollectionIfMissing', () => {
      it('should add a TicketType to an empty array', () => {
        const ticketType: ITicketType = sampleWithRequiredData;
        expectedResult = service.addTicketTypeToCollectionIfMissing([], ticketType);
        expect(expectedResult).toEqual([ticketType]);
      });

      it('should not add a TicketType to an array that contains it', () => {
        const ticketType: ITicketType = sampleWithRequiredData;
        const ticketTypeCollection: ITicketType[] = [
          {
            ...ticketType,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addTicketTypeToCollectionIfMissing(ticketTypeCollection, ticketType);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a TicketType to an array that doesn't contain it", () => {
        const ticketType: ITicketType = sampleWithRequiredData;
        const ticketTypeCollection: ITicketType[] = [sampleWithPartialData];
        expectedResult = service.addTicketTypeToCollectionIfMissing(ticketTypeCollection, ticketType);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(ticketType);
      });

      it('should add only unique TicketType to an array', () => {
        const ticketTypeArray: ITicketType[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const ticketTypeCollection: ITicketType[] = [sampleWithRequiredData];
        expectedResult = service.addTicketTypeToCollectionIfMissing(ticketTypeCollection, ...ticketTypeArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const ticketType: ITicketType = sampleWithRequiredData;
        const ticketType2: ITicketType = sampleWithPartialData;
        expectedResult = service.addTicketTypeToCollectionIfMissing([], ticketType, ticketType2);
        expect(expectedResult).toEqual([ticketType, ticketType2]);
      });

      it('should accept null and undefined values', () => {
        const ticketType: ITicketType = sampleWithRequiredData;
        expectedResult = service.addTicketTypeToCollectionIfMissing([], null, ticketType, undefined);
        expect(expectedResult).toEqual([ticketType]);
      });

      it('should return initial array if no TicketType is added', () => {
        const ticketTypeCollection: ITicketType[] = [sampleWithRequiredData];
        expectedResult = service.addTicketTypeToCollectionIfMissing(ticketTypeCollection, undefined, null);
        expect(expectedResult).toEqual(ticketTypeCollection);
      });
    });

    describe('compareTicketType', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareTicketType(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 23701 };
        const entity2 = null;

        const compareResult1 = service.compareTicketType(entity1, entity2);
        const compareResult2 = service.compareTicketType(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 23701 };
        const entity2 = { id: 26385 };

        const compareResult1 = service.compareTicketType(entity1, entity2);
        const compareResult2 = service.compareTicketType(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 23701 };
        const entity2 = { id: 23701 };

        const compareResult1 = service.compareTicketType(entity1, entity2);
        const compareResult2 = service.compareTicketType(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
