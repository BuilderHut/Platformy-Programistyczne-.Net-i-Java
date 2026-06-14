import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { DATE_FORMAT } from 'app/config/input.constants';
import { IDrivingLicense } from '../driving-license.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../driving-license.test-samples';

import { DrivingLicenseService, RestDrivingLicense } from './driving-license.service';

const requireRestSample: RestDrivingLicense = {
  ...sampleWithRequiredData,
  issueDate: sampleWithRequiredData.issueDate?.format(DATE_FORMAT),
  expirationDate: sampleWithRequiredData.expirationDate?.format(DATE_FORMAT),
};

describe('DrivingLicense Service', () => {
  let service: DrivingLicenseService;
  let httpMock: HttpTestingController;
  let expectedResult: IDrivingLicense | IDrivingLicense[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(DrivingLicenseService);
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

    it('should create a DrivingLicense', () => {
      const drivingLicense = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(drivingLicense).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a DrivingLicense', () => {
      const drivingLicense = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(drivingLicense).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a DrivingLicense', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of DrivingLicense', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a DrivingLicense', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests.length).toBe(1);
    });

    it('should handle exceptions for searching a DrivingLicense', () => {
      const queryObject: any = {
        page: 0,
        size: 20,
        query: '',
        sort: [],
      };
      service.search(queryObject).subscribe(() => expectedResult);

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(null, { status: 500, statusText: 'Internal Server Error' });
      expect(expectedResult).toBe(null);
    });

    describe('addDrivingLicenseToCollectionIfMissing', () => {
      it('should add a DrivingLicense to an empty array', () => {
        const drivingLicense: IDrivingLicense = sampleWithRequiredData;
        expectedResult = service.addDrivingLicenseToCollectionIfMissing([], drivingLicense);
        expect(expectedResult).toEqual([drivingLicense]);
      });

      it('should not add a DrivingLicense to an array that contains it', () => {
        const drivingLicense: IDrivingLicense = sampleWithRequiredData;
        const drivingLicenseCollection: IDrivingLicense[] = [
          {
            ...drivingLicense,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addDrivingLicenseToCollectionIfMissing(drivingLicenseCollection, drivingLicense);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a DrivingLicense to an array that doesn't contain it", () => {
        const drivingLicense: IDrivingLicense = sampleWithRequiredData;
        const drivingLicenseCollection: IDrivingLicense[] = [sampleWithPartialData];
        expectedResult = service.addDrivingLicenseToCollectionIfMissing(drivingLicenseCollection, drivingLicense);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(drivingLicense);
      });

      it('should add only unique DrivingLicense to an array', () => {
        const drivingLicenseArray: IDrivingLicense[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const drivingLicenseCollection: IDrivingLicense[] = [sampleWithRequiredData];
        expectedResult = service.addDrivingLicenseToCollectionIfMissing(drivingLicenseCollection, ...drivingLicenseArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const drivingLicense: IDrivingLicense = sampleWithRequiredData;
        const drivingLicense2: IDrivingLicense = sampleWithPartialData;
        expectedResult = service.addDrivingLicenseToCollectionIfMissing([], drivingLicense, drivingLicense2);
        expect(expectedResult).toEqual([drivingLicense, drivingLicense2]);
      });

      it('should accept null and undefined values', () => {
        const drivingLicense: IDrivingLicense = sampleWithRequiredData;
        expectedResult = service.addDrivingLicenseToCollectionIfMissing([], null, drivingLicense, undefined);
        expect(expectedResult).toEqual([drivingLicense]);
      });

      it('should return initial array if no DrivingLicense is added', () => {
        const drivingLicenseCollection: IDrivingLicense[] = [sampleWithRequiredData];
        expectedResult = service.addDrivingLicenseToCollectionIfMissing(drivingLicenseCollection, undefined, null);
        expect(expectedResult).toEqual(drivingLicenseCollection);
      });
    });

    describe('compareDrivingLicense', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareDrivingLicense(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 11488 };
        const entity2 = null;

        const compareResult1 = service.compareDrivingLicense(entity1, entity2);
        const compareResult2 = service.compareDrivingLicense(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 11488 };
        const entity2 = { id: 21872 };

        const compareResult1 = service.compareDrivingLicense(entity1, entity2);
        const compareResult2 = service.compareDrivingLicense(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 11488 };
        const entity2 = { id: 11488 };

        const compareResult1 = service.compareDrivingLicense(entity1, entity2);
        const compareResult2 = service.compareDrivingLicense(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
