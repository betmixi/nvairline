import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import EventResolve from './route/event-routing-resolve.service';

const eventRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/event').then(m => m.Event),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/event-detail').then(m => m.EventDetail),
    resolve: {
      event: EventResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/event-update').then(m => m.EventUpdate),
    resolve: {
      event: EventResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/event-update').then(m => m.EventUpdate),
    resolve: {
      event: EventResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default eventRoute;
