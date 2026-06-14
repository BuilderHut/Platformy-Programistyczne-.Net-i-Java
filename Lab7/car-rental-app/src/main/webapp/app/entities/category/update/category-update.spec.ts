import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { TranslateModule } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { ICar } from 'app/entities/car/car.model';
import { CarService } from 'app/entities/car/service/car.service';
import { ICategory } from '../category.model';
import { CategoryService } from '../service/category.service';

import { CategoryFormService } from './category-form.service';
import { CategoryUpdate } from './category-update';

describe('Category Management Update Component', () => {
  let comp: CategoryUpdate;
  let fixture: ComponentFixture<CategoryUpdate>;
  let activatedRoute: ActivatedRoute;
  let categoryFormService: CategoryFormService;
  let categoryService: CategoryService;
  let carService: CarService;

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

    fixture = TestBed.createComponent(CategoryUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    categoryFormService = TestBed.inject(CategoryFormService);
    categoryService = TestBed.inject(CategoryService);
    carService = TestBed.inject(CarService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call Car query and add missing value', () => {
      const category: ICategory = { id: 4374 };
      const carses: ICar[] = [{ id: 30624 }];
      category.carses = carses;

      const carCollection: ICar[] = [{ id: 30624 }];
      vitest.spyOn(carService, 'query').mockReturnValue(of(new HttpResponse({ body: carCollection })));
      const additionalCars = [...carses];
      const expectedCollection: ICar[] = [...additionalCars, ...carCollection];
      vitest.spyOn(carService, 'addCarToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ category });
      comp.ngOnInit();

      expect(carService.query).toHaveBeenCalled();
      expect(carService.addCarToCollectionIfMissing).toHaveBeenCalledWith(
        carCollection,
        ...additionalCars.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.carsSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const category: ICategory = { id: 4374 };
      const cars: ICar = { id: 30624 };
      category.carses = [cars];

      activatedRoute.data = of({ category });
      comp.ngOnInit();

      expect(comp.carsSharedCollection()).toContainEqual(cars);
      expect(comp.category).toEqual(category);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<ICategory>();
      const category = { id: 6752 };
      vitest.spyOn(categoryFormService, 'getCategory').mockReturnValue(category);
      vitest.spyOn(categoryService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ category });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(category);
      saveSubject.complete();

      // THEN
      expect(categoryFormService.getCategory).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(categoryService.update).toHaveBeenCalledWith(expect.objectContaining(category));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<ICategory>();
      const category = { id: 6752 };
      vitest.spyOn(categoryFormService, 'getCategory').mockReturnValue({ id: null });
      vitest.spyOn(categoryService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ category: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(category);
      saveSubject.complete();

      // THEN
      expect(categoryFormService.getCategory).toHaveBeenCalled();
      expect(categoryService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<ICategory>();
      const category = { id: 6752 };
      vitest.spyOn(categoryService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ category });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(categoryService.update).toHaveBeenCalled();
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
  });
});
