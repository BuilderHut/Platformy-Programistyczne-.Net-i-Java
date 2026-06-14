import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../driving-license.test-samples';

import { DrivingLicenseFormService } from './driving-license-form.service';

describe('DrivingLicense Form Service', () => {
  let service: DrivingLicenseFormService;

  beforeEach(() => {
    service = TestBed.inject(DrivingLicenseFormService);
  });

  describe('Service methods', () => {
    describe('createDrivingLicenseFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createDrivingLicenseFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            licenseNumber: expect.any(Object),
            issueDate: expect.any(Object),
            expirationDate: expect.any(Object),
          }),
        );
      });

      it('passing IDrivingLicense should create a new form with FormGroup', () => {
        const formGroup = service.createDrivingLicenseFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            licenseNumber: expect.any(Object),
            issueDate: expect.any(Object),
            expirationDate: expect.any(Object),
          }),
        );
      });
    });

    describe('getDrivingLicense', () => {
      it('should return NewDrivingLicense for default DrivingLicense initial value', () => {
        const formGroup = service.createDrivingLicenseFormGroup(sampleWithNewData);

        const drivingLicense = service.getDrivingLicense(formGroup);

        expect(drivingLicense).toMatchObject(sampleWithNewData);
      });

      it('should return NewDrivingLicense for empty DrivingLicense initial value', () => {
        const formGroup = service.createDrivingLicenseFormGroup();

        const drivingLicense = service.getDrivingLicense(formGroup);

        expect(drivingLicense).toMatchObject({});
      });

      it('should return IDrivingLicense', () => {
        const formGroup = service.createDrivingLicenseFormGroup(sampleWithRequiredData);

        const drivingLicense = service.getDrivingLicense(formGroup);

        expect(drivingLicense).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IDrivingLicense should not enable id FormControl', () => {
        const formGroup = service.createDrivingLicenseFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewDrivingLicense should disable id FormControl', () => {
        const formGroup = service.createDrivingLicenseFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
