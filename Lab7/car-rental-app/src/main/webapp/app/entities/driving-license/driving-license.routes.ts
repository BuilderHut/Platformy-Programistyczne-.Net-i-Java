import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import DrivingLicenseResolve from './route/driving-license-routing-resolve.service';

const drivingLicenseRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/driving-license').then(m => m.DrivingLicense),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/driving-license-detail').then(m => m.DrivingLicenseDetail),
    resolve: {
      drivingLicense: DrivingLicenseResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/driving-license-update').then(m => m.DrivingLicenseUpdate),
    resolve: {
      drivingLicense: DrivingLicenseResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/driving-license-update').then(m => m.DrivingLicenseUpdate),
    resolve: {
      drivingLicense: DrivingLicenseResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default drivingLicenseRoute;
