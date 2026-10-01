import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { Authority } from 'app/shared/jhipster/constants';

import TicketResolve from './route/ticket-routing-resolve.service';

const STAFF_AUTHORITIES = { authorities: [Authority.ADMIN, Authority.STAFF] };

const ticketRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/ticket').then(m => m.Ticket),
    data: {
      defaultSort: `id,${ASC}`,
      ...STAFF_AUTHORITIES,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/ticket-detail').then(m => m.TicketDetail),
    resolve: {
      ticket: TicketResolve,
    },
    data: STAFF_AUTHORITIES,
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/ticket-update').then(m => m.TicketUpdate),
    resolve: {
      ticket: TicketResolve,
    },
    data: STAFF_AUTHORITIES,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/ticket-update').then(m => m.TicketUpdate),
    resolve: {
      ticket: TicketResolve,
    },
    data: STAFF_AUTHORITIES,
    canActivate: [UserRouteAccessService],
  },
];

export default ticketRoute;
