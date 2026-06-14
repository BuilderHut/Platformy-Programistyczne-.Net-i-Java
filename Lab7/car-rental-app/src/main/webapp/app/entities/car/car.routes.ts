import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import CarResolve from './route/car-routing-resolve.service';

const carRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/car').then(m => m.Car),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/car-detail').then(m => m.CarDetail),
    resolve: {
      car: CarResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/car-update').then(m => m.CarUpdate),
    resolve: {
      car: CarResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/car-update').then(m => m.CarUpdate),
    resolve: {
      car: CarResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default carRoute;
