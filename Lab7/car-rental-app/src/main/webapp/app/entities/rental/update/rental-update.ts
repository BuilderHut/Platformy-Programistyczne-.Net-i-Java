import { HttpResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbInputDatepicker } from '@ng-bootstrap/ng-bootstrap/datepicker';
import { TranslateModule } from '@ngx-translate/core';
import { Observable, finalize, map } from 'rxjs';

import { ICar } from 'app/entities/car/car.model';
import { CarService } from 'app/entities/car/service/car.service';
import { ICustomer } from 'app/entities/customer/customer.model';
import { CustomerService } from 'app/entities/customer/service/customer.service';
import { RentalStatus } from 'app/entities/enumerations/rental-status.model';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { IRental } from '../rental.model';
import { RentalService } from '../service/rental.service';

import { RentalFormGroup, RentalFormService } from './rental-form.service';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'jhi-rental-update',
  templateUrl: './rental-update.html',
  imports: [TranslateDirective, TranslateModule, FontAwesomeModule, AlertError, ReactiveFormsModule, NgbInputDatepicker],
})
export class RentalUpdate implements OnInit {
  readonly isSaving = signal(false);
  rental: IRental | null = null;
  rentalStatusValues = Object.keys(RentalStatus);

  carsSharedCollection = signal<ICar[]>([]);
  customersSharedCollection = signal<ICustomer[]>([]);

  protected rentalService = inject(RentalService);
  protected rentalFormService = inject(RentalFormService);
  protected carService = inject(CarService);
  protected customerService = inject(CustomerService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: RentalFormGroup = this.rentalFormService.createRentalFormGroup();

  compareCar = (o1: ICar | null, o2: ICar | null): boolean => this.carService.compareCar(o1, o2);

  compareCustomer = (o1: ICustomer | null, o2: ICustomer | null): boolean => this.customerService.compareCustomer(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ rental }) => {
      this.rental = rental;
      if (rental) {
        this.updateForm(rental);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const rental = this.rentalFormService.getRental(this.editForm);
    if (rental.id === null) {
      this.subscribeToSaveResponse(this.rentalService.create(rental));
    } else {
      this.subscribeToSaveResponse(this.rentalService.update(rental));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IRental | null>): void {
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

  protected updateForm(rental: IRental): void {
    this.rental = rental;
    this.rentalFormService.resetForm(this.editForm, rental);

    this.carsSharedCollection.update(cars => this.carService.addCarToCollectionIfMissing<ICar>(cars, rental.car));
    this.customersSharedCollection.update(customers =>
      this.customerService.addCustomerToCollectionIfMissing<ICustomer>(customers, rental.customer),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.carService
      .query()
      .pipe(map((res: HttpResponse<ICar[]>) => res.body ?? []))
      .pipe(map((cars: ICar[]) => this.carService.addCarToCollectionIfMissing<ICar>(cars, this.rental?.car)))
      .subscribe((cars: ICar[]) => this.carsSharedCollection.set(cars));

    this.customerService
      .query()
      .pipe(map((res: HttpResponse<ICustomer[]>) => res.body ?? []))
      .pipe(
        map((customers: ICustomer[]) => this.customerService.addCustomerToCollectionIfMissing<ICustomer>(customers, this.rental?.customer)),
      )
      .subscribe((customers: ICustomer[]) => this.customersSharedCollection.set(customers));
  }
}
