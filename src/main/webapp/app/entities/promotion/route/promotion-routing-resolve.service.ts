import { HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';

import { EMPTY, Observable, catchError, of } from 'rxjs';

import { IPromotion } from '../promotion.model';
import { PromotionService } from '../service/promotion.service';

const promotionResolve = (route: ActivatedRouteSnapshot): Observable<null | IPromotion> => {
  const { id } = route.params;
  if (id) {
    const router = inject(Router);
    const service = inject(PromotionService);
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

export default promotionResolve;
