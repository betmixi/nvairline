import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import EventImageResolve from './route/event-image-routing-resolve.service';

const eventImageRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/event-image').then(m => m.EventImage),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/event-image-detail').then(m => m.EventImageDetail),
    resolve: {
      eventImage: EventImageResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/event-image-update').then(m => m.EventImageUpdate),
    resolve: {
      eventImage: EventImageResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/event-image-update').then(m => m.EventImageUpdate),
    resolve: {
      eventImage: EventImageResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default eventImageRoute;
