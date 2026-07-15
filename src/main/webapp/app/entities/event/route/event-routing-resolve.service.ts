import { HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';

import { EMPTY, Observable, catchError, of } from 'rxjs';

import { IEvent } from '../event.model';
import { EventService } from '../service/event.service';

const eventResolve = (route: ActivatedRouteSnapshot): Observable<null | IEvent> => {
  const { id } = route.params;
  if (id) {
    const router = inject(Router);
    const service = inject(EventService);
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

export default eventResolve;
