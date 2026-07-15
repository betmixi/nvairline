import { HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';

import { EMPTY, Observable, catchError, of } from 'rxjs';

import { ICoupon } from '../coupon.model';
import { CouponService } from '../service/coupon.service';

const couponResolve = (route: ActivatedRouteSnapshot): Observable<null | ICoupon> => {
  const { id } = route.params;
  if (id) {
    const router = inject(Router);
    const service = inject(CouponService);
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

export default couponResolve;
