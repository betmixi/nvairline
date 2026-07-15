import { HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';

import { EMPTY, Observable, catchError, of } from 'rxjs';

import { IBookingDetail } from '../booking-detail.model';
import { BookingDetailService } from '../service/booking-detail.service';

const bookingDetailResolve = (route: ActivatedRouteSnapshot): Observable<null | IBookingDetail> => {
  const { id } = route.params;
  if (id) {
    const router = inject(Router);
    const service = inject(BookingDetailService);
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

export default bookingDetailResolve;
