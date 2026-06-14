import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { ICar, NewCar } from '../car.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ICar for edit and NewCarFormGroupInput for create.
 */
type CarFormGroupInput = ICar | PartialWithRequiredKeyOf<NewCar>;

type CarFormDefaults = Pick<NewCar, 'id' | 'categorieses'>;

type CarFormGroupContent = {
  id: FormControl<ICar['id'] | NewCar['id']>;
  brand: FormControl<ICar['brand']>;
  model: FormControl<ICar['model']>;
  productionYear: FormControl<ICar['productionYear']>;
  dailyPrice: FormControl<ICar['dailyPrice']>;
  status: FormControl<ICar['status']>;
  categorieses: FormControl<ICar['categorieses']>;
};

export type CarFormGroup = FormGroup<CarFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class CarFormService {
  createCarFormGroup(car?: CarFormGroupInput): CarFormGroup {
    const carRawValue = {
      ...this.getFormDefaults(),
      ...(car ?? { id: null }),
    };
    return new FormGroup<CarFormGroupContent>({
      id: new FormControl(
        { value: carRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      brand: new FormControl(carRawValue.brand, {
        validators: [Validators.required],
      }),
      model: new FormControl(carRawValue.model, {
        validators: [Validators.required],
      }),
      productionYear: new FormControl(carRawValue.productionYear, {
        validators: [Validators.required, Validators.min(1990)],
      }),
      dailyPrice: new FormControl(carRawValue.dailyPrice, {
        validators: [Validators.required, Validators.min(0)],
      }),
      status: new FormControl(carRawValue.status, {
        validators: [Validators.required],
      }),
      categorieses: new FormControl(carRawValue.categorieses ?? []),
    });
  }

  getCar(form: CarFormGroup): ICar | NewCar {
    return form.getRawValue();
  }

  resetForm(form: CarFormGroup, car: CarFormGroupInput): void {
    const carRawValue = { ...this.getFormDefaults(), ...car };
    form.reset({
      ...carRawValue,
      id: { value: carRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): CarFormDefaults {
    return {
      id: null,
      categorieses: [],
    };
  }
}
