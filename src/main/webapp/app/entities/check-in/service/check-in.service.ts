import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import dayjs from 'dayjs/esm';
import { Observable, map } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { ICheckIn, NewCheckIn } from '../check-in.model';

export type PartialUpdateCheckIn = Partial<ICheckIn> & Pick<ICheckIn, 'id'>;

type RestOf<T extends ICheckIn | NewCheckIn> = Omit<T, 'checkInTime'> & {
  checkInTime?: string | null;
};

export type RestCheckIn = RestOf<ICheckIn>;

export type NewRestCheckIn = RestOf<NewCheckIn>;

export type PartialUpdateRestCheckIn = RestOf<PartialUpdateCheckIn>;

@Injectable()
export class CheckInsService {
  readonly checkInsParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly checkInsResource = httpResource<RestCheckIn[]>(() => {
    const params = this.checkInsParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of checkIn that have been fetched. It is updated when the checkInsResource emits a new value.
   * In case of error while fetching the checkIns, the signal is set to an empty array.
   */
  readonly checkIns = computed(() =>
    (this.checkInsResource.hasValue() ? this.checkInsResource.value() : []).map(item => this.convertValueFromServer(item)),
  );
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/check-ins');

  protected convertValueFromServer(restCheckIn: RestCheckIn): ICheckIn {
    return {
      ...restCheckIn,
      checkInTime: restCheckIn.checkInTime ? dayjs(restCheckIn.checkInTime) : undefined,
    };
  }
}

@Injectable({ providedIn: 'root' })
export class CheckInService extends CheckInsService {
  protected readonly http = inject(HttpClient);

  create(checkIn: NewCheckIn): Observable<ICheckIn> {
    const copy = this.convertValueFromClient(checkIn);
    return this.http.post<RestCheckIn>(this.resourceUrl, copy).pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(checkIn: ICheckIn): Observable<ICheckIn> {
    const copy = this.convertValueFromClient(checkIn);
    return this.http
      .put<RestCheckIn>(`${this.resourceUrl}/${encodeURIComponent(this.getCheckInIdentifier(checkIn))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(checkIn: PartialUpdateCheckIn): Observable<ICheckIn> {
    const copy = this.convertValueFromClient(checkIn);
    return this.http
      .patch<RestCheckIn>(`${this.resourceUrl}/${encodeURIComponent(this.getCheckInIdentifier(checkIn))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<ICheckIn> {
    return this.http
      .get<RestCheckIn>(`${this.resourceUrl}/${encodeURIComponent(id)}`)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<HttpResponse<ICheckIn[]>> {
    const options = createRequestOption(req);
    return this.http
      .get<RestCheckIn[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => res.clone({ body: this.convertResponseArrayFromServer(res.body!) })));
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getCheckInIdentifier(checkIn: Pick<ICheckIn, 'id'>): number {
    return checkIn.id;
  }

  compareCheckIn(o1: Pick<ICheckIn, 'id'> | null, o2: Pick<ICheckIn, 'id'> | null): boolean {
    return o1 && o2 ? this.getCheckInIdentifier(o1) === this.getCheckInIdentifier(o2) : o1 === o2;
  }

  addCheckInToCollectionIfMissing<Type extends Pick<ICheckIn, 'id'>>(
    checkInCollection: Type[],
    ...checkInsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const checkIns: Type[] = checkInsToCheck.filter(isPresent);
    if (checkIns.length > 0) {
      const checkInCollectionIdentifiers = checkInCollection.map(checkInItem => this.getCheckInIdentifier(checkInItem));
      const checkInsToAdd = checkIns.filter(checkInItem => {
        const checkInIdentifier = this.getCheckInIdentifier(checkInItem);
        if (checkInCollectionIdentifiers.includes(checkInIdentifier)) {
          return false;
        }
        checkInCollectionIdentifiers.push(checkInIdentifier);
        return true;
      });
      return [...checkInsToAdd, ...checkInCollection];
    }
    return checkInCollection;
  }

  protected convertValueFromClient<T extends ICheckIn | NewCheckIn | PartialUpdateCheckIn>(checkIn: T): RestOf<T> {
    return {
      ...checkIn,
      checkInTime: checkIn.checkInTime?.toJSON() ?? null,
    };
  }

  protected convertResponseFromServer(res: RestCheckIn): ICheckIn {
    return this.convertValueFromServer(res);
  }

  protected convertResponseArrayFromServer(res: RestCheckIn[]): ICheckIn[] {
    return res.map(item => this.convertValueFromServer(item));
  }
}
