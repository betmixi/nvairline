import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { Authority } from 'app/shared/jhipster/constants';

import PromotionResolve from './route/promotion-routing-resolve.service';

const promotionRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/promotion').then(m => m.Promotion),
    data: {
      defaultSort: `displayOrder,${ASC}`,
      authorities: [Authority.ADMIN],
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/promotion-detail').then(m => m.PromotionDetail),
    resolve: {
      promotion: PromotionResolve,
    },
    data: { authorities: [Authority.ADMIN] },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/promotion-update').then(m => m.PromotionUpdate),
    resolve: {
      promotion: PromotionResolve,
    },
    data: { authorities: [Authority.ADMIN] },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/promotion-update').then(m => m.PromotionUpdate),
    resolve: {
      promotion: PromotionResolve,
    },
    data: { authorities: [Authority.ADMIN] },
    canActivate: [UserRouteAccessService],
  },
];

export default promotionRoute;
