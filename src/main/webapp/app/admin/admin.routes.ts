import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { Authority } from 'app/shared/jhipster/constants';
/* jhipster-needle-add-admin-module-import - JHipster will add admin modules imports here */

const routes: Routes = [
  {
    path: 'dashboard',
    loadComponent: () => import('./dashboard/dashboard').then(m => m.DashboardComponent),
    title: 'Admin Dashboard',
    data: { authorities: [Authority.ADMIN] },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'docs',
    loadComponent: () => import('./docs/docs'),
    title: 'global.menu.admin.apidocs',
    data: { authorities: [Authority.ADMIN] },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'configuration',
    loadComponent: () => import('./configuration/configuration'),
    title: 'configuration.title',
    data: { authorities: [Authority.ADMIN] },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'health',
    loadComponent: () => import('./health/health'),
    title: 'health.title',
    data: { authorities: [Authority.ADMIN] },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'logs',
    loadComponent: () => import('./logs/logs'),
    title: 'logs.title',
    data: { authorities: [Authority.ADMIN] },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'metrics',
    loadComponent: () => import('./metrics/metrics'),
    title: 'metrics.title',
    data: { authorities: [Authority.ADMIN] },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'reviews',
    loadComponent: () => import('./reviews/reviews').then(m => m.AdminReviewsComponent),
    title: 'Quản lý đánh giá',
    data: { authorities: [Authority.ADMIN] },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'support-requests',
    loadComponent: () => import('./support/support-admin').then(m => m.AdminSupportComponent),
    title: 'Quản lý yêu cầu hỗ trợ',
    data: { authorities: [Authority.ADMIN] },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'customer-bookings',
    loadComponent: () => import('./customer-bookings/customer-bookings').then(m => m.AdminCustomerBookingsComponent),
    title: 'Quản lý đặt vé và thống kê',
    data: { authorities: [Authority.ADMIN] },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'events/:eventId/showtimes',
    loadComponent: () => import('./manage-showtimes/manage-showtimes').then(m => m.ManageShowtimesComponent),
    title: 'Quản lý giờ bay',
    data: { authorities: [Authority.ADMIN] },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'aircraft/:aircraftId/seats',
    loadComponent: () => import('./manage-seats/manage-seats').then(m => m.ManageSeatsComponent),
    title: 'Quản lý ghế',
    data: { authorities: [Authority.ADMIN] },
    canActivate: [UserRouteAccessService],
  },
  {
    // Nhan vien van hanh (ROLE_STAFF) chi duoc vao day + Booking/Ticket, khong vao duoc cac trang tren.
    path: 'check-in',
    loadComponent: () => import('./check-in/check-in').then(m => m.AdminCheckInComponent),
    title: 'Check-in',
    data: { authorities: [Authority.ADMIN, Authority.STAFF] },
    canActivate: [UserRouteAccessService],
  },
  /* jhipster-needle-add-admin-route - JHipster will add admin routes here */
];

export default routes;
