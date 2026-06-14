import { ChangeDetectionStrategy, Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbInputDatepicker } from '@ng-bootstrap/ng-bootstrap/datepicker';
import { TranslateModule } from '@ngx-translate/core';
import { Observable, finalize } from 'rxjs';

import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { IDrivingLicense } from '../driving-license.model';
import { DrivingLicenseService } from '../service/driving-license.service';

import { DrivingLicenseFormGroup, DrivingLicenseFormService } from './driving-license-form.service';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'jhi-driving-license-update',
  templateUrl: './driving-license-update.html',
  imports: [TranslateDirective, TranslateModule, FontAwesomeModule, AlertError, ReactiveFormsModule, NgbInputDatepicker],
})
export class DrivingLicenseUpdate implements OnInit {
  readonly isSaving = signal(false);
  drivingLicense: IDrivingLicense | null = null;

  protected drivingLicenseService = inject(DrivingLicenseService);
  protected drivingLicenseFormService = inject(DrivingLicenseFormService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: DrivingLicenseFormGroup = this.drivingLicenseFormService.createDrivingLicenseFormGroup();

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ drivingLicense }) => {
      this.drivingLicense = drivingLicense;
      if (drivingLicense) {
        this.updateForm(drivingLicense);
      }
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const drivingLicense = this.drivingLicenseFormService.getDrivingLicense(this.editForm);
    if (drivingLicense.id === null) {
      this.subscribeToSaveResponse(this.drivingLicenseService.create(drivingLicense));
    } else {
      this.subscribeToSaveResponse(this.drivingLicenseService.update(drivingLicense));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IDrivingLicense | null>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving.set(false);
  }

  protected updateForm(drivingLicense: IDrivingLicense): void {
    this.drivingLicense = drivingLicense;
    this.drivingLicenseFormService.resetForm(this.editForm, drivingLicense);
  }
}
