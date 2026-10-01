import { HttpClient, HttpResponse } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import dayjs from 'dayjs/esm';
import { Observable, map } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IMyTicket, RestMyTicket } from './my-ticket.model';

@Injectable({ providedIn: 'root' })
export class MyTicketService {
  private readonly http = inject(HttpClient);
  private readonly applicationConfigService = inject(ApplicationConfigService);

  private readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/tickets');

  /** Lấy ví vé của người dùng đang đăng nhập. */
  query(req?: Record<string, unknown>): Observable<HttpResponse<IMyTicket[]>> {
    const options = createRequestOption(req);

    return this.http
      .get<RestMyTicket[]>(`${this.resourceUrl}/my-wallet`, { params: options, observe: 'response' })
      .pipe(map(response => response.clone({ body: (response.body ?? []).map(ticket => this.convertFromServer(ticket)) })));
  }

  private convertFromServer(ticket: RestMyTicket): IMyTicket {
    return {
      ...ticket,
      bookingDate: ticket.bookingDate ? dayjs(ticket.bookingDate) : null,
      eventStartTime: ticket.eventStartTime ? dayjs(ticket.eventStartTime) : null,
      eventEndTime: ticket.eventEndTime ? dayjs(ticket.eventEndTime) : null,
      showtimeStartTime: ticket.showtimeStartTime ? dayjs(ticket.showtimeStartTime) : null,
    };
  }
}
