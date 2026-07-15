import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { IEventImage } from '../event-image.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../event-image.test-samples';

import { EventImageService } from './event-image.service';

const requireRestSample: IEventImage = {
  ...sampleWithRequiredData,
};

describe('EventImage Service', () => {
  let service: EventImageService;
  let httpMock: HttpTestingController;
  let expectedResult: IEventImage | IEventImage[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(EventImageService);
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

    it('should create a EventImage', () => {
      const eventImage = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(eventImage).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a EventImage', () => {
      const eventImage = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(eventImage).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a EventImage', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of EventImage', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a EventImage', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests).toHaveLength(1);
    });

    describe('addEventImageToCollectionIfMissing', () => {
      it('should add a EventImage to an empty array', () => {
        const eventImage: IEventImage = sampleWithRequiredData;
        expectedResult = service.addEventImageToCollectionIfMissing([], eventImage);
        expect(expectedResult).toEqual([eventImage]);
      });

      it('should not add a EventImage to an array that contains it', () => {
        const eventImage: IEventImage = sampleWithRequiredData;
        const eventImageCollection: IEventImage[] = [
          {
            ...eventImage,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addEventImageToCollectionIfMissing(eventImageCollection, eventImage);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a EventImage to an array that doesn't contain it", () => {
        const eventImage: IEventImage = sampleWithRequiredData;
        const eventImageCollection: IEventImage[] = [sampleWithPartialData];
        expectedResult = service.addEventImageToCollectionIfMissing(eventImageCollection, eventImage);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(eventImage);
      });

      it('should add only unique EventImage to an array', () => {
        const eventImageArray: IEventImage[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const eventImageCollection: IEventImage[] = [sampleWithRequiredData];
        expectedResult = service.addEventImageToCollectionIfMissing(eventImageCollection, ...eventImageArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const eventImage: IEventImage = sampleWithRequiredData;
        const eventImage2: IEventImage = sampleWithPartialData;
        expectedResult = service.addEventImageToCollectionIfMissing([], eventImage, eventImage2);
        expect(expectedResult).toEqual([eventImage, eventImage2]);
      });

      it('should accept null and undefined values', () => {
        const eventImage: IEventImage = sampleWithRequiredData;
        expectedResult = service.addEventImageToCollectionIfMissing([], null, eventImage, undefined);
        expect(expectedResult).toEqual([eventImage]);
      });

      it('should return initial array if no EventImage is added', () => {
        const eventImageCollection: IEventImage[] = [sampleWithRequiredData];
        expectedResult = service.addEventImageToCollectionIfMissing(eventImageCollection, undefined, null);
        expect(expectedResult).toEqual(eventImageCollection);
      });
    });

    describe('compareEventImage', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareEventImage(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 19308 };
        const entity2 = null;

        const compareResult1 = service.compareEventImage(entity1, entity2);
        const compareResult2 = service.compareEventImage(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 19308 };
        const entity2 = { id: 14540 };

        const compareResult1 = service.compareEventImage(entity1, entity2);
        const compareResult2 = service.compareEventImage(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 19308 };
        const entity2 = { id: 19308 };

        const compareResult1 = service.compareEventImage(entity1, entity2);
        const compareResult2 = service.compareEventImage(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
