import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import TicketTypeResolve from './route/ticket-type-routing-resolve.service';

const ticketTypeRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/ticket-type').then(m => m.TicketType),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/ticket-type-detail').then(m => m.TicketTypeDetail),
    resolve: {
      ticketType: TicketTypeResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/ticket-type-update').then(m => m.TicketTypeUpdate),
    resolve: {
      ticketType: TicketTypeResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/ticket-type-update').then(m => m.TicketTypeUpdate),
    resolve: {
      ticketType: TicketTypeResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default ticketTypeRoute;
