import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import NotificationResolve from './route/notification-routing-resolve.service';

const notificationRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/notification').then(m => m.Notification),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/notification-detail').then(m => m.NotificationDetail),
    resolve: {
      notification: NotificationResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/notification-update').then(m => m.NotificationUpdate),
    resolve: {
      notification: NotificationResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/notification-update').then(m => m.NotificationUpdate),
    resolve: {
      notification: NotificationResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default notificationRoute;
