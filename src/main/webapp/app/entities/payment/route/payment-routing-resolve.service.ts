import { HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';

import { EMPTY, Observable, catchError, of } from 'rxjs';

import { IPayment } from '../payment.model';
import { PaymentService } from '../service/payment.service';

const paymentResolve = (route: ActivatedRouteSnapshot): Observable<null | IPayment> => {
  const { id } = route.params;
  if (id) {
    const router = inject(Router);
    const service = inject(PaymentService);
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

export default paymentResolve;
