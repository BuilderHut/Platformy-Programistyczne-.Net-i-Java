import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import dayjs from 'dayjs/esm';
import { Observable, asapScheduler, catchError, map, scheduled } from 'rxjs';

import { DATE_FORMAT } from 'app/config/input.constants';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { SearchWithPagination } from 'app/core/request/request.model';
import { isPresent } from 'app/core/util/operators';
import { IDrivingLicense, NewDrivingLicense } from '../driving-license.model';

export type PartialUpdateDrivingLicense = Partial<IDrivingLicense> & Pick<IDrivingLicense, 'id'>;

type RestOf<T extends IDrivingLicense | NewDrivingLicense> = Omit<T, 'issueDate' | 'expirationDate'> & {
  issueDate?: string | null;
  expirationDate?: string | null;
};

export type RestDrivingLicense = RestOf<IDrivingLicense>;

export type NewRestDrivingLicense = RestOf<NewDrivingLicense>;

export type PartialUpdateRestDrivingLicense = RestOf<PartialUpdateDrivingLicense>;

@Injectable()
export class DrivingLicensesService {
  readonly drivingLicensesParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly drivingLicensesResource = httpResource<RestDrivingLicense[]>(() => {
    const params = this.drivingLicensesParams();
    if (!params) {
      return undefined;
    }
    return { url: params.query ? this.resourceSearchUrl : this.resourceUrl, params };
  });
  /**
   * This signal holds the list of drivingLicense that have been fetched. It is updated when the drivingLicensesResource emits a new value.
   * In case of error while fetching the drivingLicenses, the signal is set to an empty array.
   */
  readonly drivingLicenses = computed(() =>
    (this.drivingLicensesResource.hasValue() ? this.drivingLicensesResource.value() : []).map(item => this.convertValueFromServer(item)),
  );
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/driving-licenses');
  protected readonly resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/driving-licenses/_search');

  protected convertValueFromServer(restDrivingLicense: RestDrivingLicense): IDrivingLicense {
    return {
      ...restDrivingLicense,
      issueDate: restDrivingLicense.issueDate ? dayjs(restDrivingLicense.issueDate) : undefined,
      expirationDate: restDrivingLicense.expirationDate ? dayjs(restDrivingLicense.expirationDate) : undefined,
    };
  }
}

@Injectable({ providedIn: 'root' })
export class DrivingLicenseService extends DrivingLicensesService {
  protected readonly http = inject(HttpClient);

  create(drivingLicense: NewDrivingLicense): Observable<IDrivingLicense> {
    const copy = this.convertValueFromClient(drivingLicense);
    return this.http.post<RestDrivingLicense>(this.resourceUrl, copy).pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(drivingLicense: IDrivingLicense): Observable<IDrivingLicense> {
    const copy = this.convertValueFromClient(drivingLicense);
    return this.http
      .put<RestDrivingLicense>(`${this.resourceUrl}/${encodeURIComponent(this.getDrivingLicenseIdentifier(drivingLicense))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(drivingLicense: PartialUpdateDrivingLicense): Observable<IDrivingLicense> {
    const copy = this.convertValueFromClient(drivingLicense);
    return this.http
      .patch<RestDrivingLicense>(`${this.resourceUrl}/${encodeURIComponent(this.getDrivingLicenseIdentifier(drivingLicense))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<IDrivingLicense> {
    return this.http
      .get<RestDrivingLicense>(`${this.resourceUrl}/${encodeURIComponent(id)}`)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<HttpResponse<IDrivingLicense[]>> {
    const options = createRequestOption(req);
    return this.http
      .get<RestDrivingLicense[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => res.clone({ body: this.convertResponseArrayFromServer(res.body!) })));
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  search(req: SearchWithPagination): Observable<IDrivingLicense[]> {
    const options = createRequestOption(req);
    return this.http.get<RestDrivingLicense[]>(this.resourceSearchUrl, { params: options }).pipe(
      map(res => this.convertResponseArrayFromServer(res)),
      catchError(() => scheduled([], asapScheduler)),
    );
  }

  getDrivingLicenseIdentifier(drivingLicense: Pick<IDrivingLicense, 'id'>): number {
    return drivingLicense.id;
  }

  compareDrivingLicense(o1: Pick<IDrivingLicense, 'id'> | null, o2: Pick<IDrivingLicense, 'id'> | null): boolean {
    return o1 && o2 ? this.getDrivingLicenseIdentifier(o1) === this.getDrivingLicenseIdentifier(o2) : o1 === o2;
  }

  addDrivingLicenseToCollectionIfMissing<Type extends Pick<IDrivingLicense, 'id'>>(
    drivingLicenseCollection: Type[],
    ...drivingLicensesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const drivingLicenses: Type[] = drivingLicensesToCheck.filter(isPresent);
    if (drivingLicenses.length > 0) {
      const drivingLicenseCollectionIdentifiers = drivingLicenseCollection.map(drivingLicenseItem =>
        this.getDrivingLicenseIdentifier(drivingLicenseItem),
      );
      const drivingLicensesToAdd = drivingLicenses.filter(drivingLicenseItem => {
        const drivingLicenseIdentifier = this.getDrivingLicenseIdentifier(drivingLicenseItem);
        if (drivingLicenseCollectionIdentifiers.includes(drivingLicenseIdentifier)) {
          return false;
        }
        drivingLicenseCollectionIdentifiers.push(drivingLicenseIdentifier);
        return true;
      });
      return [...drivingLicensesToAdd, ...drivingLicenseCollection];
    }
    return drivingLicenseCollection;
  }

  protected convertValueFromClient<T extends IDrivingLicense | NewDrivingLicense | PartialUpdateDrivingLicense>(
    drivingLicense: T,
  ): RestOf<T> {
    return {
      ...drivingLicense,
      issueDate: drivingLicense.issueDate?.format(DATE_FORMAT) ?? null,
      expirationDate: drivingLicense.expirationDate?.format(DATE_FORMAT) ?? null,
    };
  }

  protected convertResponseFromServer(res: RestDrivingLicense): IDrivingLicense {
    return this.convertValueFromServer(res);
  }

  protected convertResponseArrayFromServer(res: RestDrivingLicense[]): IDrivingLicense[] {
    return res.map(item => this.convertValueFromServer(item));
  }
}
