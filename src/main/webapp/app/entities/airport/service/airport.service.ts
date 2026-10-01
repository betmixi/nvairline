import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import { Observable } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { IAirport, NewAirport } from '../airport.model';

export type PartialUpdateAirport = Partial<IAirport> & Pick<IAirport, 'id'>;

@Injectable()
export class AirportsService {
  readonly airportsParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly airportsResource = httpResource<IAirport[]>(() => {
    const params = this.airportsParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of airport that have been fetched. It is updated when the airportsResource emits a new value.
   * In case of error while fetching the airports, the signal is set to an empty array.
   */
  readonly airports = computed(() => (this.airportsResource.hasValue() ? this.airportsResource.value() : []));
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/airports');
}

@Injectable({ providedIn: 'root' })
export class AirportService extends AirportsService {
  protected readonly http = inject(HttpClient);

  create(airport: NewAirport): Observable<IAirport> {
    return this.http.post<IAirport>(this.resourceUrl, airport);
  }

  update(airport: IAirport): Observable<IAirport> {
    return this.http.put<IAirport>(`${this.resourceUrl}/${encodeURIComponent(this.getAirportIdentifier(airport))}`, airport);
  }

  partialUpdate(airport: PartialUpdateAirport): Observable<IAirport> {
    return this.http.patch<IAirport>(`${this.resourceUrl}/${encodeURIComponent(this.getAirportIdentifier(airport))}`, airport);
  }

  find(id: number): Observable<IAirport> {
    return this.http.get<IAirport>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  query(req?: any): Observable<HttpResponse<IAirport[]>> {
    const options = createRequestOption(req);
    return this.http.get<IAirport[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getAirportIdentifier(airport: Pick<IAirport, 'id'>): number {
    return airport.id;
  }

  compareAirport(o1: Pick<IAirport, 'id'> | null, o2: Pick<IAirport, 'id'> | null): boolean {
    return o1 && o2 ? this.getAirportIdentifier(o1) === this.getAirportIdentifier(o2) : o1 === o2;
  }

  addAirportToCollectionIfMissing<Type extends Pick<IAirport, 'id'>>(
    airportCollection: Type[],
    ...airportsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const airports: Type[] = airportsToCheck.filter(isPresent);
    if (airports.length > 0) {
      const airportCollectionIdentifiers = airportCollection.map(airportItem => this.getAirportIdentifier(airportItem));
      const airportsToAdd = airports.filter(airportItem => {
        const airportIdentifier = this.getAirportIdentifier(airportItem);
        if (airportCollectionIdentifiers.includes(airportIdentifier)) {
          return false;
        }
        airportCollectionIdentifiers.push(airportIdentifier);
        return true;
      });
      return [...airportsToAdd, ...airportCollection];
    }
    return airportCollection;
  }
}
