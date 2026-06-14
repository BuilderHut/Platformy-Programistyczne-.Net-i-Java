import { ICustomer, NewCustomer } from './customer.model';

export const sampleWithRequiredData: ICustomer = {
  id: 3366,
  firstName: 'Karianne',
  lastName: 'Farrell',
  email: 'Kariane.Pfannerstill@yahoo.com',
};

export const sampleWithPartialData: ICustomer = {
  id: 29642,
  firstName: 'Destiney',
  lastName: "VonRueden-O'Keefe",
  email: 'Grace.McCullough@hotmail.com',
};

export const sampleWithFullData: ICustomer = {
  id: 4149,
  firstName: 'Jimmy',
  lastName: 'Bergnaum',
  email: 'Dustin_Stamm45@yahoo.com',
  phone: '1-891-826-4321 x0281',
};

export const sampleWithNewData: NewCustomer = {
  firstName: 'Chase',
  lastName: 'Koelpin',
  email: 'Valentine_Bruen31@hotmail.com',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
