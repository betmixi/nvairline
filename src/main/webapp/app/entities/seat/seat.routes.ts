import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { Authority } from 'app/shared/jhipster/constants';

import SeatResolve from './route/seat-routing-resolve.service';

const seatRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/seat').then(m => m.Seat),
    data: {
      defaultSort: `id,${ASC}`,
      authorities: [Authority.ADMIN],
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/seat-detail').then(m => m.SeatDetail),
    resolve: {
      seat: SeatResolve,
    },
    data: { authorities: [Authority.ADMIN] },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/seat-update').then(m => m.SeatUpdate),
    resolve: {
      seat: SeatResolve,
    },
    data: { authorities: [Authority.ADMIN] },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/seat-update').then(m => m.SeatUpdate),
    resolve: {
      seat: SeatResolve,
    },
    data: { authorities: [Authority.ADMIN] },
    canActivate: [UserRouteAccessService],
  },
];

export default seatRoute;
