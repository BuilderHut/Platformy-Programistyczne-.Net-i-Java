import { HttpResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslateModule } from '@ngx-translate/core';
import { Observable, finalize, map } from 'rxjs';

import { IDrivingLicense } from 'app/entities/driving-license/driving-license.model';
import { DrivingLicenseService } from 'app/entities/driving-license/service/driving-license.service';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { ICustomer } from '../customer.model';
import { CustomerService } from '../service/customer.service';

import { CustomerFormGroup, CustomerFormService } from './customer-form.service';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'jhi-customer-update',
  templateUrl: './customer-update.html',
  imports: [TranslateDirective, TranslateModule, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class CustomerUpdate implements OnInit {
  readonly isSaving = signal(false);
  customer: ICustomer | null = null;

  drivingLicensesCollection = signal<IDrivingLicense[]>([]);

  protected customerService = inject(CustomerService);
  protected customerFormService = inject(CustomerFormService);
  protected drivingLicenseService = inject(DrivingLicenseService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: CustomerFormGroup = this.customerFormService.createCustomerFormGroup();

  compareDrivingLicense = (o1: IDrivingLicense | null, o2: IDrivingLicense | null): boolean =>
    this.drivingLicenseService.compareDrivingLicense(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ customer }) => {
      this.customer = customer;
      if (customer) {
        this.updateForm(customer);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const customer = this.customerFormService.getCustomer(this.editForm);
    if (customer.id === null) {
      this.subscribeToSaveResponse(this.customerService.create(customer));
    } else {
      this.subscribeToSaveResponse(this.customerService.update(customer));
    }
  }

  protected subscribeToSaveResponse(result: Observable<ICustomer | null>): void {
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

  protected updateForm(customer: ICustomer): void {
    this.customer = customer;
    this.customerFormService.resetForm(this.editForm, customer);

    this.drivingLicensesCollection.set(
      this.drivingLicenseService.addDrivingLicenseToCollectionIfMissing<IDrivingLicense>(
        this.drivingLicensesCollection(),
        customer.drivingLicense,
      ),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.drivingLicenseService
      .query({ filter: 'customer-is-null' })
      .pipe(map((res: HttpResponse<IDrivingLicense[]>) => res.body ?? []))
      .pipe(
        map((drivingLicenses: IDrivingLicense[]) =>
          this.drivingLicenseService.addDrivingLicenseToCollectionIfMissing<IDrivingLicense>(
            drivingLicenses,
            this.customer?.drivingLicense,
          ),
        ),
      )
      .subscribe((drivingLicenses: IDrivingLicense[]) => this.drivingLicensesCollection.set(drivingLicenses));
  }
}
