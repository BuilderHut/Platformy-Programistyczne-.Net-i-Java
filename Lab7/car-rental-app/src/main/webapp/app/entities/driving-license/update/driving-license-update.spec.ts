import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { TranslateModule } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { IDrivingLicense } from '../driving-license.model';
import { DrivingLicenseService } from '../service/driving-license.service';

import { DrivingLicenseFormService } from './driving-license-form.service';
import { DrivingLicenseUpdate } from './driving-license-update';

describe('DrivingLicense Management Update Component', () => {
  let comp: DrivingLicenseUpdate;
  let fixture: ComponentFixture<DrivingLicenseUpdate>;
  let activatedRoute: ActivatedRoute;
  let drivingLicenseFormService: DrivingLicenseFormService;
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

    fixture = TestBed.createComponent(DrivingLicenseUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    drivingLicenseFormService = TestBed.inject(DrivingLicenseFormService);
    drivingLicenseService = TestBed.inject(DrivingLicenseService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should update editForm', () => {
      const drivingLicense: IDrivingLicense = { id: 21872 };

      activatedRoute.data = of({ drivingLicense });
      comp.ngOnInit();

      expect(comp.drivingLicense).toEqual(drivingLicense);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IDrivingLicense>();
      const drivingLicense = { id: 11488 };
      vitest.spyOn(drivingLicenseFormService, 'getDrivingLicense').mockReturnValue(drivingLicense);
      vitest.spyOn(drivingLicenseService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ drivingLicense });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(drivingLicense);
      saveSubject.complete();

      // THEN
      expect(drivingLicenseFormService.getDrivingLicense).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(drivingLicenseService.update).toHaveBeenCalledWith(expect.objectContaining(drivingLicense));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IDrivingLicense>();
      const drivingLicense = { id: 11488 };
      vitest.spyOn(drivingLicenseFormService, 'getDrivingLicense').mockReturnValue({ id: null });
      vitest.spyOn(drivingLicenseService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ drivingLicense: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(drivingLicense);
      saveSubject.complete();

      // THEN
      expect(drivingLicenseFormService.getDrivingLicense).toHaveBeenCalled();
      expect(drivingLicenseService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IDrivingLicense>();
      const drivingLicense = { id: 11488 };
      vitest.spyOn(drivingLicenseService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ drivingLicense });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(drivingLicenseService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
