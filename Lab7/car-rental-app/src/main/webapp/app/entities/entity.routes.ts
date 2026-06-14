import { Routes } from '@angular/router';

const routes: Routes = [
  {
    path: 'authority',
    data: { pageTitle: 'carrentalApp.adminAuthority.home.title' },
    loadChildren: () => import('./admin/authority/authority.routes'),
  },
  {
    path: 'user-management',
    data: { pageTitle: 'userManagement.home.title' },
    loadChildren: () => import('./admin/user-management/user-management.routes'),
  },
  {
    path: 'customer',
    data: { pageTitle: 'carrentalApp.customer.home.title' },
    loadChildren: () => import('./customer/customer.routes'),
  },
  {
    path: 'driving-license',
    data: { pageTitle: 'carrentalApp.drivingLicense.home.title' },
    loadChildren: () => import('./driving-license/driving-license.routes'),
  },
  {
    path: 'car',
    data: { pageTitle: 'carrentalApp.car.home.title' },
    loadChildren: () => import('./car/car.routes'),
  },
  {
    path: 'rental',
    data: { pageTitle: 'carrentalApp.rental.home.title' },
    loadChildren: () => import('./rental/rental.routes'),
  },
  {
    path: 'category',
    data: { pageTitle: 'carrentalApp.category.home.title' },
    loadChildren: () => import('./category/category.routes'),
  },
  /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
];

export default routes;
