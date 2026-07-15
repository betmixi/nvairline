import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import BookingDetailResolve from './route/booking-detail-routing-resolve.service';

const bookingDetailRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/booking-detail').then(m => m.BookingDetail),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/booking-detail-detail').then(m => m.BookingDetailDetail),
    resolve: {
      bookingDetail: BookingDetailResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/booking-detail-update').then(m => m.BookingDetailUpdate),
    resolve: {
      bookingDetail: BookingDetailResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/booking-detail-update').then(m => m.BookingDetailUpdate),
    resolve: {
      bookingDetail: BookingDetailResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default bookingDetailRoute;
