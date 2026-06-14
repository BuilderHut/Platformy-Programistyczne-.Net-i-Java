import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import { Observable, asapScheduler, catchError, scheduled } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { SearchWithPagination } from 'app/core/request/request.model';
import { isPresent } from 'app/core/util/operators';
import { ICar, NewCar } from '../car.model';

export type PartialUpdateCar = Partial<ICar> & Pick<ICar, 'id'>;

@Injectable()
export class CarsService {
  readonly carsParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(undefined);
  readonly carsResource = httpResource<ICar[]>(() => {
    const params = this.carsParams();
    if (!params) {
      return undefined;
    }
    return { url: params.query ? this.resourceSearchUrl : this.resourceUrl, params };
  });
  /**
   * This signal holds the list of car that have been fetched. It is updated when the carsResource emits a new value.
   * In case of error while fetching the cars, the signal is set to an empty array.
   */
  readonly cars = computed(() => (this.carsResource.hasValue() ? this.carsResource.value() : []));
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/cars');
  protected readonly resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/cars/_search');
}

@Injectable({ providedIn: 'root' })
export class CarService extends CarsService {
  protected readonly http = inject(HttpClient);

  create(car: NewCar): Observable<ICar> {
    return this.http.post<ICar>(this.resourceUrl, car);
  }

  update(car: ICar): Observable<ICar> {
    return this.http.put<ICar>(`${this.resourceUrl}/${encodeURIComponent(this.getCarIdentifier(car))}`, car);
  }

  partialUpdate(car: PartialUpdateCar): Observable<ICar> {
    return this.http.patch<ICar>(`${this.resourceUrl}/${encodeURIComponent(this.getCarIdentifier(car))}`, car);
  }

  find(id: number): Observable<ICar> {
    return this.http.get<ICar>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  query(req?: any): Observable<HttpResponse<ICar[]>> {
    const options = createRequestOption(req);
    return this.http.get<ICar[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  search(req: SearchWithPagination): Observable<ICar[]> {
    const options = createRequestOption(req);
    return this.http.get<ICar[]>(this.resourceSearchUrl, { params: options }).pipe(catchError(() => scheduled([], asapScheduler)));
  }

  getCarIdentifier(car: Pick<ICar, 'id'>): number {
    return car.id;
  }

  compareCar(o1: Pick<ICar, 'id'> | null, o2: Pick<ICar, 'id'> | null): boolean {
    return o1 && o2 ? this.getCarIdentifier(o1) === this.getCarIdentifier(o2) : o1 === o2;
  }

  addCarToCollectionIfMissing<Type extends Pick<ICar, 'id'>>(carCollection: Type[], ...carsToCheck: (Type | null | undefined)[]): Type[] {
    const cars: Type[] = carsToCheck.filter(isPresent);
    if (cars.length > 0) {
      const carCollectionIdentifiers = carCollection.map(carItem => this.getCarIdentifier(carItem));
      const carsToAdd = cars.filter(carItem => {
        const carIdentifier = this.getCarIdentifier(carItem);
        if (carCollectionIdentifiers.includes(carIdentifier)) {
          return false;
        }
        carCollectionIdentifiers.push(carIdentifier);
        return true;
      });
      return [...carsToAdd, ...carCollection];
    }
    return carCollection;
  }
}
