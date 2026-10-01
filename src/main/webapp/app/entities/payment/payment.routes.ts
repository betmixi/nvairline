import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { Authority } from 'app/shared/jhipster/constants';

import PaymentResolve from './route/payment-routing-resolve.service';

const paymentRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/payment').then(m => m.Payment),
    data: {
      defaultSort: `id,${ASC}`,
      authorities: [Authority.ADMIN],
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/payment-detail').then(m => m.PaymentDetail),
    resolve: {
      payment: PaymentResolve,
    },
    data: { authorities: [Authority.ADMIN] },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/payment-update').then(m => m.PaymentUpdate),
    resolve: {
      payment: PaymentResolve,
    },
    data: { authorities: [Authority.ADMIN] },
    canActivate: [UserRouteAccessService],
  },
  {
    // Can dang nhap moi thanh toan duoc: chuyen thang sang /login neu chua dang nhap,
    // sau khi dang nhap xong se tu quay lai day (giu nguyen showtimeId/seatIds tren URL).
    path: 'pay',
    loadComponent: () => import('./pay/payment-page').then(m => m.default),
    canActivate: [UserRouteAccessService],
  },
  {
    // VNPay redirect ve day sau khi nguoi dung thanh toan xong.
    path: 'result',
    loadComponent: () => import('./result/payment-result').then(m => m.default),
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/payment-update').then(m => m.PaymentUpdate),
    resolve: {
      payment: PaymentResolve,
    },
    data: { authorities: [Authority.ADMIN] },
    canActivate: [UserRouteAccessService],
  },
];

export default paymentRoute;
