import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { Authority } from 'app/shared/jhipster/constants';

import BookingResolve from './route/booking-routing-resolve.service';

const STAFF_AUTHORITIES = { authorities: [Authority.ADMIN, Authority.STAFF] };

const bookingRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/booking').then(m => m.Booking),
    data: {
      defaultSort: `id,${ASC}`,
      ...STAFF_AUTHORITIES,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/booking-detail').then(m => m.BookingDetail),
    resolve: {
      booking: BookingResolve,
    },
    data: STAFF_AUTHORITIES,
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/booking-update').then(m => m.BookingUpdate),
    resolve: {
      booking: BookingResolve,
    },
    data: STAFF_AUTHORITIES,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/booking-update').then(m => m.BookingUpdate),
    resolve: {
      booking: BookingResolve,
    },
    data: STAFF_AUTHORITIES,
    canActivate: [UserRouteAccessService],
  },
];

export default bookingRoute;
