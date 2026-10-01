import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import dayjs from 'dayjs/esm';
import { Observable, map } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { IShowtime, IShowtimeSeat, NewShowtime } from '../showtime.model';

export type PartialUpdateShowtime = Partial<IShowtime> & Pick<IShowtime, 'id'>;

type RestOf<T extends IShowtime | NewShowtime> = Omit<T, 'startTime' | 'endTime'> & {
  startTime?: string | null;
  endTime?: string | null;
};

export type RestShowtime = RestOf<IShowtime>;

export type NewRestShowtime = RestOf<NewShowtime>;

export type PartialUpdateRestShowtime = RestOf<PartialUpdateShowtime>;

@Injectable()
export class ShowtimesService {
  readonly showtimesParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly showtimesResource = httpResource<RestShowtime[]>(() => {
    const params = this.showtimesParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of showtime that have been fetched. It is updated when the showtimesResource emits a new value.
   * In case of error while fetching the showtimes, the signal is set to an empty array.
   */
  readonly showtimes = computed(() =>
    (this.showtimesResource.hasValue() ? this.showtimesResource.value() : []).map(item => this.convertValueFromServer(item)),
  );
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/showtimes');

  protected convertValueFromServer(restShowtime: RestShowtime): IShowtime {
    return {
      ...restShowtime,
      startTime: restShowtime.startTime ? dayjs(restShowtime.startTime) : undefined,
      endTime: restShowtime.endTime ? dayjs(restShowtime.endTime) : undefined,
    };
  }
}

@Injectable({ providedIn: 'root' })
export class ShowtimeService extends ShowtimesService {
  protected readonly http = inject(HttpClient);

  create(showtime: NewShowtime): Observable<IShowtime> {
    const copy = this.convertValueFromClient(showtime);
    return this.http.post<RestShowtime>(this.resourceUrl, copy).pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(showtime: IShowtime): Observable<IShowtime> {
    const copy = this.convertValueFromClient(showtime);
    return this.http
      .put<RestShowtime>(`${this.resourceUrl}/${encodeURIComponent(this.getShowtimeIdentifier(showtime))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<IShowtime> {
    return this.http
      .get<RestShowtime>(`${this.resourceUrl}/${encodeURIComponent(id)}`)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  /** Danh sach suat chieu cong khai cua mot phim. */
  findByEvent(eventId: number): Observable<HttpResponse<IShowtime[]>> {
    return this.http
      .get<RestShowtime[]>(`${this.resourceUrl}/event/${eventId}`, { observe: 'response' })
      .pipe(map(res => res.clone({ body: this.convertResponseArrayFromServer(res.body ?? []) })));
  }

  /** So do ghe cong khai cua mot suat chieu. */
  getSeats(showtimeId: number): Observable<IShowtimeSeat[]> {
    return this.http.get<IShowtimeSeat[]>(`${this.resourceUrl}/${showtimeId}/seats`);
  }

  query(req?: any): Observable<HttpResponse<IShowtime[]>> {
    const options = createRequestOption(req);
    return this.http
      .get<RestShowtime[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => res.clone({ body: this.convertResponseArrayFromServer(res.body!) })));
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getShowtimeIdentifier(showtime: Pick<IShowtime, 'id'>): number {
    return showtime.id;
  }

  compareShowtime(o1: Pick<IShowtime, 'id'> | null, o2: Pick<IShowtime, 'id'> | null): boolean {
    return o1 && o2 ? this.getShowtimeIdentifier(o1) === this.getShowtimeIdentifier(o2) : o1 === o2;
  }

  addShowtimeToCollectionIfMissing<Type extends Pick<IShowtime, 'id'>>(
    showtimeCollection: Type[],
    ...showtimesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const showtimes: Type[] = showtimesToCheck.filter(isPresent);
    if (showtimes.length > 0) {
      const showtimeCollectionIdentifiers = showtimeCollection.map(showtimeItem => this.getShowtimeIdentifier(showtimeItem));
      const showtimesToAdd = showtimes.filter(showtimeItem => {
        const showtimeIdentifier = this.getShowtimeIdentifier(showtimeItem);
        if (showtimeCollectionIdentifiers.includes(showtimeIdentifier)) {
          return false;
        }
        showtimeCollectionIdentifiers.push(showtimeIdentifier);
        return true;
      });
      return [...showtimesToAdd, ...showtimeCollection];
    }
    return showtimeCollection;
  }

  protected convertValueFromClient<T extends IShowtime | NewShowtime | PartialUpdateShowtime>(showtime: T): RestOf<T> {
    return {
      ...showtime,
      startTime: showtime.startTime?.toJSON() ?? null,
      endTime: showtime.endTime?.toJSON() ?? null,
    };
  }

  protected convertResponseFromServer(res: RestShowtime): IShowtime {
    return this.convertValueFromServer(res);
  }

  protected convertResponseArrayFromServer(res: RestShowtime[]): IShowtime[] {
    return res.map(item => this.convertValueFromServer(item));
  }
}
