import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { TranslateModule } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { IDrivingLicense } from 'app/entities/driving-license/driving-license.model';
import { DrivingLicenseService } from 'app/entities/driving-license/service/driving-license.service';
import { ICustomer } from '../customer.model';
import { CustomerService } from '../service/customer.service';

import { CustomerFormService } from './customer-form.service';
import { CustomerUpdate } from './customer-update';

describe('Customer Management Update Component', () => {
  let comp: CustomerUpdate;
  let fixture: ComponentFixture<CustomerUpdate>;
  let activatedRoute: ActivatedRoute;
  let customerFormService: CustomerFormService;
  let customerService: CustomerService;
  let drivingLicenseService: DrivingLicenseService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [TranslateModule.forRoot()],
      providers: [
        provideHttpClientTesting(),
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    });

    fixture = TestBed.createComponent(CustomerUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    customerFormService = TestBed.inject(CustomerFormService);
    customerService = TestBed.inject(CustomerService);
    drivingLicenseService = TestBed.inject(DrivingLicenseService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call drivingLicense query and add missing value', () => {
      const customer: ICustomer = { id: 21032 };
      const drivingLicense: IDrivingLicense = { id: 11488 };
      customer.drivingLicense = drivingLicense;

      const drivingLicenseCollection: IDrivingLicense[] = [{ id: 11488 }];
      vitest.spyOn(drivingLicenseService, 'query').mockReturnValue(of(new HttpResponse({ body: drivingLicenseCollection })));
      const expectedCollection: IDrivingLicense[] = [drivingLicense, ...drivingLicenseCollection];
      vitest.spyOn(drivingLicenseService, 'addDrivingLicenseToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ customer });
      comp.ngOnInit();

      expect(drivingLicenseService.query).toHaveBeenCalled();
      expect(drivingLicenseService.addDrivingLicenseToCollectionIfMissing).toHaveBeenCalledWith(drivingLicenseCollection, drivingLicense);
      expect(comp.drivingLicensesCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const customer: ICustomer = { id: 21032 };
      const drivingLicense: IDrivingLicense = { id: 11488 };
      customer.drivingLicense = drivingLicense;

      activatedRoute.data = of({ customer });
      comp.ngOnInit();

      expect(comp.drivingLicensesCollection()).toContainEqual(drivingLicense);
      expect(comp.customer).toEqual(customer);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<ICustomer>();
      const customer = { id: 26915 };
      vitest.spyOn(customerFormService, 'getCustomer').mockReturnValue(customer);
      vitest.spyOn(customerService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ customer });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(customer);
      saveSubject.complete();

      // THEN
      expect(customerFormService.getCustomer).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(customerService.update).toHaveBeenCalledWith(expect.objectContaining(customer));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<ICustomer>();
      const customer = { id: 26915 };
      vitest.spyOn(customerFormService, 'getCustomer').mockReturnValue({ id: null });
      vitest.spyOn(customerService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ customer: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(customer);
      saveSubject.complete();

      // THEN
      expect(customerFormService.getCustomer).toHaveBeenCalled();
      expect(customerService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<ICustomer>();
      const customer = { id: 26915 };
      vitest.spyOn(customerService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ customer });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(customerService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareDrivingLicense', () => {
      it('should forward to drivingLicenseService', () => {
        const entity = { id: 11488 };
        const entity2 = { id: 21872 };
        vitest.spyOn(drivingLicenseService, 'compareDrivingLicense');
        comp.compareDrivingLicense(entity, entity2);
        expect(drivingLicenseService.compareDrivingLicense).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
