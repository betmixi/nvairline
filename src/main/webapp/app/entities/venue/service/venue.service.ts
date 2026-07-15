import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import { Observable } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { IVenue, NewVenue } from '../venue.model';

export type PartialUpdateVenue = Partial<IVenue> & Pick<IVenue, 'id'>;

@Injectable()
export class VenuesService {
  readonly venuesParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(undefined);
  readonly venuesResource = httpResource<IVenue[]>(() => {
    const params = this.venuesParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of venue that have been fetched. It is updated when the venuesResource emits a new value.
   * In case of error while fetching the venues, the signal is set to an empty array.
   */
  readonly venues = computed(() => (this.venuesResource.hasValue() ? this.venuesResource.value() : []));
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/venues');
}

@Injectable({ providedIn: 'root' })
export class VenueService extends VenuesService {
  protected readonly http = inject(HttpClient);

  create(venue: NewVenue): Observable<IVenue> {
    return this.http.post<IVenue>(this.resourceUrl, venue);
  }

  update(venue: IVenue): Observable<IVenue> {
    return this.http.put<IVenue>(`${this.resourceUrl}/${encodeURIComponent(this.getVenueIdentifier(venue))}`, venue);
  }

  partialUpdate(venue: PartialUpdateVenue): Observable<IVenue> {
    return this.http.patch<IVenue>(`${this.resourceUrl}/${encodeURIComponent(this.getVenueIdentifier(venue))}`, venue);
  }

  find(id: number): Observable<IVenue> {
    return this.http.get<IVenue>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  query(req?: any): Observable<HttpResponse<IVenue[]>> {
    const options = createRequestOption(req);
    return this.http.get<IVenue[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getVenueIdentifier(venue: Pick<IVenue, 'id'>): number {
    return venue.id;
  }

  compareVenue(o1: Pick<IVenue, 'id'> | null, o2: Pick<IVenue, 'id'> | null): boolean {
    return o1 && o2 ? this.getVenueIdentifier(o1) === this.getVenueIdentifier(o2) : o1 === o2;
  }

  addVenueToCollectionIfMissing<Type extends Pick<IVenue, 'id'>>(
    venueCollection: Type[],
    ...venuesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const venues: Type[] = venuesToCheck.filter(isPresent);
    if (venues.length > 0) {
      const venueCollectionIdentifiers = venueCollection.map(venueItem => this.getVenueIdentifier(venueItem));
      const venuesToAdd = venues.filter(venueItem => {
        const venueIdentifier = this.getVenueIdentifier(venueItem);
        if (venueCollectionIdentifiers.includes(venueIdentifier)) {
          return false;
        }
        venueCollectionIdentifiers.push(venueIdentifier);
        return true;
      });
      return [...venuesToAdd, ...venueCollection];
    }
    return venueCollection;
  }
}
