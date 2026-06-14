import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IDrivingLicense, NewDrivingLicense } from '../driving-license.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IDrivingLicense for edit and NewDrivingLicenseFormGroupInput for create.
 */
type DrivingLicenseFormGroupInput = IDrivingLicense | PartialWithRequiredKeyOf<NewDrivingLicense>;

type DrivingLicenseFormDefaults = Pick<NewDrivingLicense, 'id'>;

type DrivingLicenseFormGroupContent = {
  id: FormControl<IDrivingLicense['id'] | NewDrivingLicense['id']>;
  licenseNumber: FormControl<IDrivingLicense['licenseNumber']>;
  issueDate: FormControl<IDrivingLicense['issueDate']>;
  expirationDate: FormControl<IDrivingLicense['expirationDate']>;
};

export type DrivingLicenseFormGroup = FormGroup<DrivingLicenseFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class DrivingLicenseFormService {
  createDrivingLicenseFormGroup(drivingLicense?: DrivingLicenseFormGroupInput): DrivingLicenseFormGroup {
    const drivingLicenseRawValue = {
      ...this.getFormDefaults(),
      ...(drivingLicense ?? { id: null }),
    };
    return new FormGroup<DrivingLicenseFormGroupContent>({
      id: new FormControl(
        { value: drivingLicenseRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      licenseNumber: new FormControl(drivingLicenseRawValue.licenseNumber, {
        validators: [Validators.required],
      }),
      issueDate: new FormControl(drivingLicenseRawValue.issueDate, {
        validators: [Validators.required],
      }),
      expirationDate: new FormControl(drivingLicenseRawValue.expirationDate, {
        validators: [Validators.required],
      }),
    });
  }

  getDrivingLicense(form: DrivingLicenseFormGroup): IDrivingLicense | NewDrivingLicense {
    return form.getRawValue();
  }

  resetForm(form: DrivingLicenseFormGroup, drivingLicense: DrivingLicenseFormGroupInput): void {
    const drivingLicenseRawValue = { ...this.getFormDefaults(), ...drivingLicense };
    form.reset({
      ...drivingLicenseRawValue,
      id: { value: drivingLicenseRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): DrivingLicenseFormDefaults {
    return {
      id: null,
    };
  }
}
