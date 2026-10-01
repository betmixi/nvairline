import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { Authority } from 'app/shared/jhipster/constants';

import CheckInResolve from './route/check-in-routing-resolve.service';

const STAFF_AUTHORITIES = { authorities: [Authority.ADMIN, Authority.STAFF] };

const checkInRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/check-in').then(m => m.CheckIn),
    data: {
      defaultSort: `id,${ASC}`,
      ...STAFF_AUTHORITIES,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/check-in-detail').then(m => m.CheckInDetail),
    resolve: {
      checkIn: CheckInResolve,
    },
    data: STAFF_AUTHORITIES,
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/check-in-update').then(m => m.CheckInUpdate),
    resolve: {
      checkIn: CheckInResolve,
    },
    data: STAFF_AUTHORITIES,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/check-in-update').then(m => m.CheckInUpdate),
    resolve: {
      checkIn: CheckInResolve,
    },
    data: STAFF_AUTHORITIES,
    canActivate: [UserRouteAccessService],
  },
];

export default checkInRoute;
