import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { Authority } from 'app/shared/jhipster/constants';

import CouponResolve from './route/coupon-routing-resolve.service';

const couponRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/coupon').then(m => m.Coupon),
    data: {
      defaultSort: `id,${ASC}`,
      authorities: [Authority.ADMIN],
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/coupon-detail').then(m => m.CouponDetail),
    resolve: {
      coupon: CouponResolve,
    },
    data: { authorities: [Authority.ADMIN] },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/coupon-update').then(m => m.CouponUpdate),
    resolve: {
      coupon: CouponResolve,
    },
    data: { authorities: [Authority.ADMIN] },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/coupon-update').then(m => m.CouponUpdate),
    resolve: {
      coupon: CouponResolve,
    },
    data: { authorities: [Authority.ADMIN] },
    canActivate: [UserRouteAccessService],
  },
];

export default couponRoute;
