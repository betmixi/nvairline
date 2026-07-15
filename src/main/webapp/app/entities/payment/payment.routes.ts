import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import PaymentResolve from './route/payment-routing-resolve.service';

const paymentRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/payment').then(m => m.Payment),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/payment-detail').then(m => m.PaymentDetail),
    resolve: {
      payment: PaymentResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/payment-update').then(m => m.PaymentUpdate),
    resolve: {
      payment: PaymentResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/payment-update').then(m => m.PaymentUpdate),
    resolve: {
      payment: PaymentResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default paymentRoute;
