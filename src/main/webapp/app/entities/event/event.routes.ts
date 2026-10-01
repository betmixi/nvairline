import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { Authority } from 'app/shared/jhipster/constants';

import EventResolve from './route/event-routing-resolve.service';

const eventRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/event').then(m => m.Event),
    data: {
      defaultSort: `id,${ASC}`,
      authorities: [Authority.ADMIN],
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/event-detail').then(m => m.EventDetail),
    resolve: {
      event: EventResolve,
    },
    data: { authorities: [Authority.ADMIN] },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/event-update').then(m => m.EventUpdate),
    resolve: {
      event: EventResolve,
    },
    data: { authorities: [Authority.ADMIN] },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/showtimes',
    loadComponent: () => import('app/user/showtimes/list/showtime-list').then(m => m.default),
  },
  {
    path: ':id/showtimes/:showtimeId/seats',
    loadComponent: () => import('app/user/showtimes/seat-picker/seat-picker').then(m => m.default),
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/event-update').then(m => m.EventUpdate),
    resolve: {
      event: EventResolve,
    },
    data: { authorities: [Authority.ADMIN] },
    canActivate: [UserRouteAccessService],
  },
];

export default eventRoute;
