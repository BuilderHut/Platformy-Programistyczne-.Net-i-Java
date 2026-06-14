import { IDrivingLicense } from 'app/entities/driving-license/driving-license.model';

export interface ICustomer {
  id: number;
  firstName?: string | null;
  lastName?: string | null;
  email?: string | null;
  phone?: string | null;
  drivingLicense?: Pick<IDrivingLicense, 'id' | 'licenseNumber'> | null;
}

export type NewCustomer = Omit<ICustomer, 'id'> & { id: null };
