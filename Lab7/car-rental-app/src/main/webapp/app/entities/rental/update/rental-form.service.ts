import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IRental, NewRental } from '../rental.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IRental for edit and NewRentalFormGroupInput for create.
 */
type RentalFormGroupInput = IRental | PartialWithRequiredKeyOf<NewRental>;

type RentalFormDefaults = Pick<NewRental, 'id'>;

type RentalFormGroupContent = {
  id: FormControl<IRental['id'] | NewRental['id']>;
  startDate: FormControl<IRental['startDate']>;
  endDate: FormControl<IRental['endDate']>;
  totalPrice: FormControl<IRental['totalPrice']>;
  status: FormControl<IRental['status']>;
  car: FormControl<IRental['car']>;
  customer: FormControl<IRental['customer']>;
};

export type RentalFormGroup = FormGroup<RentalFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class RentalFormService {
  createRentalFormGroup(rental?: RentalFormGroupInput): RentalFormGroup {
    const rentalRawValue = {
      ...this.getFormDefaults(),
      ...(rental ?? { id: null }),
    };
    return new FormGroup<RentalFormGroupContent>({
      id: new FormControl(
        { value: rentalRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      startDate: new FormControl(rentalRawValue.startDate, {
        validators: [Validators.required],
      }),
      endDate: new FormControl(rentalRawValue.endDate, {
        validators: [Validators.required],
      }),
      totalPrice: new FormControl(rentalRawValue.totalPrice, {
        validators: [Validators.min(0)],
      }),
      status: new FormControl(rentalRawValue.status, {
        validators: [Validators.required],
      }),
      car: new FormControl(rentalRawValue.car, {
        validators: [Validators.required],
      }),
      customer: new FormControl(rentalRawValue.customer, {
        validators: [Validators.required],
      }),
    });
  }

  getRental(form: RentalFormGroup): IRental | NewRental {
    return form.getRawValue();
  }

  resetForm(form: RentalFormGroup, rental: RentalFormGroupInput): void {
    const rentalRawValue = { ...this.getFormDefaults(), ...rental };
    form.reset({
      ...rentalRawValue,
      id: { value: rentalRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): RentalFormDefaults {
    return {
      id: null,
    };
  }
}
