import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { Authority } from 'app/shared/jhipster/constants';

import VenueResolve from './route/venue-routing-resolve.service';

const venueRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/venue').then(m => m.Venue),
    data: {
      defaultSort: `id,${ASC}`,
      authorities: [Authority.ADMIN],
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/venue-detail').then(m => m.VenueDetail),
    resolve: {
      venue: VenueResolve,
    },
    data: { authorities: [Authority.ADMIN] },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/venue-update').then(m => m.VenueUpdate),
    resolve: {
      venue: VenueResolve,
    },
    data: { authorities: [Authority.ADMIN] },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/venue-update').then(m => m.VenueUpdate),
    resolve: {
      venue: VenueResolve,
    },
    data: { authorities: [Authority.ADMIN] },
    canActivate: [UserRouteAccessService],
  },
];

export default venueRoute;
