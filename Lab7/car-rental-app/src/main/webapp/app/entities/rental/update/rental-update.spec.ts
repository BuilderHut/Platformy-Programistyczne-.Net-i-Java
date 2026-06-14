import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { TranslateModule } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { ICar } from 'app/entities/car/car.model';
import { CarService } from 'app/entities/car/service/car.service';
import { ICustomer } from 'app/entities/customer/customer.model';
import { CustomerService } from 'app/entities/customer/service/customer.service';
import { IRental } from '../rental.model';
import { RentalService } from '../service/rental.service';

import { RentalFormService } from './rental-form.service';
import { RentalUpdate } from './rental-update';

describe('Rental Management Update Component', () => {
  let comp: RentalUpdate;
  let fixture: ComponentFixture<RentalUpdate>;
  let activatedRoute: ActivatedRoute;
  let rentalFormService: RentalFormService;
  let rentalService: RentalService;
  let carService: CarService;
  let customerService: CustomerService;

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

    fixture = TestBed.createComponent(RentalUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    rentalFormService = TestBed.inject(RentalFormService);
    rentalService = TestBed.inject(RentalService);
    carService = TestBed.inject(CarService);
    customerService = TestBed.inject(CustomerService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call Car query and add missing value', () => {
      const rental: IRental = { id: 17269 };
      const car: ICar = { id: 30624 };
      rental.car = car;

      const carCollection: ICar[] = [{ id: 30624 }];
      vitest.spyOn(carService, 'query').mockReturnValue(of(new HttpResponse({ body: carCollection })));
      const additionalCars = [car];
      const expectedCollection: ICar[] = [...additionalCars, ...carCollection];
      vitest.spyOn(carService, 'addCarToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ rental });
      comp.ngOnInit();

      expect(carService.query).toHaveBeenCalled();
      expect(carService.addCarToCollectionIfMissing).toHaveBeenCalledWith(
        carCollection,
        ...additionalCars.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.carsSharedCollection()).toEqual(expectedCollection);
    });

    it('should call Customer query and add missing value', () => {
      const rental: IRental = { id: 17269 };
      const customer: ICustomer = { id: 26915 };
      rental.customer = customer;

      const customerCollection: ICustomer[] = [{ id: 26915 }];
      vitest.spyOn(customerService, 'query').mockReturnValue(of(new HttpResponse({ body: customerCollection })));
      const additionalCustomers = [customer];
      const expectedCollection: ICustomer[] = [...additionalCustomers, ...customerCollection];
      vitest.spyOn(customerService, 'addCustomerToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ rental });
      comp.ngOnInit();

      expect(customerService.query).toHaveBeenCalled();
      expect(customerService.addCustomerToCollectionIfMissing).toHaveBeenCalledWith(
        customerCollection,
        ...additionalCustomers.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.customersSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const rental: IRental = { id: 17269 };
      const car: ICar = { id: 30624 };
      rental.car = car;
      const customer: ICustomer = { id: 26915 };
      rental.customer = customer;

      activatedRoute.data = of({ rental });
      comp.ngOnInit();

      expect(comp.carsSharedCollection()).toContainEqual(car);
      expect(comp.customersSharedCollection()).toContainEqual(customer);
      expect(comp.rental).toEqual(rental);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IRental>();
      const rental = { id: 12599 };
      vitest.spyOn(rentalFormService, 'getRental').mockReturnValue(rental);
      vitest.spyOn(rentalService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ rental });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(rental);
      saveSubject.complete();

      // THEN
      expect(rentalFormService.getRental).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(rentalService.update).toHaveBeenCalledWith(expect.objectContaining(rental));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IRental>();
      const rental = { id: 12599 };
      vitest.spyOn(rentalFormService, 'getRental').mockReturnValue({ id: null });
      vitest.spyOn(rentalService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ rental: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(rental);
      saveSubject.complete();

      // THEN
      expect(rentalFormService.getRental).toHaveBeenCalled();
      expect(rentalService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IRental>();
      const rental = { id: 12599 };
      vitest.spyOn(rentalService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ rental });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(rentalService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareCar', () => {
      it('should forward to carService', () => {
        const entity = { id: 30624 };
        const entity2 = { id: 14019 };
        vitest.spyOn(carService, 'compareCar');
        comp.compareCar(entity, entity2);
        expect(carService.compareCar).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareCustomer', () => {
      it('should forward to customerService', () => {
        const entity = { id: 26915 };
        const entity2 = { id: 21032 };
        vitest.spyOn(customerService, 'compareCustomer');
        comp.compareCustomer(entity, entity2);
        expect(customerService.compareCustomer).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
