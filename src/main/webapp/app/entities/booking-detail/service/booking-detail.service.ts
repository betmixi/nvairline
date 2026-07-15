import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import { Observable } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { IBookingDetail, NewBookingDetail } from '../booking-detail.model';

export type PartialUpdateBookingDetail = Partial<IBookingDetail> & Pick<IBookingDetail, 'id'>;

@Injectable()
export class BookingDetailsService {
  readonly bookingDetailsParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly bookingDetailsResource = httpResource<IBookingDetail[]>(() => {
    const params = this.bookingDetailsParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of bookingDetail that have been fetched. It is updated when the bookingDetailsResource emits a new value.
   * In case of error while fetching the bookingDetails, the signal is set to an empty array.
   */
  readonly bookingDetails = computed(() => (this.bookingDetailsResource.hasValue() ? this.bookingDetailsResource.value() : []));
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/booking-details');
}

@Injectable({ providedIn: 'root' })
export class BookingDetailService extends BookingDetailsService {
  protected readonly http = inject(HttpClient);

  create(bookingDetail: NewBookingDetail): Observable<IBookingDetail> {
    return this.http.post<IBookingDetail>(this.resourceUrl, bookingDetail);
  }

  update(bookingDetail: IBookingDetail): Observable<IBookingDetail> {
    return this.http.put<IBookingDetail>(
      `${this.resourceUrl}/${encodeURIComponent(this.getBookingDetailIdentifier(bookingDetail))}`,
      bookingDetail,
    );
  }

  partialUpdate(bookingDetail: PartialUpdateBookingDetail): Observable<IBookingDetail> {
    return this.http.patch<IBookingDetail>(
      `${this.resourceUrl}/${encodeURIComponent(this.getBookingDetailIdentifier(bookingDetail))}`,
      bookingDetail,
    );
  }

  find(id: number): Observable<IBookingDetail> {
    return this.http.get<IBookingDetail>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  query(req?: any): Observable<HttpResponse<IBookingDetail[]>> {
    const options = createRequestOption(req);
    return this.http.get<IBookingDetail[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getBookingDetailIdentifier(bookingDetail: Pick<IBookingDetail, 'id'>): number {
    return bookingDetail.id;
  }

  compareBookingDetail(o1: Pick<IBookingDetail, 'id'> | null, o2: Pick<IBookingDetail, 'id'> | null): boolean {
    return o1 && o2 ? this.getBookingDetailIdentifier(o1) === this.getBookingDetailIdentifier(o2) : o1 === o2;
  }

  addBookingDetailToCollectionIfMissing<Type extends Pick<IBookingDetail, 'id'>>(
    bookingDetailCollection: Type[],
    ...bookingDetailsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const bookingDetails: Type[] = bookingDetailsToCheck.filter(isPresent);
    if (bookingDetails.length > 0) {
      const bookingDetailCollectionIdentifiers = bookingDetailCollection.map(bookingDetailItem =>
        this.getBookingDetailIdentifier(bookingDetailItem),
      );
      const bookingDetailsToAdd = bookingDetails.filter(bookingDetailItem => {
        const bookingDetailIdentifier = this.getBookingDetailIdentifier(bookingDetailItem);
        if (bookingDetailCollectionIdentifiers.includes(bookingDetailIdentifier)) {
          return false;
        }
        bookingDetailCollectionIdentifiers.push(bookingDetailIdentifier);
        return true;
      });
      return [...bookingDetailsToAdd, ...bookingDetailCollection];
    }
    return bookingDetailCollection;
  }
}
