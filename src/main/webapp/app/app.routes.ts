import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { Authority } from 'app/shared/jhipster/constants';

import { errorRoute } from './layouts/error/error.route';

const routes: Routes = [
  {
    path: '',
    loadComponent: () => import('./home/home'),
    title: 'home.title',
  },
  {
    path: '',
    loadComponent: () => import('./layouts/navbar/navbar'),
    outlet: 'navbar',
  },
  {
    path: 'admin',
    data: {
      authorities: [Authority.ADMIN],
    },
    canActivate: [UserRouteAccessService],
    loadChildren: () => import('./admin/admin.routes'),
  },
  {
    path: 'account',
    loadChildren: () => import('./account/account.route'),
  },
  {
    path: 'login',
    loadComponent: () => import('./login/login'),
    title: 'login.title',
  },
  {
    // Vi ve cua nguoi dung: xem ve da mua va ma QR.
    path: 'my-tickets',
    canActivate: [UserRouteAccessService],
    loadComponent: () => import('./my-tickets/my-tickets'),
    title: 'Vé của tôi',
  },
  {
    path: 'organizer/register',
    canActivate: [UserRouteAccessService],
    loadComponent: () => import('./organizer/organizer-register'),
    title: 'Become Organizer',
  },
  {
    path: 'organizer/dashboard',
    canActivate: [UserRouteAccessService],
    loadComponent: () => import('./organizer/dashboard/dashboard').then(m => m.DashboardComponent),
    title: 'Organizer Dashboard',
  },
  {
    path: 'organizer/events',
    loadComponent: () => import('./organizer/events/event').then(m => m.OrganizerEventComponent),
  },
  {
    path: 'organizer/revenue',
    canActivate: [UserRouteAccessService],
    loadComponent: () => import('./organizer/revenue/revenue').then(m => m.RevenueComponent),
    title: 'Revenue',
  },
  {
    path: 'organizer/events/:eventId/tickets',
    canActivate: [UserRouteAccessService],
    loadComponent: () => import('./organizer/manage-tickets/manage-tickets').then(m => m.ManageTicketsComponent),
  },
  {
    path: 'events',
    loadComponent: () => import('app/user/events/events').then(m => m.EventsComponent),
  },
  {
    // Xem chi tiet su kien truoc khi mua ve, khong can dang nhap.
    path: 'events/:id',
    loadComponent: () => import('app/user/events/detail/event-detail').then(m => m.default),
  },
  {
    path: '',
    loadChildren: () => import('./entities/entity.routes'),
  },
  ...errorRoute,
];

export default routes;
