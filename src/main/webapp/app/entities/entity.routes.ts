import { Routes } from '@angular/router';

const routes: Routes = [
  {
    path: 'authority',
    data: { pageTitle: 'dugxApp.adminAuthority.home.title' },
    loadChildren: () => import('./admin/authority/authority.routes'),
  },
  {
    path: 'event',
    data: { pageTitle: 'dugxApp.event.home.title' },
    loadChildren: () => import('./event/event.routes'),
  },
  {
    path: 'category',
    data: { pageTitle: 'dugxApp.category.home.title' },
    loadChildren: () => import('./category/category.routes'),
  },
  {
    path: 'organizer',
    data: { pageTitle: 'dugxApp.organizer.home.title' },
    loadChildren: () => import('./organizer/organizer.routes'),
  },
  {
    path: 'venue',
    data: { pageTitle: 'dugxApp.venue.home.title' },
    loadChildren: () => import('./venue/venue.routes'),
  },
  {
    path: 'ticket-type',
    data: { pageTitle: 'dugxApp.ticketType.home.title' },
    loadChildren: () => import('./ticket-type/ticket-type.routes'),
  },
  {
    path: 'booking',
    data: { pageTitle: 'dugxApp.booking.home.title' },
    loadChildren: () => import('./booking/booking.routes'),
  },
  {
    path: 'booking-detail',
    data: { pageTitle: 'dugxApp.bookingDetail.home.title' },
    loadChildren: () => import('./booking-detail/booking-detail.routes'),
  },
  {
    path: 'payment',
    data: { pageTitle: 'dugxApp.payment.home.title' },
    loadChildren: () => import('./payment/payment.routes'),
  },
  {
    path: 'review',
    data: { pageTitle: 'dugxApp.review.home.title' },
    loadChildren: () => import('./review/review.routes'),
  },
  {
    path: 'favorite',
    data: { pageTitle: 'dugxApp.favorite.home.title' },
    loadChildren: () => import('./favorite/favorite.routes'),
  },
  {
    path: 'notification',
    data: { pageTitle: 'dugxApp.notification.home.title' },
    loadChildren: () => import('./notification/notification.routes'),
  },
  {
    path: 'event-image',
    data: { pageTitle: 'dugxApp.eventImage.home.title' },
    loadChildren: () => import('./event-image/event-image.routes'),
  },
  {
    path: 'coupon',
    data: { pageTitle: 'dugxApp.coupon.home.title' },
    loadChildren: () => import('./coupon/coupon.routes'),
  },
  {
    path: 'report',
    data: { pageTitle: 'dugxApp.report.home.title' },
    loadChildren: () => import('./report/report.routes'),
  },
  {
    path: 'check-in',
    data: { pageTitle: 'dugxApp.checkIn.home.title' },
    loadChildren: () => import('./check-in/check-in.routes'),
  },
  {
    path: 'audit-log',
    data: { pageTitle: 'dugxApp.auditLog.home.title' },
    loadChildren: () => import('./audit-log/audit-log.routes'),
  },
  {
    path: 'address',
    data: { pageTitle: 'dugxApp.address.home.title' },
    loadChildren: () => import('./address/address.routes'),
  },
  {
    path: 'ticket',
    data: { pageTitle: 'dugxApp.ticket.home.title' },
    loadChildren: () => import('./ticket/ticket.routes'),
  },
  {
    path: 'user-management',
    data: { pageTitle: 'userManagement.home.title' },
    loadChildren: () => import('./admin/user-management/user-management.routes'),
  },
  /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
];

export default routes;
