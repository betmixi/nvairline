import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import OrganizerResolve from './route/organizer-routing-resolve.service';

const organizerRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/organizer').then(m => m.Organizer),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/organizer-detail').then(m => m.OrganizerDetail),
    resolve: {
      organizer: OrganizerResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/organizer-update').then(m => m.OrganizerUpdate),
    resolve: {
      organizer: OrganizerResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/organizer-update').then(m => m.OrganizerUpdate),
    resolve: {
      organizer: OrganizerResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default organizerRoute;
