import { HttpResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslateModule } from '@ngx-translate/core';
import { Observable, finalize, map } from 'rxjs';

import { ICategory } from 'app/entities/category/category.model';
import { CategoryService } from 'app/entities/category/service/category.service';
import { CarStatus } from 'app/entities/enumerations/car-status.model';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { ICar } from '../car.model';
import { CarService } from '../service/car.service';

import { CarFormGroup, CarFormService } from './car-form.service';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'jhi-car-update',
  templateUrl: './car-update.html',
  imports: [TranslateDirective, TranslateModule, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class CarUpdate implements OnInit {
  readonly isSaving = signal(false);
  car: ICar | null = null;
  carStatusValues = Object.keys(CarStatus);

  categoriesSharedCollection = signal<ICategory[]>([]);

  protected carService = inject(CarService);
  protected carFormService = inject(CarFormService);
  protected categoryService = inject(CategoryService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: CarFormGroup = this.carFormService.createCarFormGroup();

  compareCategory = (o1: ICategory | null, o2: ICategory | null): boolean => this.categoryService.compareCategory(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ car }) => {
      this.car = car;
      if (car) {
        this.updateForm(car);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const car = this.carFormService.getCar(this.editForm);
    if (car.id === null) {
      this.subscribeToSaveResponse(this.carService.create(car));
    } else {
      this.subscribeToSaveResponse(this.carService.update(car));
    }
  }

  protected subscribeToSaveResponse(result: Observable<ICar | null>): void {
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

  protected updateForm(car: ICar): void {
    this.car = car;
    this.carFormService.resetForm(this.editForm, car);

    this.categoriesSharedCollection.update(categories =>
      this.categoryService.addCategoryToCollectionIfMissing<ICategory>(categories, ...(car.categorieses ?? [])),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.categoryService
      .query()
      .pipe(map((res: HttpResponse<ICategory[]>) => res.body ?? []))
      .pipe(
        map((categories: ICategory[]) =>
          this.categoryService.addCategoryToCollectionIfMissing<ICategory>(categories, ...(this.car?.categorieses ?? [])),
        ),
      )
      .subscribe((categories: ICategory[]) => this.categoriesSharedCollection.set(categories));
  }
}
