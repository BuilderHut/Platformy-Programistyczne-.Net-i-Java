import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import RentalResolve from './route/rental-routing-resolve.service';

const rentalRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/rental').then(m => m.Rental),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/rental-detail').then(m => m.RentalDetail),
    resolve: {
      rental: RentalResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/rental-update').then(m => m.RentalUpdate),
    resolve: {
      rental: RentalResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/rental-update').then(m => m.RentalUpdate),
    resolve: {
      rental: RentalResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default rentalRoute;
