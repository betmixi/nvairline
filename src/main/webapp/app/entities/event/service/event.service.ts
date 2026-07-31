import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import dayjs from 'dayjs/esm';
import { Observable, map } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { IEvent, NewEvent } from '../event.model';

export type PartialUpdateEvent = Partial<IEvent> & Pick<IEvent, 'id'>;

type RestOf<T extends IEvent | NewEvent> = Omit<T, 'startTime' | 'endTime' | 'createdDate'> & {
  startTime?: string | null;
  endTime?: string | null;
  createdDate?: string | null;
};

export type RestEvent = RestOf<IEvent>;

export type NewRestEvent = RestOf<NewEvent>;

export type PartialUpdateRestEvent = RestOf<PartialUpdateEvent>;

@Injectable()
export class EventsService {
  readonly eventsParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(undefined);
  readonly eventsResource = httpResource<RestEvent[]>(() => {
    const params = this.eventsParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of event that have been fetched. It is updated when the eventsResource emits a new value.
   * In case of error while fetching the events, the signal is set to an empty array.
   */
  readonly events = computed(() =>
    (this.eventsResource.hasValue() ? this.eventsResource.value() : []).map(item => this.convertValueFromServer(item)),
  );
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/events');

  protected convertValueFromServer(restEvent: RestEvent): IEvent {
    return {
      ...restEvent,
      startTime: restEvent.startTime ? dayjs(restEvent.startTime) : undefined,
      endTime: restEvent.endTime ? dayjs(restEvent.endTime) : undefined,
      createdDate: restEvent.createdDate ? dayjs(restEvent.createdDate) : undefined,
    };
  }
}

@Injectable({ providedIn: 'root' })
export class EventService extends EventsService {
  protected readonly http = inject(HttpClient);

  create(event: NewEvent): Observable<IEvent> {
    const copy = this.convertValueFromClient(event);
    return this.http.post<RestEvent>(this.resourceUrl, copy).pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(event: IEvent): Observable<IEvent> {
    const copy = this.convertValueFromClient(event);
    return this.http
      .put<RestEvent>(`${this.resourceUrl}/${encodeURIComponent(this.getEventIdentifier(event))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(event: PartialUpdateEvent): Observable<IEvent> {
    const copy = this.convertValueFromClient(event);
    return this.http
      .patch<RestEvent>(`${this.resourceUrl}/${encodeURIComponent(this.getEventIdentifier(event))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<IEvent> {
    return this.http.get<RestEvent>(`${this.resourceUrl}/${encodeURIComponent(id)}`).pipe(map(res => this.convertResponseFromServer(res)));
  }

  /** Xem chi tiet su kien khong can dang nhap (dung cho trang xem truoc khi mua). */
  findPublic(id: number): Observable<IEvent> {
    return this.http
      .get<RestEvent>(`${this.resourceUrl}/public/${encodeURIComponent(id)}`)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<HttpResponse<IEvent[]>> {
    const options = createRequestOption(req);
    return this.http
      .get<RestEvent[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => res.clone({ body: this.convertResponseArrayFromServer(res.body!) })));
  }

  /** Danh sach su kien khong can dang nhap (dung cho trang chu / danh sach su kien). */
  queryPublic(req?: any): Observable<HttpResponse<IEvent[]>> {
    const options = createRequestOption(req);
    return this.http
      .get<RestEvent[]>(`${this.resourceUrl}/public`, { params: options, observe: 'response' })
      .pipe(map(res => res.clone({ body: this.convertResponseArrayFromServer(res.body!) })));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    alert('Đang dùng EventService này');

    console.log(this.resourceUrl);
    console.log(`${this.resourceUrl}/${id}`);

    return this.http.delete(`${this.resourceUrl}/${id}`, {
      observe: 'response',
    });
  }

  getEventIdentifier(event: Pick<IEvent, 'id'>): number {
    return event.id;
  }

  compareEvent(o1: Pick<IEvent, 'id'> | null, o2: Pick<IEvent, 'id'> | null): boolean {
    return o1 && o2 ? this.getEventIdentifier(o1) === this.getEventIdentifier(o2) : o1 === o2;
  }

  addEventToCollectionIfMissing<Type extends Pick<IEvent, 'id'>>(
    eventCollection: Type[],
    ...eventsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const events: Type[] = eventsToCheck.filter(isPresent);
    if (events.length > 0) {
      const eventCollectionIdentifiers = eventCollection.map(eventItem => this.getEventIdentifier(eventItem));
      const eventsToAdd = events.filter(eventItem => {
        const eventIdentifier = this.getEventIdentifier(eventItem);
        if (eventCollectionIdentifiers.includes(eventIdentifier)) {
          return false;
        }
        eventCollectionIdentifiers.push(eventIdentifier);
        return true;
      });
      return [...eventsToAdd, ...eventCollection];
    }
    return eventCollection;
  }

  protected convertValueFromClient<T extends IEvent | NewEvent | PartialUpdateEvent>(event: T): RestOf<T> {
    return {
      ...event,
      startTime: event.startTime?.toJSON() ?? null,
      endTime: event.endTime?.toJSON() ?? null,
      createdDate: event.createdDate?.toJSON() ?? null,
    };
  }

  protected convertResponseFromServer(res: RestEvent): IEvent {
    return this.convertValueFromServer(res);
  }

  protected convertResponseArrayFromServer(res: RestEvent[]): IEvent[] {
    return res.map(item => this.convertValueFromServer(item));
  }
}
