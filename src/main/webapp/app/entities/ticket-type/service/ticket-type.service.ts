import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import dayjs from 'dayjs/esm';
import { Observable, map } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { ITicketType, NewTicketType } from '../ticket-type.model';

export type PartialUpdateTicketType = Partial<ITicketType> & Pick<ITicketType, 'id'>;

type RestOf<T extends ITicketType | NewTicketType> = Omit<T, 'saleStart' | 'saleEnd'> & {
  saleStart?: string | null;
  saleEnd?: string | null;
};

export type RestTicketType = RestOf<ITicketType>;

export type NewRestTicketType = RestOf<NewTicketType>;

export type PartialUpdateRestTicketType = RestOf<PartialUpdateTicketType>;

@Injectable()
export class TicketTypesService {
  readonly ticketTypesParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly ticketTypesResource = httpResource<RestTicketType[]>(() => {
    const params = this.ticketTypesParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of ticketType that have been fetched. It is updated when the ticketTypesResource emits a new value.
   * In case of error while fetching the ticketTypes, the signal is set to an empty array.
   */
  readonly ticketTypes = computed(() =>
    (this.ticketTypesResource.hasValue() ? this.ticketTypesResource.value() : []).map(item => this.convertValueFromServer(item)),
  );
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/ticket-types');

  protected convertValueFromServer(restTicketType: RestTicketType): ITicketType {
    return {
      ...restTicketType,
      saleStart: restTicketType.saleStart ? dayjs(restTicketType.saleStart) : undefined,
      saleEnd: restTicketType.saleEnd ? dayjs(restTicketType.saleEnd) : undefined,
    };
  }
}

@Injectable({ providedIn: 'root' })
export class TicketTypeService extends TicketTypesService {
  protected readonly http = inject(HttpClient);

  create(ticketType: NewTicketType): Observable<ITicketType> {
    const copy = this.convertValueFromClient(ticketType);
    return this.http.post<RestTicketType>(this.resourceUrl, copy).pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(ticketType: ITicketType): Observable<ITicketType> {
    const copy = this.convertValueFromClient(ticketType);
    return this.http
      .put<RestTicketType>(`${this.resourceUrl}/${encodeURIComponent(this.getTicketTypeIdentifier(ticketType))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(ticketType: PartialUpdateTicketType): Observable<ITicketType> {
    const copy = this.convertValueFromClient(ticketType);
    return this.http
      .patch<RestTicketType>(`${this.resourceUrl}/${encodeURIComponent(this.getTicketTypeIdentifier(ticketType))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<ITicketType> {
    return this.http
      .get<RestTicketType>(`${this.resourceUrl}/${encodeURIComponent(id)}`)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }
  findByEvent(eventId: number): Observable<HttpResponse<ITicketType[]>> {
    return this.http.get<ITicketType[]>(`${this.resourceUrl}/event/${eventId}`, { observe: 'response' });
  }
  query(req?: any): Observable<HttpResponse<ITicketType[]>> {
    const options = createRequestOption(req);
    return this.http
      .get<RestTicketType[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => res.clone({ body: this.convertResponseArrayFromServer(res.body!) })));
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getTicketTypeIdentifier(ticketType: Pick<ITicketType, 'id'>): number {
    return ticketType.id;
  }

  compareTicketType(o1: Pick<ITicketType, 'id'> | null, o2: Pick<ITicketType, 'id'> | null): boolean {
    return o1 && o2 ? this.getTicketTypeIdentifier(o1) === this.getTicketTypeIdentifier(o2) : o1 === o2;
  }

  addTicketTypeToCollectionIfMissing<Type extends Pick<ITicketType, 'id'>>(
    ticketTypeCollection: Type[],
    ...ticketTypesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const ticketTypes: Type[] = ticketTypesToCheck.filter(isPresent);
    if (ticketTypes.length > 0) {
      const ticketTypeCollectionIdentifiers = ticketTypeCollection.map(ticketTypeItem => this.getTicketTypeIdentifier(ticketTypeItem));
      const ticketTypesToAdd = ticketTypes.filter(ticketTypeItem => {
        const ticketTypeIdentifier = this.getTicketTypeIdentifier(ticketTypeItem);
        if (ticketTypeCollectionIdentifiers.includes(ticketTypeIdentifier)) {
          return false;
        }
        ticketTypeCollectionIdentifiers.push(ticketTypeIdentifier);
        return true;
      });
      return [...ticketTypesToAdd, ...ticketTypeCollection];
    }
    return ticketTypeCollection;
  }

  protected convertValueFromClient<T extends ITicketType | NewTicketType | PartialUpdateTicketType>(ticketType: T): RestOf<T> {
    return {
      ...ticketType,
      saleStart: ticketType.saleStart?.toJSON() ?? null,
      saleEnd: ticketType.saleEnd?.toJSON() ?? null,
    };
  }

  protected convertResponseFromServer(res: RestTicketType): ITicketType {
    return this.convertValueFromServer(res);
  }

  protected convertResponseArrayFromServer(res: RestTicketType[]): ITicketType[] {
    return res.map(item => this.convertValueFromServer(item));
  }
}
