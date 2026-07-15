import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import FavoriteResolve from './route/favorite-routing-resolve.service';

const favoriteRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/favorite').then(m => m.Favorite),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/favorite-detail').then(m => m.FavoriteDetail),
    resolve: {
      favorite: FavoriteResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/favorite-update').then(m => m.FavoriteUpdate),
    resolve: {
      favorite: FavoriteResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/favorite-update').then(m => m.FavoriteUpdate),
    resolve: {
      favorite: FavoriteResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default favoriteRoute;
