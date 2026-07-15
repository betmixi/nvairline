import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import dayjs from 'dayjs/esm';
import { Observable, map } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { IBooking, NewBooking } from '../booking.model';

export type PartialUpdateBooking = Partial<IBooking> & Pick<IBooking, 'id'>;

type RestOf<T extends IBooking | NewBooking> = Omit<T, 'bookingDate'> & {
  bookingDate?: string | null;
};

export type RestBooking = RestOf<IBooking>;

export type NewRestBooking = RestOf<NewBooking>;

export type PartialUpdateRestBooking = RestOf<PartialUpdateBooking>;

@Injectable()
export class BookingsService {
  readonly bookingsParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly bookingsResource = httpResource<RestBooking[]>(() => {
    const params = this.bookingsParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of booking that have been fetched. It is updated when the bookingsResource emits a new value.
   * In case of error while fetching the bookings, the signal is set to an empty array.
   */
  readonly bookings = computed(() =>
    (this.bookingsResource.hasValue() ? this.bookingsResource.value() : []).map(item => this.convertValueFromServer(item)),
  );
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/bookings');

  protected convertValueFromServer(restBooking: RestBooking): IBooking {
    return {
      ...restBooking,
      bookingDate: restBooking.bookingDate ? dayjs(restBooking.bookingDate) : undefined,
    };
  }
}

@Injectable({ providedIn: 'root' })
export class BookingService extends BookingsService {
  protected readonly http = inject(HttpClient);

  create(booking: NewBooking): Observable<IBooking> {
    const copy = this.convertValueFromClient(booking);
    return this.http.post<RestBooking>(this.resourceUrl, copy).pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(booking: IBooking): Observable<IBooking> {
    const copy = this.convertValueFromClient(booking);
    return this.http
      .put<RestBooking>(`${this.resourceUrl}/${encodeURIComponent(this.getBookingIdentifier(booking))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(booking: PartialUpdateBooking): Observable<IBooking> {
    const copy = this.convertValueFromClient(booking);
    return this.http
      .patch<RestBooking>(`${this.resourceUrl}/${encodeURIComponent(this.getBookingIdentifier(booking))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<IBooking> {
    return this.http
      .get<RestBooking>(`${this.resourceUrl}/${encodeURIComponent(id)}`)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<HttpResponse<IBooking[]>> {
    const options = createRequestOption(req);
    return this.http
      .get<RestBooking[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => res.clone({ body: this.convertResponseArrayFromServer(res.body!) })));
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getBookingIdentifier(booking: Pick<IBooking, 'id'>): number {
    return booking.id;
  }

  compareBooking(o1: Pick<IBooking, 'id'> | null, o2: Pick<IBooking, 'id'> | null): boolean {
    return o1 && o2 ? this.getBookingIdentifier(o1) === this.getBookingIdentifier(o2) : o1 === o2;
  }

  addBookingToCollectionIfMissing<Type extends Pick<IBooking, 'id'>>(
    bookingCollection: Type[],
    ...bookingsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const bookings: Type[] = bookingsToCheck.filter(isPresent);
    if (bookings.length > 0) {
      const bookingCollectionIdentifiers = bookingCollection.map(bookingItem => this.getBookingIdentifier(bookingItem));
      const bookingsToAdd = bookings.filter(bookingItem => {
        const bookingIdentifier = this.getBookingIdentifier(bookingItem);
        if (bookingCollectionIdentifiers.includes(bookingIdentifier)) {
          return false;
        }
        bookingCollectionIdentifiers.push(bookingIdentifier);
        return true;
      });
      return [...bookingsToAdd, ...bookingCollection];
    }
    return bookingCollection;
  }

  protected convertValueFromClient<T extends IBooking | NewBooking | PartialUpdateBooking>(booking: T): RestOf<T> {
    return {
      ...booking,
      bookingDate: booking.bookingDate?.toJSON() ?? null,
    };
  }

  protected convertResponseFromServer(res: RestBooking): IBooking {
    return this.convertValueFromServer(res);
  }

  protected convertResponseArrayFromServer(res: RestBooking[]): IBooking[] {
    return res.map(item => this.convertValueFromServer(item));
  }
}
