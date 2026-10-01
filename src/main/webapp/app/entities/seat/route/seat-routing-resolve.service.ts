import { HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';

import { EMPTY, Observable, catchError, of } from 'rxjs';

import { ISeat } from '../seat.model';
import { SeatService } from '../service/seat.service';

const seatResolve = (route: ActivatedRouteSnapshot): Observable<null | ISeat> => {
  const { id } = route.params;
  if (id) {
    const router = inject(Router);
    const service = inject(SeatService);
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

export default seatResolve;
