import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import { Observable } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { ISeat, NewSeat } from '../seat.model';

export type PartialUpdateSeat = Partial<ISeat> & Pick<ISeat, 'id'>;

@Injectable()
export class SeatsService {
  readonly seatsParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(undefined);
  readonly seatsResource = httpResource<ISeat[]>(() => {
    const params = this.seatsParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of seat that have been fetched. It is updated when the seatsResource emits a new value.
   * In case of error while fetching the seats, the signal is set to an empty array.
   */
  readonly seats = computed(() => (this.seatsResource.hasValue() ? this.seatsResource.value() : []));
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/seats');
}

@Injectable({ providedIn: 'root' })
export class SeatService extends SeatsService {
  protected readonly http = inject(HttpClient);

  create(seat: NewSeat): Observable<ISeat> {
    return this.http.post<ISeat>(this.resourceUrl, seat);
  }

  update(seat: ISeat): Observable<ISeat> {
    return this.http.put<ISeat>(`${this.resourceUrl}/${encodeURIComponent(this.getSeatIdentifier(seat))}`, seat);
  }

  partialUpdate(seat: PartialUpdateSeat): Observable<ISeat> {
    return this.http.patch<ISeat>(`${this.resourceUrl}/${encodeURIComponent(this.getSeatIdentifier(seat))}`, seat);
  }

  find(id: number): Observable<ISeat> {
    return this.http.get<ISeat>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  query(req?: any): Observable<HttpResponse<ISeat[]>> {
    const options = createRequestOption(req);
    return this.http.get<ISeat[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  findByAircraft(aircraftId: number): Observable<HttpResponse<ISeat[]>> {
    return this.http.get<ISeat[]>(`${this.resourceUrl}/aircraft/${aircraftId}`, { observe: 'response' });
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getSeatIdentifier(seat: Pick<ISeat, 'id'>): number {
    return seat.id;
  }

  compareSeat(o1: Pick<ISeat, 'id'> | null, o2: Pick<ISeat, 'id'> | null): boolean {
    return o1 && o2 ? this.getSeatIdentifier(o1) === this.getSeatIdentifier(o2) : o1 === o2;
  }

  addSeatToCollectionIfMissing<Type extends Pick<ISeat, 'id'>>(
    seatCollection: Type[],
    ...seatsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const seats: Type[] = seatsToCheck.filter(isPresent);
    if (seats.length > 0) {
      const seatCollectionIdentifiers = seatCollection.map(seatItem => this.getSeatIdentifier(seatItem));
      const seatsToAdd = seats.filter(seatItem => {
        const seatIdentifier = this.getSeatIdentifier(seatItem);
        if (seatCollectionIdentifiers.includes(seatIdentifier)) {
          return false;
        }
        seatCollectionIdentifiers.push(seatIdentifier);
        return true;
      });
      return [...seatsToAdd, ...seatCollection];
    }
    return seatCollection;
  }
}
