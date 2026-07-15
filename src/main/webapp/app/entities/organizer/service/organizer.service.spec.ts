import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { IOrganizer } from '../organizer.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../organizer.test-samples';

import { OrganizerService } from './organizer.service';

const requireRestSample: IOrganizer = {
  ...sampleWithRequiredData,
};

describe('Organizer Service', () => {
  let service: OrganizerService;
  let httpMock: HttpTestingController;
  let expectedResult: IOrganizer | IOrganizer[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(OrganizerService);
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

    it('should create a Organizer', () => {
      const organizer = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(organizer).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Organizer', () => {
      const organizer = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(organizer).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Organizer', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Organizer', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Organizer', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests).toHaveLength(1);
    });

    describe('addOrganizerToCollectionIfMissing', () => {
      it('should add a Organizer to an empty array', () => {
        const organizer: IOrganizer = sampleWithRequiredData;
        expectedResult = service.addOrganizerToCollectionIfMissing([], organizer);
        expect(expectedResult).toEqual([organizer]);
      });

      it('should not add a Organizer to an array that contains it', () => {
        const organizer: IOrganizer = sampleWithRequiredData;
        const organizerCollection: IOrganizer[] = [
          {
            ...organizer,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addOrganizerToCollectionIfMissing(organizerCollection, organizer);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Organizer to an array that doesn't contain it", () => {
        const organizer: IOrganizer = sampleWithRequiredData;
        const organizerCollection: IOrganizer[] = [sampleWithPartialData];
        expectedResult = service.addOrganizerToCollectionIfMissing(organizerCollection, organizer);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(organizer);
      });

      it('should add only unique Organizer to an array', () => {
        const organizerArray: IOrganizer[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const organizerCollection: IOrganizer[] = [sampleWithRequiredData];
        expectedResult = service.addOrganizerToCollectionIfMissing(organizerCollection, ...organizerArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const organizer: IOrganizer = sampleWithRequiredData;
        const organizer2: IOrganizer = sampleWithPartialData;
        expectedResult = service.addOrganizerToCollectionIfMissing([], organizer, organizer2);
        expect(expectedResult).toEqual([organizer, organizer2]);
      });

      it('should accept null and undefined values', () => {
        const organizer: IOrganizer = sampleWithRequiredData;
        expectedResult = service.addOrganizerToCollectionIfMissing([], null, organizer, undefined);
        expect(expectedResult).toEqual([organizer]);
      });

      it('should return initial array if no Organizer is added', () => {
        const organizerCollection: IOrganizer[] = [sampleWithRequiredData];
        expectedResult = service.addOrganizerToCollectionIfMissing(organizerCollection, undefined, null);
        expect(expectedResult).toEqual(organizerCollection);
      });
    });

    describe('compareOrganizer', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareOrganizer(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 278 };
        const entity2 = null;

        const compareResult1 = service.compareOrganizer(entity1, entity2);
        const compareResult2 = service.compareOrganizer(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 278 };
        const entity2 = { id: 29151 };

        const compareResult1 = service.compareOrganizer(entity1, entity2);
        const compareResult2 = service.compareOrganizer(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 278 };
        const entity2 = { id: 278 };

        const compareResult1 = service.compareOrganizer(entity1, entity2);
        const compareResult2 = service.compareOrganizer(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
