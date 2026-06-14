import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import dayjs from 'dayjs/esm';
import { Observable, asapScheduler, catchError, map, scheduled } from 'rxjs';

import { DATE_FORMAT } from 'app/config/input.constants';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { SearchWithPagination } from 'app/core/request/request.model';
import { isPresent } from 'app/core/util/operators';
import { IRental, NewRental } from '../rental.model';

export type PartialUpdateRental = Partial<IRental> & Pick<IRental, 'id'>;

type RestOf<T extends IRental | NewRental> = Omit<T, 'startDate' | 'endDate'> & {
  startDate?: string | null;
  endDate?: string | null;
};

export type RestRental = RestOf<IRental>;

export type NewRestRental = RestOf<NewRental>;

export type PartialUpdateRestRental = RestOf<PartialUpdateRental>;

@Injectable()
export class RentalsService {
  readonly rentalsParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly rentalsResource = httpResource<RestRental[]>(() => {
    const params = this.rentalsParams();
    if (!params) {
      return undefined;
    }
    return { url: params.query ? this.resourceSearchUrl : this.resourceUrl, params };
  });
  /**
   * This signal holds the list of rental that have been fetched. It is updated when the rentalsResource emits a new value.
   * In case of error while fetching the rentals, the signal is set to an empty array.
   */
  readonly rentals = computed(() =>
    (this.rentalsResource.hasValue() ? this.rentalsResource.value() : []).map(item => this.convertValueFromServer(item)),
  );
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/rentals');
  protected readonly resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/rentals/_search');

  protected convertValueFromServer(restRental: RestRental): IRental {
    return {
      ...restRental,
      startDate: restRental.startDate ? dayjs(restRental.startDate) : undefined,
      endDate: restRental.endDate ? dayjs(restRental.endDate) : undefined,
    };
  }
}

@Injectable({ providedIn: 'root' })
export class RentalService extends RentalsService {
  protected readonly http = inject(HttpClient);

  create(rental: NewRental): Observable<IRental> {
    const copy = this.convertValueFromClient(rental);
    return this.http.post<RestRental>(this.resourceUrl, copy).pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(rental: IRental): Observable<IRental> {
    const copy = this.convertValueFromClient(rental);
    return this.http
      .put<RestRental>(`${this.resourceUrl}/${encodeURIComponent(this.getRentalIdentifier(rental))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(rental: PartialUpdateRental): Observable<IRental> {
    const copy = this.convertValueFromClient(rental);
    return this.http
      .patch<RestRental>(`${this.resourceUrl}/${encodeURIComponent(this.getRentalIdentifier(rental))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<IRental> {
    return this.http.get<RestRental>(`${this.resourceUrl}/${encodeURIComponent(id)}`).pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<HttpResponse<IRental[]>> {
    const options = createRequestOption(req);
    return this.http
      .get<RestRental[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => res.clone({ body: this.convertResponseArrayFromServer(res.body!) })));
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  search(req: SearchWithPagination): Observable<IRental[]> {
    const options = createRequestOption(req);
    return this.http.get<RestRental[]>(this.resourceSearchUrl, { params: options }).pipe(
      map(res => this.convertResponseArrayFromServer(res)),
      catchError(() => scheduled([], asapScheduler)),
    );
  }

  getRentalIdentifier(rental: Pick<IRental, 'id'>): number {
    return rental.id;
  }

  compareRental(o1: Pick<IRental, 'id'> | null, o2: Pick<IRental, 'id'> | null): boolean {
    return o1 && o2 ? this.getRentalIdentifier(o1) === this.getRentalIdentifier(o2) : o1 === o2;
  }

  addRentalToCollectionIfMissing<Type extends Pick<IRental, 'id'>>(
    rentalCollection: Type[],
    ...rentalsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const rentals: Type[] = rentalsToCheck.filter(isPresent);
    if (rentals.length > 0) {
      const rentalCollectionIdentifiers = rentalCollection.map(rentalItem => this.getRentalIdentifier(rentalItem));
      const rentalsToAdd = rentals.filter(rentalItem => {
        const rentalIdentifier = this.getRentalIdentifier(rentalItem);
        if (rentalCollectionIdentifiers.includes(rentalIdentifier)) {
          return false;
        }
        rentalCollectionIdentifiers.push(rentalIdentifier);
        return true;
      });
      return [...rentalsToAdd, ...rentalCollection];
    }
    return rentalCollection;
  }

  protected convertValueFromClient<T extends IRental | NewRental | PartialUpdateRental>(rental: T): RestOf<T> {
    return {
      ...rental,
      startDate: rental.startDate?.format(DATE_FORMAT) ?? null,
      endDate: rental.endDate?.format(DATE_FORMAT) ?? null,
    };
  }

  protected convertResponseFromServer(res: RestRental): IRental {
    return this.convertValueFromServer(res);
  }

  protected convertResponseArrayFromServer(res: RestRental[]): IRental[] {
    return res.map(item => this.convertValueFromServer(item));
  }
}
