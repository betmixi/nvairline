import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import { Observable } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { ITicket, NewTicket } from '../ticket.model';

export type PartialUpdateTicket = Partial<ITicket> & Pick<ITicket, 'id'>;

@Injectable()
export class TicketsService {
  readonly ticketsParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly ticketsResource = httpResource<ITicket[]>(() => {
    const params = this.ticketsParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of ticket that have been fetched. It is updated when the ticketsResource emits a new value.
   * In case of error while fetching the tickets, the signal is set to an empty array.
   */
  readonly tickets = computed(() => (this.ticketsResource.hasValue() ? this.ticketsResource.value() : []));
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/tickets');
}

@Injectable({ providedIn: 'root' })
export class TicketService extends TicketsService {
  protected readonly http = inject(HttpClient);

  create(ticket: NewTicket): Observable<ITicket> {
    return this.http.post<ITicket>(this.resourceUrl, ticket);
  }

  update(ticket: ITicket): Observable<ITicket> {
    return this.http.put<ITicket>(`${this.resourceUrl}/${encodeURIComponent(this.getTicketIdentifier(ticket))}`, ticket);
  }

  partialUpdate(ticket: PartialUpdateTicket): Observable<ITicket> {
    return this.http.patch<ITicket>(`${this.resourceUrl}/${encodeURIComponent(this.getTicketIdentifier(ticket))}`, ticket);
  }

  find(id: number): Observable<ITicket> {
    return this.http.get<ITicket>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  query(req?: any): Observable<HttpResponse<ITicket[]>> {
    const options = createRequestOption(req);
    return this.http.get<ITicket[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getTicketIdentifier(ticket: Pick<ITicket, 'id'>): number {
    return ticket.id;
  }

  compareTicket(o1: Pick<ITicket, 'id'> | null, o2: Pick<ITicket, 'id'> | null): boolean {
    return o1 && o2 ? this.getTicketIdentifier(o1) === this.getTicketIdentifier(o2) : o1 === o2;
  }

  addTicketToCollectionIfMissing<Type extends Pick<ITicket, 'id'>>(
    ticketCollection: Type[],
    ...ticketsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const tickets: Type[] = ticketsToCheck.filter(isPresent);
    if (tickets.length > 0) {
      const ticketCollectionIdentifiers = ticketCollection.map(ticketItem => this.getTicketIdentifier(ticketItem));
      const ticketsToAdd = tickets.filter(ticketItem => {
        const ticketIdentifier = this.getTicketIdentifier(ticketItem);
        if (ticketCollectionIdentifiers.includes(ticketIdentifier)) {
          return false;
        }
        ticketCollectionIdentifiers.push(ticketIdentifier);
        return true;
      });
      return [...ticketsToAdd, ...ticketCollection];
    }
    return ticketCollection;
  }
}
