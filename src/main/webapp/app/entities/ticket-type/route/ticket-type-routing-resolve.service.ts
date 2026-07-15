import { HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';

import { EMPTY, Observable, catchError, of } from 'rxjs';

import { TicketTypeService } from '../service/ticket-type.service';
import { ITicketType } from '../ticket-type.model';

const ticketTypeResolve = (route: ActivatedRouteSnapshot): Observable<null | ITicketType> => {
  const { id } = route.params;
  if (id) {
    const router = inject(Router);
    const service = inject(TicketTypeService);
    return service.find(id).pipe(
      catchError((error: HttpErrorResponse) => {
        if (error.status === 404) {
          router.navigate(['404']);
        } else {
          router.navigate(['error']);
        }
        return EMPTY;
      }),
    );
  }

  return of(null);
};

export default ticketTypeResolve;
