import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

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
    // Quyen han cu the (ADMIN, hoac ADMIN+STAFF cho vai check-in) duoc gate
    // rieng tren tung route con trong admin.routes.ts - o day chi can dang nhap.
    path: 'admin',
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
    // Nang hang ghe cho ve da mua, thu them phan chenh lech gia qua VNPay.
    path: 'upgrade-seat',
    canActivate: [UserRouteAccessService],
    loadComponent: () => import('app/user/upgrade/upgrade-tickets'),
    title: 'Nâng hạng ghế',
  },
  {
    // Mua them hanh ly ky gui tra truoc cho ve da mua.
    path: 'baggage',
    canActivate: [UserRouteAccessService],
    loadComponent: () => import('app/user/baggage/baggage-tickets'),
    title: 'Hành lý trả trước',
  },
  {
    // 4 dich vu bo tro dung chung 1 component, chi khac addonType/tieu de/icon.
    path: 'shopping',
    canActivate: [UserRouteAccessService],
    loadComponent: () => import('app/user/addon/addon-tickets'),
    data: { addonType: 'SHOPPING', pageTitle: 'Mua sắm', pageIcon: '🛍️' },
    title: 'Mua sắm',
  },
  {
    path: 'hotel-tour',
    canActivate: [UserRouteAccessService],
    loadComponent: () => import('app/user/addon/addon-tickets'),
    data: { addonType: 'HOTEL_TOUR', pageTitle: 'Khách sạn & Tour', pageIcon: '🏨' },
    title: 'Khách sạn & Tour',
  },
  {
    path: 'insurance',
    canActivate: [UserRouteAccessService],
    loadComponent: () => import('app/user/addon/addon-tickets'),
    data: { addonType: 'INSURANCE', pageTitle: 'Bảo hiểm', pageIcon: '🛡️' },
    title: 'Bảo hiểm',
  },
  {
    path: 'other-services',
    canActivate: [UserRouteAccessService],
    loadComponent: () => import('app/user/addon/addon-tickets'),
    data: { addonType: 'OTHER_SERVICE', pageTitle: 'Dịch vụ khác', pageIcon: '🧩' },
    title: 'Dịch vụ khác',
  },
  {
    // Trang tong hop, dan huong sang tat ca dich vu bo tro co the mua them cho ve.
    path: 'services',
    canActivate: [UserRouteAccessService],
    loadComponent: () => import('app/user/services-hub/services-hub'),
    title: 'Dịch vụ bổ trợ',
  },
  {
    // UC Lien he ho tro: gui yeu cau va xem phan hoi.
    path: 'support',
    canActivate: [UserRouteAccessService],
    loadComponent: () => import('app/user/support/support'),
    title: 'Liên hệ hỗ trợ',
  },
  {
    // Tich diem Lotusmiles: xem so du va lich su tich/doi diem.
    path: 'loyalty',
    canActivate: [UserRouteAccessService],
    loadComponent: () => import('app/user/loyalty/loyalty').then(m => m.LoyaltyComponent),
    title: 'Tích điểm',
  },
  {
    path: 'events',
    loadComponent: () => import('app/user/events/events').then(m => m.EventsComponent),
  },
  {
    path: 'favorites',
    canActivate: [UserRouteAccessService],
    loadComponent: () => import('app/user/favorites/favorites').then(m => m.FavoritesComponent),
    title: 'Sự kiện đã lưu',
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
