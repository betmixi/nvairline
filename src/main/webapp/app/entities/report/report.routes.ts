import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import ReportResolve from './route/report-routing-resolve.service';

const reportRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/report').then(m => m.Report),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/report-detail').then(m => m.ReportDetail),
    resolve: {
      report: ReportResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/report-update').then(m => m.ReportUpdate),
    resolve: {
      report: ReportResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/report-update').then(m => m.ReportUpdate),
    resolve: {
      report: ReportResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default reportRoute;
