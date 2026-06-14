import { HttpResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslateModule } from '@ngx-translate/core';
import { Observable, finalize, map } from 'rxjs';

import { ICar } from 'app/entities/car/car.model';
import { CarService } from 'app/entities/car/service/car.service';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { ICategory } from '../category.model';
import { CategoryService } from '../service/category.service';

import { CategoryFormGroup, CategoryFormService } from './category-form.service';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'jhi-category-update',
  templateUrl: './category-update.html',
  imports: [TranslateDirective, TranslateModule, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class CategoryUpdate implements OnInit {
  readonly isSaving = signal(false);
  category: ICategory | null = null;

  carsSharedCollection = signal<ICar[]>([]);

  protected categoryService = inject(CategoryService);
  protected categoryFormService = inject(CategoryFormService);
  protected carService = inject(CarService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: CategoryFormGroup = this.categoryFormService.createCategoryFormGroup();

  compareCar = (o1: ICar | null, o2: ICar | null): boolean => this.carService.compareCar(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ category }) => {
      this.category = category;
      if (category) {
        this.updateForm(category);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const category = this.categoryFormService.getCategory(this.editForm);
    if (category.id === null) {
      this.subscribeToSaveResponse(this.categoryService.create(category));
    } else {
      this.subscribeToSaveResponse(this.categoryService.update(category));
    }
  }

  protected subscribeToSaveResponse(result: Observable<ICategory | null>): void {
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

  protected updateForm(category: ICategory): void {
    this.category = category;
    this.categoryFormService.resetForm(this.editForm, category);

    this.carsSharedCollection.update(cars => this.carService.addCarToCollectionIfMissing<ICar>(cars, ...(category.carses ?? [])));
  }

  protected loadRelationshipsOptions(): void {
    this.carService
      .query()
      .pipe(map((res: HttpResponse<ICar[]>) => res.body ?? []))
      .pipe(map((cars: ICar[]) => this.carService.addCarToCollectionIfMissing<ICar>(cars, ...(this.category?.carses ?? []))))
      .subscribe((cars: ICar[]) => this.carsSharedCollection.set(cars));
  }
}
