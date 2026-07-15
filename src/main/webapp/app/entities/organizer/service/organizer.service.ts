import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import { Observable } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { IOrganizer, NewOrganizer } from '../organizer.model';

export type PartialUpdateOrganizer = Partial<IOrganizer> & Pick<IOrganizer, 'id'>;

@Injectable()
export class OrganizersService {
  readonly organizersParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly organizersResource = httpResource<IOrganizer[]>(() => {
    const params = this.organizersParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of organizer that have been fetched. It is updated when the organizersResource emits a new value.
   * In case of error while fetching the organizers, the signal is set to an empty array.
   */
  readonly organizers = computed(() => (this.organizersResource.hasValue() ? this.organizersResource.value() : []));
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/organizers');
}

@Injectable({ providedIn: 'root' })
export class OrganizerService extends OrganizersService {
  protected readonly http = inject(HttpClient);

  create(organizer: NewOrganizer): Observable<IOrganizer> {
    return this.http.post<IOrganizer>(this.resourceUrl, organizer);
  }

  update(organizer: IOrganizer): Observable<IOrganizer> {
    return this.http.put<IOrganizer>(`${this.resourceUrl}/${encodeURIComponent(this.getOrganizerIdentifier(organizer))}`, organizer);
  }

  partialUpdate(organizer: PartialUpdateOrganizer): Observable<IOrganizer> {
    return this.http.patch<IOrganizer>(`${this.resourceUrl}/${encodeURIComponent(this.getOrganizerIdentifier(organizer))}`, organizer);
  }

  find(id: number): Observable<IOrganizer> {
    return this.http.get<IOrganizer>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  query(req?: any): Observable<HttpResponse<IOrganizer[]>> {
    const options = createRequestOption(req);
    return this.http.get<IOrganizer[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getOrganizerIdentifier(organizer: Pick<IOrganizer, 'id'>): number {
    return organizer.id;
  }

  compareOrganizer(o1: Pick<IOrganizer, 'id'> | null, o2: Pick<IOrganizer, 'id'> | null): boolean {
    return o1 && o2 ? this.getOrganizerIdentifier(o1) === this.getOrganizerIdentifier(o2) : o1 === o2;
  }

  addOrganizerToCollectionIfMissing<Type extends Pick<IOrganizer, 'id'>>(
    organizerCollection: Type[],
    ...organizersToCheck: (Type | null | undefined)[]
  ): Type[] {
    const organizers: Type[] = organizersToCheck.filter(isPresent);
    if (organizers.length > 0) {
      const organizerCollectionIdentifiers = organizerCollection.map(organizerItem => this.getOrganizerIdentifier(organizerItem));
      const organizersToAdd = organizers.filter(organizerItem => {
        const organizerIdentifier = this.getOrganizerIdentifier(organizerItem);
        if (organizerCollectionIdentifiers.includes(organizerIdentifier)) {
          return false;
        }
        organizerCollectionIdentifiers.push(organizerIdentifier);
        return true;
      });
      return [...organizersToAdd, ...organizerCollection];
    }
    return organizerCollection;
  }
}
