import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { Authority } from 'app/shared/jhipster/constants';

import FavoriteResolve from './route/favorite-routing-resolve.service';

const favoriteRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/favorite').then(m => m.Favorite),
    data: {
      defaultSort: `id,${ASC}`,
      authorities: [Authority.ADMIN],
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/favorite-detail').then(m => m.FavoriteDetail),
    resolve: {
      favorite: FavoriteResolve,
    },
    data: { authorities: [Authority.ADMIN] },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/favorite-update').then(m => m.FavoriteUpdate),
    resolve: {
      favorite: FavoriteResolve,
    },
    data: { authorities: [Authority.ADMIN] },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/favorite-update').then(m => m.FavoriteUpdate),
    resolve: {
      favorite: FavoriteResolve,
    },
    data: { authorities: [Authority.ADMIN] },
    canActivate: [UserRouteAccessService],
  },
];

export default favoriteRoute;
