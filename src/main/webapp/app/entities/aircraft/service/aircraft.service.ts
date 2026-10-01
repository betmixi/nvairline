import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import { Observable } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { IAircraft, NewAircraft } from '../aircraft.model';

export type PartialUpdateAircraft = Partial<IAircraft> & Pick<IAircraft, 'id'>;

@Injectable()
export class AircraftsService {
  readonly aircraftsParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly aircraftsResource = httpResource<IAircraft[]>(() => {
    const params = this.aircraftsParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of aircraft that have been fetched. It is updated when the aircraftsResource emits a new value.
   * In case of error while fetching the aircrafts, the signal is set to an empty array.
   */
  readonly aircrafts = computed(() => (this.aircraftsResource.hasValue() ? this.aircraftsResource.value() : []));
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/aircraft');
}

@Injectable({ providedIn: 'root' })
export class AircraftService extends AircraftsService {
  protected readonly http = inject(HttpClient);

  create(aircraft: NewAircraft): Observable<IAircraft> {
    return this.http.post<IAircraft>(this.resourceUrl, aircraft);
  }

  update(aircraft: IAircraft): Observable<IAircraft> {
    return this.http.put<IAircraft>(`${this.resourceUrl}/${encodeURIComponent(this.getAircraftIdentifier(aircraft))}`, aircraft);
  }

  partialUpdate(aircraft: PartialUpdateAircraft): Observable<IAircraft> {
    return this.http.patch<IAircraft>(`${this.resourceUrl}/${encodeURIComponent(this.getAircraftIdentifier(aircraft))}`, aircraft);
  }

  find(id: number): Observable<IAircraft> {
    return this.http.get<IAircraft>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  query(req?: any): Observable<HttpResponse<IAircraft[]>> {
    const options = createRequestOption(req);
    return this.http.get<IAircraft[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getAircraftIdentifier(aircraft: Pick<IAircraft, 'id'>): number {
    return aircraft.id;
  }

  compareAircraft(o1: Pick<IAircraft, 'id'> | null, o2: Pick<IAircraft, 'id'> | null): boolean {
    return o1 && o2 ? this.getAircraftIdentifier(o1) === this.getAircraftIdentifier(o2) : o1 === o2;
  }

  addAircraftToCollectionIfMissing<Type extends Pick<IAircraft, 'id'>>(
    aircraftCollection: Type[],
    ...aircraftsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const aircrafts: Type[] = aircraftsToCheck.filter(isPresent);
    if (aircrafts.length > 0) {
      const aircraftCollectionIdentifiers = aircraftCollection.map(aircraftItem => this.getAircraftIdentifier(aircraftItem));
      const aircraftsToAdd = aircrafts.filter(aircraftItem => {
        const aircraftIdentifier = this.getAircraftIdentifier(aircraftItem);
        if (aircraftCollectionIdentifiers.includes(aircraftIdentifier)) {
          return false;
        }
        aircraftCollectionIdentifiers.push(aircraftIdentifier);
        return true;
      });
      return [...aircraftsToAdd, ...aircraftCollection];
    }
    return aircraftCollection;
  }
}
